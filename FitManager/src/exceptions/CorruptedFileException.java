package exceptions;

public class CorruptedFileException extends PersistenceException {
    public CorruptedFileException(String message, String filePath) {
        super(message, filePath);
    }
    public CorruptedFileException(String message, String filePath, Throwable cause) {
        super(message, filePath, cause);
    }
}