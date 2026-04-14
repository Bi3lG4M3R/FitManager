package ui.menus;

import ui.UserInterface;
import ui.enums.MainMenuEnum;
import ui.menus.PlanMenu;
import ui.menus.StudentsMenu;
import ui.menus.EnrollmentMenu;
import ui.menus.ReportsMenu;

import application.FitManager;

public class MainMenu{
    
    private final UserInterface ui;
    private final FitManager fitManager;


    // Construtor
    public MainMenu(UserInterface ui, FitManager fitManager){
        this.ui = ui;
        this.fitManager = fitManager;
    }



    public void start(){
        int optionSelected;

        //  String array para armazenar as opções dos menus
        String[] menuOptions = new String[MainMenuEnum.values().length];

        //  Construção do array pegando as descrições do do enum
        for(int i = 0; i < MainMenuEnum.values().length; i++){
            menuOptions[i] = MainMenuEnum.values()[i].getOptionDescription();
        }


        // Execução do menu 
        do { 
            ui.showMenu("Menu Principal", menuOptions);

            optionSelected = ui.getInputInt("Selecione uma opção: ");

            
            switch(MainMenuEnum.selectFromInt(optionSelected)){
                
                case MANAGE_STUDENTS:
                    StudentsMenu studentsMenu = new StudentsMenu(ui, fitManager);
                    studentsMenu.run();
                break;

                case MANAGE_PLANS:
                    PlanMenu planMenu = new PlanMenu(ui, fitManager);
                    planMenu.run();
                break;

                case MANAGE_ENROLLMENTS:
                    EnrollmentMenu enrollmentMenu = new EnrollmentMenu(ui, fitManager);
                    enrollmentMenu.run();
                break;

                case REPORTS:
                    ReportsMenu reportsMenu = new ReportsMenu(ui, fitManager);
                    reportsMenu.run();
                break;

                case EXIT:
                    ui.showMessage("Saindo do programa...");
                break;

                default:
                    ui.showError("Opção inexistente. Selecione uma das opçãoes acima.");
                break;
            }

        } while (optionSelected != MainMenuEnum.EXIT.getOptionNumber());


    }
}
