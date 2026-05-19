package ui;

import java.time.LocalDate;

import domain.Enrollment;
import domain.payment.PaymentType;
import domain.plan.PlanType;

public interface UserInterface {

	void showMessage(String message);

	void showError(String error);

	int showMenu(String tittle, String[] options, String prompt);

	void showEnrollment(int code, String studentName, String planName, LocalDate startDate, LocalDate endDate, int durationMonths, double totalPrice, double pendingAmount, String status);

	void showEnrollment(Enrollment enrollment);

	void showCancelledEnrollment(int code, String studentName, String planName, LocalDate startDate, LocalDate endDate, int durationMonths, double totalPrice, String status, String cancellationReason);

	void showPlan(String planNameList, String planDescriptionList, String planTypeList, int planMinDurationList, double planPricePerMonthList);

	void showStudent(String studentNameList, String studentCpfList, String studentContactList, String studentBirthDateList);

	void showPlanTypeOptions();

	void showPaymentTypeOptions();

	String getInput(String prompt);

	int getInputInt(String prompt);

	double getInputDouble(String prompt);

	LocalDate getInputDate(String prompt);

	PlanType getInputPlanType(String prompt);

	PaymentType getInputPaymentType(String prompt);
}
