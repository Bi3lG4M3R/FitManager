package application;

import domain.plan.*;
import exceptions.PersistenceException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import exceptions.CorruptedFileException;
import exceptions.WriteFailureException;

public class PlanService extends Repository<Plan> {
    private static ArrayList<Plan> plans;
    // ------------------------------------------------------------------ //
    // Regras de negócio                                                    //
    // ------------------------------------------------------------------ //

    public ArrayList<Plan> listPlans() { return plans; }

    /** Retorna OperationResult<Plan> conforme diagrama — método de instância. */
    public OperationResult<Plan> findByName(String name) {
        for (Plan p : plans) {
            if (name != null && name.equals(p.getName()))
                return new OperationResult<>(true, "Plano encontrado.", p);
        }
        return new OperationResult<>(false, "Plano '" + name + "' não encontrado.");
    }

    public boolean nameExists(String name) {
        return findByName(name).isSuccess();
    }

    public OperationResult<Plan> registerPlan(String name, String description,
            PlanType type, int minDurationMonths, double pricePerMonth) {

        if (name.isBlank() || nameExists(name))
            return new OperationResult<>(false, "Nome inválido ou já existente.");
        if (description.isEmpty())
            return new OperationResult<>(false, "Descrição inválida.");
        if (type == null)
            return new OperationResult<>(false, "Tipo inválido.");
        if (minDurationMonths <= 0)
            return new OperationResult<>(false, "Duração mínima inválida.");
        if (pricePerMonth <= 0)
            return new OperationResult<>(false, "Preço inválido.");

        Plan temporary;
        switch (type) {
            case MONTHLY:
                if (minDurationMonths < 1)
                    return new OperationResult<>(false, "Duração mínima inválida.");
                
                temporary = new PlanMonthly(name, description, minDurationMonths, pricePerMonth);
            break;
            
            case QUARTERLY:
                if (minDurationMonths < 3)
                    return new OperationResult<>(false, "Duração mínima inválida.");
                
                temporary = new PlanQuarterly(name, description, minDurationMonths, pricePerMonth);
            break;
            
            case SEMI_ANNUAL:
                if (minDurationMonths < 6)
                    return new OperationResult<>(false, "Duração mínima inválida.");
                
                temporary = new PlanSemiAnnual(name, description, minDurationMonths, pricePerMonth);
            break;
            
            case ANNUAL:
                if (minDurationMonths < 12)
                    return new OperationResult<>(false, "Duração mínima inválida.");
                
                temporary = new PlanAnnual(name, description, minDurationMonths, pricePerMonth);
            break;
            
            default:
                return new OperationResult<>(false, "Tipo inválido");
        }

        plans.add(temporary);
        return new OperationResult<>(true, "O plano " + name + " foi criado com sucesso.", temporary);
    }

    public OperationResult<Plan> updatePrice(String name, double newPrice) {
        if (newPrice <= 0)
            return new OperationResult<>(false, "Preço inválido.");

        OperationResult<Plan> result = findByName(name);
        if (result.isSuccess()) {
            result.getData().updatePrice(newPrice);
            return new OperationResult<>(true,
                "O valor do plano " + result.getData().getName() + " foi alterado com sucesso.",
                result.getData());
        }
        return new OperationResult<>(false, "O plano " + name + " não foi localizado.");
    }

    // ------------------------------------------------------------------ //
    // Persistência                                                          //
    // ------------------------------------------------------------------ //

    private static final String SEP       = "|";
    private static final String SEP_REGEX = "\\|";

    @Override
    public void save(String filePath) throws PersistenceException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {

            for (Plan p : plans) {
                writer.write(encode(p));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new WriteFailureException(
                "Falha ao gravar arquivo de planos: " + e.getMessage(), filePath, e);
        }
    }

    @Override
    public void load(String filePath) throws PersistenceException {
        File file = new File(filePath);
        if (!file.exists()) return;

        plans.clear();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(SEP_REGEX, -1);
                if (parts.length != 5) {
                    throw new CorruptedFileException(
                        "Arquivo de planos corrompido na linha " + lineNumber
                        + ": esperado 5 campos, encontrado " + parts.length, filePath);
                }

                try {
                    String typeName    = parts[0];
                    String name        = parts[1];
                    String description = parts[2];
                    int minDuration    = Integer.parseInt(parts[3]);
                    double price       = Double.parseDouble(parts[4]);

                    plans.add(instantiatePlan(typeName, name, description,
                                             minDuration, price, lineNumber, filePath));
                } catch (NumberFormatException e) {
                    throw new CorruptedFileException(
                        "Arquivo de planos corrompido na linha " + lineNumber
                        + ": valor numérico inválido", filePath, e);
                }
            }

        } catch (IOException e) {
            throw new WriteFailureException(
                "Falha ao ler arquivo de planos: " + e.getMessage(), filePath, e);
        }
    }

    // ------------------------------------------------------------------ //
    // Serialização / deserialização                                        //
    // ------------------------------------------------------------------ //

    private String encode(Plan p) {
        return String.join(SEP,
            p.getType().name(),
            p.getName(),
            p.getDescription(),
            String.valueOf(p.getMinDurationMonths()),
            String.valueOf(p.getPricePerMonth())
        );
    }

    private Plan instantiatePlan(String typeName, String name, String description,
            int minDuration, double price, int lineNumber, String filePath)
            throws PersistenceException {
        switch (typeName) {
            case "MONTHLY":     return new PlanMonthly(name, description, minDuration, price);
            case "QUARTERLY":   return new PlanQuarterly(name, description, minDuration, price);
            case "SEMI_ANNUAL": return new PlanSemiAnnual(name, description, minDuration, price);
            case "ANNUAL":      return new PlanAnnual(name, description, minDuration, price);
            default:
                throw new CorruptedFileException(
                    "Arquivo de planos corrompido na linha " + lineNumber
                    + ": tipo desconhecido '" + typeName + "'", filePath);
        }
    }
}