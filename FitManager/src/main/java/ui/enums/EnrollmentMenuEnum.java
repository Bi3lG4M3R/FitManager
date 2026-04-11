package ui.enums;

/* Menu para gerenciar matriculas */

public enum EnrollmentMenuEnum {

    REGISTER_ENROLLMENT (1, "Realizar matrícula"),
    REGISTER_PAYMENT (2, "Registrar pagamento"),
    CANCEL_ENROLLMENT (3, "Cancelar matrícula"),
    CHECK_ACTIVE_ENROLLMENT (4, "Consultar matrícula ativa"),
    VIEW_HISTORY (5, "Listar histórico"),
    EXIT (0, "Sair");


    private final int option;
    private final String description;
    
    EnrollmentMenuEnum(int option, String description) {
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
    public static EnrollmentMenuEnum selectFromInt(int option) {
        for (EnrollmentMenuEnum menuOp : EnrollmentMenuEnum.values()) {
            if (menuOp.getOptionNumber() == option) {
                return menuOp;
            }
        }
        return null; // caso a opcao nao exista devolve null
    }

}