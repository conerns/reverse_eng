package org.openmrs.api;

import org.openmrs.Concept;
import org.openmrs.ConceptStateConversion;
import org.openmrs.ProgramWorkflow;
import org.openmrs.annotation.Authorized;
import org.openmrs.util.PrivilegeConstants;

import java.util.List;

public interface ConceptStateService extends OpenmrsService{

	/**
	 * Save ConceptStateConversion to database (create if new or update if changed)
	 *
	 * @param conceptStateConversion - The ConceptStateConversion to save
	 * @return ConceptStateConversion - The saved ConceptStateConversion
	 * @throws APIException
	 * <strong>Should</strong> save state conversion
	 */
	@Authorized( { PrivilegeConstants.ADD_PATIENT_PROGRAMS, PrivilegeConstants.EDIT_PATIENT_PROGRAMS })
	public ConceptStateConversion saveConceptStateConversion(ConceptStateConversion conceptStateConversion)
		throws APIException;

	/**
	 * Returns a conceptStateConversion given that conceptStateConversions primary key
	 * <code>conceptStateConversionId</code> A null value is returned if no conceptStateConversion
	 * exists with this conceptStateConversionId.
	 *
	 * @param conceptStateConversionId integer primary key of the conceptStateConversion to find
	 * @return ConceptStateConversion object that has
	 *         conceptStateConversion.conceptStateConversionId =
	 *         <code>conceptStateConversionId</code> passed in.
	 * @throws APIException
	 * <strong>Should</strong> return concept state conversion for given identifier
	 */
	@Authorized( { PrivilegeConstants.GET_PROGRAMS })
	public ConceptStateConversion getConceptStateConversion(Integer conceptStateConversionId) throws APIException;

	/**
	 * Returns all conceptStateConversions
	 *
	 * @return List&lt;ConceptStateConversion&gt; of all ConceptStateConversions that exist
	 * @throws APIException
	 * <strong>Should</strong> return all concept state conversions
	 */
	@Authorized( { PrivilegeConstants.GET_PROGRAMS })
	public List<ConceptStateConversion> getAllConceptStateConversions() throws APIException;

	/**
	 * Completely remove a conceptStateConversion from the database (not reversible) This method
	 * delegates to #purgeConceptStateConversion(conceptStateConversion, boolean) method
	 *
	 * @param conceptStateConversion the ConceptStateConversion to clean out of the database.
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.MANAGE_PROGRAMS })
	public void purgeConceptStateConversion(ConceptStateConversion conceptStateConversion) throws APIException;

	/**
	 * Completely remove a conceptStateConversion from the database (not reversible)
	 *
	 * @param conceptStateConversion the ConceptStateConversion to clean out of the database.
	 * @param cascade <code>true</code> to delete related content
	 * @throws APIException
	 * <strong>Should</strong> cascade delete given concept state conversion when given cascade is true
	 * <strong>Should</strong> not cascade delete given concept state conversion when given cascade is false
	 */
	@Authorized( { PrivilegeConstants.MANAGE_PROGRAMS })
	public void purgeConceptStateConversion(ConceptStateConversion conceptStateConversion, boolean cascade)
		throws APIException;

	/**
	 * Retrieves the ConceptStateConversion that matches the passed <code>ProgramWorkflow</code> and
	 * <code>Concept</code>
	 *
	 * @param workflow - the ProgramWorkflow to check
	 * @param trigger - the Concept to check
	 * @return ConceptStateConversion that matches the passed <code>ProgramWorkflow</code> and
	 *         <code>Concept</code>
	 * @throws APIException
	 * <strong>Should</strong> return concept state conversion for given workflow and trigger
	 */
	public ConceptStateConversion getConceptStateConversion(ProgramWorkflow workflow, Concept trigger) throws APIException;


	/**
	 * Get a concept state conversion by its uuid. There should be only one of these in the
	 * database. If multiple are found, an error is thrown.
	 *
	 * @param uuid the universally unique identifier
	 * @return the concept state conversion which matches the given uuid
	 * <strong>Should</strong> find object given valid uuid
	 * <strong>Should</strong> return null if no object found with given uuid
	 * <strong>Should</strong> return a program state with the given uuid
	 * <strong>Should</strong> throw an error when multiple program states with same uuid are found
	 */
	public ConceptStateConversion getConceptStateConversionByUuid(String uuid);
}
