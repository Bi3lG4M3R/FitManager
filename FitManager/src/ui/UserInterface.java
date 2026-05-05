package ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import domain.Enrollment;
import domain.payment.PaymentType;
import domain.plan.PlanType;


public class UserInterface {

    private final Scanner input = new Scanner(System.in);

    // METODOS PARA MOSTRAR INFORMAÇÕES PARA O USUÁRIO
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
    
    public void showEnrollment(int code, String studentName, String planName, LocalDate startDate, LocalDate endDate, int durationMonths, double totalPrice, double pendingAmount, String status){
        showMessage(
            "Código de matrícula: " + code + "\n" +
            "Nome do aluno: " + studentName + "\n" +
            "Plano escolhido: " + planName + "\n" +
            "Data de início: " + startDate + "\n" +
            "Data de término: " + endDate + "\n" +
            "Duração da matrícula: " + durationMonths + " meses\n" +
            "Valor total do plano: R$ " + String.format("%.2f", totalPrice) + "\n" +
            "Valor pendente de pagamento: R$ " + String.format("%.2f", pendingAmount) + "\n" +
            "Status: " + status + "\n" +
            "----------------------------------"
        );
    }

    public void showEnrollment(Enrollment enrollment){
        showMessage(
            "Código de matrícula: " + enrollment.getCode() + "\n" +
            "Nome do aluno: " + enrollment.getStudent().getName() + "\n" +
            "Plano escolhido: " + enrollment.getPlan().getDescription() + "\n" +
            "Data de início: " + enrollment.getStartDate() + "\n" +
            "Data de término: " + enrollment.getEndDate() + "\n" +
            "Duração da matrícula: " + enrollment.getDurationMonths() + " meses\n" +
            "Valor total do plano: R$ " + String.format("%.2f", enrollment.getTotalPrice()) + "\n" +
            "Status: " + enrollment.getStatus().getDescricao() + "\n" +
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
            "Motivo de cancelamento: " + cancellationReason + "\n" +
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

    public void showPlanTypeOptions(){
        showMessage("Tipos de planos disponíveis:");
        for (PlanType type : PlanType.values()) {
            showMessage(type.getValueOption() + " - " + type.getDescription());
        }
    }

    public void showPaymentTypeOptions(){
        showMessage("Formas de pagamento disponíveis:");
        for (PaymentType type : PaymentType.values()) {
            showMessage(type.getValueOpcao() + " - " + type.getDescription());
        }
    }

    // METODOS PARA RECEBER INFORMAÇÕES DO USUÁRIO
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

    public PlanType getInputPlanType(String prompt){
        PlanType planType = null;
        
        do { 
            try {
                showPlanTypeOptions();
                int selectedPlan = getInputInt(prompt);
                planType = PlanType.selectFromInt(selectedPlan);
                if (planType == null) {
                    showError("Opção inválida. Por favor, selecione um tipo de plano válido.");
                }
            } catch (NumberFormatException error) {
                showError("Entrada inválida. Por favor, digite um número inteiro correspondente ao tipo de plano.");
            }
        } while (planType == null);
        return planType;
    }

    public PaymentType getInputPaymentType(String prompt){
        PaymentType paymentType = null;
        do { 
            try {
                showPaymentTypeOptions();
                int selectedPayment = getInputInt(prompt);
                paymentType = PaymentType.selectFromInt(selectedPayment);
                if (paymentType == null) {
                    showError("Opção inválida. Por favor, selecione uma forma de pagamento válida.");
                }
            } catch (NumberFormatException error) {
                showError("Entrada inválida. Por favor, digite um número inteiro correspondente a forma de pagamento.");
            }
        } while (paymentType == null);
        return paymentType;
    }

    





}
