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
                    OperationResult resultRegisterEnrollment = fitManager.enrollStudent(studentCpf, planName, startDate, durationMonths, initialPayment, paymentType, paymentType.getDescription());
                    
                    if(resultRegisterEnrollment.isSuccess())
                        ui.showMessage(resultRegisterEnrollment.getMessage());
                    else
                        ui.showError("Erro ao registrar matrícula: " + resultRegisterEnrollment.getMessage());
                break;

                case REGISTER_PAYMENT:
                    int enrollmentCode = ui.getInputInt("Digite o número de matrícula a realizar pagamento: ");
                    double amount = ui.getInputDouble("Valor do pagamento: ");
                    PaymentType registerPaymentType = ui.getInputPaymentType("Selecione a forma de pagamento: ");
        
                    OperationResult resultPayment = fitManager.registerPayment(enrollmentCode, amount, registerPaymentType, registerPaymentType.getDescription());
                    if(resultPayment.isSuccess())
                        ui.showMessage(resultPayment.getMessage());
                    else
                        ui.showError("Erro ao registrar pagamento: " + resultPayment.getMessage());
                break;

                case CANCEL_ENROLLMENT:
                    int enrollmentCodeToCancel = ui.getInputInt("Digite o número de matrícula a ser cancelada: ");
                    String cancelReason = ui.getInput("Digite o motivo do cancelamento: ");
                    OperationResult resultCancelEnrollment = fitManager.cancelEnrollment(enrollmentCodeToCancel, cancelReason);
                    Double cancelationFee;
                    PaymentType feePaymentType;
                    OperationResult resultFeePayment; 
                    /* adicionar condição para cancelamento */
                    

                    if(resultCancelEnrollment.isSuccess()){
                        cancelationFee = (Double) resultCancelEnrollment.getData();
                        ui.showMessage("Taxa de cancelamento: " + String.format("%.2f", cancelationFee));
                        /*  Realiza o pagamento da taxa de cancelamento  */
                        feePaymentType = ui.getInputPaymentType("Selecione a forma de pagamento: ");
                        resultFeePayment = fitManager.registerPayment(enrollmentCodeToCancel, cancelationFee, feePaymentType, feePaymentType.getDescription());
                        if(resultFeePayment.isSuccess())
                            ui.showMessage(resultFeePayment.getMessage());
                        else
                            ui.showError("Erro ao registrar pagamento: " + resultFeePayment.getMessage());

                        ui.showMessage(resultCancelEnrollment.getMessage());
                    }else{
                        ui.showError("Erro ao cancelar matrícula: " + resultCancelEnrollment.getMessage());
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
                            String planNameHistory = enrollment.getPlan().getDescription();
                            LocalDate startDateHistory = enrollment.getStartDate();
                            LocalDate endDateHistory = enrollment.getEndDate();
                            int durationMonthsHistory = enrollment.getDurationMonths();
                            double totalPrice = enrollment.getTotalPrice();
                            double pendingAmount = enrollment.calculateBalance();

                            String status = enrollment.getStatus().toString();

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
