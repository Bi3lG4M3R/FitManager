package ui.menus;

import java.util.ArrayList;

import application.FinancialReport;
import application.FitManager;
import application.OperationResult;
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
                        String activeReport = buildActiveEnrollmentsReport(allEnrollments);
                        if (activeReport != null) {
                            ui.showMessage(activeReport);
                        } else {
                            ui.showError("Não há matrículas ativas cadastradas.");
                        }
                    }
                break;

                case PENDING_PAYMENTS_ENROLLMENTS:
                    if (allEnrollments.isEmpty()) {
                        ui.showError("Não há matrículas cadastradas.");
                    } else {
                        String pendingReport = buildPendingPaymentsReport(allEnrollments);
                        if (pendingReport != null) {
                            ui.showMessage(pendingReport);
                        } else {
                            ui.showError("Não há matrículas com pagamentos pendentes.");
                        }
                    }
                break;

                case ALL_ENROLLMENTS: 
                    if (allEnrollments.isEmpty()) {
                        ui.showError("Não há matrículas cadastradas.");
                    } else {
                        ui.showMessage(buildAllEnrollmentsReport(allEnrollments));
                    }
                break;

                case MONTHLY_FINANCIAL_REPORT:
                    int month = ui.getInputInt("Digite o mês do relatório (1-12): ");
                    if (month < 0){
                        ui.showError("Digite um mes valido ! Operacao cancelada");
                    }else{
                        int year = ui.getInputInt("Digite o ano do relatório: ");
                        if (year < 0){
                            ui.showError("Digite um mes valido ! Operacao cancelada");
                        }else{

                            OperationResult<FinancialReport> resultReport = fitManager.generateMonthlyReport(month, year);
                            if (resultReport.isSuccess()) {
                                FinancialReport report = resultReport.getData();
                                ui.showMessage(report.toDisplayString());

                                String exportChoice = ui.getInput("Deseja exportar este relatório para arquivo? (S/N): ");
                                if (exportChoice != null && exportChoice.equalsIgnoreCase("S")) {
                                    OperationResult<String> exportResult = fitManager.exportMonthlyReport(report);
                                    if (exportResult.isSuccess()) {
                                        ui.showMessage(exportResult.getMessage() + "\nArquivo: " + exportResult.getData());
                                    } else {
                                        ui.showError(exportResult.getMessage());
                                    }
                                }
                            }else{
                                ui.showError(resultReport.getMessage());
                            }
                        }
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
    
    /* Método auxiliar para construir relatório de matrículas ativas. */
    private String buildActiveEnrollmentsReport(ArrayList<Enrollment> allEnrollments) {
        String result = "Histórico de Matrículas Ativas:\n\n";
        boolean found = false;
        
        for (Enrollment enrollment : allEnrollments) {
            if (enrollment.getStatus() == domain.EnrollmentStatus.ACTIVE) {
                found = true;
                result += "Aluno: " + enrollment.getStudent().getName() + "\n";
                result += "Plano: " + enrollment.getPlan().getName() + "\n";
                result += "----------------------------------\n";
            }
        }
        
        return found ? result : null;
    }
    
    /* Método auxiliar para construir relatório de matrículas com pagamentos pendentes. */
    private String buildPendingPaymentsReport(ArrayList<Enrollment> allEnrollments) {
        String result = "Lista de matrículas com pagamentos pendentes:\n\n";
        boolean found = false;
        
        for (Enrollment enrollment : allEnrollments) {
            if (enrollment.getStatus() == domain.EnrollmentStatus.ACTIVE && enrollment.calculateBalanceForMonthsUsed() > 0) {
                found = true;
                result += "Código: " + enrollment.getCode() + "\n";
                result += "Aluno: " + enrollment.getStudent().getName() + "\n";
                result += "Plano: " + enrollment.getPlan().getName() + "\n";
                result += "Início: " + enrollment.getStartDate() + "\n";
                result += "Fim: " + enrollment.getEndDate() + "\n";
                result += "Duração: " + enrollment.getDurationMonths() + " meses\n";
                result += "Preço Total: R$ " + enrollment.getTotalPrice() + "\n";
                result += "Saldo Pendente: R$ " + enrollment.calculateBalanceForMonthsUsed() + "\n";
                result += "Status: " + enrollment.getStatus().getDescription() + "\n";
                result += "----------------------------------\n";
            }
        }
        
        return found ? result : null;
    }
    
    /* Método auxiliar para construir relatório de todas as matrículas. */
    private String buildAllEnrollmentsReport(ArrayList<Enrollment> allEnrollments) {
        String result = "Histórico de Matrículas:\n\n";
        
        for (Enrollment enrollment : allEnrollments) {
            result += "Código: " + enrollment.getCode() + "\n";
            result += "Aluno: " + enrollment.getStudent().getName() + "\n";
            result += "Plano: " + enrollment.getPlan().getName() + "\n";
            result += "Início: " + enrollment.getStartDate() + "\n";
            result += "Duração: " + enrollment.getDurationMonths() + " meses\n";
            result += "Preço Total: R$ " + enrollment.getTotalPrice() + "\n";
            result += "Status: " + enrollment.getStatus().getDescription() + "\n";
            
            if (enrollment.getStatus().getDescription().equals("CANCELADO")) {
                result += "Data de Cancelamento: " + enrollment.getCancellationDate() + "\n";
                result += "Motivo: " + enrollment.getCancellationReason() + "\n";
            } else {
                result += "Saldo Pendente: R$ " + (enrollment.getTotalPrice() - enrollment.calculateTotalPaid()) + "\n";
                result += "Fim: " + enrollment.getEndDate() + "\n";
            }
            
            result += "----------------------------\n";
        }
        
        return result;
    }

}