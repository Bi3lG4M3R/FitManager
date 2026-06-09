// application/StudentService.java
package application;

import domain.Student;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import exceptions.CorruptedFileException;
import exceptions.PersistenceException;
import exceptions.WriteFailureException;

public class StudentService extends Repository<Student> {
    private ArrayList<Student> students = new ArrayList<>();

    public OperationResult<Student> registerStudent(String name, String cpf, String contact, LocalDate birthDate) {
        try {
            if (name == null || name.isBlank())
                return new OperationResult<>(false, "O campo 'nome' é obrigatório.");
            if (cpf == null || cpf.isBlank())
                return new OperationResult<>(false, "O campo 'CPF' é obrigatório.");
            if (contact == null || contact.isBlank())
                return new OperationResult<>(false, "O campo 'contato' é obrigatório.");
            if (birthDate == null)
                return new OperationResult<>(false, "A data de nascimento é obrigatória.");
            if (!Student.validateCpf(cpf))
                return new OperationResult<>(false, "CPF inválido. Deve conter 11 dígitos válidos.");
            if (cpfExists(cpf))
                return new OperationResult<>(false, "Já existe um aluno cadastrado com este CPF.");
            Student student = new Student(name, cpf, contact, birthDate);
            students.add(student);
            return new OperationResult<>(true, "Aluno cadastrado!", student);
        } catch (Exception e) {
            return new OperationResult<>(false, "Erro interno ao cadastrar aluno: " + e.getMessage());
        }
    }

    public Student findByCpf(String cpf) {
        if (cpf == null) return null;
        cpf = cpf.replaceAll("\\D", "");
        for (Student s : students) {
            if (s.getCpf().equals(cpf)) return s;
        }
        return null;
    }

    public OperationResult<Student> removeStudent(String cpf) {
        try {
            if (cpf == null || cpf.isBlank())
                return new OperationResult<>(false, "CPF é obrigatório.");
            Student student = findByCpf(cpf);
            if (student == null)
                return new OperationResult<>(false, "CPF não cadastrado.");
            student.deactivate();
            return new OperationResult<>(true, "Estudante desativado.", student);
        } catch (Exception e) {
            return new OperationResult<>(false, "Erro interno ao desativar aluno: " + e.getMessage());
        }
    }

    public ArrayList<Student> listStudents() {
        return students;
    }

    public boolean cpfExists(String cpf) {
        return findByCpf(cpf) != null;
    }

    OperationResult<Student> reactivateStudent(String cpf) {
        try {
            if (cpf == null || cpf.isBlank())
                return new OperationResult<>(false, "CPF é obrigatório.");
            Student student = findByCpf(cpf);
            if (student == null)
                return new OperationResult<>(false, "CPF não cadastrado.");
            student.activate();
            return new OperationResult<>(true, "Estudante reativado.", student);
        } catch (Exception e) {
            return new OperationResult<>(false, "Erro interno ao reativar aluno: " + e.getMessage());
        }
    }

    // Persistência
    private static final String SEP = "|";
    private static final String SEP_REGEX = "\\|";

    @Override
    public void save(String filePath) throws PersistenceException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            for (Student s : students) {
                writer.write(encode(s));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new WriteFailureException("Falha ao gravar arquivo de alunos: " + e.getMessage(), filePath, e);
        }
    }

    @Override
    public void load(String filePath) throws PersistenceException {
        File file = new File(filePath);
        if (!file.exists()) return;
        students.clear();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(SEP_REGEX, -1);
                if (parts.length != 5) {
                    throw new CorruptedFileException("Arquivo de alunos corrompido na linha " + lineNumber, filePath);
                }
                try {
                    String name = parts[0];
                    String cpf = parts[1];
                    String contact = parts[2];
                    LocalDate birth = LocalDate.parse(parts[3]);
                    boolean active = Boolean.parseBoolean(parts[4]);
                    Student student = new Student(name, cpf, contact, birth);
                    if (!active) student.deactivate();
                    students.add(student);
                } catch (DateTimeParseException e) {
                    throw new CorruptedFileException("Arquivo de alunos corrompido na linha " + lineNumber + ": data inválida", filePath, e);
                }
            }
        } catch (IOException e) {
            throw new WriteFailureException("Falha ao ler arquivo de alunos: " + e.getMessage(), filePath, e);
        }
    }

    private String encode(Student s) {
        return String.join(SEP, s.getName(), s.getCpf(), s.getContact(), s.getBirthDate().toString(), String.valueOf(s.isActive()));
    }
}