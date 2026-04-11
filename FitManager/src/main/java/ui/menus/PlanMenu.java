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

                case REGISTER_NEW_PLAN:
                    processRegisterNewPlan();
                break;

                case CHECK_BY_NAME:
                    processCheckByName();
                break;

                case CHANGE_PRICE:
                    processChangePrice();
                break;

                // Sugestão de implementação:
                // Metodo para excluir um plano existente
                /*  case DELETE_PLAN:
                    fitManager.removePlan();
                break;  */

                case VIEW_ALL:
                    fitManager.listPlans();
                break;

                case BACK:
                    ui.showMessage("Voltando ao menu principal...");
                break;
            }
        }while(optionSelected != PlanMenuEnum.BACK.getOptionNumber());
    }

    private void processRegisterNewPlan(){
        ui.showMessage("Digite o nome do plano:");
        String planName = ui.getInput();

        ui.showMessage("Digite a descrição do plano:");
        String planDescription = ui.getInput();

        ui.showMessage("Digite o tipo do plano:");
        int planType = Integer.parseInt(ui.getInput());
        // adequar variavel acima para enviar o enum correspondente ao tipo do plano

        ui.showMessage("Digite o preço do plano:");
        double planPrice = Double.parseDouble(ui.getInput());

        ui.showMessage("Digite a duração minima do plano (em meses):");
        int planDuration = Integer.parseInt(ui.getInput());

        fitManager.registerPlan(planName, planDescription, planType, planPrice, planDuration);
    }



    private void processCheckByName(){
        ui.showMessage("Digite o nome do plano a ser consultado: ");
        String planName = ui.getInput();
        fitManager.findPlanByName(planName);
    }


    private void processChangePrice(){
        ui.showMessage("Digite o nome do plano a ser alterado: ");
        String planName = ui.getInput();

        ui.showMessage("Digite o novo preço do plano: ");
        double newPrice = Double.parseDouble(ui.getInput());

        fitManager.updatePlan(planName, newPrice);
}





}


