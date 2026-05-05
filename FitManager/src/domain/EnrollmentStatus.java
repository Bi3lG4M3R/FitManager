package domain;

public enum EnrollmentStatus{
    ACTIVE,
    CANCELLED;
    
    public String getDescricao() {
        return switch (this) {
            case ACTIVE -> "ATIVO";
            case CANCELLED -> "CANCELADO";
        };
    }
}
