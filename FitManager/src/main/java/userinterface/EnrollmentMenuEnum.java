package userinterface;

/* Menu para gerenciar matriculas */

public enum EnrollmentMenuEnum {

    REGISTER_ENROLLMENT (1, "Realizar matrícula"),
    REGISTER_PAYMENT (2, "Registrar pagamento"),
    CANCEL_ENROLLMENT (3, "Cancelar matrícula"),
    CHECK_ACTIVE_ENROLLMENT (4, "Consultar matrícula ativa"),
    VIEW_HISTORY (5, "Listar histórico"),
    EXIT (0, "Sair");


    private int option;
    private String description;
    
    EnrollmentMenuEnum(int option, String description) {
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