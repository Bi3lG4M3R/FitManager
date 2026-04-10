package ui.enums;

/* Menu principal */

public enum MainMenuEnum {

    MANAGE_STUDENTS (1, "Gerenciar Alunos"),
    MANAGE_PLANS (2, "Gerenciar Planos"),
    MANAGE_ENROLLMENTS (3, "Gerenciar Inscrições"),
    REPORTS (4, "Relatórios / Listagens"),
    EXIT (0, "Sair");


    private final int option;
    private final String description;
    
    MainMenuEnum(int option, String description) {
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

    // Metodo para selecionar uma opcao do menu a partir de um int
    public static MainMenuEnum chooseFromInt(int option) {
        for (MainMenuEnum menuOp : MainMenuEnum.values()) {
            if (menuOp.getOption() == option) {
                return menuOp;
            }
        }
        return null; // caso a opcao nao exista devolve null
    }

}
