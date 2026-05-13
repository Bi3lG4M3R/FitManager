package ui.menus;

import java.rmi.server.Operation;
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
            menuOptions[i] = EnrollmentMenuEnum.values()[i].getOptionDescription();
        }



        do { 

            do{
                ui.showMenu("GERENCIAR MATRÍCULAS", menuOptions);
                optionSelected = EnrollmentMenuEnum.selectFromInt(ui.getInputInt("Selecione uma opção: "));
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
                    // Objeto inicializado devido a erro apontado por falta de inicialização.
                    OperationResult registerEnrollmentResult = new OperationResult(false, "Nenhum metodo de pagamento selecionado.", null);

                    switch (paymentType){

                        case PIX:
                            ui.showMessage("Pagamento via PIX selecionado.");
                            String pixKey = ui.getInput("Insira a chave PIX: ");
                            
                            registerEnrollmentResult = fitManager.enrollStudent(studentCpf, planName, startDate, durationMonths, initialPayment, paymentType, paymentType.getDescription(), pixKey, 0.0, 0, null);
                        break; 
                        case CREDIT_CARD:
                            ui.showMessage("Pagamento com cartão de crédito selecionado.");
                            int installments = ui.getInputInt("Insira o número de parcelas (máximo de 12 parcelas - digite 1 para pagamento à vista): ");
                            String creditCardLastDigits = ui.getInput("Insira os últimos 4 dígitos do cartão: ");

                            registerEnrollmentResult = fitManager.enrollStudent(studentCpf, planName, startDate, durationMonths, initialPayment, paymentType, paymentType.getDescription(), null, 0.0, installments, creditCardLastDigits);
                        break;
                        case DEBIT_CARD:
                            ui.showMessage("Pagamento com cartão de débito selecionado.");
                            String debitCardLastDigits = ui.getInput("Insira os últimos 4 dígitos do cartão: ");
                            
                            registerEnrollmentResult = fitManager.enrollStudent(studentCpf, planName, startDate, durationMonths, initialPayment, paymentType, paymentType.getDescription(), null, 0.0, 0, debitCardLastDigits);
                        break;
                        
                        case CASH:
                            ui.showMessage("Pagamento em dinheiro selecionado.");
                            double amountReceived = ui.getInputDouble("Insira o valor recebido: ");

                            registerEnrollmentResult = fitManager.enrollStudent(studentCpf, planName, startDate, durationMonths, initialPayment, paymentType, paymentType.getDescription(), null, amountReceived, 0, null);
                        break;
                    }

                    //OperationResult resultRegisterEnrollment = fitManager.enrollStudent(studentCpf, planName, startDate, durationMonths, initialPayment, paymentType, paymentType.getDescription());

                    if(registerEnrollmentResult.isSuccess())
                        ui.showMessage(registerEnrollmentResult.getMessage());
                    else
                        ui.showError("Erro ao registrar matrícula: " + registerEnrollmentResult.getMessage());
                break;

                case REGISTER_PAYMENT:
                    int enrollmentCode = ui.getInputInt("Digite o número de matrícula a realizar pagamento: ");
                    double amount = ui.getInputDouble("Valor do pagamento: ");
                    PaymentType chosenPaymentType = ui.getInputPaymentType("Selecione a forma de pagamento: ");
                    OperationResult paymentResult; // = new OperationResult(false, "Nenhum metodo de pagamento selecionado.", null);
                    switch (chosenPaymentType){

                        case PIX:
                            ui.showMessage("Pagamento via PIX selecionado.");
                            String pixKey = ui.getInput("Insira a chave PIX: ");
                            
                            paymentResult = fitManager.registerPayment(enrollmentCode, amount, chosenPaymentType, chosenPaymentType.getDescription(), pixKey, 0.0, 0, null);
                        break; 
                        case CREDIT_CARD:
                            ui.showMessage("Pagamento com cartão de crédito selecionado.");
                            int installments = ui.getInputInt("Insira o número de parcelas (máximo de 12 parcelas - digite 1 para pagamento à vista): ");
                            String creditCardLastDigits = ui.getInput("Insira os últimos 4 dígitos do cartão: ");

                            paymentResult = fitManager.registerPayment(enrollmentCode, amount, chosenPaymentType, chosenPaymentType.getDescription(), null, 0.0, installments, creditCardLastDigits);
                        break;
                        case DEBIT_CARD:
                            ui.showMessage("Pagamento com cartão de débito selecionado.");
                            String debitCardLastDigits = ui.getInput("Insira os últimos 4 dígitos do cartão: ");
                            
                            paymentResult = fitManager.registerPayment(enrollmentCode, amount, chosenPaymentType, chosenPaymentType.getDescription(), null, 0.0, 0, debitCardLastDigits);
                        break;
                        
                        case CASH:
                            ui.showMessage("Pagamento em dinheiro selecionado.");
                            double amountReceived = ui.getInputDouble("Insira o valor recebido: ");

                            paymentResult = fitManager.registerPayment(enrollmentCode, amount, chosenPaymentType, chosenPaymentType.getDescription(), null, amountReceived, 0, null);
                        break;
                    }

                    if(paymentResult.isSuccess()){
                        ui.showMessage(paymentResult.getMessage());
                        ui.showMessage(((Payment) paymentResult.getData()).getPaymentSummary());
                    }else
                        ui.showError("Erro ao registrar pagamento: " + paymentResult.getMessage());
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
                                OperationResult resultFeePayment;// = fitManager.registerPayment(enrollmentCodeToCancel, cancelationFee, feePaymentType, feePaymentType.getDescription());
                            
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
