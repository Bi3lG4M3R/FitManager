package domain.plan;

public class Plan {
    String name;
    String description;
    PlanType type;
    int minDurationMonths;
    double pricePerMonth;

    /*contrutores*/
    public Plan() {
        this.name = "";
        this.description = "";
        this.type = PlanType.MONTHLY;
        this.minDurationMonths = 3;
        this.pricePerMonth = 150.00;
    }
    
    public Plan(String name, String description, PlanType type, int minDurationMonths, double pricePerMonth) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.minDurationMonths = minDurationMonths;
        this.pricePerMonth = pricePerMonth;
    }
    
    /*setters*/
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(PlanType type) {
        this.type = type;
    }

    public void setMinDurationMonths(int minDurationMonths) {
        this.minDurationMonths = minDurationMonths;
    }

    public void setPricePerMonth(double pricePerMonth) {
        this.pricePerMonth = pricePerMonth;
    }

    /*getters*/
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public PlanType getType() {
        return type;
    }

    public int getMinDurationMonths() {
        return minDurationMonths;
    }

    public double getPricePerMonth() {
        return pricePerMonth;
    }
    
    
}
