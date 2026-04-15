package ui.menus;

import application.FitManager;
import ui.UserInterface;
import ui.enums.MainMenuEnum;

public class MainMenu{
    
    private final UserInterface ui;
    private final FitManager fitManager;


    // Construtor
    public MainMenu(UserInterface ui, FitManager fitManager){
        this.ui = ui;
        this.fitManager = fitManager;
    }



    public void start(){
        MainMenuEnum optionSelected = null;

        //  String array para armazenar as opções dos menus
        String[] menuOptions = new String[MainMenuEnum.values().length];

        //  Construção do array pegando as descrições do do enum
        for(int i = 0; i < MainMenuEnum.values().length; i++){
            menuOptions[i] = MainMenuEnum.values()[i].getOptionDescription();
        }


        // Execução do menu 
        do { 

            do{
                ui.showMenu("Menu Principal", menuOptions);
                optionSelected = MainMenuEnum.selectFromInt(ui.getInputInt("Selecione uma opção: "));
                if(optionSelected == null){
                    ui.showError("Opção inexistente. Selecione uma das opções acima.");
                }
            }while(optionSelected == null);
            

            
            switch(optionSelected){
                
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

            }

        }while(optionSelected != MainMenuEnum.EXIT);


    }
}
