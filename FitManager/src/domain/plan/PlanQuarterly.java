package domain.plan;

public class PlanQuarterly extends Plan {
    
    public PlanQuarterly(String name, String description, int minDurationMonths, double pricePerMonth){
        super(name, description, minDurationMonths, pricePerMonth);
    }
    
    @Override
    public PlanType getType(){ return PlanType.QUARTERLY; }
    
    @Override
    public double calculateTotalPrice(int months){
        return this.getPricePerMonth() * months * 0.98;
    }
}
