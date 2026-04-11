package ui.menus;

import ui.UserInterface;
import ui.enums.MainMenuEnum;
//port application.FitManager;
public class MainMenu {
    
    private UserInterface ui;
    //private FitManager fitManager;

    public MainMenu(UserInterface ui/*, FitManager fitManager*/){
        this.ui = ui;
        //this.fitManager = fitManager;
    }

    public void start(){
        ui = new UserInterface();
        //fitManager = new FitManager();
        int optionSelected = -1;

        //  String array para armazenar as opções dos menus
        String[] menuOptions = new String[MainMenuEnum.values().length];

        //  Construção do array pegando as descrições do do enum
        for(int i = 0; i < MainMenuEnum.values().length; i++){
            menuOptions[i] = MainMenuEnum.values()[i].getOptionDescription();
        }

        do { 
            ui.showMenu("Menu Principal", menuOptions);
        } while (optionSelected != MainMenuEnum.EXIT.getOptionNumber());

        ui.showMessage("Saindo do programa...");

    }
}
