package ui.menus;

import java.time.LocalDate;
import java.util.ArrayList;

import application.FitManager;
import application.OperationResult;
import domain.Enrollment;
import domain.payment.Payment;
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
                    
                    OperationResult<Enrollment> resultRegisterEnrollment = null;

                    // O UI captura as variáveis a mais dependendo do tipo e chama a sobrecarga certa!
                    switch (paymentType) {
                        case PIX:
                            String pixKey = ui.getInput("Digite a chave PIX de origem: ");
                            if(pixKey == null) break;
                            resultRegisterEnrollment = fitManager.enrollStudent(studentCpf, planName, startDate, durationMonths, paymentType.getDescription(),initialPayment, pixKey);
                            break;
                        case CASH:
                            double amountReceived = ui.getInputDouble("Valor em dinheiro entregue pelo aluno: ");
                            if(amountReceived < 0) break;
                            resultRegisterEnrollment = fitManager.enrollStudent(studentCpf, planName, startDate, durationMonths, initialPayment, paymentType.getDescription(), amountReceived);
                            break;
                        case DEBIT_CARD:
                            String debitLastDigits = ui.getInput("Últimos 4 dígitos do cartão de débito: ");
                            if(debitLastDigits == null) break;
                            resultRegisterEnrollment = fitManager.enrollStudent(studentCpf, planName, startDate, durationMonths, initialPayment, paymentType.getDescription(), debitLastDigits);
                            break;
                        case CREDIT_CARD:
                            int installments = ui.getInputInt("Quantidade de parcelas: ");
                            if(installments < 0) break;
                            String creditLastDigits = ui.getInput("Últimos 4 dígitos do cartão de crédito: ");
                            if(creditLastDigits == null) break;
                            resultRegisterEnrollment = fitManager.enrollStudent(studentCpf, planName, startDate, durationMonths, initialPayment, paymentType.getDescription(), installments, creditLastDigits);
                            break;
                    }
                    
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
        
                    OperationResult<Payment> resultPayment = null;

                    switch (registerPaymentType) {
                        case PIX:
                            String pixKey = ui.getInput("Digite a chave PIX de origem: ");
                            if(pixKey == null) break;
                            resultPayment = fitManager.registerPaymentPix(enrollmentCode, amount, registerPaymentType.getDescription(), pixKey);
                            break;
                        case CASH:
                            double amountReceived = ui.getInputDouble("Valor em dinheiro entregue pelo aluno: ");
                            if(amountReceived < 0) break;
                            resultPayment = fitManager.registerPaymentCash(enrollmentCode, amount, registerPaymentType.getDescription(), amountReceived);
                            break;
                        case DEBIT_CARD:
                            String debitLastDigits = ui.getInput("Últimos 4 dígitos do cartão de débito: ");
                            if(debitLastDigits == null) break;
                            resultPayment = fitManager.registerPaymentDebit(enrollmentCode, amount, registerPaymentType.getDescription(), debitLastDigits);
                            break;
                        case CREDIT_CARD:
                            int installments = ui.getInputInt("Quantidade de parcelas: ");
                            if(installments < 0) break;
                            String creditLastDigits = ui.getInput("Últimos 4 dígitos do cartão de crédito: ");
                            if(creditLastDigits == null) break;
                            resultPayment = fitManager.registerPaymentCredit(enrollmentCode, amount, registerPaymentType.getDescription(), installments, creditLastDigits);
                            break;
                    }
                    
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
                    OperationResult<Enrollment> findEnrollmentResult = fitManager.findEnrollmentByCode(enrollmentCodeToCancel);

                    if(findEnrollmentResult.isSuccess()){ // Encontrou matricula
                        // Se o plano for anual possui taxa de cancelamento
                        Enrollment enrollmentToCancel = findEnrollmentResult.getData();
                        if(enrollmentToCancel.getPlan().getType() == domain.plan.PlanType.ANNUAL){

                            double cancelationFee = (double) fitManager.calculateCancelationFee(enrollmentCodeToCancel).getData();
                            if(cancelationFee > 0.0){   // Calcula a taxa
                                                        // Solicita pagamento
                                ui.showMessage("Taxa de cancelamento: " + String.format("%.2f", cancelationFee));

                                // Pagamento da taxa
                                PaymentType feePaymentType = ui.getInputPaymentType("Selecione a forma de pagamento: ");
                                if (feePaymentType == null) {
                                    ui.showMessage("Operação cancelada.");
                                    break;
                                }
                                OperationResult<Payment> resultFeePayment = null;

                                switch (feePaymentType) {
                                    case PIX:
                                        String pixKey = ui.getInput("Digite a chave PIX de origem: ");
                                        if(pixKey == null) break;
                                        resultFeePayment = fitManager.registerPaymentPix(enrollmentCodeToCancel, cancelationFee, "Taxa de cancelamento", pixKey);
                                        break;
                                    case CASH:
                                        double amountReceived = ui.getInputDouble("Valor em dinheiro entregue pelo aluno: ");
                                        if(amountReceived < 0) break;
                                        resultFeePayment = fitManager.registerPaymentCash(enrollmentCodeToCancel, cancelationFee, "Taxa de cancelamento", amountReceived);
                                        break;
                                    case DEBIT_CARD:
                                        String debitLastDigits = ui.getInput("Últimos 4 dígitos do cartão de débito: ");
                                        if(debitLastDigits == null) break;
                                        resultFeePayment = fitManager.registerPaymentDebit(enrollmentCodeToCancel, cancelationFee, "Taxa de cancelamento", debitLastDigits);
                                        break;
                                    case CREDIT_CARD:
                                        int installments = ui.getInputInt("Quantidade de parcelas: ");
                                        if(installments < 0) break;
                                        String creditLastDigits = ui.getInput("Últimos 4 dígitos do cartão de crédito: ");
                                        if(creditLastDigits == null) break;
                                        resultFeePayment = fitManager.registerPaymentCredit(enrollmentCodeToCancel, cancelationFee, "Taxa de cancelamento", installments, creditLastDigits);
                                        break;
                                }

                                if(resultFeePayment != null && resultFeePayment.isSuccess()){   // Pagamento foi sucesso
                                    ui.showMessage(resultFeePayment.getMessage());
                                    OperationResult<Enrollment> resultCancelEnrollment = fitManager.cancelEnrollment(enrollmentCodeToCancel, cancelReason);
                                    if(resultCancelEnrollment.isSuccess()){
                                        ui.showMessage(resultCancelEnrollment.getMessage());
                                    } else {
                                        ui.showError("Erro ao cancelar matrícula: " + resultCancelEnrollment.getMessage());
                                    }
                                }else if(resultFeePayment != null){ // Pagamento falhou
                                    ui.showError("Erro ao registrar pagamento da taxa de cancelamento: " + resultFeePayment.getMessage());
                                }
                            }
                        }else{  // Não há taxa de cancelamento
                                // Realiza somente o cancelamento
                            OperationResult<Enrollment> resultCancelEnrollment = fitManager.cancelEnrollment(enrollmentCodeToCancel, cancelReason);
                            if(resultCancelEnrollment.isSuccess()){
                                ui.showMessage(resultCancelEnrollment.getMessage());
                            } else {
                                ui.showError("Erro ao cancelar matrícula: " + resultCancelEnrollment.getMessage());
                            }
                            }    
                    }else{
                        ui.showError("Erro ao encontrar matrícula: " + findEnrollmentResult.getMessage());
                    }
                break;

                case CHECK_ACTIVE_ENROLLMENT:
                    String studentCpfToCheck = ui.getInput("Digite o CPF do aluno para consultar a matrícula: ");
                    if(studentCpfToCheck == null) break;
                    OperationResult<Enrollment> resultCheckEnrollment = fitManager.findActiveEnrollment(studentCpfToCheck);
                    if(resultCheckEnrollment.isSuccess()){
                        ui.showMessage(resultCheckEnrollment.getMessage());
                        ui.showEnrollment(resultCheckEnrollment.getData());
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
