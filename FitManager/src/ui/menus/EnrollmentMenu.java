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
            menuOptions[i] = EnrollmentMenuEnum.values()[i].getOptionDescription();
        }



        do { 

            do{
                int option = ui.showMenu("GERENCIAR MATRÍCULAS", menuOptions, "Selecione uma opção: ");
                optionSelected = EnrollmentMenuEnum.selectFromInt(option);
                if(optionSelected == null)
                    ui.showError("Opção inexistente. Selecione uma das opções acima.");  
            }while(optionSelected == null);
            

            switch(optionSelected){

                case REGISTER_ENROLLMENT:
                    String studentCpf = ui.getInput("Digite o CPF do aluno: ");
                    String planName = ui.getInput("Digite o nome do plano: ");
                    LocalDate startDate = ui.getInputDate("Digite a data de início da matrícula (dd/mm/aaaa): ");
                    int durationMonths = ui.getInputInt("Digite a duração da matrícula (numero de meses): ");
                    double initialPayment = ui.getInputDouble("Digite o valor do pagamento inicial: ");
                    PaymentType paymentType = ui.getInputPaymentType("Selecione a forma de pagamento: ");
                    
                    OperationResult resultRegisterEnrollment = null;

                    // O UI captura as variáveis a mais dependendo do tipo e chama a sobrecarga certa!
                    switch (paymentType) {
                        case PIX:
                            String pixKey = ui.getInput("Digite a chave PIX de origem: ");
                            resultRegisterEnrollment = fitManager.enrollStudent(studentCpf, planName, startDate, durationMonths, paymentType.getDescription(),initialPayment, pixKey);
                            break;
                        case CASH:
                            double amountReceived = ui.getInputDouble("Valor em dinheiro entregue pelo aluno: ");
                            resultRegisterEnrollment = fitManager.enrollStudent(studentCpf, planName, startDate, durationMonths, initialPayment, paymentType.getDescription(), amountReceived);
                            break;
                        case DEBIT_CARD:
                            String debitLastDigits = ui.getInput("Últimos 4 dígitos do cartão de débito: ");
                            resultRegisterEnrollment = fitManager.enrollStudent(studentCpf, planName, startDate, durationMonths, initialPayment, paymentType.getDescription(), debitLastDigits);
                            break;
                        case CREDIT_CARD:
                            int installments = ui.getInputInt("Quantidade de parcelas: ");
                            String creditLastDigits = ui.getInput("Últimos 4 dígitos do cartão de crédito: ");
                            resultRegisterEnrollment = fitManager.enrollStudent(studentCpf, planName, startDate, durationMonths, initialPayment, paymentType.getDescription(), installments, creditLastDigits);
                            break;
                    }
                    
                    if(resultRegisterEnrollment != null && resultRegisterEnrollment.isSuccess())
                        ui.showMessage(resultRegisterEnrollment.getMessage());
                    else
                        ui.showError("Erro ao registrar matrícula: " + (resultRegisterEnrollment != null ? resultRegisterEnrollment.getMessage() : "Erro interno."));
                break;

                case REGISTER_PAYMENT:
                    int enrollmentCode = ui.getInputInt("Digite o número de matrícula a realizar pagamento: ");
                    double amount = ui.getInputDouble("Valor do pagamento: ");
                    PaymentType registerPaymentType = ui.getInputPaymentType("Selecione a forma de pagamento: ");
        
                    OperationResult resultPayment = null;

                    switch (registerPaymentType) {
                        case PIX:
                            String pixKey = ui.getInput("Digite a chave PIX de origem: ");
                            resultPayment = fitManager.registerPaymentPix(enrollmentCode, amount, registerPaymentType.getDescription(), pixKey);
                            break;
                        case CASH:
                            double amountReceived = ui.getInputDouble("Valor em dinheiro entregue pelo aluno: ");
                            resultPayment = fitManager.registerPaymentCash(enrollmentCode, amount, registerPaymentType.getDescription(), amountReceived);
                            break;
                        case DEBIT_CARD:
                            String debitLastDigits = ui.getInput("Últimos 4 dígitos do cartão de débito: ");
                            resultPayment = fitManager.registerPaymentDebit(enrollmentCode, amount, registerPaymentType.getDescription(), debitLastDigits);
                            break;
                        case CREDIT_CARD:
                            int installments = ui.getInputInt("Quantidade de parcelas: ");
                            String creditLastDigits = ui.getInput("Últimos 4 dígitos do cartão de crédito: ");
                            resultPayment = fitManager.registerPaymentCredit(enrollmentCode, amount, registerPaymentType.getDescription(), installments, creditLastDigits);
                            break;
                    }

                    if(resultPayment != null && resultPayment.isSuccess())
                        ui.showMessage(resultPayment.getMessage());
                    else
                        ui.showError("Erro ao registrar pagamento: " + (resultPayment != null ? resultPayment.getMessage() : "Erro interno."));
                break;

                case CANCEL_ENROLLMENT:
                    int enrollmentCodeToCancel = ui.getInputInt("Digite o número de matrícula a ser cancelada: ");
                    String cancelReason = ui.getInput("Digite o motivo do cancelamento: ");
                    OperationResult findEnrollmentResult = fitManager.findEnrollmentByCode(enrollmentCodeToCancel);

                    if(findEnrollmentResult.isSuccess()){ // Encontrou matricula
                        // Se o plano for anual possui taxa de cancelamento
                        Enrollment enrollmentToCancel = (Enrollment) findEnrollmentResult.getData();
                        if(enrollmentToCancel.getPlan().getType() == domain.plan.PlanType.ANNUAL){

                            double cancelationFee = (double) fitManager.calculateCancelationFee(enrollmentCodeToCancel).getData();
                            if(cancelationFee > 0.0){   // Calcula a taxa
                                                        // Solicita pagamento
                                ui.showMessage("Taxa de cancelamento: " + String.format("%.2f", cancelationFee));

                                // Pagamento da taxa
                                PaymentType feePaymentType = ui.getInputPaymentType("Selecione a forma de pagamento: ");
                                OperationResult resultFeePayment = null;

                                switch (feePaymentType) {
                                    case PIX:
                                        String pixKey = ui.getInput("Digite a chave PIX de origem: ");
                                        resultFeePayment = fitManager.registerPaymentPix(enrollmentCodeToCancel, cancelationFee, "Taxa de cancelamento", pixKey);
                                        break;
                                    case CASH:
                                        double amountReceived = ui.getInputDouble("Valor em dinheiro entregue pelo aluno: ");
                                        resultFeePayment = fitManager.registerPaymentCash(enrollmentCodeToCancel, cancelationFee, "Taxa de cancelamento", amountReceived);
                                        break;
                                    case DEBIT_CARD:
                                        String debitLastDigits = ui.getInput("Últimos 4 dígitos do cartão de débito: ");
                                        resultFeePayment = fitManager.registerPaymentDebit(enrollmentCodeToCancel, cancelationFee, "Taxa de cancelamento", debitLastDigits);
                                        break;
                                    case CREDIT_CARD:
                                        int installments = ui.getInputInt("Quantidade de parcelas: ");
                                        String creditLastDigits = ui.getInput("Últimos 4 dígitos do cartão de crédito: ");
                                        resultFeePayment = fitManager.registerPaymentCredit(enrollmentCodeToCancel, cancelationFee, "Taxa de cancelamento", installments, creditLastDigits);
                                        break;
                                }
                                
                                if(resultFeePayment.isSuccess()){   // Pagamento foi sucesso
                                    ui.showMessage(resultFeePayment.getMessage());
                                    OperationResult resultCancelEnrollment = fitManager.cancelEnrollment(enrollmentCodeToCancel, cancelReason);
                                    
                                    if(resultCancelEnrollment.isSuccess()){
                                        ui.showMessage(resultCancelEnrollment.getMessage());
                                    } else {
                                        ui.showError("Erro ao cancelar matrícula: " + resultCancelEnrollment.getMessage());
                                    }

                                }else{ // Pagamento falhou
                                    ui.showError("Erro ao registrar pagamento da taxa de cancelamento: " + resultFeePayment.getMessage());
                                }
                            }
                        }else{  // Não há taxa de cancelamento
                                // Realiza somente o cancelamento
                            OperationResult resultCancelEnrollment = fitManager.cancelEnrollment(enrollmentCodeToCancel, cancelReason);
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
                    ArrayList<Enrollment> enrollmentHistory = fitManager.listEnrollments();
                    if(enrollmentHistory.isEmpty()){
                        ui.showMessage("Nenhuma matrícula encontrada.");
                    } else {
                        ui.showMessage("Histórico de Matrículas:");
                        for(Enrollment enrollment : enrollmentHistory){
                            int code = enrollment.getCode();
                            String studentName = enrollment.getStudent().getName();
                            String planNameHistory = enrollment.getPlan().getName();
                            LocalDate startDateHistory = enrollment.getStartDate();
                            LocalDate endDateHistory = enrollment.getEndDate();
                            int durationMonthsHistory = enrollment.getDurationMonths();
                            double totalPrice = enrollment.getTotalPrice();
                            double pendingAmount = enrollment.calculateBalance();

                            String status = enrollment.getStatus().getDescription();

                            ui.showEnrollment(code, studentName, planNameHistory, startDateHistory, endDateHistory, durationMonthsHistory, totalPrice, pendingAmount, status);

                        }
                        ui.showMessage("Histórico de matrículas exibido com sucesso.");
                    }
                    
                break;


                case BACK:
                    ui.showMessage("Voltando ao menu principal...");
                break;

            }

        }while(optionSelected != EnrollmentMenuEnum.BACK);


    }
}
