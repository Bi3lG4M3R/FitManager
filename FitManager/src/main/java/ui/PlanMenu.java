package ui;

import application.FitManager;
import application.OperationResult;
import domain.plan.Plan;
import domain.plan.PlanType;
import java.util.ArrayList;

public class PlanMenu {
    private final UserInterface ui;
    private final FitManager fitManager;

    public PlanMenu(UserInterface ui, FitManager fitManager) {
        this.ui = ui;
        this.fitManager = fitManager;
    }

    public void run() {
        boolean running = true;

        while (running) {
            int option = ui.showMenu("==== GERENCIAR PLANOS ====", new String[]{
                "Cadastrar novo plano",
                "Consultar por nome",
                "Alterar preço",
                "Listar todos",
                "Voltar"
            });

            switch (option) {
                case 1 -> registerPlan();
                case 2 -> findPlanByName();
                case 3 -> updatePlanPrice();
                case 4 -> listPlans();
                case 5 -> running = false;
                default -> ui.showError("Opção inválida.");
            }
        }
    }

    private void registerPlan() {
        String name = ui.getInput("Nome do plano: ").trim();
        String description = ui.getInput("Descrição: ").trim();
        PlanType type = readPlanType();
        int minDurationMonths = ui.readInt("Duração mínima em meses: ");
        double pricePerMonth = ui.readDouble("Preço por mês: ");

        OperationResult result = fitManager.registerPlan(name, description, type, minDurationMonths, pricePerMonth);
        showResult(result);
        ui.pressEnterToContinue();
    }

    private void findPlanByName() {
        String name = ui.getInput("Nome do plano: ").trim();
        Plan plan = fitManager.findPlanByName(name);
        if (plan == null) {
            ui.showError("Plano não encontrado.");
        } else {
            printPlan(plan);
        }
        ui.pressEnterToContinue();
    }

    private void updatePlanPrice() {
        String name = ui.getInput("Nome do plano: ").trim();
        double newPrice = ui.readDouble("Novo preço mensal: ");

        OperationResult result = fitManager.updatePlanPrice(name, newPrice);
        showResult(result);
        ui.pressEnterToContinue();
    }

    private void listPlans() {
        ArrayList<Plan> plans = fitManager.listPlans();
        if (plans.isEmpty()) {
            ui.showError("Nenhum plano cadastrado.");
        } else {
            ui.showLine("\n===== LISTA DE PLANOS =====");
            for (Plan plan : plans) {
                printPlan(plan);
                ui.showLine("---------------------------");
            }
        }
        ui.pressEnterToContinue();
    }

    private PlanType readPlanType() {
        while (true) {
            ui.showLine("\nTipos disponíveis:");
            for (PlanType planType : PlanType.values()) {
                ui.showLine(planType.getValueOption() + " - " + planType.getDescription());
            }
            int option = ui.readInt("Escolha o tipo: ");
            for (PlanType planType : PlanType.values()) {
                if (planType.getValueOption() == option) {
                    return planType;
                }
            }
            ui.showError("Tipo de plano inválido.");
        }
    }

    private void printPlan(Plan plan) {
        ui.showLine("\nNome: " + plan.getName());
        ui.showLine("Descrição: " + plan.getDescription());
        ui.showLine("Tipo: " + plan.getType().getDescription());
        ui.showLine("Duração mínima: " + plan.getMinDurationMonths() + " mês(es)");
        ui.showLine("Preço mensal: R$ " + String.format("%.2f", plan.getPricePerMonth()));
    }

    private void showResult(OperationResult result) {
        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
        } else {
            ui.showError(result.getMessage());
        }
    }
}
