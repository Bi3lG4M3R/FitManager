package ui;

import java.util.Scanner;

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
        System.out.println("\n✅ " + msg);
    }

    public void showError(String msg) {
        System.out.println("\n❌ " + msg);
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
            }
        }
    }

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
}
