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
        return this.getPricePerMonth() * months * 0.90;
    }
    
    @Override
    public double getCancellationFee(Enrollment enrollment){
        int remainingMonths = enrollment.getDurationMonths() - (int) enrollment.getMonthsUsed();
        
        if (remainingMonths <= 0) return 0.0;
        
        if (remainingMonths < 3) return this.getPricePerMonth();
        
        return enrollment.calculateBalance() / 2;
    }
}