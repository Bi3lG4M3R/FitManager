package domain.payment;

public enum PaymentType {
    PIX(1, "O método escolhido foi o Pix"),
    CREDIT_CARD(2, "O método escolhido foi o Cartão de Crédito"),
    DEBIT_CARD(3, "O método escolhido foi o Cartão de Débito"),
    CASH(4, "O método escolhido foi o Dinheiro");

    private final int option;
    private final String description;
        
    PaymentType(int option, String description){
        this.option = option;
        this.description = description;
    }
    
    public int getValueOpcao(){ return option; }

    public String getDescription() { return description; }

    public static PaymentType selectFromInt(int option) {
        for (PaymentType paymentOption : PaymentType.values()) {
            if (paymentOption.getValueOpcao() == option){
                return paymentOption;
            }
        }
        return null; // caso a opcao nao exista devolve null
    }
}
