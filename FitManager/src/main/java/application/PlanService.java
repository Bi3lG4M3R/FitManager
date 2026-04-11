package application;

import application.OperationResult;
import domain.plan.Plan;
import domain.plan.PlanType;
import java.time.LocalDate;
import java.util.ArrayList;

public class PlanService {
    private ArrayList<Plan> plans;

    public PlanService() {
         this.plans = new ArrayList<>();
    }
    
    public Plan findByName(String name){
        for(Plan item : plans){
            if(item.getName().equals(name)){
                return item;
            }
        }
        return null;
    }
    
    public boolean nameExists(String name){ return findByName(name) != null; }
    
    public OperationResult registerPlan(String name, String description, PlanType type, int minDurationMonths, double pricePerMonth) {
        LocalDate date = LocalDate.now();
        if(name.isEmpty() || nameExists(name)){
            return new OperationResult(false, "Nome inválido ou já existente.", date);
        }
        if(description.isEmpty()){
            return new OperationResult(false, "Descrição inválida.", date);
        }
        if(type == null){
            return new OperationResult(false, "Tipo inválido.", date);
        }
        if(minDurationMonths <= 0){
            return new OperationResult(false, "Duração mínima inválida.", date);
        }
        if(pricePerMonth <= 0){
            return new OperationResult(false, "Preço inválido.", date);
        }

        Plan temporary = new Plan(name, description, type, minDurationMonths, pricePerMonth);
        this.plans.add(temporary);
        return new OperationResult(true, "O plano " + name + " foi criado com sucesso.", date);
    }
}