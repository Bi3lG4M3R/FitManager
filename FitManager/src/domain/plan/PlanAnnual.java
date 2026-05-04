package domain.plan;

import domain.Enrollment;

public class PlanAnnual extends Plan {
    
    public PlanAnnual(String name, String description, int minDurationMonths, double pricePerMonth){
        super(name, description, minDurationMonths, pricePerMonth);
    }
    
    @Override
    public PlanType getType(){ return PlanType.ANNUAL; }
    
    @Override
    public double calculateTotalPrice(int months){
        if(months == super.getMinDurationMonths()){
            return this.getPricePerMonth() * months;
        }
        
        return this.getPricePerMonth() * months * 0.85;
    }
    
    @Override
    public double getCancellationFee(Enrollment enrollment){
        if (enrollment.getMonthsUsed() < enrollment.getDurationMonths()/2.0) return enrollment.getTotalPrice() * 0.2;
        
        return 0.0;
    }
}