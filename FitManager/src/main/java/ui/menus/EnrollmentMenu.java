package ui.menus;

import java.time.LocalDate;
import java.util.ArrayList;

import ui.UserInterface;
import ui.enums.EnrollmentMenuEnum;
import application.FitManager;
import application.OperationResult;
import application.EnrollmentService;
import domain.Enrollment;
import domain.payment.PaymentType;

public class EnrollmentMenu {
    private UserInterface ui;
    private FitManager fitManager;

    // Construtor
    public EnrollmentMenu(UserInterface ui, FitManager fitManager){
        this.ui = ui;
        this.fitManager = fitManager;
    }

    public void run(){
        int optionSelected;

        //  String array para armazenar as opções dos menus
        String[] menuOptions = new String[EnrollmentMenuEnum.values().length];

        //  Construção do array pegando as descrições do do enum
        for(int i = 0; i < EnrollmentMenuEnum.values().length; i++){
            menuOptions[i] = EnrollmentMenuEnum.values()[i].getOptionDescription();
        }



        do { 
            ui.showMenu("GERENCIAR MATRÍCULAS", menuOptions);

            optionSelected = ui.getInputInt("Selecione uma opção: ");

            switch(EnrollmentMenuEnum.selectFromInt(optionSelected)){

                case REGISTER_ENROLLMENT:
                    String studentCpf = ui.getInput("Digite o CPF do aluno: ");
                    String planName = ui.getInput("Digite o nome do plano: ");
                    LocalDate startDate = ui.getInputDate("Digite a data de início da matrícula (dd/mm/aaaa): ");
                    int durationMonths = ui.getInputInt("Digite a duração da matrícula (numero de meses): ");
                    double initialPayment = ui.getInputDouble("Digite o valor do pagamento inicial: ");

                    // Possibilidade de criar um metodo para mini menus como pagamento e planos
                    // para evitar semelhaça com a exibiçao dos menus principais

                    //OBS: é possivel de deixar o codigo mais limpo construindo a string com um for
                    //antes de chamar a função (autorização para alteração pendente)
                    ui.showMenu("Formas de pagamento", new String[] {
                        PaymentType.PIX.getValueOpcao() + " - "  + PaymentType.PIX.getDescription(),
                        PaymentType.CREDIT_CARD.getValueOpcao() + " - " + PaymentType.CREDIT_CARD.getDescription(),
                        PaymentType.DEBIT_CARD.getValueOpcao() + " - " + PaymentType.DEBIT_CARD.getDescription(),
                        PaymentType.CASH.getValueOpcao() + " - " + PaymentType.CASH.getDescription()
                    });
                    PaymentType paymentType = PaymentType.selectFromInt(ui.getInputInt("Selecione a forma de pagamento: "));

                    OperationResult resultRegisterEnrollment = fitManager.enrollStudent(studentCpf, planName, startDate, durationMonths, initialPayment, paymentType, paymentType.getDescription());
                    
                    if(resultRegisterEnrollment.getSuccess())
                        ui.showMessage(resultRegisterEnrollment.getMessage());
                    else
                        ui.showError("Erro ao registrar matrícula: " + resultRegisterEnrollment.getMessage());
                break;

                case REGISTER_PAYMENT:
                    int enrollmentCode = ui.getInputInt("Digite o número de matrícula a realizar pagamento: ");
                    double amount = ui.getInputDouble("Valor do pagamento: ");

                    ui.showMenu("Formas de pagamento", new String[] {
                        PaymentType.PIX.getValueOpcao() + " - "  + PaymentType.PIX.getDescription(),
                        PaymentType.CREDIT_CARD.getValueOpcao() + " - " + PaymentType.CREDIT_CARD.getDescription(),
                        PaymentType.DEBIT_CARD.getValueOpcao() + " - " + PaymentType.DEBIT_CARD.getDescription(),
                        PaymentType.CASH.getValueOpcao() + " - " + PaymentType.CASH.getDescription()
                    });
                    PaymentType registerPaymentType = PaymentType.selectFromInt(ui.getInputInt("Selecione a forma de pagamento: "));
                    
                    OperationResult resultPayment = fitManager.registerPayment(enrollmentCode, amount, registerPaymentType, registerPaymentType.getDescription());
                    if(resultPayment.getSuccess())
                        ui.showMessage(resultPayment.getMessage());
                    else
                        ui.showError("Erro ao registrar pagamento: " + resultPayment.getMessage());
                break;

                case CANCEL_ENROLLMENT:
                    int enrollmentCodeToCancel = ui.getInputInt("Digite o número de matrícula a ser cancelada: ");
                    String cancelReason = ui.getInput("Digite o motivo do cancelamento: ");
                    OperationResult resultCancelEnrollment = fitManager.cancelEnrollment(enrollmentCodeToCancel, cancelReason);
                    
                    if(resultCancelEnrollment.getSuccess())
                        ui.showMessage(resultCancelEnrollment.getMessage());
                    else
                        ui.showError("Erro ao cancelar matrícula: " + resultCancelEnrollment.getMessage());
                break;

                case CHECK_ACTIVE_ENROLLMENT:
                    String studentCpfToCheck = ui.getInput("Digite o CPF do aluno para consultar a matrícula: ");
                    OperationResult resultCheckEnrollment = fitManager.hasActiveEnrollment(studentCpfToCheck);
                    if(resultCheckEnrollment.getSuccess())
                        ui.showMessage(resultCheckEnrollment.getMessage());
                    else
                        ui.showError("Erro ao consultar matrícula: " + resultCheckEnrollment.getMessage());
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
                            String status = enrollment.getStatus().toString();

                            ui.showEnrollment(code, studentName, planNameHistory, startDateHistory, endDateHistory, status);

                        }
                    }
                    ui.showMessage("Histórico de matrículas exibido com sucesso.");
                break;


                case BACK:
                    ui.showMessage("Voltando ao menu principal...");
                break;

                default:
                    ui.showError("Opção inválida. Por favor, selecione uma opção válida.");

            }

        }while(optionSelected != EnrollmentMenuEnum.BACK.getOptionNumber());


    }
}
