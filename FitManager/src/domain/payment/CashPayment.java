package domain.payment;

import java.time.LocalDate;

class CashPayment extends Payment {
    private double amountReceived;

    /* A validação amountReceived >= amount é feita no EnrollmentService,
    antes de chamar este construtor, seguindo o padrão OperationResult do projeto.*/
    public CashPayment(LocalDate date, double amount, String description,
                       double amountReceived) {
        super(date, amount, description);
        this.amountReceived = amountReceived;
    }

    public double getAmountReceived() { return amountReceived; }

    /* Método exclusivo de CashPayment — não pertence ao contrato de Payment
     pois troco não faz sentido para os outros tipos.*/
    public double getChange() {
        return amountReceived - getAmount();
    }

    @Override
    public double getProcessingFee() {
        return 0.0; /* Dinheiro não tem taxa*/
    }

    @Override
    public String getPaymentSummary() {
        return String.format(
            "Tipo: Dinheiro | Data: %s | Valor: R$ %.2f | Recebido: R$ %.2f | Troco: R$ %.2f | Desc: %s",
            getFormattedDate(), getAmount(), amountReceived, getChange(), getDescription()
        );
    }
}