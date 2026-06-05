package application;

public class OperationResult<T> {
    private boolean success;
    private String message;
    private T data;
    
    public OperationResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public OperationResult(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    
    public boolean isSuccess(){ return this.success; }

    public String getMessage() { return message; }

    public T getData() { return data; }
}