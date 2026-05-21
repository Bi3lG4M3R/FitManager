package domain.plan;

public class PlanSemiAnnual extends Plan {
    
    public PlanSemiAnnual(String name, String description, int minDurationMonths, double pricePerMonth){
        super(name, description, minDurationMonths, pricePerMonth);
    }
    
    @Override
    public PlanType getType(){ return PlanType.SEMI_ANNUAL; }
    
    @Override
    public double calculateTotalPrice(int months){
        if(months == super.getMinDurationMonths()){
            return this.getPricePerMonth() * months;
        }
        
        return this.getPricePerMonth() * months * 0.90;
    }
}