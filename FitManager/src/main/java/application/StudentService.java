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
    
    public OperationResult removeStudent(String cpf){
        
    }
    
    public ArrayList<Student> listStudents(){
        return students;
    }
    
    public boolean cpfExists(String cpf) {
        return findByCpf(cpf) != null;
    }   
}
