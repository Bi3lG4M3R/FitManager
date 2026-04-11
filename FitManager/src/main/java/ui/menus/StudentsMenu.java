package ui.menus;

import ui.UserInterface;
import ui.enums.StudentMenuEnum;
import application.FitManager;

public class StudentsMenu{
    
    private UserInterface ui;
    private FitManager fitManager;

    // Construtor
    public StudentsMenu(UserInterface ui, FitManager fitManager){
        this.ui = ui;
        this.fitManager = fitManager;
    }


    public void run(){
        int optionSelected;

        //  String array para armazenar as opções dos menus
        String[] menuOptions = new String[StudentMenuEnum.values().length];

        //  Construção do array pegando as descrições do do enum
        for(int i = 0; i < StudentMenuEnum.values().length; i++){
            menuOptions[i] = StudentMenuEnum.values()[i].getOptionDescription();
        }

        do{ 
            ui.showMenu("GERENCIAR ALUNOS", menuOptions);
            ui.showMessage("Selecione uma opção: ");

            optionSelected = Integer.parseInt(ui.getInput());

            switch(StudentMenuEnum.selectFromInt(optionSelected)){

                case REGISTER_STUDENT:
                    fitManager.registerStudent();
                break;

                case SEARCH_BY_CPF:
                    fitManager.findStudentByCpf();
                break;

                case EDIT_STUDENT:
                    fitManager.updateStudent();
                break;

                case DELETE_STUDENT:
                    fitManager.removeStudent();
                break;

                case VIEW_ALL_STUDENTS:
                    fitManager.listStudents();
                break;

                case BACK:
                    ui.showMessage("Voltando ao menu principal...");
                break;

                default:
                    ui.showMessage("Opção inexistente, selecione uma das opçãoes acima.");
                break;
            }

        }while(optionSelected != StudentMenuEnum.BACK.getOptionNumber());

    }
}
