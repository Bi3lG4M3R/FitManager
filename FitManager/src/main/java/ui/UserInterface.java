package ui;

import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class UserInterface {

    private Scanner input = new Scanner(System.in);

        // Mostra uma mensagem para o usuário
    public void showMessage(String message){
        System.out.println(message);
    }

    // Mostra uma mensagem de erro para o usuário
    public void showError(String error){
        System.out.println("ERRO: " + error);
    }


    public void showMenu(String tittle, String[] options){
        showMessage("==== " + tittle + " ====");
        for(int i = 0; i < options.length; i++){
            showMessage((i + 1) + " - " + options[i]);
        }
    }

    // Recebe a entrada do usuário como String
    public String getInput(String prompt){
        showMessage(prompt);
        return this.input.nextLine();
    }

    public int getInputInt(String prompt){
        while (true) {
            try {
                int value = Integer.parseInt(getInput(prompt));
                return value;
            } catch (NumberFormatException error) {
                showError("Entrada inválida. Por favor, digite um número inteiro.");
            }
        }
    }

    public double getInputDouble(String prompt){
        while (true) {
            try {
                double value = Double.parseDouble(getInput(prompt));
                return value;
            } catch (NumberFormatException error) {
                showError("Entrada inválida. Por favor, digite um número decimal.");
            }
        }
    }

    public LocalDate getInputDate(String prompt){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/mm/yyyy");
        while (true) {
            try {
                LocalDate date = LocalDate.parse(getInput(prompt), formatter);
                return date;
            } catch (DateTimeParseException error) {
                showError("Entrada inválida. Por favor, digite uma data no formato dd/mm/yyyy.");
            }
        }
    }




}
