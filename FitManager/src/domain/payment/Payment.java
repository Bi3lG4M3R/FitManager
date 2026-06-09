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

    /*  Identificação polimórfica do tipo para agrupamentos e relatórios. */
    public abstract PaymentType getType();

    /* Cada subclasse gera o resumo com suas informações específicas*/
    public abstract String getPaymentSummary();

    public LocalDate getDate() { return date; }

    public double getAmount() { return amount; }

    public String getDescription() { return description; }

    public String getFormattedDate() { return date.format(DATE_FORMATTER); }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Payment other = (Payment) obj;
        return date != null && date.equals(other.date) && 
               Double.compare(amount, other.amount) == 0;
    }
    
    @Override
    public int hashCode() {
        return (date != null ? date.hashCode() : 0) ^ Double.hashCode(amount);
    }
    
}