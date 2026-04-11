package ui.enums;

/* Menu para gerenciar alunos */

public enum StudentMenuEnum {

    REGISTER_STUDENT (1, "Cadastrar novo aluno"),
    SEARCH_BY_CPF (2, "Buscar por CPF"),
    EDIT_STUDENT (3, "Editar aluno"),
    DELETE_STUDENT (4, "Excluir aluno"),
    VIEW_ALL_STUDENTS (5, "Listar todos alunos"),
    BACK (0, "Voltar ao menu principal");

    private final int option;
    private final String description;

    StudentMenuEnum(int option, String description) {
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
    public static StudentMenuEnum selectFromInt(int option) {
        for (StudentMenuEnum menuOp : StudentMenuEnum.values()) {
            if (menuOp.getOptionNumber() == option){
                return menuOp;
            }
        }
        return null; // caso a opcao nao exista devolve null
    }
}   
