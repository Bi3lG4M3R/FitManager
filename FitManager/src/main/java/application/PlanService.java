package application;

import application.OperationResult;
import domain.plan.Plan;
import domain.plan.PlanType;
import java.time.LocalDate;
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
        if(name.isEmpty() || nameExists(name)){
            return new OperationResult(false, "Nome inválido ou já existente.", LocalDate.now());
        }
        if(description.isEmpty()){
            return new OperationResult(false, "Descrição inválida.", LocalDate.now());
        }
        if(type == null){
            return new OperationResult(false, "Tipo inválido.", LocalDate.now());
        }
        if(minDurationMonths <= 0){
            return new OperationResult(false, "Duração mínima inválida.", LocalDate.now());
        }
        if(pricePerMonth <= 0){
            return new OperationResult(false, "Preço inválido.", LocalDate.now());
        }

        Plan temporary = new Plan(name, description, type, minDurationMonths, pricePerMonth);
        this.plans.add(temporary);
        return new OperationResult(true, "O plano " + name + " foi criado com sucesso.", LocalDate.now());
    }
    
    public OperationResult updatePrice(String name, double newPrice){
        if(newPrice <= 0){
            return new OperationResult(false, "Preço inválido.", LocalDate.now());
        }
        
        Plan planNamed = PlanService.findByName(name);
        
        if(planNamed != null){
            planNamed.updatePrice(newPrice);
            return new OperationResult(true, "O valor do plano " + planNamed.getName() + "foi alterado com sucesso.", LocalDate.now());
        }
        return new OperationResult(false, "O plano " + name + " não foi localizado.", LocalDate.now()); 
    }
}