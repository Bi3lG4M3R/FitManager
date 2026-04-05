package aplication;

import domain.Payment;
import domain.Enrollment;
import domain.EnrollmentStatus;
import domain.Student;
import domain.Plan;
import domain.PaymentType;
import java.util.ArrayList;
import java.time.LocalDate;

public class EnrollmentService {
    public ArrayList<Enrollment> enrollments; 
    static int nextCode;
    public OperationResult enroll(Student student, Plan plan, LocalDate startDate, 
            int duration, double amount, PaymentType paymentType, String paymentDescription){
        if(plan.getMinimumDurationMonths() > duration){
            return new OperationResult(false, "Duration shorter than the plan's minimum.");
        }
        nextCode++;
        
        Enrollment enrollment = new Enrollment(nextCode, student, plan, startDate, duration);
        enrollments.add(enrollment);
        
        Payment payment = new Payment(amount, paymentType, paymentDescription);
        enrollment.registerPayment(payment);
        
        return new OperationResult(true, "Registration successful!", enrollment);
        
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
        Payment payment = new Payment(amount, paymentType, paymentDescription);
        enrollment.registerPayment(payment);
        
        return new OperationResult(true, "Pagamento Registrado");
        
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
    
    public ArrayList<Enrollment> listEnrollment(){
        return enrollments;
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
