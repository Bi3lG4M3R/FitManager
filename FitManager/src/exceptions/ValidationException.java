package exceptions;

public class ValidationException extends FitManagerException {
    public ValidationException(String message) {
        super(message);
    }
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}