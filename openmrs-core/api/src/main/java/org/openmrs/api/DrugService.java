package org.openmrs.api;

import org.openmrs.Concept;
import org.openmrs.annotation.Authorized;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.PrivilegeConstants;

import java.util.List;

public interface DrugService extends OpenmrsService{
	
	/**
	 * Gets the possible drug routes, i.e the set members for the concept that matches the uuid
	 * specified as the value for the global property
	 * {@link OpenmrsConstants#GP_DRUG_ROUTES_CONCEPT_UUID}
	 *
	 * @return concept list of drug routes
	 * @since 1.10
	 * <strong>Should</strong> return an empty list if nothing is configured
	 */
	@Authorized(PrivilegeConstants.GET_CONCEPTS)
	public List<Concept> getDrugRoutes();

	/**
	 * Gets the possible drug dosing units, i.e the set members for the concept that matches the
	 * uuid specified as the value for the global property
	 * {@link OpenmrsConstants#GP_DRUG_DOSING_UNITS_CONCEPT_UUID}
	 *
	 * @return concept list of drug dosing units
	 * @since 1.10
	 * <strong>Should</strong> return an empty list if nothing is configured
	 * <strong>Should</strong> return a list if GP is set
	 */
	@Authorized(PrivilegeConstants.GET_CONCEPTS)
	public List<Concept> getDrugDosingUnits();

	/**
	 * Gets the possible units of dispensing, i.e the set members for the concept that matches the
	 * uuid specified as the value for the global property
	 * {@link OpenmrsConstants#GP_DRUG_DISPENSING_UNITS_CONCEPT_UUID}
	 *
	 * @return concept list of units of dispensing
	 * @since 1.10
	 * <strong>Should</strong> return an empty list if nothing is configured
	 * <strong>Should</strong> return a list if GP is set
	 * <strong>Should</strong> return the union of the dosing and dispensing units
	 */
	@Authorized(PrivilegeConstants.GET_CONCEPTS)
	public List<Concept> getDrugDispensingUnits();

	/**
	 * Gets the possible units of duration, i.e the set members for the concept that matches the
	 * uuid specified as the value for the global property
	 * {@link OpenmrsConstants#GP_DURATION_UNITS_CONCEPT_UUID}
	 *
	 * @return concept list of units of duration
	 * @since 1.10
	 * <strong>Should</strong> return an empty list if nothing is configured
	 * <strong>Should</strong> return a list if GP is set
	 */
	@Authorized(PrivilegeConstants.GET_CONCEPTS)
	public List<Concept> getDurationUnits();

	/**
	 * Gets the possible test specimen sources, i.e the set members for the concept that matches the
	 * uuid specified as the value for the global property
	 * {@link OpenmrsConstants#GP_TEST_SPECIMEN_SOURCES_CONCEPT_UUID}
	 *
	 * @return concept list of specimen sources
	 * @since 1.10
	 * <strong>Should</strong> return an empty list if nothing is configured
	 * <strong>Should</strong> return a list if GP is set
	 */
	@Authorized(PrivilegeConstants.GET_CONCEPTS)
	public List<Concept> getTestSpecimenSources();

	/**
	 * Gets the non coded drug concept, i.e the concept that matches the uuid specified as the value
	 * for the global property {@link OpenmrsConstants#GP_DRUG_ORDER_DRUG_OTHER
	 *
	 * @return concept of non coded drug
	 * @since 1.12
	 * <strong>Should</strong> return null if nothing is configured
	 * <strong>Should</strong> return a concept if GP is set
	 */
	@Authorized(PrivilegeConstants.GET_CONCEPTS)
	public Concept getNonCodedDrugConcept();
}
