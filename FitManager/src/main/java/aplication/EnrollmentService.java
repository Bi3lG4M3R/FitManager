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
        int i=0;
        while( i < enrollments.size() && enrollments.get(i).getCode()!=code){
            i++;
        }
        if(i == enrollments.size()){
            return new OperationResult(false, "Erro");
        }
        Enrollment enrollment = enrollments.get(i);
        
        if(enrollment.getStatus() != EnrollmentStatus.ACTIVE || amount <= 0){
            return new OperationResult(false, "Erro");
        }
        Payment payment = new Payment(amount, paymentType, paymentDescription);
        enrollment.registerPayment(payment);
        
        return new OperationResult(true, "Pagamento Registrado");
        
    }

    public ArrayList<Enrollment> listEnrollment(){
        return enrollments;
    }
    
    public boolean hasActiveEnrollment(String cpf){
        int i=0;
        while( enrollments.size() > i && !enrollments.get(i).getStudent().getCpf().equals(cpf)){
            i++;
        }
        if(enrollments.size()==i){
            return false;
        }
        Enrollment enrollment = enrollments.get(i);
        if(enrollment.getStatus()!= EnrollmentStatus.ACTIVE){
            return false;
        }
        return true;
    }
}   
