package exceptions;

public class WriteFailureException extends PersistenceException {
    public WriteFailureException(String message, String filePath) {
        super(message, filePath);
    }
    public WriteFailureException(String message, String filePath, Throwable cause) {
        super(message, filePath, cause);
    }
}