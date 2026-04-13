package ui;

import application.FitManager;
import domain.Enrollment;
import domain.EnrollmentStatus;
import domain.Student;
import java.util.ArrayList;

public class ReportsMenu {
    private final UserInterface ui;
    private final FitManager fitManager;

    public ReportsMenu(UserInterface ui, FitManager fitManager) {
        this.ui = ui;
        this.fitManager = fitManager;
    }

    public void run() {
        boolean running = true;

        while (running) {
            int option = ui.showMenu("=========== RELATÓRIOS ===========", new String[]{
                "Alunos com matrícula ativa",
                "Matrículas com saldo pendente",
                "Todas as matrículas",
                "Voltar"
            });

            switch (option) {
                case 1 -> listStudentsWithActiveEnrollment();
                case 2 -> listEnrollmentsWithPendingBalance();
                case 3 -> listAllEnrollments();
                case 4 -> running = false;
                default -> ui.showError("Opção inválida.");
            }
        }
    }

    private void listStudentsWithActiveEnrollment() {
        ArrayList<Enrollment> enrollments = fitManager.listEnrollments();
        boolean found = false;

        ui.showLine("\n===== ALUNOS COM MATRÍCULA ATIVA =====");
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getStatus() == EnrollmentStatus.ACTIVE) {
                Student student = enrollment.getStudent();
                ui.showLine(student.getName() + " - " + student.getFormattedCpf()
                        + " | Plano: " + enrollment.getPlan().getName());
                found = true;
            }
        }

        if (!found) {
            ui.showError("Nenhum aluno com matrícula ativa.");
        }
        ui.pressEnterToContinue();
    }

    private void listEnrollmentsWithPendingBalance() {
        ArrayList<Enrollment> enrollments = fitManager.listEnrollments();
        boolean found = false;

        ui.showLine("\n===== MATRÍCULAS COM SALDO PENDENTE =====");
        for (Enrollment enrollment : enrollments) {
            if (enrollment.calculateBalance() > 0) {
                ui.showLine("Código " + enrollment.getCode()
                        + " | Aluno: " + enrollment.getStudent().getName()
                        + " | Saldo: R$ " + String.format("%.2f", enrollment.calculateBalance()));
                found = true;
            }
        }

        if (!found) {
            ui.showError("Nenhuma matrícula com saldo pendente.");
        }
        ui.pressEnterToContinue();
    }

    private void listAllEnrollments() {
        ArrayList<Enrollment> enrollments = fitManager.listEnrollments();

        if (enrollments.isEmpty()) {
            ui.showError("Nenhuma matrícula cadastrada.");
        } else {
            ui.showLine("\n===== TODAS AS MATRÍCULAS =====");
            for (Enrollment enrollment : enrollments) {
                ui.showLine("Código " + enrollment.getCode()
                        + " | " + enrollment.getStudent().getName()
                        + " | Plano: " + enrollment.getPlan().getName()
                        + " | Status: " + enrollment.getStatus()
                        + " | Saldo: R$ " + String.format("%.2f", enrollment.calculateBalance()));
            }
        }
        ui.pressEnterToContinue();
    }
}
