package domain;

public enum EnrollmentStatus{
    ACTIVE,
    CANCELLED;
    
    public String getDescription() {
        return switch (this) {
            case ACTIVE -> "ATIVO";
            case CANCELLED -> "CANCELADO";
        };
    }
}
