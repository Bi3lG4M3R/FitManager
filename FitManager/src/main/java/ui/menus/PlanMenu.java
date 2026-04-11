package ui.menus;

import ui.UserInterface;
import ui.enums.PlanMenuEnum;
import application.FitManager;
import domain.PlanType;

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

            switch(optionSelected){
                // realizar essa alteração nos demais menus
                case PlanMenuEnum.REGISTER_NEW_PLAN.getOptionNumber():
                    processRegisterNewPlan();
                break;

                case PlanMenuEnum.CHECK_BY_NAME.getOptionNumber():
                    processCheckByName();
                break;

                case PlanMenuEnum.CHANGE_PRICE.getOptionNumber():
                    processChangePrice();
                break;

                // Sugestão de implementação:
                // Metodo para excluir um plano existente
                /*  case PlanMenuEnum.DELETE_PLAN.getOptionNumber():
                    fitManager.removePlan();
                break;  */

                case PlanMenuEnum.VIEW_ALL.getOptionNumber():
                    fitManager.listPlans();
                break;

                case PlanMenuEnum.BACK.getOptionNumber():
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

        ui.showMenu("Tipos de planos disponiveis", new String[] {
            "1 - " + PlanType.MONTHLY.getDescription(),
            "2 - " + PlanType.QUARTERLY.getDescription(),
            "3 - " + PlanType.SEMI_ANUAL.getDescription(),
            "4 - " + PlanType.ANNUAL.getDescription()
        });

        ui.showMessage("Selecione o tipo do plano:");
        int selectedPlan = Integer.parseInt(ui.getInput());
        PlanType planType = PlanType.selectFromInt(selectedPlan);

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


