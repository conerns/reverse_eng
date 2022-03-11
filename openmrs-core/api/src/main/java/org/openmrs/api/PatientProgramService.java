package org.openmrs.api;

import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.PatientProgramAttribute;
import org.openmrs.PatientState;
import org.openmrs.Cohort;
import org.openmrs.annotation.Authorized;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface PatientProgramService extends OpenmrsService{

	/**
	 * Save patientProgram to database (create if new or update if changed)
	 *
	 * @param patientProgram is the PatientProgram to be saved to the database
	 * @return PatientProgram - the saved PatientProgram
	 * @throws APIException
	 * <strong>Should</strong> update patient program
	 * <strong>Should</strong> save patient program successfully
	 * <strong>Should</strong> return patient program with assigned patient program id
	 */
	@Authorized( { PrivilegeConstants.ADD_PATIENT_PROGRAMS, PrivilegeConstants.EDIT_PATIENT_PROGRAMS })
	public PatientProgram savePatientProgram(PatientProgram patientProgram) throws APIException;

	/**
	 * Returns a PatientProgram given that PatientPrograms primary key <code>patientProgramId</code>
	 * A null value is returned if no PatientProgram exists with this patientProgramId.
	 *
	 * @param patientProgramId integer primary key of the PatientProgram to find
	 * @return PatientProgram object that has patientProgram.patientProgramId =
	 *         <code>patientProgramId</code> passed in.
	 * @throws APIException
	 * <strong>Should</strong> return patient program with given patientProgramId
	 * <strong>Should</strong> get patient program with given identifier
	 * <strong>Should</strong> return null if program does not exist
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENT_PROGRAMS })
	public PatientProgram getPatientProgram(Integer patientProgramId) throws APIException;

	/**
	 * Returns PatientPrograms that match the input parameters. If an input parameter is set to
	 * null, the parameter will not be used. Calling this method will all null parameters will
	 * return all PatientPrograms in the database A null list will never be returned. An empty list
	 * will be returned if there are no programs matching the input criteria
	 *
	 * @param patient if supplied all PatientPrograms returned will be for this Patient
	 * @param program if supplied all PatientPrograms returned will be for this Program
	 * @param minEnrollmentDate if supplied will limit PatientPrograms to those with enrollments on
	 *            or after this Date
	 * @param maxEnrollmentDate if supplied will limit PatientPrograms to those with enrollments on
	 *            or before this Date
	 * @param minCompletionDate if supplied will limit PatientPrograms to those completed on or
	 *            after this Date OR not yet completed
	 * @param maxCompletionDate if supplied will limit PatientPrograms to those completed on or
	 *            before this Date
	 * @param includeVoided if true, will also include voided PatientPrograms
	 * @return List&lt;PatientProgram&gt; of PatientPrograms that match the passed input parameters
	 * @throws APIException
	 * <strong>Should</strong> return patient programs for given patient
	 * <strong>Should</strong> return patient programs for given program
	 * <strong>Should</strong> return patient programs with dateEnrolled on or before minEnrollmentDate
	 * <strong>Should</strong> return patient programs with dateEnrolled on or after maxEnrollmentDate
	 * <strong>Should</strong> return patient programs with dateCompleted on or before minCompletionDate
	 * <strong>Should</strong> return patient programs with dateCompleted on or after maxCompletionDate
	 * <strong>Should</strong> return patient programs with dateCompleted
	 * <strong>Should</strong> return patient programs not yet completed
	 * <strong>Should</strong> return voided patient programs
	 * <strong>Should</strong> return all patient programs when all parameters are null
	 * <strong>Should</strong> return empty list when matches not found
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENT_PROGRAMS })
	public List<PatientProgram> getPatientPrograms(Patient patient, Program program, Date minEnrollmentDate,
												   Date maxEnrollmentDate, Date minCompletionDate, Date maxCompletionDate, boolean includeVoided)
		throws APIException;

	/**
	 * Completely remove a patientProgram from the database (not reversible) This method delegates
	 * to #purgePatientProgram(patientProgram, boolean) method
	 *
	 * @param patientProgram the PatientProgram to clean out of the database.
	 * @throws APIException
	 * <strong>Should</strong> delete patient program from database without cascade
	 */
	@Authorized( { PrivilegeConstants.PURGE_PATIENT_PROGRAMS })
	public void purgePatientProgram(PatientProgram patientProgram) throws APIException;

	/**
	 * Completely remove a patientProgram from the database (not reversible)
	 *
	 * @param patientProgram the PatientProgram to clean out of the database.
	 * @param cascade <code>true</code> to delete related content
	 * @throws APIException
	 * <strong>Should</strong> delete patient program from database
	 * <strong>Should</strong> cascade delete patient program states when cascade equals true
	 * <strong>Should</strong> not cascade delete patient program states when cascade equals false
	 */
	@Authorized( { PrivilegeConstants.PURGE_PATIENT_PROGRAMS })
	public void purgePatientProgram(PatientProgram patientProgram, boolean cascade) throws APIException;

	/**
	 * Voids the given patientProgram
	 *
	 * @param patientProgram patientProgram to be voided
	 * @param reason is the reason why the patientProgram is being voided
	 * @return the voided PatientProgram
	 * @throws APIException
	 * <strong>Should</strong> void patient program when reason is valid
	 * <strong>Should</strong> fail when reason is empty
	 */
	@Authorized( { PrivilegeConstants.DELETE_PATIENT_PROGRAMS })
	public PatientProgram voidPatientProgram(PatientProgram patientProgram, String reason) throws APIException;

	/**
	 * Unvoids the given patientProgram
	 *
	 * @param patientProgram patientProgram to be un-voided
	 * @return the voided PatientProgram
	 * @throws APIException
	 * <strong>Should</strong> void patient program when reason is valid
	 */
	@Authorized( { PrivilegeConstants.DELETE_PATIENT_PROGRAMS })
	public PatientProgram unvoidPatientProgram(PatientProgram patientProgram) throws APIException;

	@Transactional(readOnly = true)
	@Authorized({"Get Patient Programs"})
	public List<PatientProgram> getPatientProgramByAttributeNameAndValue(String attributeName, String attributeValue);


	/**
	 * Get a patient program by its uuid. There should be only one of these in the database. If
	 * multiple are found, an error is thrown.
	 *
	 * @param uuid the universally unique identifier
	 * @return the patient program which matches the given uuid
	 * <strong>Should</strong> find object given valid uuid
	 * <strong>Should</strong> return null if no object found with given uuid
	 * <strong>Should</strong> return a patient program with the given uuid
	 * <strong>Should</strong> throw an error when multiple patient programs with same uuid are found
	 */
	public PatientProgram getPatientProgramByUuid(String uuid);

	/**
	 *
	 * @param cohort
	 * @param programs
	 * @return List&lt;PatientProgram&gt; for all Patients in the given Cohort that are in the given
	 *         programs
	 * <strong>Should</strong> return patient programs with patients in given cohort and programs
	 * <strong>Should</strong> return patient programs with patients in given cohort
	 * <strong>Should</strong> return patient programs with programs in given programs
	 * <strong>Should</strong> return empty list when there is no match for given cohort and programs
	 * <strong>Should</strong> not return null when there is no match for given cohort and program
	 * <strong>Should</strong> not throw NullPointerException when given cohort and programs are null
	 * <strong>Should</strong> not fail when given cohort is empty
	 * <strong>Should</strong> not fail when given program is empty
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENT_PROGRAMS })
	public List<PatientProgram> getPatientPrograms(Cohort cohort, Collection<Program> programs);

	/**
	 * Get a program state by its uuid. There should be only one of these in the database. If
	 * multiple are found, an error is thrown.
	 *
	 * @param uuid the universally unique identifier
	 * @return the program which matches the given uuid
	 * <strong>Should</strong> find object given valid uuid
	 * <strong>Should</strong> return null if no object found with given uuid
	 * <strong>Should</strong> return program state with the given uuid
	 * <strong>Should</strong> throw error when multiple program states with same uuid are found
	 */
	public PatientState getPatientStateByUuid(String uuid);

	@Transactional(readOnly = true)
	@Authorized({"Get Patient Programs"})
	public PatientProgramAttribute getPatientProgramAttributeByUuid(String var1);

	public Map<Object, Object> getPatientProgramAttributeByAttributeName(List<Integer> patients, String attributeName);
}
