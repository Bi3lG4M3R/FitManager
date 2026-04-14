package ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;


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

    public void showEnrollment(int code, String studentName, String planName, LocalDate startDate, LocalDate endDate, int durationMonths, double totalPrice, String status){
        showMessage(
            "Código de matrícula: " + code + "\n" +
            "Nome do aluno: " + studentName + "\n" +
            "Plano escolhido: " + planName + "\n" +
            "Data de início: " + startDate + "\n" +
            "Data de término: " + endDate + "\n" +
            "Duração da matrícula: " + durationMonths + " meses\n" +
            "Valor total do plano: R$ " + String.format("%.2f", totalPrice) + "\n" +
            "Status: " + status + "\n" +
            "----------------------------------"
                );

    }

    public void showCancelledEnrollment(int code, String studentName, String planName, LocalDate startDate, LocalDate endDate, int durationMonths, double totalPrice, String status, String cancellationReason){
        showMessage(
            "Código de matrícula: " + code + "\n" +
            "Nome do aluno: " + studentName + "\n" +
            "Plano escolhido: " + planName + "\n" +
            "Data de início: " + startDate + "\n" +
            "Data de término: " + endDate + "\n" +
            "Duração da matrícula: " + durationMonths + " meses\n" +
            "Valor total do plano: R$ " + String.format("%.2f", totalPrice) + "\n" +
            "Status: " + status + "\n" +
            "Motivo da cancelamento: " + cancellationReason + "\n" +
            "----------------------------------"
                );

    }

    public void showPlan(String planNameList, String planDescriptionList, String planTypeList, int planMinDurationList, double planPricePerMonthList){
        showMessage(
            "Nome do plano - " + planNameList + "\n" +
            "Descrição: " + planDescriptionList + "\n" +
            "Tipo: " + planTypeList + "\n" +
            "Duração mínima: " + planMinDurationList + " meses\n" +
            "Preço por mês: R$ " + String.format("%.2f", planPricePerMonthList) + "\n" +
            "----------------------------------"
        );
    }

    public void showStudent(String studentNameList, String studentCpfList, String studentContactList, LocalDate studentBirthDateList){
        showMessage(
            "Nome do aluno - " + studentNameList + "\n" +
            "CPF: " + studentCpfList + "\n" +
            "Contato: " + studentContactList + "\n" +
            "Data de nascimento: " + studentBirthDateList + "\n" +
            "----------------------------------"
        );
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        while (true) {
            try {
                LocalDate date = LocalDate.parse(getInput(prompt).trim(), formatter);
                return date;
            } catch (DateTimeParseException error) {
                showError("Entrada inválida. Por favor, digite uma data no formato dd/MM/yyyy.");
            }
        }
    }





}
