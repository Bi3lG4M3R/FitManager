package exceptions;

public class InvalidFormatFieldException extends ValidationException {
    public InvalidFormatFieldException(String fieldName, String expectedFormat) {
        super("O campo '" + fieldName + "' está inválido. Esperado: " + expectedFormat + ".");
    }
    public InvalidFormatFieldException(String fieldName, String expectedFormat, Throwable cause) {
        super("O campo '" + fieldName + "' está inválido. Esperado: " + expectedFormat + ".", cause);
    }
}