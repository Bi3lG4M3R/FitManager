package domain.plan;

public class PlanMonthly extends Plan {
    
    public PlanMonthly(String name, String description, int minDurationMonths, double pricePerMonth){
        super(name, description, minDurationMonths, pricePerMonth);
    }
    
    @Override
    public PlanType getType(){ return PlanType.MONTHLY; }
    
    @Override
    public double calculateTotalPrice(int months){
        return this.getPricePerMonth() * months;
    }
}
