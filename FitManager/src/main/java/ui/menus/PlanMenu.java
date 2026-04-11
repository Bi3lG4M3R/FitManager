package ui.menus;

import ui.UserInterface;
import ui.enums.PlanMenuEnum;
import application.FitManager;

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

        //  String array para armazenar as opções dos menus
        String[] menuOptions = new String[PlanMenuEnum.values().length];

        //  Construção do array pegando as descrições do do enum
        for(int i = 0; i < PlanMenuEnum.values().length; i++){
            menuOptions[i] = PlanMenuEnum.values()[i].getOptionDescription();
        }

        do{ 
            ui.showMenu("GERENCIAR PLANOS", menuOptions);
            ui.showMessage("Selecione uma opção: ");

            optionSelected = Integer.parseInt(ui.getInput());

            switch(PlanMenuEnum.selectFromInt(optionSelected)){

                case REGISTER_PLAN:
                    fitManager.registerPlan();
                break;

                case SEARCH_BY_ID:
                    fitManager.findPlanById();
                break;

                case EDIT_PLAN:
                    fitManager.updatePlan();
                break;

                case DELETE_PLAN:
                    fitManager.removePlan();
                break;

                case VIEW_ALL_PLANS:
                    fitManager.listPlans();
                break;

                case BACK:
                    ui.showMessage("Voltando ao menu principal...");
                break;
            }
        }while(optionSelected != PlanMenuEnum.BACK.getOptionNumber());
    }
}
