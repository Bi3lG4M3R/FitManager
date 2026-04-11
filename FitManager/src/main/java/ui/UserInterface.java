package ui;

//Lista de imports
import java.util.Scanner;

public class UserInterface {

    private Scanner input = new Scanner(System.in);

    
    public void showMenu(String tittle, String[] options){
        System.out.println("==== " + tittle + " ====");
        for(int i = 0; i < options.length; i++){
            System.out.println((i + 1) + " - " + options[i]);
        }
    }

    // Recebe a entrada do usuário como String
    public String getInput(){
        return this.input.nextLine();
    }

    // Mostra uma mensagem para o usuário
    public void showMessage(String message){
        System.out.println(message);
    }

    // Mostra uma mensagem de erro para o usuário
    public void showError(String error){
        System.out.println("ERRO: " + error);
    }


}
