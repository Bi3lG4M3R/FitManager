package ui;

import application.FitManager;

public class MainMenu {
    private final UserInterface ui;
    private final FitManager fitManager;

    public MainMenu(UserInterface ui, FitManager fitManager) {
        this.ui = ui;
        this.fitManager = fitManager;
    }

    public void start() {
        boolean running = true;

        while (running) {
            int option = ui.showMenu("======== FITMANAGER ========", new String[]{
                "Gerenciar alunos",
                "Gerenciar planos",
                "Gerenciar matrículas",
                "Relatórios / listagens",
                "Sair"
            });

            switch (option) {
                case 1 -> new StudentMenu(ui, fitManager).run();
                case 2 -> new PlanMenu(ui, fitManager).run();
                case 3 -> new EnrollmentMenu(ui, fitManager).run();
                case 4 -> new ReportsMenu(ui, fitManager).run();
                case 5 -> {
                    ui.showMessage("Encerrando o sistema.");
                    running = false;
                }
                default -> ui.showError("Opção inválida.");
            }
        }
    }
}
