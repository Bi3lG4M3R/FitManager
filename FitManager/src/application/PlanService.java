package application;

import java.util.ArrayList;

import domain.plan.Plan;
import domain.plan.AnnualPlan;
import domain.plan.MonthlyPlan;
import domain.plan.QuarterlyPlan;
import domain.plan.SemiAnnualPlan;
import domain.plan.PlanType;

public class PlanService {
    private ArrayList<Plan> plans;

    public PlanService() {
         this.plans = new ArrayList<>();
    }
    
    public ArrayList<Plan> listPlans(){
        return plans;
    }
    
    public Plan findByName(String name){
        for (Plan item : this.plans) {
            if (name != null && name.equals(item.getName())){
                return item;
            }
        }
        return null;
    }
    
    public boolean nameExists(String name){ return findByName(name) != null; }
    
    public OperationResult registerPlan(String name, String description, PlanType type, int minDurationMonths, double pricePerMonth) {
        if(name.isBlank() || nameExists(name)){
            return new OperationResult(false, "Nome inválido ou já existente.");
        }
        if(description.isEmpty()){
            return new OperationResult(false, "Descrição inválida.");
        }
        if(type == null){
            return new OperationResult(false, "Tipo inválido.");
        }
        if(minDurationMonths <= 0){
            return new OperationResult(false, "Duração mínima inválida.");
        }
        if(pricePerMonth <= 0){
            return new OperationResult(false, "Preço inválido.");
        }
        
        Plan temporary;
        switch(type){
            case MONTHLY:
                if(minDurationMonths < 1){
                    return new OperationResult(false, "Duração mínima inválida.");
                }
                temporary = new MonthlyPlan(name, description, minDurationMonths, pricePerMonth);
            break;
            
            case QUARTERLY:
                if(minDurationMonths < 3){
                    return new OperationResult(false, "Duração mínima inválida.");
                }
                temporary = new QuarterlyPlan(name, description, minDurationMonths, pricePerMonth);
            break;
            
            case SEMI_ANNUAL:
                if(minDurationMonths < 6){
                    return new OperationResult(false, "Duração mínima inválida.");
                }
                temporary = new SemiAnnualPlan(name, description, minDurationMonths, pricePerMonth);
            break;
            
            case ANNUAL:
                if(minDurationMonths < 12){
                    return new OperationResult(false, "Duração mínima inválida.");
                }
                temporary = new AnnualPlan(name, description, minDurationMonths, pricePerMonth);
            break;
            
            default:
                return new OperationResult(false, "Tipo inválido");
        }
        
        this.plans.add(temporary);
        return new OperationResult(true, "O plano " + name + " foi criado com sucesso.", temporary);
    }
    
    public OperationResult updatePrice(String name, double newPrice){
        if(newPrice <= 0){
            return new OperationResult(false, "Preço inválido.");
        }
        
        Plan planNamed = this.findByName(name);
        
        if(planNamed != null){
            planNamed.updatePrice(newPrice);
            return new OperationResult(true, "O valor do plano " + planNamed.getName() + " foi alterado com sucesso.", planNamed);
        }
        return new OperationResult(false, "O plano " + name + " não foi localizado."); 
    }
}