package org.openmrs.api.db;

import org.openmrs.Concept;
import org.openmrs.ConceptStateConversion;
import org.openmrs.ProgramWorkflow;

import java.util.List;

public interface ConceptStateDAO {
	// **************************
	// CONCEPT STATE CONVERSION
	// **************************

	/**
	 * Save ConceptStateConversion to database (create if new or update if changed)
	 *
	 * @param csc The ConceptStateConversion to save
	 * @return The saved ConceptStateConversion
	 * @throws DAOException
	 */
	public ConceptStateConversion saveConceptStateConversion(ConceptStateConversion csc) throws DAOException;

	/**
	 * Returns all conceptStateConversions
	 *
	 * @return List&lt;ConceptStateConversion&gt; of all ConceptStateConversions that exist
	 * @throws DAOException
	 */
	public List<ConceptStateConversion> getAllConceptStateConversions() throws DAOException;

	/**
	 * Returns a conceptStateConversion given that conceptStateConversions primary key
	 * <code>conceptStateConversionId</code> A null value is returned if no conceptStateConversion
	 * exists with this conceptStateConversionId.
	 *
	 * @param id integer primary key of the conceptStateConversion to find
	 * @return ConceptStateConversion object that has
	 *         conceptStateConversion.conceptStateConversionId =
	 *         <code>conceptStateConversionId</code> passed in.
	 * @throws DAOException
	 */
	public ConceptStateConversion getConceptStateConversion(Integer id);

	/**
	 * Completely remove a conceptStateConversion from the database (not reversible)
	 *
	 * @param csc the ConceptStateConversion to clean out of the database.
	 * @throws DAOException
	 */
	public void deleteConceptStateConversion(ConceptStateConversion csc);

	/**
	 * Retrieves the ConceptStateConversion that matches the passed <code>ProgramWorkflow</code> and
	 * <code>Concept</code>
	 *
	 * @param workflow the ProgramWorkflow to check
	 * @param trigger the Concept to check
	 * @return ConceptStateConversion that matches the passed <code>ProgramWorkflow</code> and
	 *         <code>Concept</code>
	 * @throws DAOException
	 */
	public ConceptStateConversion getConceptStateConversion(ProgramWorkflow workflow, Concept trigger);

	/**
	 * @param uuid
	 * @return concept state conversion or null
	 */
	public ConceptStateConversion getConceptStateConversionByUuid(String uuid);
}
