package userinterface;

/* Menu para gerenciar alunos */

public enum StudentMenuEnum {

    REGISTER_STUDENT (1, "Cadastrar novo aluno"),
    SEARCH_BY_CPF (2, "Buscar por CPF"),
    EDIT_STUDENT (3, "Editar aluno"),
    DELETE_STUDENT (4, "Excluir aluno"),
    VIEW_ALL_STUDENTS (5, "Listar todos alunos"),
    BACK (0, "Voltar ao menu principal");

    private int option;
    private String description;

    StudentMenuEnum(int option, String description) {
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
