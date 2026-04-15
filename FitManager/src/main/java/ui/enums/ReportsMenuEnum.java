package ui.enums;

/* Menu de Relatórios */

public enum ReportsMenuEnum {

    ACTIVE_ENROLLMENTS_STUDENTS (1, "Alunos com matrículas ativas"),
    PENDING_PAYMENTS_ENROLLMENTS (2, "Listar matrículas com saldo pendente"),
    ALL_ENROLLMENTS (3, "Consultar aluno por CPF"),
    BACK (4, "Voltar ao menu principal");



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