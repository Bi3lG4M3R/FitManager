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
    
    public int getValueOption(){ return option; }

    public String getDescription() { return description; }

    public static PlanType selectFromInt(int option) {
        for (PlanType planOption : PlanType.values()) {
            if (planOption.getValueOption() == option){
                return planOption;
            }
        }
        return null; // caso a opcao nao exista devolve null
    }
}