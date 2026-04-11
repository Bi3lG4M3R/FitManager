package application;

import domain.Student;
import java.util.ArrayList;

public class StudentService {
    
    private ArrayList<Student> students = new ArrayList<>();
    
    public OperationResult registerStudent(){
        
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
        return new OperationResult(true, "Estudante removido.");
    }
    
    public ArrayList<Student> listStudents(){
        return students;
    }
    
    public boolean cpfExists(String cpf) {
        return findByCpf(cpf) != null;
    }   
}
