package ui.menus;
import application.FitManager;
import application.OperationResult;
import domain.payment.PaymentType;
import ui.UserInterface;


public class PaymentMenu {
    private final UserInterface ui;
    private final FitManager fitManager;
    String pixKey;
    int installments;
    String cardLastDigits;
    double amountReceived;


    // Construtor
    public PaymentMenu(UserInterface ui, FitManager fitManager) {
        this.ui = ui;
        this.fitManager = fitManager;
        this.pixKey = null;
        this.installments = 0;
        this.cardLastDigits = null;
        this.amountReceived = 0.0;
    }

    public OperationResult requestPaymentData(PaymentType paymentType) {
        // VALIDAÇÃO INICIAL: Tipo de pagamento nulo
        if (paymentType == null) {
            return new OperationResult(false, "Tipo de pagamento não pode ser nulo.", null);
        }

        PaymentMenu paymentMenuData = new PaymentMenu(this.ui, this.fitManager);

        switch(paymentType){
            case PIX:
                //PaymentMenu pixPaymentMenu = new PaymentMenu(this.ui, this.fitManager);

                ui.showMessage("Pagamento via PIX selecionado.");
                
                paymentMenuData.pixKey = ui.getInput("Insira a chave PIX: ");
                
                //return new OperationResult(true, "Dados PIX coletados com sucesso.", pixPaymentMenu);
            break;


            case CREDIT_CARD:
                //PaymentMenu creditCardPaymentMenu = new PaymentMenu(this.ui, this.fitManager);

                ui.showMessage("Pagamento com cartão de crédito selecionado.");
                
                do{
                    paymentMenuData.installments = ui.getInputInt("Insira o número de parcelas (parcelamento máximo de 12 parcelas | 1 para pagamento à vista): ");

                    if (paymentMenuData.installments < 1 || paymentMenuData.installments > 12)
                        ui.showError("O número de parcelas deve estar entre 1 e 12.");  
                }while(paymentMenuData.installments < 1 || paymentMenuData.installments > 12);
                
                do{
                        paymentMenuData.cardLastDigits = ui.getInput("Insira os últimos 4 dígitos do cartão: ");
                
                    if (paymentMenuData.cardLastDigits == null || paymentMenuData.cardLastDigits.trim().isEmpty())
                        ui.showError("Últimos 4 dígitos do cartão não podem estar vazios.");            
                    else{
                        if (!paymentMenuData.cardLastDigits.matches("\\d{4}"))
                        ui.showError("A entrada deve conter apenas números e somente 4 digitos.");
                    } // verifica se a entrada possui exatamente 4 caracteres e se são apenas números.
                
                }while(paymentMenuData.cardLastDigits == null || paymentMenuData.cardLastDigits.trim().isEmpty() ||
                        !paymentMenuData.cardLastDigits.matches("\\d{4}"));
    
                //return new OperationResult(true, "Dados do cartão de crédito coletados com sucesso.", creditCardPaymentMenu);
            break;


            case DEBIT_CARD:
                ui.showMessage("Pagamento com cartão de débito selecionado.");

                //PaymentMenu debitCardPaymentMenu = new PaymentMenu(this.ui, this.fitManager);
                
                // VERFICA DIGITOS DO CARTÃO
                do{
                        paymentMenuData.cardLastDigits = ui.getInput("Insira os últimos 4 dígitos do cartão: ");
                
                    if (paymentMenuData.cardLastDigits == null || paymentMenuData.cardLastDigits.trim().isEmpty())
                        ui.showError("Últimos 4 dígitos do cartão não podem estar vazios.");            
                    else{
                        if (!paymentMenuData.cardLastDigits.matches("\\d{4}"))
                        ui.showError("A entrada deve conter apenas números e somente 4 digitos.");
                    } // verifica se a entrada possui exatamente 4 caracteres e se são apenas números.
                
                }while(paymentMenuData.cardLastDigits == null || paymentMenuData.cardLastDigits.trim().isEmpty() ||
                        !paymentMenuData.cardLastDigits.matches("\\d{4}"));

                //return new OperationResult(true, "Dados do cartão de débito coletados com sucesso.", debitCardPaymentMenu);
            break;

            
            case CASH:
                ui.showMessage("Pagamento em dinheiro selecionado.");
                //PaymentMenu cashPaymentMenu = new PaymentMenu(this.ui, this.fitManager);
                
                do{
                    paymentMenuData.amountReceived = ui.getInputDouble("Insira o valor recebido: ");
                
                    if (paymentMenuData.amountReceived <= 0) 
                        return new OperationResult(false, "Valor recebido deve ser maior que zero.", null);
                
                }while(paymentMenuData.amountReceived <= 0);
                //return new OperationResult(true, "Dados de dinheiro coletados com sucesso.", cashPaymentMenu);
            break;
   
        }
            
        return new OperationResult(true, "Dados de pagamento coletados com sucesso.", paymentMenuData);
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
