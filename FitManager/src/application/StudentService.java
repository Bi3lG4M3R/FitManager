package application;

import domain.Student;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

/**
 * Serviço de alunos. Estende Repository<Student> (herança).
 *
 * Formato do arquivo students.csv (separador |):
 *   name|cpf|contact|birthDate(ISO yyyy-MM-dd)|active(true/false)
 *
 * O separador | foi escolhido para evitar conflito com vírgulas
 * que podem aparecer em nomes ou contatos.
 */
public class StudentService extends Repository<Student> {

    // ------------------------------------------------------------------ //
    // Regras de negócio                                                    //
    // ------------------------------------------------------------------ //

    public OperationResult<Student> registerStudent(String name, String cpf,
            String contact, LocalDate birthDate) {
        if (cpfExists(cpf)) {
            return new OperationResult<>(false, "CPF ja cadastrado.");
        }
        if (!Student.validateCpf(cpf)) {
            return new OperationResult<>(false, "CPF invalido.");
        }
        Student student = new Student(name, cpf, contact, birthDate);
        items.add(student);
        return new OperationResult<>(true, "Aluno cadastrado!", student);
    }

    public Student findByCpf(String cpf) {
        cpf = cpf.replaceAll("\\D", "");
        for (Student s : items) {
            if (s.getCpf().equals(cpf)) return s;
        }
        return null;
    }

    public OperationResult<Student> removeStudent(String cpf) {
        Student student = findByCpf(cpf);
        if (student == null) {
            return new OperationResult<>(false, "CPF não cadastrado.");
        }
        student.deactivate();
        return new OperationResult<>(true, "Estudante desativado.", student);
    }

    public ArrayList<Student> listStudents() { return items; }

    public boolean cpfExists(String cpf) { return findByCpf(cpf) != null; }

    OperationResult<Student> reactivateStudent(String cpf) {
        Student student = findByCpf(cpf);
        if (student == null) {
            return new OperationResult<>(false, "CPF não cadastrado.");
        }
        student.activate();
        return new OperationResult<>(true, "Estudante reativado.", student);
    }

    // ------------------------------------------------------------------ //
    // Persistência                                                          //
    // ------------------------------------------------------------------ //

    private static final String SEP = "|";
    private static final String SEP_REGEX = "\\|";

    /**
     * Grava todos os alunos no arquivo, um por linha.
     * Usa try-with-resources para garantir fechamento mesmo em falha.
     */
    @Override
    public void save(String filePath) throws PersistenceException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {

            for (Student s : items) {
                writer.write(encode(s));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new PersistenceException(
                "Falha ao gravar arquivo de alunos: " + e.getMessage(), filePath, e);
        }
    }

    /**
     * Reconstrói a lista de alunos a partir do arquivo.
     * Arquivo ausente → coleção vazia (situação normal).
     * Linha mal formada → PersistenceException (arquivo corrompido).
     */
    @Override
    public void load(String filePath) throws PersistenceException {
        File file = new File(filePath);
        if (!file.exists()) return; // arquivo ausente é normal

        items.clear();

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
                    throw new PersistenceException(
                        "Arquivo de alunos corrompido na linha " + lineNumber
                        + ": esperado 5 campos, encontrado " + parts.length,
                        filePath);
                }

                try {
                    String name      = parts[0];
                    String cpf       = parts[1];
                    String contact   = parts[2];
                    LocalDate birth  = LocalDate.parse(parts[3]);
                    boolean active   = Boolean.parseBoolean(parts[4]);

                    Student student = new Student(name, cpf, contact, birth);
                    if (!active) student.deactivate();
                    items.add(student);

                } catch (DateTimeParseException e) {
                    throw new PersistenceException(
                        "Arquivo de alunos corrompido na linha " + lineNumber
                        + ": data inválida '" + parts[3] + "'",
                        filePath, e);
                }
            }

        } catch (IOException e) {
            throw new PersistenceException(
                "Falha ao ler arquivo de alunos: " + e.getMessage(), filePath, e);
        }
    }

    // ------------------------------------------------------------------ //
    // Serialização de uma linha                                            //
    // ------------------------------------------------------------------ //

    /** Converte um Student para uma linha de texto. */
    private String encode(Student s) {
        return String.join(SEP,
            s.getName(),
            s.getCpf(),
            s.getContact(),
            s.getBirthDate().toString(),        // ISO: yyyy-MM-dd
            String.valueOf(s.isActive())
        );
    }
}