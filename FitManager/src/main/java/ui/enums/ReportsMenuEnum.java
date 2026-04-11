package ui.enums;

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
    public int getOptionNumber() {
        return option;
    }

    public String getOptionDescription() {
        return description;
    }

    // Metodo para selecionar uma opcao do menu a partir de um int
    public static ReportsMenuEnum selectFromInt(int option) {
        for (ReportsMenuEnum menuOp : ReportsMenuEnum.values()) {
            if (menuOp.getOptionNumber() == option) {
                return menuOp;
            }
        }
        return null; // caso a opcao nao exista devolve null
    }

}