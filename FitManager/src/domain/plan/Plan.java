package domain.plan;

import domain.Enrollment;

public abstract class Plan {
    private String name;
    private String description;
    private int minDurationMonths;
    private double pricePerMonth;
    
    public Plan(String name, String description, int minDurationMonths, double pricePerMonth) {
        this.name = name;
        this.description = description;
        this.minDurationMonths = minDurationMonths;
        this.pricePerMonth = pricePerMonth;
    }
    
    /*setters*/
    public void setName(String name) { this.name = name; }
    
    public void setDescription(String description) { this.description = description; }

    public void setMinDurationMonths(int minDurationMonths) { this.minDurationMonths = minDurationMonths; }

    public void updatePrice(double newPrice) { this.pricePerMonth = newPrice; }

    /*getters*/
    public String getName() { return name; }

    public String getDescription() { return description; }

    public abstract PlanType getType();

    public int getMinDurationMonths() { return minDurationMonths; }

    public double getPricePerMonth() { return pricePerMonth; }
    
    public abstract double calculateTotalPrice(int months);
    
    public double getCancellationFee(Enrollment enrollment) { return 0.0; };
}