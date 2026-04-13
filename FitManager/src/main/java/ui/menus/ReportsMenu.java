package ui.menus;

import ui.UserInterface;
import ui.enums.ReportsMenuEnum;
import domain.plan.PlanType;
import domain.plan.Plan;

import java.util.ArrayList;

import application.FitManager;
import application.OperationResult;

public class ReportsMenu{
    
    private final UserInterface ui;
    private final FitManager fitManager;

    // Construtor
    public ReportsMenu(UserInterface ui, FitManager fitManager){
        this.ui = ui;
        this.fitManager = fitManager;
    }



    public void run(){
        int optionSelected;
        
        String planName;
        //  String array para armazenar as opções dos menus
        String[] menuOptions = new String[ReportsMenuEnum.values().length];

        //  Construção do array a partir das descrições do do enum
        for(int i = 0; i < ReportsMenuEnum.values().length; i++){
            menuOptions[i] = ReportsMenuEnum.values()[i].getOptionDescription();
        }



        do{ 
            ui.showMenu("RELATÓRIOS", menuOptions);

            optionSelected = ui.getInputInt("Selecione uma opção: ");

            switch(ReportsMenuEnum.selectFromInt(optionSelected)) {


                case VIEW_ALL_PLANS:
                    ArrayList<Plan> planList = fitManager.listPlans();
                    if(planList.isEmpty()){
                        ui.showMessage("Nenhum plano cadastrado.");
                    } else {
                        ui.showMessage("Histórico de Planos:");
                        for(Plan plan : planList){
                            String planNameList = plan.getName();
                            String planDescriptionList = plan.getDescription();
                            String planTypeList = plan.getType().getDescription(); 
                            int planMinDurationList = plan.getMinDurationMonths();
                            double planPricePerMonthList = plan.getPricePerMonth();

                            ui.showMessage(
                                "Nome do plano - " + planNameList + "\n" +
                                "Descrição: " + planDescriptionList + "\n" +
                                "Tipo: " + planTypeList + "\n" +
                                "Duração mínima: " + planMinDurationList + " meses\n" +
                                "Preço por mês: R$ " + String.format("%.2f", planPricePerMonthList) + "\n" +
                                "----------------------------------"
                            );
                            

                        }
                    }
                break;


                case VIEW_PENDING_PAYMENTS_ENROLLMENTS:
                    
                break;

                case CONSULT_STUDENT_BY_CPF:
                break;

                case CONSULT_PLAN_BY_NAME:
                break;

                case CONSULT_ACTIVE_ENROLLMENTS_BY_STUDENT_CPF:
                break;


                case BACK:
                    ui.showMessage("Voltando ao menu principal...");
                break;
            }
        }while(optionSelected != ReportsMenuEnum.BACK.getOptionNumber());
    }

}