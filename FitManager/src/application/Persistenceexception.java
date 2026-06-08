package application;

/**
 * Exceção lançada quando ocorre falha de I/O na camada de persistência.
 *
 * Situações cobertas:
 *  - Arquivo corrompido na leitura (formato inválido, dados inconsistentes)
 *  - Falha de escrita no encerramento (disco cheio, sem permissão)
 *
 * Arquivo ausente NÃO lança esta exceção — é situação normal tratada
 * internamente em cada load(), que simplesmente mantém a coleção vazia.
 */
public class PersistenceException extends Exception {

    private final String filePath;

    public PersistenceException(String message, String filePath) {
        super(message);
        this.filePath = filePath;
    }

    public PersistenceException(String message, String filePath, Throwable cause) {
        super(message, cause);
        this.filePath = filePath;
    }

    /** Caminho do arquivo que causou o problema. */
    public String getFilePath() {
        return filePath;
    }
}