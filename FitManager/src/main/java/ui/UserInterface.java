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

    public String getInput(String input){
        return this.input.nextLine();
    }

    public void showMessage(String message){
        System.out.println(message);
    }

    public void showError(String error){
        System.out.println("ERRO: " + error);
    }


}
