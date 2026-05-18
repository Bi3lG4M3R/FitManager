package application;

import java.time.LocalDate;
import java.util.ArrayList;

import domain.Enrollment;
import domain.Student;
import domain.payment.*;
import domain.plan.*;

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
        if (student == null) {
            return new OperationResult(false, "Não foi possível encontrar o aluno.");
        }
        if (enrollmentService.hasActiveEnrollment(student.getCpf())) {
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

    /* ------------------------------------------------------------------ */
    /* enrollStudent — sobrecargas criando as instâncias corretas        */
    /* ------------------------------------------------------------------ */

    /* PIX */
    public OperationResult enrollStudent(String cpf, String planName, LocalDate startDate,
            int durationMonths, String paymentDescription, double initialAmount, String pixKey) {
        if (!validate(cpf, planName, initialAmount)) return validationError(cpf, planName, initialAmount);
        
        Payment payment = new PixPayment(startDate, initialAmount, paymentDescription, pixKey);
        return enrollmentService.enroll(studentService.findByCpf(cpf), PlanService.findByName(planName), startDate, durationMonths, payment);
    }

    /* Dinheiro */
    public OperationResult enrollStudent(String cpf, String planName, LocalDate startDate,
            int durationMonths, double initialAmount, String paymentDescription, double amountReceived) {
        if (!validate(cpf, planName, initialAmount)) return validationError(cpf, planName, initialAmount);
        
        Payment payment = new CashPayment(startDate, initialAmount, paymentDescription, amountReceived);
        return enrollmentService.enroll(studentService.findByCpf(cpf), PlanService.findByName(planName), startDate, durationMonths, payment);
    }

    /* Débito */
    public OperationResult enrollStudent(String cpf, String planName, LocalDate startDate,
            int durationMonths, double initialAmount, String paymentDescription, String cardLastDigits) {
        if (!validate(cpf, planName, initialAmount)) return validationError(cpf, planName, initialAmount);
        
        Payment payment = new DebitCardPayment(startDate, initialAmount, paymentDescription, cardLastDigits);
        return enrollmentService.enroll(studentService.findByCpf(cpf), PlanService.findByName(planName), startDate, durationMonths, payment);
    }

    /* Crédito */
    public OperationResult enrollStudent(String cpf, String planName, LocalDate startDate,
            int durationMonths, double initialAmount, String paymentDescription, int installments, String cardLastDigits) {
        if (!validate(cpf, planName, initialAmount)) return validationError(cpf, planName, initialAmount);
        
        Payment payment = new CreditCardPayment(startDate, initialAmount, paymentDescription, installments, cardLastDigits);
        return enrollmentService.enroll(studentService.findByCpf(cpf), PlanService.findByName(planName), startDate, durationMonths, payment);
    }

    /* ------------------------------------------------------------------ */
    /* registerPayment — sobrecargas criando as instâncias corretas      */
    /* ------------------------------------------------------------------ */

    public OperationResult registerPaymentPix(int code, double amount, String description, String pixKey) {
        return enrollmentService.registerPayment(code, new PixPayment(LocalDate.now(), amount, description, pixKey));
    }

    public OperationResult registerPaymentCash(int code, double amount, String description, double amountReceived) {
        return enrollmentService.registerPayment(code, new CashPayment(LocalDate.now(), amount, description, amountReceived));
    }

    public OperationResult registerPaymentDebit(int code, double amount, String description, String cardLastDigits) {
        return enrollmentService.registerPayment(code, new DebitCardPayment(LocalDate.now(), amount, description, cardLastDigits));
    }

    public OperationResult registerPaymentCredit(int code, double amount, String description, int installments, String cardLastDigits) {
        return enrollmentService.registerPayment(code, new CreditCardPayment(LocalDate.now(), amount, description, installments, cardLastDigits));
    }

    private boolean validate(String cpf, String planName, double initialAmount) {
        Student student = studentService.findByCpf(cpf);
        if (student == null || !student.isActive()) return false;
        if (PlanService.findByName(planName) == null) return false;
        if (enrollmentService.hasActiveEnrollment(cpf)) return false;
        if (initialAmount <= 0) return false;
        return true;
    }

    private OperationResult validationError(String cpf, String planName, double initialAmount) {
        Student student = studentService.findByCpf(cpf);
        if (student == null) return new OperationResult(false, "Aluno não encontrado.");
        if (!student.isActive()) return new OperationResult(false, "O aluno inativo não pode ser matriculado.");
        if (PlanService.findByName(planName) == null) return new OperationResult(false, "Plano não encontrado.");
        if (enrollmentService.hasActiveEnrollment(cpf)) return new OperationResult(false, "O aluno já possui matrícula ativa.");
        if (initialAmount <= 0) return new OperationResult(false, "A matrícula exige pagamento inicial maior que zero.");
        return new OperationResult(false, "Erro de validação.");
    }

    public OperationResult findEnrollmentByCode(int code) {
        if (enrollmentService.findByCode(code) == null) {
            return new OperationResult(false, "Matrícula não encontrada.");
        }
        return new OperationResult(true, "Matricula encontrada.", enrollmentService.findByCode(code));
    }

    public OperationResult cancelEnrollment(int code, String reason) { return enrollmentService.cancel(code, reason); }

    public OperationResult calculateCancelationFee(int code) { return enrollmentService.calculateCancelationFee(code); }

    public OperationResult findActiveEnrollment(String cpf) {
        if (studentService.findByCpf(cpf) == null) {
            return new OperationResult(false, "Aluno não encontrado.");
        }
        if (!studentService.findByCpf(cpf).isActive()) {
            return new OperationResult(false, "Aluno inativo não possui matrícula ativa.");
        }
        if (!enrollmentService.hasActiveEnrollment(cpf)) {
            return new OperationResult(false, "Nenhuma matrícula ativa encontrada para este aluno.");
        }
        return new OperationResult(true, "Matrícula ativa encontrada.", enrollmentService.findActiveByStudent(cpf));
    }

    public ArrayList<Enrollment> listEnrollments() { return enrollmentService.listEnrollments(); }
}