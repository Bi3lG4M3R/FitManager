package userinterface;

/* Menu de Relatórios */

public enum ReportsMenuEnum {

    VIEW_ACTIVE_STUDENTS (1, "Alunos com matrícula ativa"),
    PENDING_PAYMENTS_ENROLLMENTS (2, "Matrículas com saldo pendente"),
    VIEW_ALL_ENROLLMENTS (3, "Todas as matrículas"),
    EXIT (0, "Sair");


    private final int option;
    private final String description;
    
    ReportsMenuEnum(int option, String description) {
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