package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import domain.Enrollment;
import domain.payment.Payment;


public class FinancialReport {
 
    private int month;
    private int year;
    private double totalRevenue;
    private Map<String, Double> revenueByPlanType;
    private Map<String, Double> revenueByPaymentType;
    private double totalProcessingFees;
    private int enrollmentsStarted;
    private int enrollmentsCancelled;
    private ArrayList<String> mostContractedPlanTypes;

    public FinancialReport(int month, int year) {
        this.month = month;
        this.year = year;
        this.totalRevenue = 0.0;
        this.revenueByPlanType = new HashMap<>();
        this.revenueByPaymentType = new HashMap<>();
        this.totalProcessingFees = 0.0;
        this.enrollmentsStarted = 0;
        this.enrollmentsCancelled = 0;
        this.mostContractedPlanTypes = new ArrayList<>();
    }

    public void calculate(ArrayList<Enrollment> enrollments) {
        resetMetrics();

        Map<String, Integer> contractedCountByPlanType = new HashMap<>();

        for (Enrollment enrollment : enrollments) {
            String planTypeName = enrollment.getPlan().getType().getDescription();

            if (isSamePeriod(enrollment.getStartDate())) {
                this.enrollmentsStarted++;
                contractedCountByPlanType.put(
                    planTypeName,
                    contractedCountByPlanType.getOrDefault(planTypeName, 0) + 1
                );
            }

            if (isSamePeriod(enrollment.getCancellationDate())) {
                this.enrollmentsCancelled++;
            }

            for (Payment payment : enrollment.getPayments()) {
                if (!isSamePeriod(payment.getDate())) {
                    continue;
                }

                double processingFee = payment.getProcessingFee();
                double netRevenue = payment.getAmount() - processingFee;
                String paymentTypeName = payment.getType().getDescription();

                this.totalRevenue += netRevenue;
                this.totalProcessingFees += processingFee;

                revenueByPlanType.put(
                    planTypeName,
                    revenueByPlanType.getOrDefault(planTypeName, 0.0) + netRevenue
                );

                revenueByPaymentType.put(
                    paymentTypeName,
                    revenueByPaymentType.getOrDefault(paymentTypeName, 0.0) + netRevenue
                );
            }
        }

        updateMostContractedPlanTypes(contractedCountByPlanType);
    }

    private void resetMetrics() {
        this.totalRevenue = 0.0;
        this.revenueByPlanType.clear();
        this.revenueByPaymentType.clear();
        this.totalProcessingFees = 0.0;
        this.enrollmentsStarted = 0;
        this.enrollmentsCancelled = 0;
        this.mostContractedPlanTypes.clear();
    }

    private void updateMostContractedPlanTypes(Map<String, Integer> contractedCountByPlanType) {
        if (contractedCountByPlanType.isEmpty()) {
            return;
        }

        int maxCount = 0;
        for (Integer count : contractedCountByPlanType.values()) {
            if (count > maxCount) {
                maxCount = count;
            }
        }

        TreeMap<String, Integer> ordered = new TreeMap<>(contractedCountByPlanType);
        for (Map.Entry<String, Integer> entry : ordered.entrySet()) {
            if (entry.getValue() == maxCount) {
                mostContractedPlanTypes.add(entry.getKey());
            }
        }
    }

    private boolean isSamePeriod(LocalDate date) {
        return date != null && date.getMonthValue() == this.month && date.getYear() == this.year;
    }

    public String toDisplayString() {
        StringBuilder reportText = new StringBuilder();
        reportText.append("Relatório Financeiro Mensal\n");
        reportText.append("Período: ").append(String.format("%02d/%d", month, year)).append("\n");
        reportText.append("----------------------------------\n");
        reportText.append("Receita Total: R$ ").append(String.format("%.2f", totalRevenue)).append("\n");
        reportText.append("Taxas de Processamento: R$ ").append(String.format("%.2f", totalProcessingFees)).append("\n");
        reportText.append("Matrículas Iniciadas: ").append(enrollmentsStarted).append("\n");
        reportText.append("Matrículas Canceladas: ").append(enrollmentsCancelled).append("\n\n");

        reportText.append("Receita por Tipo de Plano:\n");
        appendRevenueMap(reportText, revenueByPlanType);

        reportText.append("\nReceita por Tipo de Pagamento:\n");
        appendRevenueMap(reportText, revenueByPaymentType);

        reportText.append("\nTipo(s) de Plano Mais Contratado(s):\n");
        if (mostContractedPlanTypes.isEmpty()) {
            reportText.append("- Nenhum\n");
        } else {
            for (String planType : mostContractedPlanTypes) {
                reportText.append("- ").append(planType).append("\n");
            }
        }

        return reportText.toString();
    }

    private void appendRevenueMap(StringBuilder reportText, Map<String, Double> source) {
        if (source.isEmpty()) {
            reportText.append("- Nenhum\n");
            return;
        }

        TreeMap<String, Double> ordered = new TreeMap<>(source);
        for (Map.Entry<String, Double> entry : ordered.entrySet()) {
            reportText
                .append("- ")
                .append(entry.getKey())
                .append(": R$ ")
                .append(String.format("%.2f", entry.getValue()))
                .append("\n");
        }
    }

    public String exportToFile(String directoryPath) throws IOException {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = String.format("financial_report_%04d_%02d.txt", this.year, this.month);
        String fullPath = directoryPath + "/" + fileName;

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(fullPath), StandardCharsets.UTF_8))) {
            writer.write(toDisplayString());
        }

        return fullPath;
    }

    public int getMonth() { return month; }

    public int getYear() { return year; }

    public double getTotalRevenue() { return totalRevenue; }

    public Map<String, Double> getRevenueByPlanType() { return revenueByPlanType; }

    public Map<String, Double> getRevenueByPaymentType() { return revenueByPaymentType; }

    public double getTotalProcessingFees() { return totalProcessingFees; }

    public int getEnrollmentsStarted() { return enrollmentsStarted; }

    public int getEnrollmentsCancelled() { return enrollmentsCancelled; }

    public ArrayList<String> getMostContractedPlanTypes() { return mostContractedPlanTypes; }
}