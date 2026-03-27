package dominio;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

public class Payment {
    LocalDate date;
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    double amount;
    PaymentType type;
    String description;

    public Payment() {
        this.date = LocalDate.of(2000, Month.JANUARY, 1);
        this.amount = 00.00;
        this.type = PaymentType(1);
        this.description = description;
    }
    
    public Payment(LocalDate date, double amount, PaymentType type, String description) {
        this.date = date;
        this.amount = amount;
        this.type = type;
        this.description = description;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setType(PaymentType type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public PaymentType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    private PaymentType PaymentType(int i) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    
}
