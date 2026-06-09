package exceptions;

public class BusinessException extends FitManagerException {
    public BusinessException(String message) {
        super(message);
    }
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}