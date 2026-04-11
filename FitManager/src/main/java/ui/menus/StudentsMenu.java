package ui.menus;

import ui.UserInterface;
import ui.enums.StudentMenuEnum;
import application.FitManager;

public class StudentsMenu {
    
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
    }
}
