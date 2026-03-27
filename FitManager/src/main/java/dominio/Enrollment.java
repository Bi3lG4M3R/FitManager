package domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Enrollment {

    private int code;
    private Student student;
    private Plan plan;
    private LocalDate startDate;
    private LocalDate endDate;
    private int durationMonths;
    // totalPrice armazenado no momento da criação — alterações no preço do plano não afetam contratos já existentes
    private double totalPrice;
    private EnrollmentStatus status;
    private ArrayList<Payment> payments;
    private LocalDate cancellationDate;
    private String cancellationReason;
    

    public Enrollment(int code, Student student, Plan plan, LocalDate startDate, int durationMonths) {
        this.code = code;
        this.student = student;
        this.plan = plan;
        this.startDate = startDate;
        this.durationMonths = durationMonths;
        // endDate calculada uma única vez na construção usando plusMonths()
        this.endDate = startDate.plusMonths(durationMonths);
        // totalPrice fixado na criação com base no preço atual do plano
        this.totalPrice = plan.calculateTotalPrice(durationMonths);
        this.status = EnrollmentStatus.ACTIVE;
        this.payments = new ArrayList<>();
    }

    public void registerPayment(Payment payment) {
        payments.add(payment);
    }
    
    //Soma todos pagamentos
    public double calculateTotalPaid() {
        double total = 0;
        for (Payment payment : payments) {
            total += payment.getAmount();
        }
        return total;
    }

    /**Resultado positivo = saldo pendente; resultado negativo = crédito 
     * Calculado dinamicamente a partir dos pagamentos — nunca armazenado como atributo separado
     * para evitar inconsistências entre o saldo e a lista de pagamentos*/
    public double calculateBalance() {
        return totalPrice - calculateTotalPaid();
    }

    /**CANCEL. A transição é unidirecional: CANCELLED não pode voltar para ACTIVE.
     * Registra opcionalmente um motivo para fins de histórico*/
    public void cancel(String reason) {
        this.status = EnrollmentStatus.CANCELLED;
        this.cancellationDate = LocalDate.now();
        //operador ternário.
        this.cancellationReason = (reason != null && !reason.isBlank()) ? reason : "Não informado";
    }

    public long getMonthsUsed() {
        // Se já foi cancelada, conta até a data de cancelamento; caso contrário, até hoje
        LocalDate end = (status == EnrollmentStatus.CANCELLED && cancellationDate != null)? cancellationDate : LocalDate.now();
        //Retorna o tempo de permanencia da matricula
        return ChronoUnit.MONTHS.between(startDate, end);
    }

    /* Getters*/
    public int getCode(){return code; }
    public Student getStudent() { return student; }
    public Plan getPlan() { return plan; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public int getDurationMonths() { return durationMonths; }
    public double getTotalPrice() { return totalPrice; }
    public EnrollmentStatus getStatus() { return status; }
    public List<Payment> getPayments() { return Collections.unmodifiableList(payments); }
    public LocalDate getCancellationDate() { return cancellationDate; }
    public String getCancellationReason() { return cancellationReason; }

}