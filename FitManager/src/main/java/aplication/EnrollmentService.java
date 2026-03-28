package aplication;

import domain.Payment;
import domain.Enrollment;
import domain.Student;
import domain.Plan;
import domain.PaymentType;
import java.util.ArrayList;
import java.time.LocalDate;

public class EnrollmentService {
    public ArrayList<Enrollment> enrollments; 
    static int nextCode;
    public OperationResult enroll(Student student, Plan plan, LocalDate startDate, 
            int duration, Double amount, PaymentType paymentType, String paymentDescription){
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
}   
