package ui.menus;

import java.time.LocalDate;
import java.util.ArrayList;

import ui.UserInterface;
import ui.enums.StudentMenuEnum;
import application.FitManager;
import application.OperationResult;
import domain.Student;

public class StudentsMenu{
    
    private UserInterface ui;
    private FitManager fitManager;

    // Construtor
    public StudentsMenu(UserInterface ui, FitManager fitManager){
        this.ui = ui;
        this.fitManager = fitManager;
    }


    public void run(){
        int optionSelected;

        //  String array para armazenar as opções dos menus
        String[] menuOptions = new String[StudentMenuEnum.values().length];

        //  Construção do array pegando as descrições do do enum
        for(int i = 0; i < StudentMenuEnum.values().length; i++){
            menuOptions[i] = StudentMenuEnum.values()[i].getOptionDescription();
        }

        do{ 
            ui.showMenu("GERENCIAR ALUNOS", menuOptions);

            optionSelected = ui.getInputInt("Selecione uma opção: ");

            switch(StudentMenuEnum.selectFromInt(optionSelected)){

                case REGISTER_STUDENT:
                    String studentName = ui.getInput("Digite o nome do aluno: ");
                    String studentCpf = ui.getInput("Digite o CPF do aluno: ");
                    String studentContact = ui.getInput("Digite um meio de contato (E-mail ou telefone): ");
                    LocalDate studentBirthDate = ui.getInputDate("Digite a data de nascimento do aluno (dd/mm/aaaa): ");
                    
                    OperationResult resultRegister = fitManager.registerStudent(studentName, studentCpf, studentContact, studentBirthDate);
                    if(resultRegister.getSuccess()){
                        ui.showMessage(resultRegister.getMessage());
                    } else {
                        ui.showMessage("Erro ao cadastrar aluno: " + resultRegister.getMessage());
                    }
                break;

                case SEARCH_BY_CPF:
                    String cpfToSearch = ui.getInput("Digite o CPF do aluno a ser consultado: ");
                    Student studentFound = fitManager.findStudentByCpf(cpfToSearch);
                    if(studentFound == null){
                        ui.showError("Aluno não encontrado.");
                    } else {
                        String studentNameList = studentFound.getName();
                        String studentCpfList = studentFound.getCpf();
                        String studentContactList = studentFound.getContact();
                        LocalDate studentBirthDateList = studentFound.getBirthDate();
                        ui.showStudent(studentNameList, studentCpfList, studentContactList, studentBirthDateList);
                    }
                break;

                case DELETE_STUDENT:
                    String cpfToDelete = ui.getInput("Digite o CPF do aluno a ser excluído: ");
                    OperationResult resultDelete = fitManager.removeStudent(cpfToDelete);
                    if(resultDelete.getSuccess()){
                        ui.showMessage(resultDelete.getMessage());
                    } else {
                        ui.showMessage("Erro ao excluir aluno: " + resultDelete.getMessage());
                    }
                break;

                case VIEW_ALL_STUDENTS:
                    ArrayList<Student> studentList = fitManager.listStudents();
                    if(studentList.isEmpty()){
                        ui.showMessage("Nenhum aluno cadastrado.");
                    } else {
                        ui.showMessage("Histórico de Alunos:");
                        for(Student student : studentList){
                            String studentNameList = student.getName();
                            String studentCpfList = student.getCpf();
                            String studentContactList = student.getContact();
                            LocalDate studentBirthDateList = student.getBirthDate();

                            ui.showStudent(studentNameList, studentCpfList, studentContactList, studentBirthDateList);

                        }
                    }
                break;

                case BACK:
                    ui.showMessage("Voltando ao menu principal...");
                break;

                default:
                    ui.showMessage("Opção inexistente, selecione uma das opçãoes acima.");
                break;
            }

        }while(optionSelected != StudentMenuEnum.BACK.getOptionNumber());

    }

}
