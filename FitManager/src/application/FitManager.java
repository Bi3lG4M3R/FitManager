package application;

import java.time.LocalDate;
import java.util.ArrayList;

import domain.Enrollment;
import domain.Student;
import domain.payment.PaymentType;
import domain.plan.Plan;
import domain.plan.PlanType;

public class FitManager {
    private final StudentService studentService;
    private final PlanService planService;
    private final EnrollmentService enrollmentService;

    public FitManager() {
        this.studentService = new StudentService();
        this.planService = new PlanService();
        this.enrollmentService = new EnrollmentService();
    }

    public OperationResult registerStudent(String name, String cpf, String contact, LocalDate birthDate) {
        return studentService.registerStudent(name, cpf, contact, birthDate);
    }

    public Student findStudentByCpf(String cpf) { return studentService.findByCpf(cpf); }

    public OperationResult removeStudent(String cpf) {
        Student student = studentService.findByCpf(cpf);
        if(student == null){
            return new OperationResult(false, "Não foi possível encontrar o aluno.");
        }
        if(enrollmentService.hasActiveEnrollment(student.getCpf())) {
            return new OperationResult(false, "Não é possível remover/inativar um aluno com matrícula ativa.");
        }
        return studentService.removeStudent(student.getCpf());
    }

    public ArrayList<Student> listStudents() { return studentService.listStudents(); }

    public OperationResult registerPlan(String name, String description, PlanType type, int minDurationMonths, double pricePerMonth) {
        return planService.registerPlan(name, description, type, minDurationMonths, pricePerMonth);
    }

    public Plan findPlanByName(String name) { return PlanService.findByName(name); }

    public OperationResult updatePlanPrice(String name, double newPrice) {
        return planService.updatePrice(name, newPrice);
    }

    public ArrayList<Plan> listPlans() { return planService.listPlans(); }

    public OperationResult enrollStudent(String cpf, String planName, LocalDate startDate, int durationMonths,
                                         double initialAmount, PaymentType paymentType, String paymentDescription) {
        Student student = studentService.findByCpf(cpf);
        if(student == null) {
            return new OperationResult(false, "Aluno não encontrado.");
        }
        Plan plan = PlanService.findByName(planName);
        if(plan == null) {
            return new OperationResult(false, "Plano não encontrado.");
        }
        if(enrollmentService.hasActiveEnrollment(student.getCpf())) {
            return new OperationResult(false, "O aluno já possui matrícula ativa.");
        }
        if(initialAmount <= 0) {
            return new OperationResult(false, "A matrícula exige pagamento inicial maior que zero.");
        }
        if(!student.isActive()){
            return new OperationResult(false, "O aluno inativo não pode ser matriculado.");
        }
        
        return enrollmentService.enroll(student, plan, startDate, durationMonths, initialAmount, paymentType, paymentDescription);
    }

    public OperationResult registerPayment(int code, double amount, PaymentType paymentType, String paymentDescription) {
        return enrollmentService.registerPayment(code, amount, paymentType, paymentDescription);
    }

    public OperationResult cancelEnrollment(int code, String reason) { return enrollmentService.cancel(code, reason); }

    public OperationResult findActiveEnrollment(String cpf) {
        if(studentService.findByCpf(cpf) == null) {
            return new OperationResult(false, "Aluno não encontrado.");
        }

        if(studentService.findByCpf(cpf).isActive() == false){
            return new OperationResult(false, "Aluno inativo não possui matrícula ativa.");
        }

        if(enrollmentService.hasActiveEnrollment(cpf) == false) {
            return new OperationResult(false, "Nenhuma matrícula ativa encontrada para este aluno.");
        }
        return new OperationResult(true, "Matrícula ativa encontrada.", enrollmentService.findActiveByStudent(cpf)); 
    
        }

    public ArrayList<Enrollment> listEnrollments() { return enrollmentService.listEnrollments(); }
}