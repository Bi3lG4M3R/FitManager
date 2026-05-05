package domain.payment;

public enum PaymentType {
    PIX(1, "PIX"),
    CREDIT_CARD(2, "Cartão de Crédito"),
    DEBIT_CARD(3, "Cartão de Débito"),
    CASH(4, "Dinheiro");

    private final int option;
    private final String description;

    PaymentType(int option, String description) {
        this.option = option;
        this.description = description;
    }

    public int getValueOpcao() { return option; }

    public String getDescription() { return description; }

    public static PaymentType selectFromInt(int option) {
        for (PaymentType type : values()) {
            if (type.option == option) return type;
        }
        return null;
    }
}