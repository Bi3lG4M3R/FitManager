package application;

import java.time.LocalDate;
import java.util.ArrayList;
import exceptions.PersistenceException;
import domain.Enrollment;
import domain.Student;
import domain.payment.*;
import domain.plan.*;

public class FitManager {
    private final StudentService    studentService;
    private final PlanService       planService;
    private final EnrollmentService enrollmentService;

    public FitManager() {
        this.studentService    = new StudentService();
        this.planService       = new PlanService();
        this.enrollmentService = new EnrollmentService();

        new java.io.File("data").mkdirs(); // garante que o diretório data/ existe
    }

    // ------------------------------------------------------------------ //
    // Persistência                                                          //
    // ------------------------------------------------------------------ //

    private static final String FILE_STUDENTS    = "data/students.csv";
    private static final String FILE_PLANS       = "data/plans.csv";
    private static final String FILE_ENROLLMENTS = "data/enrollments.csv";

    /** Carrega na ordem correta: students → plans → enrollments */
    public void loadAll() throws PersistenceException {
        studentService.load(FILE_STUDENTS);
        planService.load(FILE_PLANS);
        enrollmentService.load(FILE_ENROLLMENTS, studentService, planService);
    }

    /** Grava na ordem correta: enrollments → plans → students */
    public void saveAll() throws PersistenceException {
        enrollmentService.save(FILE_ENROLLMENTS);
        planService.save(FILE_PLANS);
        studentService.save(FILE_STUDENTS);
    }

    /** Tenta salvar silenciosamente após operações relevantes. */
    private boolean trySaveAll() {
        try {
            saveAll();
            return true;
        } catch (PersistenceException e) {
            return false;
        }
    }

    // ------------------------------------------------------------------ //
    // Alunos — idêntico ao original, + trySaveAll() nas mutações          //
    // ------------------------------------------------------------------ //

    public OperationResult<Student> registerStudent(String name, String cpf, String contact, LocalDate birthDate) {
        OperationResult<Student> result = studentService.registerStudent(name, cpf, contact, birthDate);
        if (result.isSuccess()) trySaveAll();
        return result;
    }

    public Student findStudentByCpf(String cpf) { return studentService.findByCpf(cpf); }

    public OperationResult<Student> removeStudent(String cpf) {
        Student student = studentService.findByCpf(cpf);
        if (student == null)
            return new OperationResult<>(false, "Não foi possível encontrar o aluno.");
        if (enrollmentService.hasActiveEnrollment(student.getCpf()))
            return new OperationResult<>(false, "Não é possível desativar um aluno com matrícula ativa.");

        OperationResult<Student> result = studentService.removeStudent(student.getCpf());
        if (result.isSuccess()) trySaveAll();
        return result;
    }

    public OperationResult<Student> activateStudent(String cpf) {
        Student student = studentService.findByCpf(cpf);
        if (student == null)
            return new OperationResult<>(false, "Não foi possível encontrar o aluno.");
        if (student.isActive())
            return new OperationResult<>(false, "O aluno já está ativo no sistema.");

        OperationResult<Student> result = studentService.reactivateStudent(student.getCpf());
        if (result.isSuccess()) trySaveAll();
        return result;
    }

    public ArrayList<Student> listStudents() { return studentService.listStudents(); }

    // ------------------------------------------------------------------ //
    // Planos — idêntico ao original, + trySaveAll() nas mutações          //
    // ------------------------------------------------------------------ //

    public OperationResult<Plan> registerPlan(String name, String description, PlanType type, int minDurationMonths, double pricePerMonth) {
        OperationResult<Plan> result = planService.registerPlan(name, description, type, minDurationMonths, pricePerMonth);
        if (result.isSuccess()) trySaveAll();
        return result;
    }

    /** findByName agora retorna OperationResult<Plan> conforme diagrama */
    public OperationResult<Plan> findPlanByName(String name) { return planService.findByName(name); }

    public OperationResult<Plan> updatePlanPrice(String name, double newPrice) {
        OperationResult<Plan> result = planService.updatePrice(name, newPrice);
        if (result.isSuccess()) trySaveAll();
        return result;
    }

    public ArrayList<Plan> listPlans() { return planService.listPlans(); }

    // ------------------------------------------------------------------ //
    // Matrículas — idêntico ao original, + trySaveAll() nas mutações      //
    // ------------------------------------------------------------------ //

    /* PIX */
    public OperationResult<Enrollment> enrollStudent(String cpf, String planName, LocalDate startDate,
            int durationMonths, String paymentDescription, double initialAmount, String pixKey) {
        if (!validate(cpf, planName, initialAmount)) return validationError(cpf, planName, initialAmount);
        Payment payment = new PixPayment(startDate, initialAmount, paymentDescription, pixKey);
        OperationResult<Enrollment> result = enrollmentService.enroll(
            studentService.findByCpf(cpf), planService.findByName(planName).getData(),
            startDate, durationMonths, payment);
        if (result.isSuccess()) trySaveAll();
        return result;
    }

    /* Dinheiro */
    public OperationResult<Enrollment> enrollStudent(String cpf, String planName, LocalDate startDate,
            int durationMonths, double initialAmount, String paymentDescription, double amountReceived) {
        if (!validate(cpf, planName, initialAmount)) return validationError(cpf, planName, initialAmount);
        Payment payment = new CashPayment(startDate, initialAmount, paymentDescription, amountReceived);
        OperationResult<Enrollment> result = enrollmentService.enroll(
            studentService.findByCpf(cpf), planService.findByName(planName).getData(),
            startDate, durationMonths, payment);
        if (result.isSuccess()) trySaveAll();
        return result;
    }

    /* Débito */
    public OperationResult<Enrollment> enrollStudent(String cpf, String planName, LocalDate startDate,
            int durationMonths, double initialAmount, String paymentDescription, String cardLastDigits) {
        if (!validate(cpf, planName, initialAmount)) return validationError(cpf, planName, initialAmount);
        Payment payment = new DebitCardPayment(startDate, initialAmount, paymentDescription, cardLastDigits);
        OperationResult<Enrollment> result = enrollmentService.enroll(
            studentService.findByCpf(cpf), planService.findByName(planName).getData(),
            startDate, durationMonths, payment);
        if (result.isSuccess()) trySaveAll();
        return result;
    }

    /* Crédito */
    public OperationResult<Enrollment> enrollStudent(String cpf, String planName, LocalDate startDate,
            int durationMonths, double initialAmount, String paymentDescription, int installments, String cardLastDigits) {
        if (!validate(cpf, planName, initialAmount)) return validationError(cpf, planName, initialAmount);
        Payment payment = new CreditCardPayment(startDate, initialAmount, paymentDescription, installments, cardLastDigits);
        OperationResult<Enrollment> result = enrollmentService.enroll(
            studentService.findByCpf(cpf), planService.findByName(planName).getData(),
            startDate, durationMonths, payment);
        if (result.isSuccess()) trySaveAll();
        return result;
    }

    // ------------------------------------------------------------------ //
    // Pagamentos adicionais — idêntico ao original, + trySaveAll()        //
    // ------------------------------------------------------------------ //

    public OperationResult<Payment> registerPaymentPix(int code, double amount, String description, String pixKey) {
        OperationResult<Payment> result = enrollmentService.registerPayment(code, new PixPayment(LocalDate.now(), amount, description, pixKey));
        if (result.isSuccess()) trySaveAll();
        return result;
    }

    public OperationResult<Payment> registerPaymentCash(int code, double amount, String description, double amountReceived) {
        OperationResult<Payment> result = enrollmentService.registerPayment(code, new CashPayment(LocalDate.now(), amount, description, amountReceived));
        if (result.isSuccess()) trySaveAll();
        return result;
    }

    public OperationResult<Payment> registerPaymentDebit(int code, double amount, String description, String cardLastDigits) {
        OperationResult<Payment> result = enrollmentService.registerPayment(code, new DebitCardPayment(LocalDate.now(), amount, description, cardLastDigits));
        if (result.isSuccess()) trySaveAll();
        return result;
    }

    public OperationResult<Payment> registerPaymentCredit(int code, double amount, String description, int installments, String cardLastDigits) {
        OperationResult<Payment> result = enrollmentService.registerPayment(code, new CreditCardPayment(LocalDate.now(), amount, description, installments, cardLastDigits));
        if (result.isSuccess()) trySaveAll();
        return result;
    }

    // ------------------------------------------------------------------ //
    // Consultas e cancelamento — idêntico ao original                     //
    // ------------------------------------------------------------------ //

    public OperationResult<Enrollment> findEnrollmentByCode(int code) {
        if (enrollmentService.findByCode(code) == null)
            return new OperationResult<>(false, "Matrícula não encontrada.");
        return new OperationResult<>(true, "Matricula encontrada.", enrollmentService.findByCode(code));
    }

    public OperationResult<Enrollment> cancelEnrollment(int code, String reason) {
        OperationResult<Enrollment> result = enrollmentService.cancel(code, reason);
        if (result.isSuccess()) trySaveAll();
        return result;
    }

    public OperationResult<Double> calculateCancelationFee(int code) {
        return enrollmentService.calculateCancelationFee(code);
    }

    public OperationResult<Enrollment> findActiveEnrollment(String cpf) {
        if (studentService.findByCpf(cpf) == null)
            return new OperationResult<>(false, "Aluno não encontrado.");
        if (!studentService.findByCpf(cpf).isActive())
            return new OperationResult<>(false, "Aluno inativo não possui matrícula ativa.");
        if (!enrollmentService.hasActiveEnrollment(cpf))
            return new OperationResult<>(false, "Nenhuma matrícula ativa encontrada para este aluno.");
        return new OperationResult<>(true, "Matrícula ativa encontrada.", enrollmentService.findActiveByStudent(cpf));
    }

    public ArrayList<Enrollment> listEnrollments() { return enrollmentService.listEnrollments(); }

    // ------------------------------------------------------------------ //
    // Validação interna — idêntico ao original                            //
    // ------------------------------------------------------------------ //

    private boolean validate(String cpf, String planName, double initialAmount) {
        Student student = studentService.findByCpf(cpf);
        if (student == null || !student.isActive()) return false;
        if (!planService.findByName(planName).isSuccess()) return false;
        if (enrollmentService.hasActiveEnrollment(cpf)) return false;
        if (initialAmount <= 0) return false;
        return true;
    }

    private OperationResult<Enrollment> validationError(String cpf, String planName, double initialAmount) {
        Student student = studentService.findByCpf(cpf);
        if (student == null) return new OperationResult<>(false, "Aluno não encontrado.");
        if (!student.isActive()) return new OperationResult<>(false, "O aluno inativo não pode ser matriculado.");
        if (!planService.findByName(planName).isSuccess()) return new OperationResult<>(false, "Plano não encontrado.");
        if (enrollmentService.hasActiveEnrollment(cpf)) return new OperationResult<>(false, "O aluno já possui matrícula ativa.");
        if (initialAmount <= 0) return new OperationResult<>(false, "A matrícula exige pagamento inicial maior que zero.");
        return new OperationResult<>(false, "Erro de validação.");
    }
}