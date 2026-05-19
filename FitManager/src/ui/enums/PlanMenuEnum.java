package ui.enums;

/* Menu para gerenciar planos */

public enum PlanMenuEnum {

    REGISTER_NEW_PLAN (1, "Cadastrar novo plano"),
    CHECK_BY_NAME (2, "Consultar por nome"),
    CHANGE_PRICE (3, "Alterar preço"),
    VIEW_ALL (4, "Listar todos"),
    BACK (0, "Voltar ao menu principal");


    private final int option;
    private final String description;
    
    PlanMenuEnum(int option, String description) {
        this.option = option;
        this.description = description;
    }


    // Getters
    public int getOptionNumber() {
        return option;
    }

    public String getOptionDescription() {
        return description;
    }

    // Metodo para selecionar uma opcao do menu a partir de um int
    public static PlanMenuEnum selectFromInt(int option) {
        for (PlanMenuEnum menuOp : PlanMenuEnum.values()) {
            if (menuOp.getOptionNumber() == option) {
                return menuOp;
            }
        }
        return null; // caso a opcao nao exista devolve null
    }

}
