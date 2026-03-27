package dominio;

public enum PaymentType {
    PIX(1), CREDIT_CARD(2), DEBIT_CARD(3), CASH(4);
    
    public int option;
    
    PaymentType(int option){
        this.option = option;
    }
    
    public int getValorOpcao(){
        return option;
    }
}
