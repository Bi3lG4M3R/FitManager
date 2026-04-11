package application;

import java.time.Month;
import java.time.LocalDate;

public class OperationResult {
    private boolean success;
    private String message;
    private LocalDate data;

    public OperationResult() {
        this.success = false;
        this.message = "";
        this.data = LocalDate.of(2000, Month.JANUARY, 1);
    }
    
    public OperationResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public OperationResult(boolean success, String message, LocalDate data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    
    public void isSuccess(){ this.success = true; }

    public String getMessage() { return message; }

    public LocalDate getData() { return data; }
}