/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.api.impl;

import org.openmrs.*;
import org.openmrs.api.APIException;
import org.openmrs.api.PatientProgramService;
import org.openmrs.api.ProgramNameDuplicatedException;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.ProgramWorkflowDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Default implementation of the ProgramWorkflow-related services class. This method should not be
 * invoked by itself. Spring injection is used to inject this implementation into the
 * ServiceContext. Which implementation is injected is determined by the spring application context
 * file: /metadata/api/spring/applicationContext.xml
 *
 * @see org.openmrs.api.ProgramWorkflowService
 */
@Transactional
public class ProgramWorkflowServiceImpl extends BaseOpenmrsService implements ProgramWorkflowService {
	
	private static final Logger log = LoggerFactory.getLogger(ProgramWorkflowServiceImpl.class);
	
	protected ProgramWorkflowDAO dao;
        
	public ProgramWorkflowServiceImpl() {
	}
	
	/**
	 * @see org.openmrs.api.ProgramWorkflowService#setProgramWorkflowDAO(org.openmrs.api.db.ProgramWorkflowDAO)
	 */
	@Override
	public void setProgramWorkflowDAO(ProgramWorkflowDAO dao) {
		this.dao = dao;
	}
	
	// **************************
	// PROGRAM
	// **************************
	
	/**
	 * @see org.openmrs.api.ProgramWorkflowService#saveProgram(org.openmrs.Program)
	 */
	@Override
	public Program saveProgram(Program program) throws APIException {
		// Program
		if (program.getConcept() == null) {
			throw new APIException("Program.concept.required", (Object[]) null);
		}
		
		for (ProgramWorkflow workflow : program.getAllWorkflows()) {
			if (workflow.getConcept() == null) {
				throw new APIException("ProgramWorkflow.concept.required", (Object[]) null);
			}			
			ensureProgramIsSet(workflow, program);						
			for (ProgramWorkflowState state : workflow.getStates()) {
				if (state.getConcept() == null || state.getInitial() == null || state.getTerminal() == null) {
					throw new APIException("ProgramWorkflowState.requires", (Object[]) null);
				}				

				ensureProgramWorkflowIsSet(state, workflow);
			}
		}
		return dao.saveProgram(program);
	}
	 
	private void ensureProgramIsSet(ProgramWorkflow workflow, Program program) {		
		if (workflow.getProgram() == null) {
			workflow.setProgram(program);
		} else if (!workflow.getProgram().equals(program)) {
			throw new APIException("Program.error.contains.ProgramWorkflow", new Object[] { workflow.getProgram() });
		}
	}
	
	private void ensureProgramWorkflowIsSet(ProgramWorkflowState state, ProgramWorkflow workflow) {
		if (state.getProgramWorkflow() == null) {
			state.setProgramWorkflow(workflow);
		} else if (!state.getProgramWorkflow().equals(workflow)) {
			throw new APIException("ProgramWorkflow.error.contains.state", new Object[] { workflow.getProgram() });
		}
		
	}
	
	/**
	 * @see org.openmrs.api.ProgramWorkflowService#getProgram(java.lang.Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public Program getProgram(Integer id) {
		return dao.getProgram(id);
	}
	
	/**
	 * @see org.openmrs.api.ProgramWorkflowService#getProgram(java.lang.String)
	 */
	@Transactional(readOnly = true)
	public Program getProgram(String name) {
		return Context.getProgramWorkflowService().getProgramByName(name);
	}
	
	/**
	 * @see org.openmrs.api.ProgramWorkflowService#getProgram(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public Program getProgramByName(String name) throws APIException {
		List<Program> programs = dao.getProgramsByName(name, false);
		
		if (programs.isEmpty()) {
			programs = dao.getProgramsByName(name, true);
		}
		
		//Must be unique not retired or unique retired
		if (programs.size() > 1) {
			throw new ProgramNameDuplicatedException(name);
		}
		return programs.isEmpty() ? null : programs.get(0);
	}
	
	/**
	 * @see org.openmrs.api.ProgramWorkflowService#getAllPrograms()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Program> getAllPrograms() throws APIException {
		return Context.getProgramWorkflowService().getAllPrograms(true);
	}
	
	/**
	 * @see org.openmrs.api.ProgramWorkflowService#getAllPrograms(boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Program> getAllPrograms(boolean includeRetired) throws APIException {
		return dao.getAllPrograms(includeRetired);
	}
	
	/**
	 * @see org.openmrs.api.ProgramWorkflowService#getPrograms(String)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Program> getPrograms(String nameFragment) throws APIException {
		return dao.findPrograms(nameFragment);
	}
	
	/**
	 * @see org.openmrs.api.ProgramWorkflowService#purgeProgram(org.openmrs.Program)
	 */
	@Override
	public void purgeProgram(Program program) throws APIException {
		Context.getProgramWorkflowService().purgeProgram(program, false);
	}
	
	/**
	 * @see org.openmrs.api.ProgramWorkflowService#purgeProgram(org.openmrs.Program, boolean)
	 */
	@Override
	public void purgeProgram(Program program, boolean cascade) throws APIException {
		if (cascade && !program.getAllWorkflows().isEmpty()) {
			throw new APIException("Program.cascade.purging.not.implemented", (Object[]) null);
		}
		PatientProgramService patientProgramService = Context.getPatientProgramService();
		for (PatientProgram patientProgram : patientProgramService.getPatientPrograms(null, program, null,
		    null, null, null, true)) {
			patientProgramService.purgePatientProgram(patientProgram);
		}
		dao.deleteProgram(program);
	}
	
	/**
	 * @see org.openmrs.api.ProgramWorkflowService#retireProgram(org.openmrs.Program, java.lang.String)
	 */
	@Override
	public Program retireProgram(Program program, String reason) throws APIException {
		//program.setRetired(true); - Note the BaseRetireHandler aspect is already setting the retired flag and reason
		for (ProgramWorkflow workflow : program.getWorkflows()) {
			workflow.setRetired(true);
			for (ProgramWorkflowState state : workflow.getStates()) {
				state.setRetired(true);
			}
		}
		return saveProgram(program);
	}
	
	/**
	 * @see org.openmrs.api.ProgramWorkflowService#unretireProgram(org.openmrs.Program)
	 */
	@Override
	public Program unretireProgram(Program program) throws APIException {
		Date lastModifiedDate = program.getDateChanged();
		program.setRetired(false);
		for (ProgramWorkflow workflow : program.getAllWorkflows()) {
			if (lastModifiedDate != null && lastModifiedDate.equals(workflow.getDateChanged())) {
				workflow.setRetired(false);
				for (ProgramWorkflowState state : workflow.getStates()) {
					if (lastModifiedDate.equals(state.getDateChanged())) {
						state.setRetired(false);
					}
				}
			}
		}
		return saveProgram(program);
	}
	
	// **************************
	// PATIENT PROGRAM 
	// **************************
	
	
	
	/**
	 * @see org.openmrs.api.ProgramWorkflowService#getPossibleOutcomes(Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Concept> getPossibleOutcomes(Integer programId) {
		List<Concept> possibleOutcomes = new ArrayList<>();
		Program program = Context.getProgramWorkflowService().getProgram(programId);
		if (program == null) {
			return possibleOutcomes;
		}
		Concept outcomesConcept = program.getOutcomesConcept();
		if (outcomesConcept == null) {
			return possibleOutcomes;
		}
		if (!outcomesConcept.getAnswers().isEmpty()) {
			for (ConceptAnswer conceptAnswer : outcomesConcept.getAnswers()) {
				possibleOutcomes.add(conceptAnswer.getAnswerConcept());
			}
			return possibleOutcomes;
		}
		if (!outcomesConcept.getSetMembers().isEmpty()) {
			return outcomesConcept.getSetMembers();
		}
		return possibleOutcomes;
	}
	
	// **************************
	// CONCEPT STATE CONVERSION 
	// **************************
	
	
	
	/**
	 * @see org.openmrs.api.ProgramWorkflowService#getProgramsByConcept(org.openmrs.Concept)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Program> getProgramsByConcept(Concept concept) {
		return dao.getProgramsByConcept(concept);
	}
	
	/**
	 * @see org.openmrs.api.ProgramWorkflowService#getProgramWorkflowsByConcept(org.openmrs.Concept)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ProgramWorkflow> getProgramWorkflowsByConcept(Concept concept) {
		return dao.getProgramWorkflowsByConcept(concept);
	}
	
	/**
	 * @see org.openmrs.api.ProgramWorkflowService#getProgramWorkflowStatesByConcept(org.openmrs.Concept)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ProgramWorkflowState> getProgramWorkflowStatesByConcept(Concept concept) {
		return dao.getProgramWorkflowStatesByConcept(concept);
	}
	
	/**
	 * @see org.openmrs.api.ProgramWorkflowService#getProgramByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public Program getProgramByUuid(String uuid) {
		return dao.getProgramByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.api.ProgramWorkflowService#getWorkflow(Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public ProgramWorkflowState getState(Integer stateId) {
		return dao.getState(stateId);
	}
	
	/**
	 * @see org.openmrs.api.ProgramWorkflowService#getStateByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public ProgramWorkflowState getStateByUuid(String uuid) {
		return dao.getStateByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.api.ProgramWorkflowService#getWorkflow(Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public ProgramWorkflow getWorkflow(Integer workflowId) {
		return dao.getWorkflow(workflowId);
	}
	
	/**
	 * @see org.openmrs.api.ProgramWorkflowService#getWorkflowByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public ProgramWorkflow getWorkflowByUuid(String uuid) {
		return dao.getWorkflowByUuid(uuid);
	}
        
        
}
