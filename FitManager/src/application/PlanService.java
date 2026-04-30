package application;

import domain.plan.*;
import java.util.ArrayList;

public class PlanService {
    private static ArrayList<Plan> plans;

    public PlanService() {
         PlanService.plans = new ArrayList<>();
    }
    
    public ArrayList<Plan> listPlans(){
        return plans;
    }
    
    public static Plan findByName(String name){
        for (Plan item : plans) {
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
                temporary = new PlanMonthly(name, description, minDurationMonths, pricePerMonth);
            break;
            
            case QUARTERLY:
                temporary = new PlanQuarterly(name, description, minDurationMonths, pricePerMonth);
            break;
            
            case SEMI_ANNUAL:
                temporary = new PlanSemiAnnual(name, description, minDurationMonths, pricePerMonth);
            break;
            
            case ANNUAL:
                temporary = new PlanAnnual(name, description, minDurationMonths, pricePerMonth);
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
        
        Plan planNamed = PlanService.findByName(name);
        
        if(planNamed != null){
            planNamed.updatePrice(newPrice);
            return new OperationResult(true, "O valor do plano " + planNamed.getName() + "foi alterado com sucesso.", planNamed);
        }
        return new OperationResult(false, "O plano " + name + " não foi localizado."); 
    }
}