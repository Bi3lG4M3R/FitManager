package userinterface;

/* Menu para gerenciar planos */

public enum PlanMenuEnum {

    REGISTER_NEW_PLAN (1, "Cadastrar novo plano"),
    CHECK_BY_NAME (2, "Consultar por nome"),
    CHANGE_PRICE (3, "Alterar preço"),
    VIEW_ALL (4, "Listar todos"),
    EXIT (0, "Sair");


    private int option;
    private String description;
    
    PlanMenuEnum(int option, String description) {
        this.option = option;
        this.description = description;
    }


    // Getters
    public int getOption() {
        return option;
    }

    public String getDescription() {
        return description;
    }



}
