package ui.menus;
import application.FitManager;
import application.OperationResult;
import domain.payment.PaymentType;
import ui.UserInterface;


public class PaymentDataMenu {
    private final UserInterface ui;
    private final FitManager fitManager;
    private String pixKey;
    private int installments;
    private String cardLastDigits;
    private double amountReceived;

    // Construtor
    public PaymentDataMenu(UserInterface ui, FitManager fitManager) {
        this.ui = ui;
        this.fitManager = fitManager;
    }

    public OperationResult requestPaymentData(PaymentType paymentType) {
        // Validação inicial: Tipo de pagamento nulo
        if (paymentType == null)
            return new OperationResult(false, "Tipo de pagamento não informado.", null);

        PaymentDataMenu paymentData = new PaymentDataMenu(this.ui, this.fitManager);

        switch(paymentType){
            case PIX:
                ui.showMessage("Pagamento via PIX selecionado.");
                do{
                    paymentData.pixKey = ui.getInput("Insira a chave PIX: ");
                    if (paymentData.pixKey == null || paymentData.pixKey.trim().isEmpty())
                        ui.showError("A chave PIX não pode estar vazia.");
                }while(paymentData.pixKey == null || paymentData.pixKey.trim().isEmpty());
            break;


            case CREDIT_CARD:
                ui.showMessage("Pagamento com cartão de crédito selecionado.");
                do{ // Valida número de parcelas
                    paymentData.installments = ui.getInputInt("Insira o número de parcelas (parcelamento máximo de 12 parcelas | 1 para pagamento à vista): ");
                    if (paymentData.installments < 1 || paymentData.installments > 12)
                        ui.showError("O número de parcelas deve estar entre 1 e 12.");  
                }while(paymentData.installments < 1 || paymentData.installments > 12);
                
                do{ // Valida últimos 4 dígitos do cartão
                    paymentData.cardLastDigits = ui.getInput("Insira os últimos 4 dígitos do cartão: ");
                    if (paymentData.cardLastDigits == null || paymentData.cardLastDigits.trim().isEmpty())
                        ui.showError("Últimos 4 dígitos do cartão não podem estar vazios.");            
                    else{
                        if (!paymentData.cardLastDigits.matches("\\d{4}"))
                        ui.showError("A entrada deve conter apenas números e somente 4 digitos.");
                    } // verifica se a entrada possui exatamente 4 caracteres e se são apenas números.
                
                }while(paymentData.cardLastDigits == null || paymentData.cardLastDigits.trim().isEmpty() ||
                        !paymentData.cardLastDigits.matches("\\d{4}"));
            break;


            case DEBIT_CARD:
                ui.showMessage("Pagamento com cartão de débito selecionado.");
                // Valida últimos 4 dígitos do cartão
                do{
                    paymentData.cardLastDigits = ui.getInput("Insira os últimos 4 dígitos do cartão: ");
                    if (paymentData.cardLastDigits == null || paymentData.cardLastDigits.trim().isEmpty())
                        ui.showError("Últimos 4 dígitos do cartão não podem estar vazios.");            
                    else{
                        if (!paymentData.cardLastDigits.matches("\\d{4}"))
                        ui.showError("A entrada deve conter apenas números e somente 4 digitos.");
                    } // verifica se a entrada possui exatamente 4 caracteres e se são apenas números.
                }while(paymentData.cardLastDigits == null || paymentData.cardLastDigits.trim().isEmpty() ||
                        !paymentData.cardLastDigits.matches("\\d{4}"));
            break;

            
            case CASH:
                ui.showMessage("Pagamento em dinheiro selecionado.");      
                do{
                    paymentData.amountReceived = ui.getInputDouble("Insira o valor recebido: ");
                    if (paymentData.amountReceived <= 0) 
                        ui.showError("Valor recebido deve ser maior que zero.");
                }while(paymentData.amountReceived <= 0);
            break;
   
        }
            
        return new OperationResult(true, "Informações de pagamento coletadas com sucesso.", paymentData);
    }

    // ==================== GETTERS ====================

    public String getPixKey() {
        return pixKey;
    }

    public int getInstallments() {
        return installments;
    }

    public String getCardLastDigits() {
        return cardLastDigits;
    }

    public double getAmountReceived() {
        return amountReceived;
    }
}
