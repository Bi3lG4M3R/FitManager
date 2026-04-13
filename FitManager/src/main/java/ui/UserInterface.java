package ui;

import java.util.Scanner;
<<<<<<< HEAD

public class UserInterface {
    private final Scanner scanner;

    public UserInterface() {
        this.scanner = new Scanner(System.in);
    }

    public int showMenu(String title, String[] options) {
        System.out.println();
        System.out.println(title);
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + " - " + options[i]);
        }
        return readInt("Escolha uma opção: ");
    }

    public String getInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public void showMessage(String msg) {
        System.out.println("\n " + msg);
    }

    public void showError(String msg) {
        System.out.println("\n " + msg);
    }

    public void showLine(String msg) {
        System.out.println(msg);
    }

    public void pressEnterToContinue() {
        System.out.print("\nPressione Enter para continuar...");
        scanner.nextLine();
    }

    public int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                showError("Digite um número inteiro válido.");
=======
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import domain.Student;

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
>>>>>>> 732efee3190b70482e2667c5c520e9cd0661ccb8
            }
        }
    }

<<<<<<< HEAD
    public double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String raw = scanner.nextLine().trim().replace(',', '.');
                return Double.parseDouble(raw);
            } catch (NumberFormatException e) {
                showError("Digite um número válido.");
            }
        }
    }
=======
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
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/mm/yyyy");
        while (true) {
            try {
                LocalDate date = LocalDate.parse(getInput(prompt), Student.dateFormatter);
                return date;
            } catch (DateTimeParseException error) {
                showError("Entrada inválida. Por favor, digite uma data no formato dd/mm/yyyy.");
            }
        }
    }




>>>>>>> 732efee3190b70482e2667c5c520e9cd0661ccb8
}
