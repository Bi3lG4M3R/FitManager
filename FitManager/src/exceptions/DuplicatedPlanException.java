package exceptions;

public class DuplicatedPlanException extends BusinessException {
    public DuplicatedPlanException(String planName) {
        super("Já existe um plano cadastrado com o nome '" + planName + "'.");
    }
}