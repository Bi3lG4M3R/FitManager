package application;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import exceptions.CorruptedFileException;
import exceptions.WriteFailureException;

import domain.Enrollment;
import domain.EnrollmentStatus;
import domain.Student;
import domain.payment.*;
import domain.plan.Plan;

public class EnrollmentService extends Repository<Enrollment> {

    // nextCode agora é de instância (não static) para funcionar corretamente
    // com persistência — o valor é salvo e restaurado junto com o arquivo.
    private int nextCode = 0;

    // ------------------------------------------------------------------ //
    // Regras de negócio — idênticas ao original                           //
    // ------------------------------------------------------------------ //

    public OperationResult<Enrollment> enroll(Student student, Plan plan, LocalDate startDate, int duration, Payment payment) {
        if (plan.getMinDurationMonths() > duration) {
            return new OperationResult<>(false, "Duração inferior à mínima prevista no plano.");
        }

        nextCode++;

        Enrollment enrollment = new Enrollment(nextCode, student, plan, startDate, duration);
        items.add(enrollment);
        enrollment.registerPayment(payment);

        return new OperationResult<>(true, "Cadastro realizado com sucesso!", enrollment);
    }

    public OperationResult<Payment> registerPayment(int code, Payment payment) {
        Enrollment enrollment = findByCode(code);

        if (enrollment == null)
            return new OperationResult<>(false, "Matrícula não encontrada.");
        if (enrollment.getStatus() != EnrollmentStatus.ACTIVE)
            return new OperationResult<>(false, "Não é possível registrar pagamento em uma matrícula inativa.");
        if (payment.getAmount() <= 0)
            return new OperationResult<>(false, "O valor do pagamento deve ser maior que zero.");
    
        
        // Validação específica para pagamentos em dinheiro
        if(payment instanceof domain.payment.CashPayment) {
            domain.payment.CashPayment cashPayment = (domain.payment.CashPayment) payment;
            if(cashPayment.getAmountReceived() < cashPayment.getAmount()) {
                return new OperationResult<>(false, "Valor recebido é menor que o valor do pagamento.");
            }
        }

        enrollment.registerPayment(payment);
        return new OperationResult<>(true, "Pagamento Registrado", payment);
    }

    public Enrollment findActiveByStudent(String cpf) {
        for (Enrollment enrollment : items) {
            if (enrollment.getStudent().getCpf().equals(cpf) && enrollment.getStatus() == EnrollmentStatus.ACTIVE)
                return enrollment;
        }
        return null;
    }

    public Enrollment findByCode(int code) {
        for (Enrollment enrollment : items) {
            if (enrollment.getCode() == code) return enrollment;
        }
        return null;
    }

    public ArrayList<Enrollment> listEnrollments() { return items; }

    public OperationResult<Enrollment> cancel(int code, String reason) {
        Enrollment enrollment = findByCode(code);
        if (enrollment == null) return new OperationResult<>(false, "Matricula não encontrada.");
        if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) return new OperationResult<>(false, "Matricula ja cancelada.");

        enrollment.cancel(reason);
        return new OperationResult<>(true, "Matricula cancelada!!", enrollment);
    }

    public OperationResult<Double> calculateCancelationFee(int code) {
        Enrollment enrollment = findByCode(code);
        if (enrollment == null) return new OperationResult<>(false, "Matricula não encontrada.");
        if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) return new OperationResult<>(false, "Matricula ja cancelada.");

        double balanceMonthsUsed = enrollment.calculateBalanceForMonthsUsed();
        double fee = enrollment.getPlan().getCancellationFee(enrollment);

        if (balanceMonthsUsed > 0.0)
            return new OperationResult<>(true, "Taxa de cancelamento: ", fee + balanceMonthsUsed);
        return new OperationResult<>(true, "Taxa de cancelamento: ", fee);
    }

    public boolean hasActiveEnrollment(String cpf) {
        return findActiveByStudent(cpf) != null;
    }

    // ------------------------------------------------------------------ //
    // Persistência                                                          //
    // ------------------------------------------------------------------ //

    private static final String SEP           = "|";
    private static final String SEP_REGEX     = "\\|";
    private static final String NEXT_CODE_KEY = "NEXT_CODE";
    private static final String PAYMENT_PREFIX = "PAYMENT";
    private static final String END_PAYMENTS  = "END_PAYMENTS";
    private static final String NULL_MARKER   = "NULL";

    /**
     * Grava nextCode na primeira linha, depois cada matrícula em um bloco:
     *   cabeçalho da matrícula
     *   uma linha por pagamento
     *   END_PAYMENTS
     *   linha em branco
     */
    @Override
    public void save(String filePath) throws PersistenceException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {

            writer.write(NEXT_CODE_KEY + SEP + nextCode);
            writer.newLine();

            for (Enrollment e : items) {
                writer.write(encodeHeader(e));
                writer.newLine();

                for (Payment p : e.getPayments()) {
                    writer.write(encodePayment(p));
                    writer.newLine();
                }

                writer.write(END_PAYMENTS);
                writer.newLine();
                writer.newLine();
            }

        } catch (IOException e) {
            throw new WriteFailureException("Falha ao gravar arquivo de matrículas: " + e.getMessage(), filePath, e);
        }
    }

    /**
     * Reconstrói matrículas a partir do arquivo.
     * Alunos e planos DEVEM estar carregados antes desta chamada.
     * Referências cruzadas são resolvidas por CPF e nome do plano.
     * Se um CPF ou nome de plano não for encontrado, lança PersistenceException.
     */
    public void load(String filePath, StudentService students, PlanService plans)
            throws PersistenceException {

        File file = new File(filePath);
        if (!file.exists()) return; // arquivo ausente é normal

        items.clear();
        nextCode = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            // Primeira linha: nextCode
            String firstLine = reader.readLine();
            if (firstLine == null) return;
            String[] ncParts = firstLine.trim().split(SEP_REGEX, -1);
            if (ncParts.length != 2 || !ncParts[0].equals(NEXT_CODE_KEY)) {
                throw new CorruptedFileException(
                    "Arquivo de matrículas corrompido: primeira linha inválida.", filePath);
            }
            try {
                nextCode = Integer.parseInt(ncParts[1]);
            } catch (NumberFormatException e) {
                throw new CorruptedFileException(
                    "Arquivo de matrículas corrompido: nextCode inválido.", filePath, e);
            }

            // Blocos de matrícula
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) continue;

                Enrollment enrollment = decodeHeader(line, lineNumber, filePath, students, plans);

                // Lê pagamentos até END_PAYMENTS
                String paymentLine;
                while ((paymentLine = reader.readLine()) != null) {
                    lineNumber++;
                    paymentLine = paymentLine.trim();
                    if (paymentLine.equals(END_PAYMENTS)) break;
                    if (paymentLine.isEmpty()) continue;
                    enrollment.registerPayment(decodePayment(paymentLine, lineNumber, filePath));
                }

                items.add(enrollment);
            }

        } catch (IOException e) {
            throw new WriteFailureException(
                "Falha ao ler arquivo de matrículas: " + e.getMessage(), filePath, e);
        }
    }

    /** Obrigatório pelo contrato abstrato — não usar diretamente. */
    @Override
    public void load(String filePath) throws PersistenceException {
        throw new UnsupportedOperationException(
            "Use load(filePath, students, plans) para matrículas.");
    }

    // ------------------------------------------------------------------ //
    // Encode / decode                                                      //
    // ------------------------------------------------------------------ //

    // Formato: code|cpf|planName|startDate|durationMonths|status|cancellationDate|cancellationReason
    private String encodeHeader(Enrollment e) {
        String cancDate   = e.getCancellationDate()   != null ? e.getCancellationDate().toString() : NULL_MARKER;
        String cancReason = e.getCancellationReason() != null ? e.getCancellationReason()           : NULL_MARKER;
        return String.join(SEP,
            String.valueOf(e.getCode()),
            e.getStudent().getCpf(),
            e.getPlan().getName(),
            e.getStartDate().toString(),
            String.valueOf(e.getDurationMonths()),
            e.getStatus().name(),
            cancDate,
            cancReason
        );
    }

    private Enrollment decodeHeader(String line, int lineNumber, String filePath,
            StudentService students, PlanService plans) throws PersistenceException {

        String[] p = line.split(SEP_REGEX, -1);
        if (p.length != 8) {
            throw new CorruptedFileException(
                "Arquivo de matrículas corrompido na linha " + lineNumber
                + ": esperado 8 campos, encontrado " + p.length, filePath);
        }

        try {
            int code            = Integer.parseInt(p[0]);
            String cpf          = p[1];
            String planName     = p[2];
            LocalDate startDate = LocalDate.parse(p[3]);
            int durationMonths  = Integer.parseInt(p[4]);
            String statusStr    = p[5];
            // CORRIGIDO: p[6] é a data de cancelamento — era ignorada antes
            String cancDateStr  = p[6];
            String cancReason   = p[7].equals(NULL_MARKER) ? null : p[7];

            Student student = students.findByCpf(cpf);
            if (student == null) {
                throw new CorruptedFileException(
                    "Arquivo de matrículas corrompido na linha " + lineNumber
                    + ": aluno com CPF '" + cpf + "' não encontrado.", filePath);
            }

            OperationResult<Plan> planResult = plans.findByName(planName);
            if (!planResult.isSuccess()) {
                throw new CorruptedFileException(
                    "Arquivo de matrículas corrompido na linha " + lineNumber
                    + ": plano '" + planName + "' não encontrado.", filePath);
            }

            Enrollment enrollment = new Enrollment(code, student, planResult.getData(), startDate, durationMonths);

            if (statusStr.equals("CANCELLED")) {
                // CORRIGIDO: restaura a data original lida do arquivo em vez de usar LocalDate.now()
                LocalDate cancDate = cancDateStr.equals(NULL_MARKER) ? LocalDate.now() : LocalDate.parse(cancDateStr);
                enrollment.cancelWithDate(cancDate, cancReason);
            }

            return enrollment;

        } catch (NumberFormatException | DateTimeParseException e) {
            throw new CorruptedFileException(
                "Arquivo de matrículas corrompido na linha " + lineNumber
                + ": valor inválido — " + e.getMessage(), filePath, e);
        }
    }

    // Formato por tipo:
    //   PAYMENT|PIX|date|amount|description|pixKey
    //   PAYMENT|CASH|date|amount|description|amountReceived
    //   PAYMENT|DEBIT|date|amount|description|cardLastDigits
    //   PAYMENT|CREDIT|date|amount|description|installments|cardLastDigits
    private String encodePayment(Payment p) {
        StringBuilder sb = new StringBuilder(PAYMENT_PREFIX + SEP);

        if (p instanceof PixPayment) {
            sb.append("PIX").append(SEP).append(p.getDate()).append(SEP)
              .append(p.getAmount()).append(SEP).append(p.getDescription()).append(SEP)
              .append(((PixPayment) p).getPixKey());

        } else if (p instanceof CashPayment) {
            sb.append("CASH").append(SEP).append(p.getDate()).append(SEP)
              .append(p.getAmount()).append(SEP).append(p.getDescription()).append(SEP)
              .append(((CashPayment) p).getAmountReceived());

        } else if (p instanceof DebitCardPayment) {
            sb.append("DEBIT").append(SEP).append(p.getDate()).append(SEP)
              .append(p.getAmount()).append(SEP).append(p.getDescription()).append(SEP)
              .append(((DebitCardPayment) p).getCardLastDigits());

        } else if (p instanceof CreditCardPayment) {
            CreditCardPayment cp = (CreditCardPayment) p;
            sb.append("CREDIT").append(SEP).append(p.getDate()).append(SEP)
              .append(p.getAmount()).append(SEP).append(p.getDescription()).append(SEP)
              .append(cp.getInstallments()).append(SEP).append(cp.getCardLastDigits());
        }

        return sb.toString();
    }

    private Payment decodePayment(String line, int lineNumber, String filePath)
            throws PersistenceException {

        String[] p = line.split(SEP_REGEX, -1);
        if (p.length < 6 || !p[0].equals(PAYMENT_PREFIX)) {
            throw new CorruptedFileException(
                "Arquivo de matrículas corrompido na linha " + lineNumber
                + ": linha de pagamento inválida.", filePath);
        }

        try {
            String type        = p[1];
            LocalDate date     = LocalDate.parse(p[2]);
            double amount      = Double.parseDouble(p[3]);
            String description = p[4];

            switch (type) {
                case "PIX":
                    return new PixPayment(date, amount, description, p[5]);
                case "CASH":
                    return new CashPayment(date, amount, description, Double.parseDouble(p[5]));
                case "DEBIT":
                    return new DebitCardPayment(date, amount, description, p[5]);
                case "CREDIT":
                    if (p.length < 7) throw new CorruptedFileException(
                        "Arquivo de matrículas corrompido na linha " + lineNumber
                        + ": pagamento CREDIT incompleto.", filePath);
                    return new CreditCardPayment(date, amount, description,
                                                 Integer.parseInt(p[5]), p[6]);
                default:
                    throw new CorruptedFileException(
                        "Arquivo de matrículas corrompido na linha " + lineNumber
                        + ": tipo de pagamento desconhecido '" + type + "'", filePath);
            }

        } catch (NumberFormatException | DateTimeParseException e) {
            throw new CorruptedFileException(
                "Arquivo de matrículas corrompido na linha " + lineNumber
                + ": valor inválido — " + e.getMessage(), filePath, e);
        }
    }
}