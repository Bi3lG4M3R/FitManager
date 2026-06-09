package exceptions;

public class DuplicateEnrollmentException extends BusinessException {
    public DuplicateEnrollmentException(String cpf) {
        super("O aluno '" + cpf + "' já possui matrícula ativa.");
    }
}