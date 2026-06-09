package exceptions;

public class StudentWithActiveEnrollmentException extends BusinessException {
    public StudentWithActiveEnrollmentException(String cpf) {
        super("Não é possível desativar o aluno '" + cpf + "' porque ele possui matrícula ativa.");
    }
}