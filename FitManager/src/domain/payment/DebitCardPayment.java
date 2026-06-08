package domain.payment;

import java.time.LocalDate;

public class DebitCardPayment extends Payment {
    private String cardLastDigits;

    public DebitCardPayment(LocalDate date, double amount, String description,
                            String cardLastDigits) {
        super(date, amount, description);
        this.cardLastDigits = cardLastDigits;
    }

    public String getCardLastDigits() { return cardLastDigits; }

    @Override
    public double getProcessingFee() {
        return 0.0; /* Débito não tem taxa*/
    }

    @Override
    public String getPaymentSummary() {
        return String.format(
            "[DÉBITO] Data: %s | Valor: R$ %.2f | Cartão: **** %s | Taxa: R$ %.2f (sem taxa) | Valor Líquido: R$ %.2f | %s",
            getFormattedDate(), getAmount(), cardLastDigits, getProcessingFee(), getAmount(), getDescription()
        );
    }
}