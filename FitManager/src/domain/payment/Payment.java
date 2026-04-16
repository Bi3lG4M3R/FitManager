package domain.payment;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

public class Payment {
    private LocalDate date;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private double amount;
    private PaymentType type;
    private String description;

    public Payment() {
        this.date = LocalDate.of(2000, Month.JANUARY, 1);
        this.amount = 00.00;
        this.type = PaymentType.valueOf("PIX");
        this.description = "";
    }
    
    public Payment(LocalDate date, double amount, PaymentType type, String description) {
        this.date = date;
        this.amount = amount;
        this.type = type;
        this.description = description;
    }

    public void setDate(LocalDate date) { this.date = date; }

    public void setAmount(double amount) { this.amount = amount; }

    public void setType(PaymentType type) { this.type = type; }

    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }

    public double getAmount() { return amount; }

    public PaymentType getType() { return type; }

    public String getDescription() { return description; }
}