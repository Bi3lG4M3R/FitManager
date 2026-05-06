package application;

import java.time.LocalDate;
import java.util.ArrayList;

import domain.Enrollment;
import domain.EnrollmentStatus;
import domain.Student;
import domain.payment.CashPayment;
import domain.payment.CreditCardPayment;
import domain.payment.DebitCardPayment;
import domain.payment.Payment;
import domain.payment.PaymentType;
import domain.payment.PixPayment;
import domain.plan.Plan;

public class EnrollmentService {
    private ArrayList<Enrollment> enrollments = new ArrayList<>();
    static int nextCode;

    public OperationResult enroll(Student student, Plan plan, LocalDate startDate,
            int duration, double amount, PaymentType paymentType, String paymentDescription,
            String extraData, double amountReceived, int installments, String cardLastDigits) {

        if (plan.getMinDurationMonths() > duration) {
            return new OperationResult(false, "Duração inferior à mínima prevista no plano.");
        }

        /* Validação específica de dinheiro antes de criar qualquer objeto*/
        if (paymentType == PaymentType.CASH && amountReceived < amount) {
            return new OperationResult(false, "Valor recebido é inferior ao valor do pagamento.");
        }

        nextCode++;

        Enrollment enrollment = new Enrollment(nextCode, student, plan, startDate, duration);
        enrollments.add(enrollment);

        Payment payment = buildPayment(startDate, amount, paymentType, paymentDescription,
                extraData, amountReceived, installments, cardLastDigits);
        enrollment.registerPayment(payment);

        return new OperationResult(true, "Cadastro realizado com sucesso!", enrollment);
    }

    public OperationResult registerPayment(int code, double amount, PaymentType paymentType,
            String paymentDescription, String extraData, double amountReceived,
            int installments, String cardLastDigits) {

        int index = 0;
        while (index < enrollments.size() && enrollments.get(index).getCode() != code) {
            index++;
        }
        if(index == enrollments.size()){
            return new OperationResult(false, "Matrícula não encontrada.");
        }

        Enrollment enrollment = enrollments.get(index);
        
        if(enrollment.getStatus() != EnrollmentStatus.ACTIVE){
            return new OperationResult(false, "Não é possível registrar pagamento em uma matrícula inativa.");
        }
        if(amount <= 0){
            return new OperationResult(false, "O valor do pagamento deve ser maior que zero.");
        }
        if (paymentType == null) {
            return new OperationResult(false, "Tipo de pagamento inválido.");
        }

        if (paymentDescription == null || paymentDescription.isBlank()) {
            return new OperationResult(false, "Descrição do pagamento inválida.");
        }

        /*Validação específica de dinheiro antes de criar o objeto*/
        if (paymentType == PaymentType.CASH && amountReceived < amount) {
            return new OperationResult(false, "Valor recebido é inferior ao valor do pagamento.");
        }

        Payment payment = buildPayment(LocalDate.now(), amount, paymentType, paymentDescription,
                extraData, amountReceived, installments, cardLastDigits);
        enrollment.registerPayment(payment);

        return new OperationResult(true, "Pagamento Registrado", payment);
    }

    /*Único ponto onde o tipo concreto de Payment é decidido.
    O switch aqui é legítimo: o objeto ainda não existe e precisa ser instanciado.
     Em todo o restante do sistema, Payment é usado pelo tipo da superclasse.*/
    private Payment buildPayment(LocalDate date, double amount, PaymentType type,
            String description, String extraData, double amountReceived,
            int installments, String cardLastDigits) {

        switch (type) {
            case PIX:
                return new PixPayment(date, amount, description, extraData);
            case CREDIT_CARD:
                return new CreditCardPayment(date, amount, description, installments, cardLastDigits);
            case DEBIT_CARD:
                return new DebitCardPayment(date, amount, description, cardLastDigits);
            case CASH:
                return new CashPayment(date, amount, description, amountReceived);
            default:
                throw new IllegalArgumentException("Tipo de pagamento inválido: " + type);
        }
    }

    public Enrollment findActiveByStudent(String cpf) {
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getStudent().getCpf().equals(cpf) && enrollment.getStatus() == EnrollmentStatus.ACTIVE) {
                return enrollment;
            }
        }
        return null;
    }

    public Enrollment findByCode(int code) {
        int index = 0;
        while (index < enrollments.size() && enrollments.get(index).getCode() != code) {
            index++;
        }
        if (index != enrollments.size()) {
            return enrollments.get(index);
        }
        return null;
    }

    public ArrayList<Enrollment> listEnrollments() {
        return enrollments;
    }

    public OperationResult cancel(int code, String reason) {
        Enrollment enrollment = findByCode(code);
        if (enrollment == null) {
            return new OperationResult(false, "Erro.");
        }
        if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {
            return new OperationResult(false, "Matricula ja cancelada.");
        }
        enrollment.cancel(reason);
        double balanceMonthsUsed = enrollment.calculateBalanceForMonthsUsed();
        if(balanceMonthsUsed > 0.0)
            return new OperationResult(true, "Matricula  cancelada!!", enrollment.getPlan().getCancellationFee(enrollment) + balanceMonthsUsed);

        return new OperationResult(true, "Matricula  cancelada!!", enrollment.getPlan().getCancellationFee(enrollment));
    }

    public boolean hasActiveEnrollment(String cpf) {
        int index = 0;
        while (enrollments.size() > index && !enrollments.get(index).getStudent().getCpf().equals(cpf)) {
            index++;
        }
        if (enrollments.size() == index) {
            return false;
        }
        Enrollment enrollment = enrollments.get(index);
        return enrollment.getStatus() == EnrollmentStatus.ACTIVE;
    }
}