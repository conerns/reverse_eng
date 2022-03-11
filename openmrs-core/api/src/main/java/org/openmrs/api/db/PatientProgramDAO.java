package org.openmrs.api.db;

import org.openmrs.*;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface PatientProgramDAO {

	public PatientProgramAttribute getPatientProgramAttributeByUuid(String var1);

	public List<PatientProgram> getPatientProgramByAttributeNameAndValue(String attributeName, String attributeValue);

	public Map<Object, Object> getPatientProgramAttributeByAttributeName(List<Integer> patientIds, String attributeName);

	/**
	 * Save patientProgram to database (create if new or update if changed)
	 *
	 * @param patientProgram is the PatientProgram to be saved to the database
	 * @return PatientProgram - the saved PatientProgram
	 * @throws DAOException
	 */
	public PatientProgram savePatientProgram(PatientProgram patientProgram) throws DAOException;

	/**
	 * Returns a PatientProgram given that PatientPrograms primary key <code>patientProgramId</code>
	 * A null value is returned if no PatientProgram exists with this patientProgramId.
	 *
	 * @param id integer primary key of the PatientProgram to find
	 * @return PatientProgram object that has patientProgram.patientProgramId =
	 *         <code>patientProgramId</code> passed in.
	 * @throws DAOException
	 */
	public PatientProgram getPatientProgram(Integer id);

	public List<PatientProgram> getPatientPrograms(Cohort cohort, Collection<Program> programs);

	/**
	 * Returns PatientPrograms that match the input parameters. If an input parameter is set to
	 * null, the parameter will not be used. Calling this method will all null parameters will
	 * return all PatientPrograms in the database A null list will never be returned. An empty list
	 * will be returned if there are no programs matching the input criteria
	 *
	 * @param patient - if supplied all PatientPrograms returned will be for this Patient
	 * @param program - if supplied all PatientPrograms returned will be for this Program
	 * @param minEnrollmentDate - if supplied will limit PatientPrograms to those with enrollments
	 *            on or after this Date
	 * @param maxEnrollmentDate - if supplied will limit PatientPrograms to those with enrollments
	 *            on or before this Date
	 * @param minCompletionDate - if supplied will limit PatientPrograms to those completed on or
	 *            after this Date OR not yet completed
	 * @param maxCompletionDate - if supplied will limit PatientPrograms to those completed on or
	 *            before this Date
	 * @param includeVoided - boolean, if true will return voided PatientPrograms as well. If false,
	 *            will not return voided PatientPrograms
	 * @return List&lt;PatientProgram&gt; of PatientPrograms that match the passed input parameters
	 * @throws DAOException
	 */
	public List<PatientProgram> getPatientPrograms(Patient patient, Program program, Date minEnrollmentDate,
												   Date maxEnrollmentDate, Date minCompletionDate, Date maxCompletionDate, boolean includeVoided)
		throws DAOException;

	/**
	 * Completely remove a patientProgram from the database (not reversible) This method delegates
	 * to #purgePatientProgram(patientProgram, boolean) method
	 *
	 * @param patientProgram the PatientProgram to clean out of the database.
	 * @throws DAOException
	 */
	public void deletePatientProgram(PatientProgram patientProgram) throws DAOException;



	/**
	 * @param uuid
	 * @return patient program or null
	 */
	public PatientProgram getPatientProgramByUuid(String uuid);

	public PatientState getPatientStateByUuid(String uuid);
}
