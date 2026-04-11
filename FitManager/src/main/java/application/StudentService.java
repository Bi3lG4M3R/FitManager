package application;

import domain.Student;
import java.time.LocalDate;
import java.util.ArrayList;

public class StudentService {
    
    private ArrayList<Student> students = new ArrayList<>();
    
    public OperationResult registerStudent(String name, String cpf, String contact, LocalDate birthDate){
        if (cpfExists(cpf)) {
            return new OperationResult(false, "CPF ja cadastrado.");
        }
        if (!Student.validateCpf(cpf)) {
            return new OperationResult(false, "CPF invalido.");
        }
        Student student = new Student(name, cpf, contact, birthDate);
        students.add(student);
        return new OperationResult(true, "Aluno cadastrado!", student);
    }
    
    public Student findByCpf(String cpf){
        for (Student comparation : students) {
            if (comparation.getCpf().equals(cpf)) {
                return comparation;
            }
        }
        return null;
    }
    
    public OperationResult removeStudent(String cpf) {
        Student student = findByCpf(cpf);
        if (student == null) {
            return new OperationResult(false, "CPF não cadastrado.");
        }
        student.deactivate();
        return new OperationResult(true, "Estudante removido.", student);
    }
    
    public ArrayList<Student> listStudents(){
        return students;
    }
    
    public boolean cpfExists(String cpf) {
        return findByCpf(cpf) != null;
    }   
}
