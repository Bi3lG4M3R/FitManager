package ui.menus;

import java.util.ArrayList;

import application.FitManager;
import domain.Enrollment;
import ui.UserInterface;
import ui.enums.ReportsMenuEnum;

public class ReportsMenu{
    
    private final UserInterface ui;
    private final FitManager fitManager;

    // Construtor
    public ReportsMenu(UserInterface ui, FitManager fitManager){
        this.ui = ui;
        this.fitManager = fitManager;
    }



    public void run(){
        ReportsMenuEnum optionSelected;
        
        
        //  String array para armazenar as opções dos menus
        String[] menuOptions = new String[ReportsMenuEnum.values().length];

        //  Construção do array a partir das descrições do do enum
        for(int i = 0; i < ReportsMenuEnum.values().length; i++){
            menuOptions[i] = ReportsMenuEnum.values()[i].getOptionNumber() + " - " + ReportsMenuEnum.values()[i].getOptionDescription();
        }



        do{ 

            do{
                int option = ui.showMenu("RELATÓRIOS", menuOptions, "Selecione uma opção: ");
                if(option == 0){
                    optionSelected = ReportsMenuEnum.BACK;
                } else {
                    optionSelected = ReportsMenuEnum.selectFromInt(option);
                    if(optionSelected == null)
                        ui.showError("Opção inexistente. Selecione uma das opções acima.");
                }
            }while(optionSelected == null);
            
            ArrayList<Enrollment> allEnrollments = fitManager.listEnrollments();

            switch(optionSelected) {
                case ACTIVE_ENROLLMENTS_STUDENTS:
                    if(allEnrollments.isEmpty()){
                        ui.showError("Não há matriculas cadastradas.");
                    } else {
                        String result = "Histórico de Matrículas Ativas:\n\n";
                        boolean found = false;

                        for (Enrollment enrollment : allEnrollments) {
                            if (enrollment.getStatus() == domain.EnrollmentStatus.ACTIVE) {
                                found = true;
                                result = result + "Aluno: " + enrollment.getStudent().getName() + "\n";
                                result = result + "Plano: " + enrollment.getPlan().getName() + "\n";
                                result = result + "----------------------------------\n";
                            }
                        }

                        if (found) {
                            ui.showMessage(result);
                        } else {
                            ui.showError("Não há matrículas ativas cadastradas.");
                        }
                    }
                break;

                case PENDING_PAYMENTS_ENROLLMENTS:
                    if (allEnrollments.isEmpty()) {
                        ui.showError("Não há matrículas cadastradas.");
                    } else {
                        String result = "Lista de matrículas com pagamentos pendentes:\n\n";
                        boolean found = false;

                        for (Enrollment enrollment : allEnrollments) {
                            if (enrollment.getStatus() == domain.EnrollmentStatus.ACTIVE && enrollment.calculateBalanceForMonthsUsed() > 0) {
                                found = true;
                                result = result + "Código: " + enrollment.getCode() + "\n";
                                result = result + "Aluno: " + enrollment.getStudent().getName() + "\n";
                                result = result + "Plano: " + enrollment.getPlan().getName() + "\n";
                                result = result + "Início: " + enrollment.getStartDate() + "\n";
                                result = result + "Fim: " + enrollment.getEndDate() + "\n";
                                result = result + "Duração: " + enrollment.getDurationMonths() + " meses\n";
                                result = result + "Preço Total: R$ " + enrollment.getTotalPrice() + "\n";
                                result = result + "Saldo Pendente: R$ " + enrollment.calculateBalanceForMonthsUsed() + "\n";
                                result = result + "Status: " + enrollment.getStatus().getDescription() + "\n";
                                result = result + "----------------------------------\n";
                            }
                        }

                        if (found) {
                            ui.showMessage(result);
                        } else {
                            ui.showError("Não há matrículas com pagamentos pendentes.");
                        }
                    }
                break;

                case ALL_ENROLLMENTS: 
                    if (allEnrollments.isEmpty()) {
                        ui.showError("Não há matrículas cadastradas.");
                    } else {
                        String result = "Histórico de Matrículas:\n\n";

                        for (Enrollment enrollment : allEnrollments) {
                            result = result + "Código: " + enrollment.getCode() + "\n";
                            result = result + "Aluno: " + enrollment.getStudent().getName() + "\n";
                            result = result + "Plano: " + enrollment.getPlan().getName() + "\n";
                            result = result + "Início: " + enrollment.getStartDate() + "\n";
                            result = result + "Duração: " + enrollment.getDurationMonths() + " meses\n";
                            result = result + "Preço Total: R$ " + enrollment.getTotalPrice() + "\n";
                            result = result + "Status: " + enrollment.getStatus().getDescription() + "\n";

                            // Lógica condicional para cancelados
                            if (enrollment.getStatus().getDescription().equals("CANCELADO")) {
                                result = result + "Data de Cancelamento: " + enrollment.getCancellationDate() + "\n";
                                result = result + "Motivo: " + enrollment.getCancellationReason() + "\n";
                            } else {
                                double pendingAmount = enrollment.getTotalPrice() - enrollment.calculateTotalPaid();
                                result = result + "Saldo Pendente: R$ " + pendingAmount + "\n";
                                result = result + "Fim: " + enrollment.getEndDate() + "\n";
                            }

                            result = result + "----------------------------\n";
                        }

                        ui.showMessage(result);
                    }
                break;

                case BACK:
                    ui.showMessage("Voltando ao menu principal...");
                break;

                default:
                    ui.showMessage("Opção inexistente, selecione uma das opçãoes acima.");
                break;
            }
        }while(optionSelected != ReportsMenuEnum.BACK);
    }

}