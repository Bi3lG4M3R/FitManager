package domain.payment;

import java.time.LocalDate;

public class PixPayment extends Payment {
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
            "[PIX] Data: %s | Valor: R$ %.2f | Chave: %s | Taxa: R$ %.2f (sem taxa) | Valor Líquido: R$ %.2f | %s",
            getFormattedDate(), getAmount(), pixKey, getProcessingFee(), getAmount(), getDescription()
        );
    }
}