package domain.plan;

import domain.Enrollment;

public class MonthlyPlan extends Plan {
    
    public MonthlyPlan(String name, String description, int minDurationMonths, double pricePerMonth){
        super(name, description, minDurationMonths, pricePerMonth);
    }
    
    @Override
    public PlanType getType(){ return PlanType.MONTHLY; }
    
    @Override
    public double calculateTotalPrice(int months){
        return this.getPricePerMonth() * months;
    }
    
    @Override
    public double getCancellationFee(Enrollment enrollment){
        return 0.0;
    }
}
