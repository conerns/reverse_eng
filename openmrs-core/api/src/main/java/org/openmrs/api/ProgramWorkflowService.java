/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.api;

import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.db.ProgramWorkflowDAO;
import org.openmrs.util.PrivilegeConstants;

import java.util.List;

/**
 * Contains methods pertaining to management of Programs, ProgramWorkflows, ProgramWorkflowStates,
 * PatientPrograms (12), PatientStates (1), and ConceptStateConversions (10) Use:<br>
 * 
 * <pre>
 *   Program program = new Program();
 *   program.set___(___);
 *   ...etc
 *   Context.getProgramWorkflowService().saveProgram(program);
 * </pre>
 */
public interface ProgramWorkflowService extends OpenmrsService {
	
	/**
	 * Setter for the ProgramWorkflow DataAccessObject (DAO). The DAO is used for saving and
	 * retrieving from the database
	 * 
	 * @param dao - The DAO for this service
	 */
	public void setProgramWorkflowDAO(ProgramWorkflowDAO dao);
	
	// **************************
	// PROGRAM
	// **************************
	
	/**
	 * Save <code>program</code> to database (create if new or update if changed)
	 * 
	 * @param program is the Program to be saved to the database
	 * @return The Program that was saved
	 * @throws APIException
	 * <strong>Should</strong> create program workflows
	 * <strong>Should</strong> save program successfully
	 * <strong>Should</strong> save workflows associated with program
	 * <strong>Should</strong> save states associated with program
	 * <strong>Should</strong> update detached program
	 */
	@Authorized( { PrivilegeConstants.MANAGE_PROGRAMS })
	public Program saveProgram(Program program) throws APIException;
	
	/**
	 * Returns a program given that programs primary key <code>programId</code> A null value is
	 * returned if no program exists with this programId.
	 * 
	 * @param programId integer primary key of the program to find
	 * @return Program object that has program.programId = <code>programId</code> passed in.
	 * @throws APIException
	 * <strong>Should</strong> return program matching the given programId
	 * <strong>Should</strong> return null when programId does not exist
	 */
	@Authorized( { PrivilegeConstants.GET_PROGRAMS })
	public Program getProgram(Integer programId) throws APIException;
	
	/**
	 * Returns a program given the program's exact <code>name</code> A null value is returned if
	 * there is no program with this name
	 * 
	 * @param name the exact name of the program to match on
	 * @return Program matching the <code>name</code> to Program.name
	 * @throws APIException
	 * @throws ProgramNameDuplicatedException when there are more than one program in the dB with
	 *             the given name.
	 * <strong>Should</strong> return program when name matches
	 * <strong>Should</strong> return null when program does not exist with given name
	 * <strong>Should</strong> fail when two programs found with same name
	 */
	@Authorized( { PrivilegeConstants.GET_PROGRAMS })
	public Program getProgramByName(String name) throws APIException;
	
	/**
	 * Returns all programs, includes retired programs. This method delegates to the
	 * #getAllPrograms(boolean) method
	 * 
	 * @return List&lt;Program&gt; of all existing programs, including retired programs
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_PROGRAMS })
	public List<Program> getAllPrograms() throws APIException;
	
	/**
	 * Returns all programs
	 * 
	 * @param includeRetired whether or not to include retired programs
	 * @return List&lt;Program&gt; all existing programs, including retired based on the input parameter
	 * @throws APIException
	 * <strong>Should</strong> return all programs including retired when includeRetired equals true
	 * <strong>Should</strong> return all programs excluding retired when includeRetired equals false
	 */
	@Authorized( { PrivilegeConstants.GET_PROGRAMS })
	public List<Program> getAllPrograms(boolean includeRetired) throws APIException;
	
	/**
	 * Returns programs that match the given string. A null list will never be returned. An empty
	 * list will be returned if there are no programs matching this <code>nameFragment</code>
	 * 
	 * @param nameFragment is the string used to search for programs
	 * @return List&lt;Program&gt; - list of Programs whose name matches the input parameter
	 * @throws APIException
	 * <strong>Should</strong> return all programs with partial name match
	 * <strong>Should</strong> return all programs when exact name match
	 * <strong>Should</strong> return empty list when name does not match
	 * <strong>Should</strong> not return a null list
	 * <strong>Should</strong> return programs when nameFragment matches beginning of program name
	 * <strong>Should</strong> return programs when nameFragment matches ending of program name
	 * <strong>Should</strong> return programs when nameFragment matches middle of program name
	 * <strong>Should</strong> return programs when nameFragment matches entire program name
	 * <strong>Should</strong> return programs ordered by name
	 * <strong>Should</strong> return empty list when nameFragment does not match any
	 */
	@Authorized( { PrivilegeConstants.GET_PROGRAMS })
	public List<Program> getPrograms(String nameFragment) throws APIException;
	
	/**
	 * Completely remove a program from the database (not reversible) This method delegates to
	 * #purgeProgram(program, boolean) method
	 * 
	 * @param program the Program to clean out of the database.
	 * @throws APIException
	 * <strong>Should</strong> delete program successfully
	 */
	@Authorized( { PrivilegeConstants.MANAGE_PROGRAMS })
	public void purgeProgram(Program program) throws APIException;
	
	/**
	 * Completely remove a program from the database (not reversible)
	 * 
	 * @param cascade <code>true</code> to delete related content
	 * @throws APIException
	 * <strong>Should</strong> delete program successfully
	 * <strong>Should</strong> not delete child associations when cascade equals false
	 * <strong>Should</strong> throw APIException when given cascade equals true
	 * <strong>Should</strong> purge program with patients enrolled
	 */
	@Authorized( { PrivilegeConstants.MANAGE_PROGRAMS })
	public void purgeProgram(Program program, boolean cascade) throws APIException;
	
	/**
	 * Retires the given program
	 * 
	 * @param program Program to be retired
	 * @param reason String for retiring the program
	 * @return the Program which has been retired
	 * @throws APIException
	 * <strong>Should</strong> retire program successfully
	 * <strong>Should</strong> retire workflows associated with given program
	 * <strong>Should</strong> retire states associated with given program
	 */
	@Authorized( { PrivilegeConstants.MANAGE_PROGRAMS })
	public Program retireProgram(Program program, String reason) throws APIException;
	
	/**
	 * Unretires the given program
	 * 
	 * @param program Program to be unretired
	 * @return the Program which has been unretired
	 * @throws APIException
	 * <strong>Should</strong> unretire program successfully
	 * <strong>Should</strong> unretire workflows associated with given program
	 * <strong>Should</strong> unretire states associated with given program
	 */
	@Authorized( { PrivilegeConstants.MANAGE_PROGRAMS })
	public Program unretireProgram(Program program) throws APIException;
	
	// **************************
	// PATIENT PROGRAM 
	// **************************
	
	/**
	 * Get a program by its uuid. There should be only one of these in the database. If multiple are
	 * found, an error is thrown.
	 * 
	 * @param uuid the universally unique identifier
	 * @return the program which matches the given uuid
	 * <strong>Should</strong> find object given valid uuid
	 * <strong>Should</strong> return null if no object found with given uuid
	 * <strong>Should</strong> return program with given uuid
	 * <strong>Should</strong> throw error when multiple programs with same uuid are found
	 */
	public Program getProgramByUuid(String uuid);
	
	
	/**
	 * Get all possible outcome concepts for a program. Will return all concept answers
	 * {@link org.openmrs.Concept#getAnswers()} if they exist, then all concept set members
	 * {@link org.openmrs.Concept#getSetMembers()} if they exist, then empty List.
	 * 
	 * @param programId
	 * @return outcome concepts or empty List if none exist
	 */
	@Authorized( { PrivilegeConstants.GET_PROGRAMS })
	public List<Concept> getPossibleOutcomes(Integer programId);
	
	// **************************
	// CONCEPT STATE CONVERSION
	// **************************
	
	/**
	 * Get {@code ProgramWorkflow} by internal identifier.
	 * 
	 * @param workflowId the primary key of the workflow to find, null not ok
	 * @return the program workflow matching given id or null if not found
	 * @since 2.2.0
	 */
	public ProgramWorkflow getWorkflow(Integer workflowId);
	
	/**
	 * Get ProgramWorkflow by its UUID
	 * 
	 * @param uuid
	 * @return program work flow or null
	 * <strong>Should</strong> find object given valid uuid
	 * <strong>Should</strong> return null if no object found with given uuid
	 */
	public ProgramWorkflow getWorkflowByUuid(String uuid);
	
	
	
	/**
	 * Get {@code ProgramWorkflowState} by internal identifier.
	 * 
	 * @param stateId the primary key of the state to find, null not ok
	 * @return the program workflow state matching given id or null if not found
	 * @since 2.2.0
	 */
	public ProgramWorkflowState getState(Integer stateId);
	
	/**
	 * Get a state by its uuid. There should be only one of these in the database. If multiple are
	 * found, an error is thrown.
	 * 
	 * @param uuid the universally unique identifier
	 * @return the program workflow state which matches the given uuid
	 * <strong>Should</strong> find object given valid uuid
	 * <strong>Should</strong> return null if no object found with given uuid
	 * <strong>Should</strong> return a state with the given uuid
	 * <strong>Should</strong> throw an error when multiple states with same uuid are found
	 */
	public ProgramWorkflowState getStateByUuid(String uuid);
					
		
	/**
	 * Returns a list of Programs that are using a particular concept.
	 * 
	 * @param concept - The Concept being used.
	 * @return - A List of Programs
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENT_PROGRAMS })
	public List<Program> getProgramsByConcept(Concept concept);
	
	/**
	 * Returns a list of ProgramWorkflows that are using a particular concept.
	 * 
	 * @param concept - The Concept being used.
	 * @return - A List of ProgramWorkflows
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENT_PROGRAMS })
	public List<ProgramWorkflow> getProgramWorkflowsByConcept(Concept concept);
	
	/**
	 * Returns a list of ProgramWorkflowStates that are using a particular concept.
	 * 
	 * @param concept - The Concept being used.
	 * @return - A List of ProgramWorkflowStates
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENT_PROGRAMS })
	public List<ProgramWorkflowState> getProgramWorkflowStatesByConcept(Concept concept);
}
