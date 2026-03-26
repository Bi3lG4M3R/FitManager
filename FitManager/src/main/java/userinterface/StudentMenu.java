package userinterface;

/* Menu gerenciar alunos */

public enum StudentMenu {
    REGISTER_STUDENT (1, "Cadastrar novo aluno"),
    SEARCH_BY_CPF (2, "Buscar por CPF"),
    EDIT_STUDENT (3, "Editar aluno"),
    DELETE_STUDENT (4, "Excluir aluno"),
    LIST_ALL_STUDENTS (5, "Listar todos alunos"),
    BACK (0, "Voltar ao menu principal");

    private int option;
    private String description;

    StudentMenu(int option, String description) {
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
