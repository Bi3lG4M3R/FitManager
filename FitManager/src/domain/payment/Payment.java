package domain.payment;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class Payment {
    private LocalDate date;
    private double amount;
    private String description;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Payment(LocalDate date, double amount, String description) {
        this.date = date;
        this.amount = amount;
        this.description = description;
    }

    /* Cada subclasse define sua taxa de processamento (0.0 se isenta)*/
    public abstract double getProcessingFee();

    /* Cada subclasse gera o resumo com suas informações específicas*/
    public abstract String getPaymentSummary();

    public LocalDate getDate() { return date; }

    public double getAmount() { return amount; }

    public String getDescription() { return description; }

    public String getFormattedDate() { return date.format(DATE_FORMATTER); }
}