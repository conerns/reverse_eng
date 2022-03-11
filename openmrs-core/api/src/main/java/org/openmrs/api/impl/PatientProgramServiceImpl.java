package org.openmrs.api.impl;

import org.openmrs.*;
import org.openmrs.api.APIException;
import org.openmrs.api.PatientProgramService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.PatientProgramDAO;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

public class PatientProgramServiceImpl extends BaseOpenmrsService implements PatientProgramService {
	
	protected PatientProgramDAO dao;

	public void setPatientProgramDAO(PatientProgramDAO dao) {
		this.dao = dao;
	}

	/**
	 * @see org.openmrs.api.ProgramWorkflowService#triggerStateConversion(org.openmrs.Patient,
	 *      org.openmrs.Concept, java.util.Date)
	 */
	public void triggerStateConversion(Patient patient, Concept trigger, Date dateConverted) {

		// Check input parameters
		if (patient == null) {
			throw new APIException("convert.state.invalid.patient", (Object[]) null);
		}
		if (trigger == null) {
			throw new APIException("convert.state.patient.without.valid.trigger", (Object[]) null);
		}
		if (dateConverted == null) {
			throw new APIException("convert.state.invalid.date", (Object[]) null);
		}

		for (PatientProgram patientProgram : getPatientPrograms(patient, null, null, null, null, null, false)) {
			//skip past patient programs that already completed
			if (patientProgram.getDateCompleted() == null) {
				Set<ProgramWorkflow> workflows = patientProgram.getProgram().getWorkflows();
				for (ProgramWorkflow workflow : workflows) {
					// (getWorkflows() is only returning over nonretired workflows)
					PatientState patientState = patientProgram.getCurrentState(workflow);

					// #1080 cannot exit patient from care  
					// Should allow a transition from a null state to a terminal state
					// Or we should require a user to ALWAYS add an initial workflow/state when a patient is added to a program
					ProgramWorkflowState currentState = (patientState != null) ? patientState.getState() : null;
					ProgramWorkflowState transitionState = workflow.getState(trigger);

					//log.debug("Transitioning from current state [" + currentState + "]");
					//log.debug("|---> Transitioning to final state [" + transitionState + "]");

					if (transitionState != null && workflow.isLegalTransition(currentState, transitionState)) {
						patientProgram.transitionToState(transitionState, dateConverted);
						//log.debug("State Conversion Triggered: patientProgram=" + patientProgram + " transition from "
						//	+ currentState + " to " + transitionState + " on " + dateConverted);
					}
				}

				// #1068 - Exiting a patient from care causes "not-null property references
				// a null or transient value: org.openmrs.PatientState.dateCreated". Explicitly
				// calling the savePatientProgram() method will populate the metadata properties.
				// 
				// #1067 - We should explicitly save the patient program rather than let 
				// Hibernate do so when it flushes the session.
				savePatientProgram(patientProgram);
			}
		}
	}

	@Override
	public List<PatientProgram> getPatientProgramByAttributeNameAndValue(String attributeName, String attributeValue) {
		return dao.getPatientProgramByAttributeNameAndValue(attributeName, attributeValue);
	}

	@Override
	public PatientProgramAttribute getPatientProgramAttributeByUuid(String uuid) {
		return dao.getPatientProgramAttributeByUuid(uuid);
	}

	@Override
	public Map<Object, Object> getPatientProgramAttributeByAttributeName(List<Integer> patients, String attributeName){
		return dao.getPatientProgramAttributeByAttributeName(patients, attributeName);
	}
	
	/**
	 * @see org.openmrs.api.ProgramWorkflowService#savePatientProgram(org.openmrs.PatientProgram)
	 */
	@Override
	public PatientProgram savePatientProgram(PatientProgram patientProgram) throws APIException {

		if (patientProgram.getPatient() == null || patientProgram.getProgram() == null) {
			throw new APIException("PatientProgram.requires", (Object[]) null);
		}

		// Patient State
		for (PatientState state : patientProgram.getStates()) {
			if (state.getState() == null) {
				throw new APIException("PatientState.requires", (Object[]) null);
			}
			if (state.getPatientProgram() == null) {
				state.setPatientProgram(patientProgram);
			} else if (!state.getPatientProgram().equals(patientProgram)) {
				throw new APIException("PatientProgram.already.assigned", new Object[] { state.getPatientProgram() });
			}
			if (patientProgram.getVoided() || state.getVoided()) {
				state.setVoided(true);
				if (state.getVoidReason() == null && patientProgram.getVoidReason() != null) {
					state.setVoidReason(patientProgram.getVoidReason());
				}
			}
		}
		// Makes sure that the end dates of most recent states in each workflow
		// and the program end date are consistent
		if (patientProgram.getDateCompleted() != null) {
			for (PatientState state : patientProgram.getMostRecentStateInEachWorkflow()) {
				// The EndDate of active states only should be updated
				if (state.getEndDate() == null) {
					state.setEndDate(patientProgram.getDateCompleted());
				}
			}
		}

		return dao.savePatientProgram(patientProgram);
	}

	/**
	 * @see org.openmrs.api.ProgramWorkflowService#getPatientProgram(java.lang.Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public PatientProgram getPatientProgram(Integer patientProgramId) {
		return dao.getPatientProgram(patientProgramId);
	}

	/**
	 * @see org.openmrs.api.ProgramWorkflowService#getPatientPrograms(Patient, Program, Date, Date,
	 *      Date, Date, boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<PatientProgram> getPatientPrograms(Patient patient, Program program, Date minEnrollmentDate,
												   Date maxEnrollmentDate, Date minCompletionDate, Date maxCompletionDate, boolean includeVoided)
		throws APIException {
		return dao.getPatientPrograms(patient, program, minEnrollmentDate, maxEnrollmentDate, minCompletionDate,
			maxCompletionDate, includeVoided);
	}

	/**
	 * @see org.openmrs.api.ProgramWorkflowService#getPatientPrograms(Cohort, Collection)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<PatientProgram> getPatientPrograms(Cohort cohort, Collection<Program> programs) {
		if (cohort.getMemberIds().isEmpty()) {
			return dao.getPatientPrograms(null, programs);
		} else {
			return dao.getPatientPrograms(cohort, programs);
		}
	}

	/**
	 * @see org.openmrs.api.ProgramWorkflowService#purgePatientProgram(org.openmrs.PatientProgram)
	 */
	@Override
	public void purgePatientProgram(PatientProgram patientProgram) throws APIException {
		Context.getPatientProgramService().purgePatientProgram(patientProgram, false);

	}

	/**
	 * @see org.openmrs.api.ProgramWorkflowService#purgePatientProgram(org.openmrs.PatientProgram,
	 *      boolean)
	 */
	@Override
	public void purgePatientProgram(PatientProgram patientProgram, boolean cascade) throws APIException {
		if (cascade && !patientProgram.getStates().isEmpty()) {
			throw new APIException("PatientProgram.cascade.purging.not.implemented", (Object[]) null);
		}
		dao.deletePatientProgram(patientProgram);
	}

	/**
	 * @see org.openmrs.api.ProgramWorkflowService#voidPatientProgram(org.openmrs.PatientProgram,
	 *      java.lang.String)
	 */
	@Override
	public PatientProgram voidPatientProgram(PatientProgram patientProgram, String reason) {
		patientProgram.setVoided(true);
		patientProgram.setVoidReason(reason);
		return Context.getPatientProgramService().savePatientProgram(patientProgram); // The savePatientProgram method handles all of the voiding defaults and cascades
	}

	/**
	 * @see org.openmrs.api.ProgramWorkflowService#voidPatientProgram(org.openmrs.PatientProgram,
	 *      java.lang.String)
	 */
	@Override
	public PatientProgram unvoidPatientProgram(PatientProgram patientProgram) {
		Date voidDate = patientProgram.getDateVoided();
		patientProgram.setVoided(false);
		for (PatientState state : patientProgram.getStates()) {
			if (voidDate != null && voidDate.equals(state.getDateVoided())) {
				state.setVoided(false);
				state.setVoidedBy(null);
				state.setDateVoided(null);
				state.setVoidReason(null);
			}
		}
		return Context.getPatientProgramService().savePatientProgram(patientProgram); // The savePatientProgram method handles all of the unvoiding defaults
	}

	/**
	 * @see org.openmrs.api.ProgramWorkflowService#getPatientProgramByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public PatientProgram getPatientProgramByUuid(String uuid) {
		return dao.getPatientProgramByUuid(uuid);
	}

	@Override
	@Transactional(readOnly = true)
	public PatientState getPatientStateByUuid(String uuid) {
		return dao.getPatientStateByUuid(uuid);
	}
}
