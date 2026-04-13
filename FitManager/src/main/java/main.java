import application.FitManager;
import ui.UserInterface;
import ui.menus.MainMenu;

public class Main {
    public static void main(String[] args) {
        UserInterface ui = new UserInterface();
        FitManager fitManager = new FitManager();
        MainMenu mainMenu = new MainMenu(ui, fitManager);
        mainMenu.start();
    }
}