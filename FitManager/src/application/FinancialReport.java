package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import domain.Enrollment;
import domain.plan.Plan;
import domain.plan.PlanType;
import domain.payment.PaymentType;


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
}