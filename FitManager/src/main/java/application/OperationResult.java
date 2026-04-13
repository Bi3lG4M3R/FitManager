package application;

public class OperationResult {
    private boolean success;
    private String message;
    private Object data;
    
    public OperationResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public OperationResult(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    
    public void isSuccess(){ this.success = true; }

    public String getMessage() { return message; }

    public Object getData() { return data; }
}