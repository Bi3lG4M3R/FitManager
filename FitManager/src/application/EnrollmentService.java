package application;

import java.time.LocalDate;
import java.util.ArrayList;

import domain.Enrollment;
import domain.EnrollmentStatus;
import domain.Student;
import domain.payment.Payment;
import domain.plan.Plan;

public class EnrollmentService {
    private ArrayList<Enrollment> enrollments = new ArrayList<>();
    static int nextCode;

    // Agora recebemos diretamente o objeto Payment pronto!
    public OperationResult<Enrollment> enroll(Student student, Plan plan, LocalDate startDate, int duration, Payment payment) {

        if (plan.getMinDurationMonths() > duration) {
            return new OperationResult<>(false, "Duração inferior à mínima prevista no plano.");
        }

        nextCode++;

        Enrollment enrollment = new Enrollment(nextCode, student, plan, startDate, duration);
        enrollments.add(enrollment);

        enrollment.registerPayment(payment);

        return new OperationResult<>(true, "Cadastro realizado com sucesso!", enrollment);
    }

    public OperationResult<Payment> registerPayment(int code, Payment payment) {
        Enrollment enrollment = findByCode(code);
        
        if(enrollment == null){
            return new OperationResult<>(false, "Matrícula não encontrada.");
        }
        if(enrollment.getStatus() != EnrollmentStatus.ACTIVE){
            return new OperationResult<>(false, "Não é possível registrar pagamento em uma matrícula inativa.");
        }
        if(payment.getAmount() <= 0){
            return new OperationResult<>(false, "O valor do pagamento deve ser maior que zero.");
        }
        
        // Validação específica para pagamentos em dinheiro
        if(payment instanceof domain.payment.CashPayment) {
            domain.payment.CashPayment cashPayment = (domain.payment.CashPayment) payment;
            if(cashPayment.getAmountReceived() < cashPayment.getAmount()) {
                return new OperationResult(false, "Valor recebido é menor que o valor do pagamento.");
            }
        }

        enrollment.registerPayment(payment);
        return new OperationResult<>(true, "Pagamento Registrado", payment);
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
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getCode() == code) return enrollment;
        }
        return null;
    }

    public ArrayList<Enrollment> listEnrollments() { return enrollments; }
    
    public OperationResult<Enrollment> cancel(int code, String reason){
        Enrollment enrollment = findByCode(code); 
        if(enrollment==null) return new OperationResult<>(false, "Matricula não encontrada.");
        if(enrollment.getStatus()!=EnrollmentStatus.ACTIVE) return new OperationResult<>(false, "Matricula ja cancelada.");
        
        enrollment.cancel(reason);
        return new OperationResult<>(true, "Matricula cancelada!!", enrollment);
    }

    public OperationResult<Double> calculateCancelationFee(int code){
        Enrollment enrollment = findByCode(code); 
        if(enrollment==null) return new OperationResult<>(false, "Matricula não encontrada.");
        if(enrollment.getStatus()!=EnrollmentStatus.ACTIVE) return new OperationResult<>(false, "Matricula ja cancelada.");

        double balanceMonthsUsed = enrollment.calculateBalanceForMonthsUsed();
        double fee = enrollment.getPlan().getCancellationFee(enrollment);
        
        if(balanceMonthsUsed > 0.0) {
            return new OperationResult<>(true, "Taxa de cancelamento: ", fee + balanceMonthsUsed);
        }
        return new OperationResult<>(true, "Taxa de cancelamento: ", fee);
    }

    public boolean hasActiveEnrollment(String cpf) {
        return findActiveByStudent(cpf) != null;
    }
}