
import exceptions.PersistenceException;
import application.FitManager;
import ui.JOptionPaneUI;
import ui.TerminalUI;
import ui.UserInterface;
import ui.menus.MainMenu;


public class Main {
    public static void main(String[] args) {
        UserInterface tempUi = new TerminalUI();

        tempUi.showMessage("Bem-vindo ao FitManager!");
        String[] uiOption = {"1 - Interação via terminal", "2 - Interação via JOptionPane"};
        int choice;

        UserInterface ui = null;
        do{
            choice = tempUi.showMenu("Formas de interação", uiOption, "Escolha a tipo de interação:");
            if(choice == 1)
                ui = new TerminalUI();

            if(choice == 2)
                ui = new JOptionPaneUI();

            if(choice != 1 && choice != 2)
                tempUi.showError("Opção inválida. Selecione uma opção válida.");
        }while(choice != 1 && choice != 2);

        FitManager fitManager = new FitManager();   
        
        try {
        fitManager.loadAll();
        } catch (PersistenceException e) {
            tempUi.showError("Arquivo corrompido: " + e.getFilePath() + "\n" + e.getMessage());
            tempUi.showMessage("Encerrando o sistema. Verifique o arquivo e reinicie.");
            return; // encerra o main
        }
        
        MainMenu mainMenu = new MainMenu(ui, fitManager);

        mainMenu.start();
    }
}