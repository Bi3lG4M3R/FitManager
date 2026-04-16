package application;

import java.time.LocalDate;
import java.util.ArrayList;

import domain.Enrollment;
import domain.EnrollmentStatus;
import domain.Student;
import domain.payment.Payment;
import domain.payment.PaymentType;
import domain.plan.Plan;

public class EnrollmentService {
    private ArrayList<Enrollment> enrollments = new ArrayList<>(); 
    static int nextCode;
    public OperationResult enroll(Student student, Plan plan, LocalDate startDate, 
            int duration, double amount, PaymentType paymentType, String paymentDescription){
        if(plan.getMinDurationMonths() > duration){
            return new OperationResult(false, "Duração inferior à mínima prevista no plano.");
        }
        nextCode++;
        
        Enrollment enrollment = new Enrollment(nextCode, student, plan, startDate, duration);
        enrollments.add(enrollment);
        
        Payment payment = new Payment(startDate, amount, paymentType, paymentDescription);
        enrollment.registerPayment(payment);
        
        return new OperationResult(true, "Cadastro realizado com sucesso!", enrollment);
        
    }
    
    public OperationResult registerPayment(int code, double amount, PaymentType paymentType, String paymentDescription){
        int index=0;
        while( index < enrollments.size() && enrollments.get(index).getCode()!=code){
            index++;
        }
        if(index == enrollments.size()){
            return new OperationResult(false, "Erro");
        }
        Enrollment enrollment = enrollments.get(index);
        
        if(enrollment.getStatus() != EnrollmentStatus.ACTIVE || amount <= 0){
            return new OperationResult(false, "Erro");
        }
        Payment payment = new Payment(LocalDate.now(), amount, paymentType, paymentDescription);
        enrollment.registerPayment(payment);
        
        return new OperationResult(true, "Pagamento Registrado", payment);
        
    }
    
    public Enrollment findActiveByStudent(String cpf){
        for(Enrollment enrollment : enrollments){
            if(enrollment.getStudent().getCpf().equals(cpf) && enrollment.getStatus() == EnrollmentStatus.ACTIVE){
                return enrollment;
            }
        }
        return null;
    }
    
    public Enrollment findByCode(int code){
        int index = 0;
        while( index < enrollments.size() && enrollments.get(index).getCode()!=code){
            index++;
        }
        if(index != enrollments.size()){
            return enrollments.get(index);
        }
        return null;
    } 
    
    public ArrayList<Enrollment> listEnrollments(){
        return enrollments;
    }
    
    public OperationResult cancel(int code, String reason){
        Enrollment enrollment = findByCode(code); 
        if(enrollment==null){
            return new OperationResult(false, "Erro.");
        }
        if(enrollment.getStatus()!=EnrollmentStatus.ACTIVE){
            return new OperationResult(false, "Matricula ja cancelada.");
        }
        enrollment.cancel(reason);
        return new OperationResult(true, "Matricula  cancelada!!");
    }
    
    public boolean hasActiveEnrollment(String cpf){
        int index=0;
        while( enrollments.size() > index && !enrollments.get(index).getStudent().getCpf().equals(cpf)){
            index++;
        }
        if(enrollments.size()==index){
            return false;
        }
        Enrollment enrollment = enrollments.get(index);
        return enrollment.getStatus() == EnrollmentStatus.ACTIVE;
    }
    
}   
