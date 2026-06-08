package exceptions;

public class PersistenceException extends FitManagerException {
    private final String filePath;
    public PersistenceException(String message, String filePath) {
        super(message);
        this.filePath = filePath;
    }
    public PersistenceException(String message, String filePath, Throwable cause) {
        super(message, cause);
        this.filePath = filePath;
    }
    public String getFilePath() { return filePath; }
}