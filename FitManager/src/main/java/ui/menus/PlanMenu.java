package ui.menus;

import ui.UserInterface;
import ui.enums.PlanMenuEnum;
import domain.plan.PlanType;
import application.FitManager;
import application.OperationResult;

public class PlanMenu{
    
    private UserInterface ui;
    private FitManager fitManager;

    // Construtor
    public PlanMenu(UserInterface ui, FitManager fitManager){
        this.ui = ui;
        this.fitManager = fitManager;
    }



    public void run(){
        int optionSelected;
        
        String planName;
        //  String array para armazenar as opções dos menus
        String[] menuOptions = new String[PlanMenuEnum.values().length];

        //  Construção do array a partir das descrições do do enum
        for(int i = 0; i < PlanMenuEnum.values().length; i++){
            menuOptions[i] = PlanMenuEnum.values()[i].getOptionDescription();
        }



        do{ 
            ui.showMenu("GERENCIAR PLANOS", menuOptions);

            optionSelected = ui.getInputInt("Selecione uma opção: ");

            switch(PlanMenuEnum.selectFromInt(optionSelected)){


                case REGISTER_NEW_PLAN:
                    planName = ui.getInput("Digite o nome do plano: ");

                    String planDescription = ui.getInput("Digite a descrição do plano:");

                    ui.showMenu("Tipos de planos disponiveis", new String[] {
                        PlanType.MONTHLY.getValueOpcao() + " - " + PlanType.MONTHLY.getDescription(),
                        PlanType.QUARTERLY.getValueOpcao() + " - " + PlanType.QUARTERLY.getDescription(),
                        PlanType.SEMI_ANNUAL.getValueOpcao() + " - " + PlanType.SEMI_ANNUAL.getDescription(),
                        PlanType.ANNUAL.getValueOpcao() + " - " + PlanType.ANNUAL.getDescription()
                    });

                    int selectedPlan = ui.getInputInt("Selecione o tipo do plano: ");
                    PlanType planType = PlanType.selectFromInt(selectedPlan);

                    double planPrice = ui.getInputDouble("Digite o preço do plano: ");

                    int planDuration = ui.getInputInt("Digite a duração minima do plano (em meses): ");

                    OperationResult result = new fitManager.registerPlan(planName, planDescription, planType, planPrice, planDuration);
                break;




                case CHECK_BY_NAME:
                    planName = ui.getInput("Digite o nome do plano a ser consultado: ");
                    fitManager.findPlanByName(planName);
                break;





                case CHANGE_PRICE:
                    planName = ui.getInput("Digite o nome do plano a ser alterado: ");
                    double newPrice = ui.getInputDouble("Digite o novo preço do plano: ");
                    fitManager.updatePlan(planName, newPrice);
                break;





                case VIEW_ALL:
                    fitManager.listPlans();
                break;





                case BACK:
                    ui.showMessage("Voltando ao menu principal...");
                break;
            }
        }while(optionSelected != PlanMenuEnum.BACK.getOptionNumber());
    }

}



