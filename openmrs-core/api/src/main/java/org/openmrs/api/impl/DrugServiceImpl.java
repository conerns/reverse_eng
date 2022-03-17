package org.openmrs.api.impl;

import org.openmrs.Concept;
import org.openmrs.api.DrugService;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DrugServiceImpl implements DrugService {


	/**
	 * @see org.openmrs.api.OrderService#getDrugRoutes()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Concept> getDrugRoutes() {
		return getSetMembersOfConceptSetFromGP(OpenmrsConstants.GP_DRUG_ROUTES_CONCEPT_UUID);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Concept> getDrugDosingUnits() {
		return getSetMembersOfConceptSetFromGP(OpenmrsConstants.GP_DRUG_DOSING_UNITS_CONCEPT_UUID);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Concept> getDrugDispensingUnits() {
		List<Concept> dispensingUnits = new ArrayList<>(
			getSetMembersOfConceptSetFromGP(OpenmrsConstants.GP_DRUG_DISPENSING_UNITS_CONCEPT_UUID));
		for (Concept concept : getDrugDosingUnits()) {
			if (!dispensingUnits.contains(concept)) {
				dispensingUnits.add(concept);
			}
		}
		return dispensingUnits;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Concept> getDurationUnits() {
		return getSetMembersOfConceptSetFromGP(OpenmrsConstants.GP_DURATION_UNITS_CONCEPT_UUID);
	}

	/**
	 * @see org.openmrs.api.OrderService#getTestSpecimenSources()
	 */
	@Override
	public List<Concept> getTestSpecimenSources() {
		return getSetMembersOfConceptSetFromGP(OpenmrsConstants.GP_TEST_SPECIMEN_SOURCES_CONCEPT_UUID);
	}

	@Override
	public Concept getNonCodedDrugConcept() {
		String conceptUuid = Context.getAdministrationService().getGlobalProperty(OpenmrsConstants.GP_DRUG_ORDER_DRUG_OTHER);
		if (StringUtils.hasText(conceptUuid)) {
			return Context.getConceptService().getConceptByUuid(conceptUuid);
		}
		return null;
	}
	
	private List<Concept> getSetMembersOfConceptSetFromGP(String globalProperty) {
		String conceptUuid = Context.getAdministrationService().getGlobalProperty(globalProperty);
		Concept concept = Context.getConceptService().getConceptByUuid(conceptUuid);
		if (concept != null && concept.getSet()) {
			return concept.getSetMembers();
		}
		return Collections.emptyList();
	}

	@Override
	public void onStartup() {
		
	}

	@Override
	public void onShutdown() {

	}
}
