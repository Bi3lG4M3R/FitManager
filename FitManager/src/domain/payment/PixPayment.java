package domain.payment;

import java.time.LocalDate;

class PixPayment extends Payment {
    private String pixKey;

    public PixPayment(LocalDate date, double amount, String description, String pixKey) {
        super(date, amount, description);
        this.pixKey = pixKey;
    }

    public String getPixKey() { return pixKey; }

    @Override
    public double getProcessingFee() {
        return 0.0; /* PIX não tem taxa de processamento*/
    }

    @Override
    public String getPaymentSummary() {
        return String.format(
            "Tipo: PIX | Data: %s | Valor: R$ %.2f | Chave: %s | Desc: %s",
            getFormattedDate(), getAmount(), pixKey, getDescription()
        );
    }
}