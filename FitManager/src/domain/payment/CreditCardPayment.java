package domain.payment;

import java.time.LocalDate;

class CreditCardPayment extends Payment {
    private static final double PROCESSING_FEE_RATE = 0.025; // 2,5%

    private int installments;
    private String cardLastDigits;

    public CreditCardPayment(LocalDate date, double amount, String description,
                             int installments, String cardLastDigits) {
        super(date, amount, description);
        this.installments = installments;
        this.cardLastDigits = cardLastDigits;
    }

    public int getInstallments() { return installments; }

    public String getCardLastDigits() { return cardLastDigits; }

    @Override
    public double getProcessingFee() {
        /*A academia absorve o custo: o aluno paga o valor nominal,
         mas apenas (amount - fee) é creditado no saldo.*/
        return getAmount() * PROCESSING_FEE_RATE;
    }

    @Override
    public String getPaymentSummary() {
        String parcelamento = installments > 1
            ? installments + "x de R$ " + String.format("%.2f", getAmount() / installments)
            : "à vista";

        return String.format(
            "Tipo: Cartão de Crédito | Data: %s | Valor: R$ %.2f (%s) | Cartão: **** %s | Taxa processamento: R$ %.2f | Desc: %s",
            getFormattedDate(), getAmount(), parcelamento, cardLastDigits,
            getProcessingFee(), getDescription()
        );
    }
}