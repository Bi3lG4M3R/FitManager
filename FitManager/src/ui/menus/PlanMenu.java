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
            menuOptions[i] = PlanMenuEnum.values()[i].getOptionDescription();
        }



        do{ 
            
            do{
                ui.showMenu("GERENCIAR PLANOS", menuOptions);
                optionSelected = PlanMenuEnum.selectFromInt(ui.getInputInt("Selecione uma opção: "));
                if(optionSelected == null)
                    ui.showError("Opção inexistente. Selecione uma das opções acima.");  
            }while(optionSelected == null);


            switch(optionSelected){


                case REGISTER_NEW_PLAN:
                    planName = ui.getInput("Digite o nome do plano: ");
                    String planDescription = ui.getInput("Digite a descrição do plano:");
                    PlanType planType = ui.getInputPlanType("Selecione o tipo do plano: ");
                    double planPrice = ui.getInputDouble("Digite o preço do plano: ");
                    int planDuration = ui.getInputInt("Digite a duração minima do plano (em meses): ");

                    OperationResult resultRegister = fitManager.registerPlan(planName, planDescription, planType, planDuration, planPrice);
                    if(resultRegister.isSuccess())
                        ui.showMessage(resultRegister.getMessage());
                     else 
                        ui.showError("Erro ao registrar plano: " + resultRegister.getMessage());
                    
                break;




                case CHECK_BY_NAME:
                    String planNameToSearch = ui.getInput("Digite o nome do plano a ser consultado: ");
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
                    planName = ui.getInput("Digite o nome do plano a ser alterado: ");
                    double newPrice = ui.getInputDouble("Digite o novo preço do plano: ");
                    OperationResult resultUpdate = fitManager.updatePlanPrice(planName, newPrice);
                    if(resultUpdate.isSuccess())
                        ui.showMessage(resultUpdate.getMessage());
                    else
                        ui.showError("Erro ao atualizar preço do plano: " + resultUpdate.getMessage());
                break;





                case VIEW_ALL:
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

                            ui.showPlan(planNameList, planDescriptionList, planTypeList, planMinDurationList, planPricePerMonthList);
                        }
                    }
                break;


                case BACK:
                    ui.showMessage("Voltando ao menu principal...");
                break;
            }
        }while(optionSelected != PlanMenuEnum.BACK);
    }

}



