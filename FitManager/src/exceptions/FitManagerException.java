package exceptions;

public class FitManagerException extends Exception {
    public FitManagerException(String message) { super(message); }
    public FitManagerException(String message, Throwable cause) { super(message, cause); }
}