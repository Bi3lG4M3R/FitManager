package ui.menus;

import java.time.LocalDate;
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
            menuOptions[i] = ReportsMenuEnum.values()[i].getOptionDescription();
        }



        do{ 

            do{
                ui.showMenu("RELATÓRIOS", menuOptions);
                optionSelected = ReportsMenuEnum.selectFromInt(ui.getInputInt("Selecione uma opção: "));
                if(optionSelected == null)
                    ui.showError("Opção inexistente. Selecione uma das opções acima.");
            }while(optionSelected == null);

            switch(optionSelected) {


                case ACTIVE_ENROLLMENTS_STUDENTS:
                    boolean hasActiveEnrollments = false;
                    ArrayList<Enrollment> activeEnrollments = fitManager.listEnrollments();
                    if(activeEnrollments.isEmpty()){
                        ui.showError("Não há matriculas cadastradas.");
                    } else {
                        ui.showMessage("Histórico de Matrículas Ativas:");
                        for(Enrollment enrollment : activeEnrollments){
                            if(enrollment.getStatus() == domain.EnrollmentStatus.ACTIVE){
                                hasActiveEnrollments = true;
                                String studentName = enrollment.getStudent().getName();
                                String planName = enrollment.getPlan().getName();
                                
                                ui.showMessage(
                                    "Aluno: " + studentName + "\n" +
                                    "Plano: " + planName + "\n" +
                                    "----------------------------------"
                                );
                            }
                        }
                        if(hasActiveEnrollments){
                            ui.showMessage("Fim da lista de matriculas ativas.");
                        }else{
                            ui.showError("Não há matriculas ativas cadastradas.");
                        }
                    }
                break;

                case PENDING_PAYMENTS_ENROLLMENTS:
                    ArrayList<Enrollment> pendingPaymentsEnrollments = fitManager.listEnrollments();
                    if(pendingPaymentsEnrollments.isEmpty()){
                        ui.showError("Não há matriculas cadastradas.");
                    } else {
                        ui.showMessage("Lista de matriculas com pagamentos pendentes:");
                        for(Enrollment enrollment : pendingPaymentsEnrollments){
                                if(enrollment.getStatus() == domain.EnrollmentStatus.ACTIVE && enrollment.calculateTotalPaid() < enrollment.getTotalPrice()){
                                int code = enrollment.getCode();
                                String studentName = enrollment.getStudent().getName();
                                String planName = enrollment.getPlan().getName();
                                double totalPrice = enrollment.getTotalPrice();
                                double pendingAmount = enrollment.getTotalPrice() - enrollment.calculateTotalPaid();
                                LocalDate startDate = enrollment.getStartDate();
                                LocalDate endDate = enrollment.getEndDate();
                                int durationMonths = enrollment.getDurationMonths();
                                String status = enrollment.getStatus().toString();

                                ui.showEnrollment(code, studentName, planName, startDate, endDate, durationMonths, totalPrice, pendingAmount, status);
                                }
                            }
                            ui.showMessage("Fim da lista de matriculas ativas."); 
                    }
                break;


                case ALL_ENROLLMENTS: 
                    ArrayList<Enrollment> allEnrollments = fitManager.listEnrollments();
                    if(allEnrollments.isEmpty()){
                        ui.showError("Não há matriculas cadastradas.");
                    } else {
                        ui.showMessage("Histórico de Matrículas:");
                        for(Enrollment enrollment : allEnrollments){
                                
                                int code = enrollment.getCode();
                                String studentName = enrollment.getStudent().getName();
                                String planName = enrollment.getPlan().getName();
                                double totalPrice = enrollment.getTotalPrice();
                                double pendingAmount = enrollment.getTotalPrice() - enrollment.calculateTotalPaid();
                                LocalDate startDate = enrollment.getStartDate();
                                LocalDate endDate = enrollment.getEndDate();
                                int durationMonths = enrollment.getDurationMonths();
                                String status = enrollment.getStatus().toString();

                                if(status.equals("CANCELLED")){
                                    endDate = enrollment.getCancellationDate();
                                    String cancellationReason = enrollment.getCancellationReason();
                                    ui.showCancelledEnrollment(code, studentName, planName, startDate, endDate, durationMonths, totalPrice, status, cancellationReason);
                                }else{
                                    ui.showEnrollment(code, studentName, planName, startDate, endDate, durationMonths, totalPrice, pendingAmount, status);
                                }

                            }
                            
                            ui.showMessage("Fim da lista de matriculas ativas."); 
                    }
                    
                break;

            }
        }while(optionSelected != ReportsMenuEnum.BACK);
    }

}