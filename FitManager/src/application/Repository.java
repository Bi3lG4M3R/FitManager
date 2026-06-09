package application;

import exceptions.PersistenceException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe abstrata genérica que centraliza o comportamento estrutural
 * comum a todos os serviços: manter uma coleção tipada, listar e contar.
 *
 * Os métodos save() e load() são abstratos — cada serviço concreto
 * fornece sua própria implementação de persistência em texto.
 *
 * Decisão de projeto: os serviços usam HERANÇA (extends Repository<T>)
 * pela conveniência de implementação e pela baixa complexidade da hierarquia.
 * O tradeoff semântico (um serviço "é" um repositório?) foi aceito pelo grupo
 * em favor da simplicidade, conforme permitido pelo enunciado.
 */
public abstract class Repository<T> {

    protected List<T> items = new ArrayList<>();

    /** Retorna todos os itens da coleção. */
    public List<T> listAll() {
        return Collections.unmodifiableList(items);
    }

    /** Retorna a quantidade de itens na coleção. */
    public int count() {
        return items.size();
    }

    /**
     * Grava o estado atual da coleção no arquivo indicado.
     * Cada serviço concreto define o formato de texto.
     *
     * @param filePath caminho do arquivo de destino
     * @throws PersistenceException se ocorrer falha de escrita
     */
    public abstract void save(String filePath) throws PersistenceException;

    /**
     * Reconstrói a coleção a partir do arquivo indicado.
     * Arquivo ausente é situação normal — a coleção permanece vazia.
     * Arquivo corrompido lança PersistenceException.
     *
     * @param filePath caminho do arquivo de origem
     * @throws PersistenceException se o arquivo existir mas estiver corrompido
     */
    public abstract void load(String filePath) throws PersistenceException;
}