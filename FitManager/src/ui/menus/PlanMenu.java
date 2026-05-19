package ui.menus;

import java.util.ArrayList;

import application.FitManager;
import application.OperationResult;
import domain.plan.Plan;
import domain.plan.PlanType;
import ui.UserInterface;
import ui.enums.PlanMenuEnum;

public class PlanMenu{
    
    private final UserInterface ui;
    private final FitManager fitManager;

    // Construtor
    public PlanMenu(UserInterface ui, FitManager fitManager){
        this.ui = ui;
        this.fitManager = fitManager;
    }



    public void run(){
        PlanMenuEnum optionSelected;
        
        String planName;
        //  String array para armazenar as opções dos menus
        String[] menuOptions = new String[PlanMenuEnum.values().length];

        //  Construção do array a partir das descrições do do enum
        for(int i = 0; i < PlanMenuEnum.values().length; i++){
            menuOptions[i] = PlanMenuEnum.values()[i].getOptionNumber() + " - " + PlanMenuEnum.values()[i].getOptionDescription();
        }

        do{ 
            
            do{
                int option = ui.showMenu("GERENCIAR PLANOS", menuOptions, "Selecione uma opção: ");
                if(option == 0){
                    optionSelected = PlanMenuEnum.BACK;
                } else {
                    optionSelected = PlanMenuEnum.selectFromInt(option);
                    if(optionSelected == null)
                        ui.showError("Opção inexistente. Selecione uma das opções acima.");  
                }
            }while(optionSelected == null);

            ArrayList<Plan> planList = fitManager.listPlans();

            switch(optionSelected){

                case REGISTER_NEW_PLAN:
                    planName = ui.getInput("Digite o nome do plano: ");
                    if(planName == null) break;
                    String planDescription = ui.getInput("Digite a descrição do plano:");
                    if(planDescription == null) break;
                    PlanType planType = ui.getInputPlanType("Selecione o tipo do plano: ");
                    if (planType == null) break;
                    double planPrice = ui.getInputDouble("Digite o preço por mês do plano: ");
                    if(planPrice < 0) break;
                    int planDuration = ui.getInputInt("Digite a duração mínima do plano (em meses): ");
                    if(planDuration < 0) break;

                    OperationResult resultRegister = fitManager.registerPlan(planName, planDescription, planType, planDuration, planPrice);
                    if(resultRegister.isSuccess())
                        ui.showMessage(resultRegister.getMessage());
                     else 
                        ui.showError("Erro ao registrar plano: " + resultRegister.getMessage());
                break;

                case CHECK_BY_NAME:
                    String planNameToSearch = ui.getInput("Digite o nome do plano a ser consultado: ");
                    if(planNameToSearch == null) break;
                    Plan planFound = fitManager.findPlanByName(planNameToSearch);
                    if(planFound == null){
                        ui.showError("Plano não encontrado.");
                    } else {
                        String planNameList = planFound.getName();
                        String planDescriptionList = planFound.getDescription();
                        String planTypeList = planFound.getType().getDescription(); 
                        int planMinDurationList = planFound.getMinDurationMonths();
                        double planPricePerMonthList = planFound.getPricePerMonth();

                        ui.showPlan(planNameList, planDescriptionList, planTypeList, planMinDurationList, planPricePerMonthList);
                    }
                break;

                case CHANGE_PRICE:
                    planName = ui.getInput(formatPlanList(planList) + "\nDigite o nome do plano a ser alterado: ");
                    if(planName == null) break;
                    double newPrice = ui.getInputDouble("Digite o novo preço do plano: ");
                    if(newPrice < 0) break;
                    OperationResult resultUpdate = fitManager.updatePlanPrice(planName, newPrice);
                    if(resultUpdate.isSuccess())
                        ui.showMessage(resultUpdate.getMessage());
                    else
                        ui.showError("Erro ao atualizar preço do plano: " + resultUpdate.getMessage());
                break;

                case VIEW_ALL:
                    if(planList.isEmpty()){
                        ui.showMessage("Nenhum plano cadastrado.");
                    } else {
                        String result = "Histórico de Planos:\n\n";

                        for (Plan plan : planList) {
                            result = result + "Nome: " + plan.getName() + "\n";
                            result = result + "Descrição: " + plan.getDescription() + "\n";
                            result = result + "Tipo: " + plan.getType().getDescription() + "\n";
                            result = result + "Duração Mínima: " + plan.getMinDurationMonths() + " meses\n";
                            result = result + "Preço Mensal: R$ " + plan.getPricePerMonth() + "\n";
                            result = result + "----------------------------\n";
                        }

                        ui.showMessage(result);
                    }
                break;

                case BACK:
                    ui.showMessage("Voltando ao menu principal...");
                break;
            }
        }while(optionSelected != PlanMenuEnum.BACK);
    }
    
    public static String formatPlanList(ArrayList<Plan> planList) {
        if (planList.isEmpty()) {
            return "Nenhum plano cadastrado.";
        }

        String result = "Histórico de Planos:\n\n";

        for (Plan plan : planList) {
            result = result + "Nome: " + plan.getName() + "\n";
            result = result + "Preço Mensal: R$ " + String.format("%.2f", plan.getPricePerMonth()) + "\n";
            result = result + "----------------------------\n";
        }

        return result;
    }

}



