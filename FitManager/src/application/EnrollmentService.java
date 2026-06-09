// application/EnrollmentService.java
package application;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import domain.Enrollment;
import domain.EnrollmentStatus;
import domain.Student;
import domain.payment.*;
import domain.plan.Plan;

public class EnrollmentService extends Repository<Enrollment> {
    private ArrayList<Enrollment> enrollments = new ArrayList<>();
    private int nextCode = 0;

    public OperationResult<Enrollment> enroll(Student student, Plan plan, LocalDate startDate, int duration, Payment payment) {
        try {
            // Validações básicas
            if (student == null)
                return new OperationResult<>(false, "Aluno não pode ser nulo.");
            if (!student.isActive())
                return new OperationResult<>(false, "Aluno inativo não pode ser matriculado.");
            if (plan == null)
                return new OperationResult<>(false, "Plano não pode ser nulo.");
            if (startDate == null)
                return new OperationResult<>(false, "Data de início é obrigatória.");
            if (duration <= 0)
                return new OperationResult<>(false, "Duração deve ser maior que zero.");
            if (payment == null)
                return new OperationResult<>(false, "Pagamento inicial é obrigatório.");
            // Validação de pagamento
            OperationResult<Boolean> paymentValidation = validatePayment(payment);
            if (!paymentValidation.isSuccess())
                return new OperationResult<>(false, paymentValidation.getMessage());
            // Regras de negócio
            if (hasActiveEnrollment(student.getCpf()))
                return new OperationResult<>(false, "Aluno já possui matrícula ativa.");
            if (plan.getMinDurationMonths() > duration)
                return new OperationResult<>(false, "Duração inferior à mínima prevista no plano (" + plan.getMinDurationMonths() + " meses).");
            nextCode++;
            Enrollment enrollment = new Enrollment(nextCode, student, plan, startDate, duration);
            enrollments.add(enrollment);
            enrollment.registerPayment(payment);
            return new OperationResult<>(true, "Matrícula realizada com sucesso!", enrollment);
        } catch (Exception e) {
            return new OperationResult<>(false, "Erro interno ao realizar matrícula: " + e.getMessage());
        }
    }

    private OperationResult<Boolean> validatePayment(Payment payment) {
        if (payment.getDate() == null)
            return new OperationResult<>(false, "Data do pagamento é obrigatória.");
        if (payment.getAmount() <= 0)
            return new OperationResult<>(false, "Valor do pagamento deve ser maior que zero.");
        if (payment.getDescription() == null || payment.getDescription().isBlank())
            return new OperationResult<>(false, "Descrição do pagamento é obrigatória.");
        if (payment instanceof CashPayment) {
            CashPayment cash = (CashPayment) payment;
            if (cash.getAmountReceived() < cash.getAmount())
                return new OperationResult<>(false, "Valor recebido é menor que o valor do pagamento.");
        } else if (payment instanceof PixPayment) {
            if (((PixPayment) payment).getPixKey() == null || ((PixPayment) payment).getPixKey().isBlank())
                return new OperationResult<>(false, "Chave PIX é obrigatória.");
        } else if (payment instanceof DebitCardPayment) {
            String digits = ((DebitCardPayment) payment).getCardLastDigits();
            if (digits == null || digits.isBlank())
                return new OperationResult<>(false, "Últimos dígitos do cartão são obrigatórios.");
            if (!digits.matches("\\d{4}"))
                return new OperationResult<>(false, "Últimos dígitos do cartão devem conter exatamente 4 dígitos numéricos.");
        } else if (payment instanceof CreditCardPayment) {
            CreditCardPayment credit = (CreditCardPayment) payment;
            if (credit.getInstallments() <= 0)
                return new OperationResult<>(false, "Número de parcelas deve ser maior que zero.");
            String digits = credit.getCardLastDigits();
            if (digits == null || digits.isBlank())
                return new OperationResult<>(false, "Últimos dígitos do cartão são obrigatórios.");
            if (!digits.matches("\\d{4}"))
                return new OperationResult<>(false, "Últimos dígitos do cartão devem conter exatamente 4 dígitos numéricos.");
        }
        return new OperationResult<>(true, "Pagamento válido.");
    }

    public OperationResult<Payment> registerPayment(int code, Payment payment) {
        try {
            if (code <= 0)
                return new OperationResult<>(false, "Código da matrícula deve ser maior que zero.");
            OperationResult<Boolean> paymentValidation = validatePayment(payment);
            if (!paymentValidation.isSuccess())
                return new OperationResult<>(false, paymentValidation.getMessage());
            Enrollment enrollment = findByCode(code);
            if (enrollment == null)
                return new OperationResult<>(false, "Matrícula não encontrada.");
            if (enrollment.getStatus() != EnrollmentStatus.ACTIVE)
                return new OperationResult<>(false, "Não é possível registrar pagamento em matrícula inativa.");
            enrollment.registerPayment(payment);
            return new OperationResult<>(true, "Pagamento registrado com sucesso.", payment);
        } catch (Exception e) {
            return new OperationResult<>(false, "Erro interno ao registrar pagamento: " + e.getMessage());
        }
    }

    public Enrollment findActiveByStudent(String cpf) {
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getStudent().getCpf().equals(cpf) && enrollment.getStatus() == EnrollmentStatus.ACTIVE)
                return enrollment;
        }
        return null;
    }

    public Enrollment findByCode(int code) {
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getCode() == code) return enrollment;
        }
        return null;
    }

    public ArrayList<Enrollment> listEnrollments() {
        return enrollments;
    }

    public OperationResult<Enrollment> cancel(int code, String reason) {
        try {
            if (code <= 0)
                return new OperationResult<>(false, "Código da matrícula deve ser maior que zero.");
            if (reason == null || reason.isBlank())
                return new OperationResult<>(false, "Motivo do cancelamento é obrigatório.");
            Enrollment enrollment = findByCode(code);
            if (enrollment == null)
                return new OperationResult<>(false, "Matrícula não encontrada.");
            if (enrollment.getStatus() != EnrollmentStatus.ACTIVE)
                return new OperationResult<>(false, "Matrícula já está cancelada.");
            enrollment.cancel(reason);
            return new OperationResult<>(true, "Matrícula cancelada com sucesso.", enrollment);
        } catch (Exception e) {
            return new OperationResult<>(false, "Erro interno ao cancelar matrícula: " + e.getMessage());
        }
    }

    public OperationResult<Double> calculateCancelationFee(int code) {
        try {
            Enrollment enrollment = findByCode(code);
            if (enrollment == null)
                return new OperationResult<>(false, "Matrícula não encontrada.");
            if (enrollment.getStatus() != EnrollmentStatus.ACTIVE)
                return new OperationResult<>(false, "Matrícula já cancelada.");
            double balanceMonthsUsed = enrollment.calculateBalanceForMonthsUsed();
            double fee = enrollment.getPlan().getCancellationFee(enrollment);
            double total = fee + (balanceMonthsUsed > 0.0 ? balanceMonthsUsed : 0.0);
            return new OperationResult<>(true, "Taxa de cancelamento calculada.", total);
        } catch (Exception e) {
            return new OperationResult<>(false, "Erro ao calcular taxa: " + e.getMessage());
        }
    }

    public boolean hasActiveEnrollment(String cpf) {
        return findActiveByStudent(cpf) != null;
    }

    // Persistência
    private static final String SEP = "|";
    private static final String SEP_REGEX = "\\|";
    private static final String NEXT_CODE_KEY = "NEXT_CODE";
    private static final String PAYMENT_PREFIX = "PAYMENT";
    private static final String END_PAYMENTS = "END_PAYMENTS";
    private static final String NULL_MARKER = "NULL";

    @Override
    public void save(String filePath) throws exceptions.PersistenceException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            writer.write(NEXT_CODE_KEY + SEP + nextCode);
            writer.newLine();
            for (Enrollment e : enrollments) {
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
            throw new exceptions.WriteFailureException("Falha ao gravar arquivo de matrículas: " + e.getMessage(), filePath, e);
        }
    }

    public void load(String filePath, StudentService students, PlanService plans) throws exceptions.PersistenceException {
        File file = new File(filePath);
        if (!file.exists()) return;
        enrollments.clear();
        nextCode = 0;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String firstLine = reader.readLine();
            if (firstLine == null) return;
            String[] ncParts = firstLine.trim().split(SEP_REGEX, -1);
            if (ncParts.length != 2 || !ncParts[0].equals(NEXT_CODE_KEY)) {
                throw new exceptions.CorruptedFileException("Arquivo de matrículas corrompido: primeira linha inválida.", filePath);
            }
            try {
                nextCode = Integer.parseInt(ncParts[1]);
            } catch (NumberFormatException e) {
                throw new exceptions.CorruptedFileException("Arquivo de matrículas corrompido: nextCode inválido.", filePath, e);
            }
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) continue;
                Enrollment enrollment = decodeHeader(line, lineNumber, filePath, students, plans);
                String paymentLine;
                while ((paymentLine = reader.readLine()) != null) {
                    lineNumber++;
                    paymentLine = paymentLine.trim();
                    if (paymentLine.equals(END_PAYMENTS)) break;
                    if (paymentLine.isEmpty()) continue;
                    enrollment.registerPayment(decodePayment(paymentLine, lineNumber, filePath));
                }
                enrollments.add(enrollment);
            }
        } catch (IOException e) {
            throw new exceptions.WriteFailureException("Falha ao ler arquivo de matrículas: " + e.getMessage(), filePath, e);
        }
    }

    @Override
    public void load(String filePath) throws exceptions.PersistenceException {
        throw new exceptions.PersistenceException("Use load(filePath, students, plans) para matrículas.", filePath);
    }

    private String encodeHeader(Enrollment e) {
        String cancDate = e.getCancellationDate() != null ? e.getCancellationDate().toString() : NULL_MARKER;
        String cancReason = e.getCancellationReason() != null ? e.getCancellationReason() : NULL_MARKER;
        return String.join(SEP, String.valueOf(e.getCode()), e.getStudent().getCpf(), e.getPlan().getName(),
                e.getStartDate().toString(), String.valueOf(e.getDurationMonths()), e.getStatus().name(), cancDate, cancReason);
    }

    private Enrollment decodeHeader(String line, int lineNumber, String filePath, StudentService students, PlanService plans) throws exceptions.PersistenceException {
        String[] p = line.split(SEP_REGEX, -1);
        if (p.length != 8) {
            throw new exceptions.CorruptedFileException("Arquivo de matrículas corrompido na linha " + lineNumber + ": esperado 8 campos.", filePath);
        }
        try {
            int code = Integer.parseInt(p[0]);
            String cpf = p[1];
            String planName = p[2];
            LocalDate startDate = LocalDate.parse(p[3]);
            int durationMonths = Integer.parseInt(p[4]);
            String statusStr = p[5];
            String cancDateStr = p[6];
            String cancReason = p[7].equals(NULL_MARKER) ? null : p[7];
            Student student = students.findByCpf(cpf);
            if (student == null) {
                throw new exceptions.CorruptedFileException("Arquivo de matrículas corrompido na linha " + lineNumber + ": aluno com CPF '" + cpf + "' não encontrado.", filePath);
            }
            OperationResult<Plan> planResult = plans.findByName(planName);
            if (!planResult.isSuccess()) {
                throw new exceptions.CorruptedFileException("Arquivo de matrículas corrompido na linha " + lineNumber + ": plano '" + planName + "' não encontrado.", filePath);
            }
            Enrollment enrollment = new Enrollment(code, student, planResult.getData(), startDate, durationMonths);
            if (statusStr.equals("CANCELLED")) {
                LocalDate cancDate = cancDateStr.equals(NULL_MARKER) ? LocalDate.now() : LocalDate.parse(cancDateStr);
                enrollment.cancelWithDate(cancDate, cancReason);
            }
            return enrollment;
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new exceptions.CorruptedFileException("Arquivo de matrículas corrompido na linha " + lineNumber + ": valor inválido.", filePath, e);
        }
    }

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

    private Payment decodePayment(String line, int lineNumber, String filePath) throws exceptions.PersistenceException {
        String[] p = line.split(SEP_REGEX, -1);
        if (p.length < 6 || !p[0].equals(PAYMENT_PREFIX)) {
            throw new exceptions.CorruptedFileException("Arquivo de matrículas corrompido na linha " + lineNumber + ": linha de pagamento inválida.", filePath);
        }
        try {
            String type = p[1];
            LocalDate date = LocalDate.parse(p[2]);
            double amount = Double.parseDouble(p[3]);
            String description = p[4];
            switch (type) {
                case "PIX": return new PixPayment(date, amount, description, p[5]);
                case "CASH": return new CashPayment(date, amount, description, Double.parseDouble(p[5]));
                case "DEBIT": return new DebitCardPayment(date, amount, description, p[5]);
                case "CREDIT":
                    if (p.length < 7) throw new exceptions.CorruptedFileException("Arquivo de matrículas corrompido na linha " + lineNumber + ": pagamento CREDIT incompleto.", filePath);
                    return new CreditCardPayment(date, amount, description, Integer.parseInt(p[5]), p[6]);
                default:
                    throw new exceptions.CorruptedFileException("Arquivo de matrículas corrompido na linha " + lineNumber + ": tipo de pagamento desconhecido '" + type + "'", filePath);
            }
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new exceptions.CorruptedFileException("Arquivo de matrículas corrompido na linha " + lineNumber + ": valor inválido.", filePath, e);
        }
    }
}