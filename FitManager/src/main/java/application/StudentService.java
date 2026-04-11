package application;

import domain.Student;
import java.util.ArrayList;

public class StudentService {
    
    private ArrayList<Student> students = new ArrayList<>();
    
    public OperationResult registerStudent(){
        
    }
    
    public Student findByCpf(String cpf){
        
    }
    
    public OperationResult removeStudent(String cpf){
        
    }
    
    public ArrayList<Student> listStudents(){
        
    }
    
    public boolean cpfExists(String cpf) {
        for (Student comparation : students) {
            if (comparation.getCpf().equals(cpf)) {
                return true;
            }
        }
        return false;
    }
}
