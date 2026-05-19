package ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.swing.JOptionPane;

import domain.Enrollment;
import domain.payment.PaymentType;
import domain.plan.PlanType;

public class JOptionPaneUI implements UserInterface {

    // METODOS PARA MOSTRAR INFORMAÇÕES PARA O USUÁRIO
    @Override
    public void showMessage(String message){
        JOptionPane.showMessageDialog(null, message, "FitManager", JOptionPane.INFORMATION_MESSAGE);
    }

    // Mostra uma mensagem de erro para o usuário
    @Override
    public void showError(String error){
        JOptionPane.showMessageDialog(null, "ERRO: " + error, "FitManager", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public int showMenu(String tittle, String[] options, String prompt){
        StringBuilder menuText = new StringBuilder();
        menuText.append("==== ").append(tittle).append(" ====\n");

        for(int i = 0; i < options.length; i++){
            menuText.append(i + 1).append(" - ").append(options[i]).append("\n");
        }

        showMessage(menuText.toString());
        return getInputInt(prompt);
    }

    @Override
    public void showEnrollment(int code, String studentName, String planName, LocalDate startDate, LocalDate endDate, int durationMonths, double totalPrice, double pendingAmount, String status){
        showMessage(
            "Código de matrícula: " + code + "\n" +
            "Nome do aluno: " + studentName + "\n" +
            "Plano escolhido: " + planName + "\n" +
            "Data de início: " + startDate + "\n" +
            "Data de término: " + endDate + "\n" +
            "Duração da matrícula: " + durationMonths + " meses\n" +
            "Valor total do plano: R$ " + String.format("%.2f", totalPrice) + "\n" +
            "Valor pendente: R$ " + String.format("%.2f", pendingAmount) + "\n" +
            "Status: " + status + "\n" +
            "----------------------------------"
        );
    }

    @Override
    public void showEnrollment(Enrollment enrollment){
        showMessage(
            "Código de matrícula: " + enrollment.getCode() + "\n" +
            "Nome do aluno: " + enrollment.getStudent().getName() + "\n" +
            "Plano escolhido: " + enrollment.getPlan().getDescription() + "\n" +
            "Data de início: " + enrollment.getStartDate() + "\n" +
            "Data de término: " + enrollment.getEndDate() + "\n" +
            "Duração da matrícula: " + enrollment.getDurationMonths() + " meses\n" +
            "Valor total do plano: R$ " + String.format("%.2f", enrollment.getTotalPrice()) + "\n" +
            "Valor pendente: R$ " + String.format("%.2f", enrollment.calculateBalance()) + "\n" +
            "Status: " + enrollment.getStatus().getDescription() + "\n" +
            "----------------------------------"
        );
    }

    @Override
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

    @Override
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

    @Override
    public void showStudent(String studentNameList, String studentCpfList, String studentContactList, String studentBirthDateList){
        showMessage(
            "Nome do aluno - " + studentNameList + "\n" +
            "CPF: " + studentCpfList + "\n" +
            "Contato: " + studentContactList + "\n" +
            "Data de nascimento: " + studentBirthDateList + "\n" +
            "----------------------------------"
        );
    }

    @Override
    public void showPlanTypeOptions(){
        StringBuilder optionsText = new StringBuilder();
        optionsText.append("Tipos de planos disponíveis:\n");

        for (PlanType type : PlanType.values()) {
            optionsText.append(type.getValueOption()).append(" - ").append(type.getDescription()).append("\n");
        }

        showMessage(optionsText.toString());
    }

    @Override
    public void showPaymentTypeOptions(){
        StringBuilder optionsText = new StringBuilder();
        optionsText.append("Formas de pagamento disponíveis:\n");

        for (PaymentType type : PaymentType.values()) {
            optionsText.append(type.getValueOpcao()).append(" - ").append(type.getDescription()).append("\n");
        }

        showMessage(optionsText.toString());
    }

    // METODOS PARA RECEBER INFORMAÇÕES DO USUÁRIO
    @Override
    public String getInput(String prompt){
        while(true){
            String inputValue = JOptionPane.showInputDialog(null, prompt, "FitManager", JOptionPane.QUESTION_MESSAGE);
            if(inputValue != null && !inputValue.trim().isEmpty()){
                return inputValue.trim();
            }

            showError("Entrada inválida. Por favor, preencha o campo corretamente.");
        }
    }

    @Override
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

    @Override
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

    @Override
    public LocalDate getInputDate(String prompt){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        while (true) {
            try {
                LocalDate date = LocalDate.parse(getInput(prompt), formatter);
                return date;
            } catch (DateTimeParseException error) {
                showError("Entrada inválida. Por favor, digite uma data no formato dd/MM/yyyy.");
            }
        }
    }

    @Override
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

    @Override
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