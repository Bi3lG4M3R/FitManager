package ui.enums;

/* Menu principal */

public enum MainMenuEnum {

    MANAGE_STUDENTS (1, "Gerenciar Alunos"),
    MANAGE_PLANS (2, "Gerenciar Planos"),
    MANAGE_ENROLLMENTS (3, "Gerenciar Matriculas"),
    REPORTS (4, "Relatórios / Listagens"),
    EXIT (5, "Sair");


    private final int option;
    private final String description;
    
    MainMenuEnum(int option, String description) {
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


    public static MainMenuEnum selectFromInt(int option) {
        for (MainMenuEnum menuOp : MainMenuEnum.values()) {
            if (menuOp.getOptionNumber() == option) {
                return menuOp;
            }
        }
        return null; // caso a opcao nao exista devolve null
    }

}
