package ui.enums;

/* Menu de Relatórios */

public enum ReportsMenuEnum {


    VIEW_PENDING_PAYMENTS_ENROLLMENTS (1, "Listar matrículas com saldo pendente"),
    CONSULT_STUDENT_BY_CPF (2, "Consultar aluno por CPF"),
    CONSULT_PLAN_BY_NAME (3, "Consultar plano por nome"),
    CONSULT_ACTIVE_ENROLLMENTS_BY_STUDENT_CPF (4, "Consultar matrícula ativas de um aluno"),
    BACK (5, "Voltar ao menu principal");



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