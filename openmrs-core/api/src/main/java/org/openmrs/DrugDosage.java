package org.openmrs;

public class DrugDosage {
	
	private Integer dosageId;

	private Concept dosageForm;

	private Double maximumDailyDose;

	private Double minimumDailyDose;

	private Concept doseLimitUnits;

	
	public Integer getDosageId() {
		return dosageId;
	}

	public void setDosageId(Integer dosageId) {
		this.dosageId = dosageId;
	}

	public Concept getDosageForm() {
		return dosageForm;
	}

	public void setDosageForm(Concept dosageForm) {
		this.dosageForm = dosageForm;
	}

	public Double getMaximumDailyDose() {
		return maximumDailyDose;
	}

	public void setMaximumDailyDose(Double maximumDailyDose) {
		this.maximumDailyDose = maximumDailyDose;
	}

	public Double getMinimumDailyDose() {
		return minimumDailyDose;
	}

	public void setMinimumDailyDose(Double minimumDailyDose) {
		this.minimumDailyDose = minimumDailyDose;
	}

	public Concept getDoseLimitUnits() {
		return doseLimitUnits;
	}

	public void setDoseLimitUnits(Concept doseLimitUnits) {
		this.doseLimitUnits = doseLimitUnits;
	}
}
