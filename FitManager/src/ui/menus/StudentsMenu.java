package ui.menus;

import java.time.LocalDate;
import java.util.ArrayList;

import application.FitManager;
import application.OperationResult;
import domain.Student;
import ui.UserInterface;
import ui.enums.StudentMenuEnum;

public class StudentsMenu{
    
    private final UserInterface ui;
    private final FitManager fitManager;

    // Construtor
    public StudentsMenu(UserInterface ui, FitManager fitManager){
        this.ui = ui;
        this.fitManager = fitManager;
    }

    public void run(){
        StudentMenuEnum optionSelected;

        //  String array para armazenar as opções dos menus
        String[] menuOptions = new String[StudentMenuEnum.values().length];

        //  Construção do array pegando as descrições do do enum
        for(int i = 0; i < StudentMenuEnum.values().length; i++){
            menuOptions[i] = StudentMenuEnum.values()[i].getOptionNumber() + " - " + StudentMenuEnum.values()[i].getOptionDescription();
        }

        do{ 

            do{
                int option = ui.showMenu("GERENCIAR ALUNOS", menuOptions, "Selecione uma opção: ");
                if(option == 0){
                    optionSelected = StudentMenuEnum.BACK;
                } else {
                    optionSelected = StudentMenuEnum.selectFromInt(option);
                    if(optionSelected == null)
                        ui.showError("Opção inexistente. Selecione uma das opções acima.");
                }
            }while(optionSelected == null);

            ArrayList<Student> studentList = fitManager.listStudents();

            switch(optionSelected){

                case REGISTER_STUDENT:
                    String studentName = ui.getInput("Digite o nome do aluno: ");
                    if(studentName == null) break;
                    String studentCpf = ui.getInput("Digite o CPF do aluno: ");
                    if(studentCpf == null) break;
                    String studentContact = ui.getInput("Digite um meio de contato (E-mail ou telefone): ");
                    if(studentContact == null) break;
                    LocalDate studentBirthDate = ui.getInputDate("Digite a data de nascimento do aluno (dd/mm/aaaa): ");
                    if(studentBirthDate == null) break;
                    
                    OperationResult resultRegister = fitManager.registerStudent(studentName, studentCpf, studentContact, studentBirthDate);
                    if(resultRegister.isSuccess()){
                        ui.showMessage(resultRegister.getMessage());
                    } else {
                        ui.showMessage("Erro ao cadastrar aluno: " + resultRegister.getMessage());
                    }
                break;

                case SEARCH_BY_CPF:
                    String cpfToSearch = ui.getInput("Digite o CPF do aluno a ser consultado: ");
                    if(cpfToSearch == null) break;
                    Student studentFound = fitManager.findStudentByCpf(cpfToSearch);
                    if(studentFound == null){
                        ui.showError("Aluno não encontrado.");
                    } else {
                        String studentNameList = studentFound.getName();
                        String studentCpfList = studentFound.getFormattedCpf();
                        String studentContactList = studentFound.getContact();
                        String studentBirthDateList = studentFound.getFormattedBirthDate();
                        ui.showStudent(studentNameList, studentCpfList, studentContactList, studentBirthDateList);
                    }
                break;

                case DELETE_STUDENT:
                    String cpfToDelete = ui.getInput("Digite o CPF do aluno a ser desativado: ");
                    if(cpfToDelete == null) break;
                    OperationResult resultDelete = fitManager.removeStudent(cpfToDelete);
                    if(resultDelete.isSuccess()){
                        ui.showMessage(resultDelete.getMessage());
                    } else {
                        ui.showMessage("Erro ao desativar aluno: " + resultDelete.getMessage());
                    }
                break;
                
                case ACTIVATE_STUDENT:
                    String cpfToActivate = ui.getInput("Digite o CPF do aluno a ser reativado: ");
                    if(cpfToActivate == null) break;
                    OperationResult resultActivate = fitManager.activateStudent(cpfToActivate);
                    if(resultActivate.isSuccess()){
                        ui.showMessage(resultActivate.getMessage());
                    } else {
                        ui.showMessage("Erro ao excluir aluno: " + resultActivate.getMessage());
                    }
                break;

                case VIEW_ALL_STUDENTS:
                    ui.showMessage(formatStudentList(studentList));
                break;

                case BACK:
                    ui.showMessage("Voltando ao menu principal...");
                break;

                default:
                    ui.showMessage("Opção inexistente, selecione uma das opçãoes acima.");
                break;
            }

        }while(optionSelected != StudentMenuEnum.BACK);

    }

    
    public static String formatStudentList(ArrayList<Student> studentList) {
        if (studentList.isEmpty()) {
            return "Nenhum aluno cadastrado.";
        }

        String result = "Histórico de Alunos:\n\n";

        for (Student student : studentList) {
            result = result + "Nome: " + student.getName() + "\n";
            result = result + "CPF: " + student.getFormattedCpf() + "\n";
            result = result + "Contato: " + student.getContact() + "\n";
            result = result + "Data de Nasc.: " + student.getFormattedBirthDate() + "\n";
            result = result + "----------------------------\n";
        }

        return result;
    }
}
