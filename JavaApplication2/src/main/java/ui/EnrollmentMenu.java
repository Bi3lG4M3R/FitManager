package ui;

import application.FitManager;
import application.OperationResult;
import domain.Enrollment;
import domain.payment.Payment;
import domain.payment.PaymentType;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class EnrollmentMenu {
    private final UserInterface ui;
    private final FitManager fitManager;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public EnrollmentMenu(UserInterface ui, FitManager fitManager) {
        this.ui = ui;
        this.fitManager = fitManager;
    }

    public void run() {
        boolean running = true;

        while (running) {
            int option = ui.showMenu("==== GERENCIAR MATRÍCULAS ====", new String[]{
                "Realizar matrícula",
                "Registrar pagamento",
                "Cancelar matrícula",
                "Consultar matrícula ativa por CPF",
                "Listar histórico",
                "Voltar"
            });

            switch (option) {
                case 1 -> enrollStudent();
                case 2 -> registerPayment();
                case 3 -> cancelEnrollment();
                case 4 -> findActiveEnrollment();
                case 5 -> listEnrollments();
                case 6 -> running = false;
                default -> ui.showError("Opção inválida.");
            }
        }
    }

    private void enrollStudent() {
        String cpf = ui.getInput("CPF do aluno: ").trim();
        String planName = ui.getInput("Nome do plano: ").trim();
        LocalDate startDate = readDate("Data de início (dd/MM/yyyy): ");
        int durationMonths = ui.readInt("Duração em meses: ");
        double initialAmount = ui.readDouble("Pagamento inicial: ");
        PaymentType paymentType = readPaymentType();
        String paymentDescription = ui.getInput("Descrição do pagamento: ").trim();

        OperationResult result = fitManager.enrollStudent(cpf, planName, startDate, durationMonths, initialAmount, paymentType, paymentDescription);
        showResult(result);

        if (result.isSuccess() && result.getData() instanceof Enrollment enrollment) {
            ui.showLine("Código da matrícula: " + enrollment.getCode());
        }
        ui.pressEnterToContinue();
    }

    private void registerPayment() {
        int code = ui.readInt("Código da matrícula: ");
        LocalDate date = readDate("Data do pagamento (dd/MM/yyyy): ");
        double amount = ui.readDouble("Valor pago: ");
        PaymentType paymentType = readPaymentType();
        String description = ui.getInput("Descrição: ").trim();

        OperationResult result = fitManager.registerPayment(date, code, amount, paymentType, description);
        showResult(result);
        ui.pressEnterToContinue();
    }

    private void cancelEnrollment() {
        int code = ui.readInt("Código da matrícula: ");
        String reason = ui.getInput("Motivo do cancelamento: ").trim();

        OperationResult result = fitManager.cancelEnrollment(code, reason);
        showResult(result);
        ui.pressEnterToContinue();
    }

    private void findActiveEnrollment() {
        String cpf = ui.getInput("CPF do aluno: ").trim();
        Enrollment enrollment = fitManager.findActiveEnrollment(cpf);

        if (enrollment == null) {
            ui.showError("Nenhuma matrícula ativa encontrada.");
        } else {
            printEnrollment(enrollment);
        }
        ui.pressEnterToContinue();
    }

    private void listEnrollments() {
        ArrayList<Enrollment> enrollments = fitManager.listEnrollments();
        if (enrollments.isEmpty()) {
            ui.showError("Nenhuma matrícula cadastrada.");
        } else {
            ui.showLine("\n===== HISTÓRICO DE MATRÍCULAS =====");
            for (Enrollment enrollment : enrollments) {
                printEnrollment(enrollment);
                ui.showLine("---------------------------");
            }
        }
        ui.pressEnterToContinue();
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            String input = ui.getInput(prompt).trim();
            try {
                return LocalDate.parse(input, formatter);
            } catch (DateTimeParseException e) {
                ui.showError("Data inválida. Use o formato dd/MM/yyyy.");
            }
        }
    }

    private PaymentType readPaymentType() {
        while (true) {
            ui.showLine("\nTipos de pagamento:");
            for (PaymentType paymentType : PaymentType.values()) {
                ui.showLine(paymentType.getValueOpcao() + " - " + paymentType.name());
            }
            int option = ui.readInt("Escolha o tipo: ");
            for (PaymentType paymentType : PaymentType.values()) {
                if (paymentType.getValueOpcao() == option) {
                    return paymentType;
                }
            }
            ui.showError("Tipo de pagamento inválido.");
        }
    }

    private void printEnrollment(Enrollment enrollment) {
        ui.showLine("\nCódigo: " + enrollment.getCode());
        ui.showLine("Aluno: " + enrollment.getStudent().getName() + " - " + enrollment.getStudent().getFormattedCpf());
        ui.showLine("Plano: " + enrollment.getPlan().getName());
        ui.showLine("Início: " + enrollment.getStartDate().format(formatter));
        ui.showLine("Fim: " + enrollment.getEndDate().format(formatter));
        ui.showLine("Duração: " + enrollment.getDurationMonths() + " mês(es)");
        ui.showLine("Total do contrato: R$ " + String.format("%.2f", enrollment.getTotalPrice()));
        ui.showLine("Total pago: R$ " + String.format("%.2f", enrollment.calculateTotalPaid()));
        ui.showLine("Saldo: R$ " + String.format("%.2f", enrollment.calculateBalance()));
        ui.showLine("Status: " + enrollment.getStatus());

        if (!enrollment.getPayments().isEmpty()) {
            ui.showLine("Pagamentos:");
            for (Payment payment : enrollment.getPayments()) {
                ui.showLine("  - " + payment.getDate().format(formatter)
                        + " | R$ " + String.format("%.2f", payment.getAmount())
                        + " | " + payment.getType()
                        + " | " + payment.getDescription());
            }
        }
    }

    private void showResult(OperationResult result) {
        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
        } else {
            ui.showError(result.getMessage());
        }
    }
}
