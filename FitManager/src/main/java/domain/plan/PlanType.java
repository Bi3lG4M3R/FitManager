package domain.plan;

public enum PlanType {
    MONTHLY(1, "Mensal"),
    QUARTERLY(2, "Trimestral"),
    SEMI_ANNUAL(3, "Semestral"),
    ANNUAL(4, "Anual");
    
    private final int option;
    private final String description;
    
    PlanType(int option, String description){
        this.option = option;
        this.description = description;
    }
    
    public int getValueOpcao(){ return option; }

    public String getDescription() { return description; }
}