package ui;

import application.FitManager;
import application.OperationResult;
import domain.Student;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class StudentMenu {
    private final UserInterface ui;
    private final FitManager fitManager;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public StudentMenu(UserInterface ui, FitManager fitManager) {
        this.ui = ui;
        this.fitManager = fitManager;
    }

    public void run() {
        boolean running = true;

        while (running) {
            int option = ui.showMenu("==== GERENCIAR ALUNOS ====", new String[]{
                "Cadastrar novo aluno",
                "Consultar por CPF",
                "Excluir/Inativar aluno",
                "Listar todos",
                "Voltar"
            });

            switch (option) {
                case 1 -> registerStudent();
                case 2 -> findStudentByCpf();
                case 3 -> removeStudent();
                case 4 -> listStudents();
                case 5 -> running = false;
                default -> ui.showError("Opção inválida.");
            }
        }
    }

    private void registerStudent() {
        String name = ui.getInput("Nome: ").trim();
        String cpf = ui.getInput("CPF: ").trim();
        String contact = ui.getInput("Contato: ").trim();
        LocalDate birthDate = readDate("Data de nascimento (dd/MM/yyyy): ");

        OperationResult result = fitManager.registerStudent(name, cpf, contact, birthDate);
        showResult(result);
        ui.pressEnterToContinue();
    }

    private void findStudentByCpf() {
        String cpf = ui.getInput("CPF do aluno: ").trim();
        Student student = fitManager.findStudentByCpf(cpf);

        if (student == null) {
            ui.showError("Aluno não encontrado.");
        } else {
            printStudent(student);
        }
        ui.pressEnterToContinue();
    }

    private void removeStudent() {
        String cpf = ui.getInput("CPF do aluno: ").trim();
        OperationResult result = fitManager.removeStudent(cpf);
        showResult(result);
        ui.pressEnterToContinue();
    }

    private void listStudents() {
        ArrayList<Student> students = fitManager.listStudents();
        if (students.isEmpty()) {
            ui.showError("Nenhum aluno cadastrado.");
        } else {
            ui.showLine("\n===== LISTA DE ALUNOS =====");
            for (Student student : students) {
                printStudent(student);
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

    private void printStudent(Student student) {
        ui.showLine("\nNome: " + student.getName());
        ui.showLine("CPF: " + student.getFormattedCpf());
        ui.showLine("Contato: " + student.getContact());
        ui.showLine("Nascimento: " + student.getFormattedBirthDate());
        ui.showLine("Idade: " + student.calculateAge());
        ui.showLine("Status: " + (student.isActive() ? "Ativo" : "Inativo"));
    }

    private void showResult(OperationResult result) {
        if (result.isSuccess()) {
            ui.showMessage(result.getMessage());
        } else {
            ui.showError(result.getMessage());
        }
    }
}
