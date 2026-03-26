package userinterface;

/* Menu principal do programa */

public enum MainMenu {

    MANAGE_STUDENTS (1, "Gerenciar Alunos"),
    MANAGE_PLANS (2, "Gerenciar Planos"),
    MANAGE_ENROLLMENTS (3, "Gerenciar Inscrições"),
    REPORTS (4, "Relatórios / Listagens"),
    EXIT (0, "Sair");


    private int option;
    private String description;

    MainMenu(int option, String description) {
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
