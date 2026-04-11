package ui.menus;

import ui.UserInterface;
import ui.enums.MainMenuEnum;
//import application.FitManager;

public class MainMenu {
    
    private UserInterface ui;
    //private FitManager fitManager;


    // Construtor
    public MainMenu(UserInterface ui/*, FitManager fitManager*/){
        this.ui = ui;
        //this.fitManager = fitManager;
    }



    public void start(){
        ui = new UserInterface();
        //fitManager = new FitManager();
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
            ui.showMessage("Selecione uma opção: ");

            optionSelected = Integer.parseInt(ui.getInput());

            switch(MainMenuEnum.selectFromInt(optionSelected)){
                
                case MANAGE_STUDENTS:
                    StudentMenu studentMenu = new StudentMenu(ui/*, fitManager*/);
                    studentMenu.start();
                    break;
                case MANAGE_PLANS:
                    PlanMenu planMenu = new PlanMenu(ui/*, fitManager*/);
                    planMenu.run();
                    break;
                case MANAGE_ENROLLMENTS:
                    EnrollmentMenu enrollmentMenu = new EnrollmentMenu(ui/*, fitManager*/);
                    enrollmentMenu.run();
                    break;
                case REPORTS:
                    ReportsMenu reportsMenu = new ReportsMenu(ui/*, fitManager*/);
                    reportsMenu.run();
                    break;
                case EXIT:
                    break;
                default:
                    ui.showMessage("Opção inexistente, selecione uma das opçãoes acima.");
            }



        } while (optionSelected != MainMenuEnum.EXIT.getOptionNumber());

        ui.showMessage("Saindo do programa...");

    }
}
