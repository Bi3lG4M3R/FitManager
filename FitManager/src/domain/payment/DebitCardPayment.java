package domain.payment;

import java.time.LocalDate;

class DebitCardPayment extends Payment {
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
            "Tipo: Cartão de Débito | Data: %s | Valor: R$ %.2f | Cartão: **** %s | Desc: %s",
            getFormattedDate(), getAmount(), cardLastDigits, getDescription()
        );
    }
}