/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs;

import org.openmrs.api.APIException;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.util.Locale;

/**
 * @since 1.10
 */
public class SimpleDosingInstructions extends BaseDosingInstructions {

	private Double dose;

	private Concept doseUnits;

	private DrugOrder temp;

	private Concept route;

	private OrderFrequency frequency;

	private Integer duration;

	private Concept durationUnits;

	private Boolean asNeeded;

	private String asNeededCondition;

	private String administrationInstructions;

	/**
	 * @see DosingInstructions#getDosingInstructionsAsString(java.util.Locale)
	 */
	@Override
	public String getDosingInstructionsAsString(Locale locale) {
		StringBuilder dosingInstructions = new StringBuilder();
		dosingInstructions.append(this.dose);
		dosingInstructions.append(" ");
		dosingInstructions.append(this.doseUnits.getName(locale).getName());
		dosingInstructions.append(" ");
		dosingInstructions.append(this.route.getName(locale).getName());
		dosingInstructions.append(" ");
		dosingInstructions.append(this.frequency);
		if (duration != null) {
			dosingInstructions.append(" ");
			dosingInstructions.append(this.duration);
			dosingInstructions.append(" ");
			dosingInstructions.append(this.durationUnits.getName(locale).getName());
		}
		if (this.asNeeded) {
			dosingInstructions.append(" ");
			dosingInstructions.append("PRN");
			if (this.asNeededCondition != null) {
				dosingInstructions.append(" ");
				dosingInstructions.append(this.asNeededCondition);
			}
		}
		if (this.administrationInstructions != null) {
			dosingInstructions.append(" ");
			dosingInstructions.append(this.administrationInstructions);
		}
		return dosingInstructions.toString();
	}

	/**
	 * @see DosingInstructions#setDosingInstructions(DrugOrder)
	 */
	@Override
	public void setDosingInstructions(DrugOrder order) {
		temp = new DrugOrder();
		temp.setDosingType(this.getClass());
		temp.setDose(this.dose);
		temp.setDoseUnits(this.doseUnits);
		temp.setRoute(this.route);
		temp.setFrequency(this.frequency);
		temp.setDuration(this.duration);
		temp.setDurationUnits(this.durationUnits);
		temp.setAsNeeded(this.asNeeded);
		temp.setAsNeededCondition(this.asNeededCondition);
		temp.setDosingInstructions(this.administrationInstructions);
		order.copyHelper(temp);
	}

	/**
	 * @see DosingInstructions#getDosingInstructions(DrugOrder)
	 */
	@Override
	public DosingInstructions getDosingInstructions(DrugOrder order) {
		temp.copyHelper(order);
		if (!order.getDosingType().equals(this.getClass())) {
			throw new APIException("DrugOrder.error.dosingTypeIsMismatched", new Object[] { this.getClass().getName(),
					order.getDosingType() });
		}
		SimpleDosingInstructions simpleDosingInstructions = new SimpleDosingInstructions();
		simpleDosingInstructions.setDose(temp.getDose());
		simpleDosingInstructions.setDoseUnits(temp.getDoseUnits());
		simpleDosingInstructions.setRoute(temp.getRoute());
		simpleDosingInstructions.setFrequency(temp.getFrequency());
		simpleDosingInstructions.setDuration(temp.getDuration());
		simpleDosingInstructions.setDurationUnits(temp.getDurationUnits());
		simpleDosingInstructions.setAsNeeded(temp.getAsNeeded());
		simpleDosingInstructions.setAsNeededCondition(temp.getAsNeededCondition());
		simpleDosingInstructions.setAdministrationInstructions(temp.getDosingInstructions());
		return simpleDosingInstructions;
	}

	/**
	 * @see DosingInstructions#validate(DrugOrder,
	 *      org.springframework.validation.Errors)
	 * @param order
	 * @param errors
	 *               <strong>Should</strong> reject a duration unit with a mapping
	 *               of an invalid type
	 */
	@Override
	public void validate(DrugOrder order, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "dose", "DrugOrder.error.doseIsNullForDosingTypeSimple");
		ValidationUtils.rejectIfEmpty(errors, "doseUnits", "DrugOrder.error.doseUnitsIsNullForDosingTypeSimple");
		ValidationUtils.rejectIfEmpty(errors, "route", "DrugOrder.error.routeIsNullForDosingTypeSimple");
		ValidationUtils.rejectIfEmpty(errors, "frequency", "DrugOrder.error.frequencyIsNullForDosingTypeSimple");
		if (order.getAutoExpireDate() == null && order.getDurationUnits() != null
				&& Duration.getCode(order.getDurationUnits()) == null) {
			errors.rejectValue("durationUnits", "DrugOrder.error.durationUnitsNotMappedToSnomedCtDurationCode");
		}
	}

	public Double getDose() {
		return dose;
	}

	public void setDose(Double dose) {
		this.dose = dose;
	}

	public Concept getDoseUnits() {
		return doseUnits;
	}

	public void setDoseUnits(Concept doseUnits) {
		this.doseUnits = doseUnits;
	}

	public Concept getRoute() {
		return route;
	}

	public void setRoute(Concept route) {
		this.route = route;
	}

	public OrderFrequency getFrequency() {
		return frequency;
	}

	public void setFrequency(OrderFrequency frequency) {
		this.frequency = frequency;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Concept getDurationUnits() {
		return durationUnits;
	}

	public void setDurationUnits(Concept durationUnits) {
		this.durationUnits = durationUnits;
	}

	public Boolean getAsNeeded() {
		return asNeeded;
	}

	public void setAsNeeded(Boolean asNeeded) {
		this.asNeeded = asNeeded;
	}

	public String getAsNeededCondition() {
		return asNeededCondition;
	}

	public void setAsNeededCondition(String asNeededCondition) {
		this.asNeededCondition = asNeededCondition;
	}

	public String getAdministrationInstructions() {
		return administrationInstructions;
	}

	public void setAdministrationInstructions(String administrationInstructions) {
		this.administrationInstructions = administrationInstructions;
	}
}
