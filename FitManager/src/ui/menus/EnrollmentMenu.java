package ui.menus;

import java.time.LocalDate;
import java.util.ArrayList;

import application.FitManager;
import application.OperationResult;
import domain.Enrollment;
import domain.payment.PaymentType;
import ui.UserInterface;
import ui.enums.EnrollmentMenuEnum;

public class EnrollmentMenu {
    private final UserInterface ui;
    private final FitManager fitManager;

    // Construtor
    public EnrollmentMenu(UserInterface ui, FitManager fitManager){
        this.ui = ui;
        this.fitManager = fitManager;
    }

    public void run(){
        EnrollmentMenuEnum optionSelected = null;

        //  String array para armazenar as opções dos menus
        String[] menuOptions = new String[EnrollmentMenuEnum.values().length];

        //  Construção do array pegando as descrições do do enum
        for(int i = 0; i < EnrollmentMenuEnum.values().length; i++){
            menuOptions[i] = EnrollmentMenuEnum.values()[i].getOptionNumber() + " - " + EnrollmentMenuEnum.values()[i].getOptionDescription();
        }



        do { 

            do{
                int option = ui.showMenu("GERENCIAR MATRÍCULAS", menuOptions, "Selecione uma opção: ");
                if(option == 0){
                    optionSelected = EnrollmentMenuEnum.BACK;
                } else {
                    optionSelected = EnrollmentMenuEnum.selectFromInt(option);
                    if(optionSelected == null)
                        ui.showError("Opção inexistente. Selecione uma das opções acima.");  
                }
            }while(optionSelected == null);
            
            ArrayList<Enrollment> enrollmentHistory = fitManager.listEnrollments();

            switch(optionSelected){

                case REGISTER_ENROLLMENT:
                    String studentCpf = ui.getInput("Digite o CPF do aluno: ");
                    if(studentCpf == null) break;
                    String planName = ui.getInput("Digite o nome do plano: ");
                    if(planName == null) break;
                    LocalDate startDate = ui.getInputDate("Digite a data de início da matrícula (dd/mm/aaaa): ");
                    if(startDate == null) break;
                    int durationMonths = ui.getInputInt("Digite a duração da matrícula (numero de meses): ");
                    if(durationMonths < 0) break;
                    double initialPayment = ui.getInputDouble("Digite o valor do pagamento inicial: ");
                    if(initialPayment < 0) break;
                    PaymentType paymentType = ui.getInputPaymentType("Selecione a forma de pagamento: ");
                    if (paymentType == null) {
                        ui.showMessage("Operação cancelada.");
                        break;
                    }
                    
                    OperationResult resultRegisterEnrollment = collectAndRegisterInitialPayment(studentCpf, planName, startDate, durationMonths, initialPayment, paymentType);
                    
                    if(resultRegisterEnrollment != null && resultRegisterEnrollment.isSuccess())
                        ui.showMessage(resultRegisterEnrollment.getMessage());
                    else
                        ui.showError("Erro ao registrar matrícula: " + (resultRegisterEnrollment != null ? resultRegisterEnrollment.getMessage() : "Erro interno."));
                break;

                case REGISTER_PAYMENT:
                    int enrollmentCode = ui.getInputInt(formatEnrollmentList(enrollmentHistory) + "\nDigite o número de matrícula a realizar pagamento: ");
                    if(enrollmentCode < 0) break;
                    double amount = ui.getInputDouble("Valor do pagamento: ");
                    if(amount < 0) break;
                    PaymentType registerPaymentType = ui.getInputPaymentType("Selecione a forma de pagamento: ");
                    if (registerPaymentType == null) {
                        ui.showMessage("Operação cancelada.");
                        break;
                    }
                    
                    OperationResult resultPayment = collectAndRegisterPayment(enrollmentCode, amount, registerPaymentType, registerPaymentType.getDescription());
                    
                    if(resultPayment != null && resultPayment.isSuccess())
                        ui.showMessage(resultPayment.getMessage());
                    else
                        ui.showError("Erro ao registrar pagamento: " + (resultPayment != null ? resultPayment.getMessage() : "Erro interno."));
                break;

                case CANCEL_ENROLLMENT:
                    int enrollmentCodeToCancel = ui.getInputInt(formatEnrollmentList(enrollmentHistory) + "\nDigite o número de matrícula a ser cancelada: ");
                    if(enrollmentCodeToCancel < 0) break;
                    String cancelReason = ui.getInput("Digite o motivo do cancelamento: ");
                    if(cancelReason == null) break;
                    
                    processCancellation(enrollmentCodeToCancel, cancelReason, enrollmentHistory);
                break;

                case CHECK_ACTIVE_ENROLLMENT:
                    String studentCpfToCheck = ui.getInput("Digite o CPF do aluno para consultar a matrícula: ");
                    if(studentCpfToCheck == null) break;
                    OperationResult resultCheckEnrollment = fitManager.findActiveEnrollment(studentCpfToCheck);
                    if(resultCheckEnrollment.isSuccess()){
                        ui.showMessage(resultCheckEnrollment.getMessage());
                        ui.showEnrollment((Enrollment) resultCheckEnrollment.getData());
                        // adicionar mostrar saldo pendente.
                    } else {
                        ui.showError("Erro ao consultar matrícula: " + resultCheckEnrollment.getMessage());
                    }
                break;

                case VIEW_HISTORY:
                    ui.showMessage(formatEnrollmentList(enrollmentHistory));
                break;


                case BACK:
                    ui.showMessage("Voltando ao menu principal...");
                break;

            }

        }while(optionSelected != EnrollmentMenuEnum.BACK);

    }
    
    /* Método auxiliar para coletar dados de pagamento baseado no tipo.
       Retorna null se cancelado, senão chama a função fitManager apropriada. */
    private OperationResult collectAndRegisterPayment(int enrollmentCode, double amount, PaymentType paymentType, String description) {
        switch (paymentType) {
            case PIX:
                String pixKey = ui.getInput("Digite a chave PIX de origem: ");
                if(pixKey == null) return null;
                return fitManager.registerPaymentPix(enrollmentCode, amount, description, pixKey);
            case CASH:
                double amountReceived = ui.getInputDouble("Valor em dinheiro entregue pelo aluno: ");
                if(amountReceived < 0) return null;
                return fitManager.registerPaymentCash(enrollmentCode, amount, description, amountReceived);
            case DEBIT_CARD:
                String debitLastDigits = ui.getInput("Últimos 4 dígitos do cartão de débito: ");
                if(debitLastDigits == null) return null;
                return fitManager.registerPaymentDebit(enrollmentCode, amount, description, debitLastDigits);
            case CREDIT_CARD:
                int installments = ui.getInputInt("Quantidade de parcelas: ");
                if(installments < 0) return null;
                String creditLastDigits = ui.getInput("Últimos 4 dígitos do cartão de crédito: ");
                if(creditLastDigits == null) return null;
                return fitManager.registerPaymentCredit(enrollmentCode, amount, description, installments, creditLastDigits);
        }
        return null;
    }
    
    /* Método auxiliar para processar cancelamento da matrícula com ou sem taxa. */
    private void processCancellation(int enrollmentCode, String cancelReason, ArrayList<Enrollment> enrollmentHistory) {
        OperationResult findEnrollmentResult = fitManager.findEnrollmentByCode(enrollmentCode);
        
        if(!findEnrollmentResult.isSuccess()) {
            ui.showError("Erro ao encontrar matrícula: " + findEnrollmentResult.getMessage());
            return;
        }
        
        double cancelationFee = (double) fitManager.calculateCancelationFee(enrollmentCode).getData();
        
        if(cancelationFee > 0.0) {
            ui.showMessage("Taxa de cancelamento: " + String.format("%.2f", cancelationFee));
            PaymentType feePaymentType = ui.getInputPaymentType("Selecione a forma de pagamento da taxa: ");
            
            if (feePaymentType == null) {
                ui.showMessage("Operação cancelada.");
                return;
            }
            
            OperationResult resultFeePayment = collectAndRegisterPayment(enrollmentCode, cancelationFee, feePaymentType, "Taxa de cancelamento");
            
            if(resultFeePayment == null) {
                ui.showMessage("Operação cancelada.");
                return;
            }
            
            if(resultFeePayment.isSuccess()) {
                ui.showMessage(resultFeePayment.getMessage());
            } else {
                ui.showError("Erro ao registrar pagamento da taxa: " + resultFeePayment.getMessage());
                return;
            }
        }
        
        // Realiza o cancelamento após taxa (se houver) ser paga
        OperationResult resultCancelEnrollment = fitManager.cancelEnrollment(enrollmentCode, cancelReason);
        if(resultCancelEnrollment.isSuccess()) {
            ui.showMessage(resultCancelEnrollment.getMessage());
        } else {
            ui.showError("Erro ao cancelar matrícula: " + resultCancelEnrollment.getMessage());
        }
    }
    
    /* Método auxiliar para coletar dados e registrar pagamento inicial ou adicional. */
    private OperationResult collectAndRegisterInitialPayment(String cpf, String planName, LocalDate startDate, 
                                                             int durationMonths, double initialPayment, PaymentType paymentType) {
        switch (paymentType) {
            case PIX:
                String pixKey = ui.getInput("Digite a chave PIX de origem: ");
                if(pixKey == null) return null;
                return fitManager.enrollStudent(cpf, planName, startDate, durationMonths, paymentType.getDescription(), initialPayment, pixKey);
            case CASH:
                double amountReceived = ui.getInputDouble("Valor em dinheiro entregue pelo aluno: ");
                if(amountReceived < 0) return null;
                return fitManager.enrollStudent(cpf, planName, startDate, durationMonths, initialPayment, paymentType.getDescription(), amountReceived);
            case DEBIT_CARD:
                String debitLastDigits = ui.getInput("Últimos 4 dígitos do cartão de débito: ");
                if(debitLastDigits == null) return null;
                return fitManager.enrollStudent(cpf, planName, startDate, durationMonths, initialPayment, paymentType.getDescription(), debitLastDigits);
            case CREDIT_CARD:
                int installments = ui.getInputInt("Quantidade de parcelas: ");
                if(installments < 0) return null;
                String creditLastDigits = ui.getInput("Últimos 4 dígitos do cartão de crédito: ");
                if(creditLastDigits == null) return null;
                return fitManager.enrollStudent(cpf, planName, startDate, durationMonths, initialPayment, paymentType.getDescription(), installments, creditLastDigits);
        }
        return null;
    }
    
    public static String formatEnrollmentList(ArrayList<Enrollment> enrollmentHistory) {
        if (enrollmentHistory.isEmpty()) {
            return "Nenhuma matrícula encontrada.";
        }

        String result = "Histórico de Matrículas:\n\n";

        for (Enrollment enrollment : enrollmentHistory) {
            result = result + "Código: " + enrollment.getCode() + "\n";
            result = result + "Aluno: " + enrollment.getStudent().getName() + "\n";
            result = result + "Plano: " + enrollment.getPlan().getName() + "\n";
            result = result + "Início: " + enrollment.getStartDate() + "\n";
            result = result + "Fim: " + enrollment.getEndDate() + "\n";
            result = result + "Duração: " + enrollment.getDurationMonths() + " meses\n";
            result = result + "Preço Total: R$ " + enrollment.getTotalPrice() + "\n";
            result = result + "Saldo Pendente: R$ " + enrollment.calculateBalance() + "\n";
            result = result + "Status: " + enrollment.getStatus().getDescription() + "\n";
            result = result + "----------------------------\n";
        }

        return result;
    }
}
