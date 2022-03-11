/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.api.db;

import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;

import java.util.List;

/**
 * Program- and PatientProgram- and ConceptStateConversion-related database functions
 * 
 * @version 1.0
 */
public interface ProgramWorkflowDAO {
	
	// **************************
	// PROGRAM
	// **************************
	
	/**
	 * Saves a Program to the database
	 * 
	 * @param program - The {@link Program} to save
	 * @return The saved {@link Program}
	 * @throws DAOException
	 */
	public Program saveProgram(Program program) throws DAOException;
	
	/**
	 * Retrieves a {@link Program} from the database by primary key programId
	 * 
	 * @param programId - The primary key programId to use to retrieve a {@link Program}
	 * @return Program - The {@link Program} matching the passed programId
	 * @throws DAOException
	 */
	public Program getProgram(Integer programId) throws DAOException;
	
	/**
	 * Returns all programs
	 * 
	 * @param includeRetired whether or not to include retired programs
	 * @return List&lt;Program&gt; all existing programs, including retired based on the input parameter
	 * @throws DAOException
	 */
	public List<Program> getAllPrograms(boolean includeRetired) throws DAOException;
	
	/**
	 * Returns programs that match the given string. A null list will never be returned. An empty
	 * list will be returned if there are no programs matching this <code>nameFragment</code>
	 * 
	 * @param nameFragment is the string used to search for programs
	 * @return List&lt;Program&gt; - list of Programs whose name matches the input parameter
	 * @throws DAOException
	 */
	public List<Program> findPrograms(String nameFragment) throws DAOException;
	
	/**
	 * Completely remove a program from the database (not reversible) This method delegates to
	 * #purgeProgram(program, boolean) method
	 * 
	 * @param program the Program to clean out of the database.
	 * @throws DAOException
	 */
	public void deleteProgram(Program program) throws DAOException;
	
	/**
	 * @param uuid
	 * @return program or null
	 */
	public Program getProgramByUuid(String uuid);
	
	/**
	 * Retrieves the Programs from the dB which have the given name.
	 * @param name the name of the Programs to retrieve.
	 * @param includeRetired whether to include retired programs or not
	 * <strong>Should</strong> return an empty list when there is no program in the dB with given name
	 * <strong>Should</strong> return only and exactly the programs with the given name
	 * @return all Programs with the given name.
	 */
	public List<Program> getProgramsByName(String name, boolean includeRetired);
	
	/**
	 * Retrieves a {@code ProgramWorkflowState} from the database by its primary key.
	 * 
	 * @param stateId the primary key used to retrieve program workflow state
	 * @return the program workflow state matching given id or null if not found
	 * @since 2.2.0
	 */
	public ProgramWorkflowState getState(Integer stateId);
	
	/**
	 * @param uuid
	 * @return program workflow state or null
	 */
	public ProgramWorkflowState getStateByUuid(String uuid);
	
	/**
	 * Retrieves a {@code ProgramWorkflow} from the database by its primary key.
	 * 
	 * @param workflowId the primary key used to retrieve program workflow
	 * @return the program workflow matching given id or null if not found
	 * @since 2.2.0
	 */
	public ProgramWorkflow getWorkflow(Integer workflowId);
	
	/**
	 * @param uuid
	 * @return program workflow or null
	 */
	public ProgramWorkflow getWorkflowByUuid(String uuid);
	
	/**
	 * Returns a list of Programs that are using a particular concept.
	 * 
	 * @param concept - The Concept being used.
	 * @return - A List of Programs
	 */
	public List<Program> getProgramsByConcept(Concept concept);
	
	/**
	 * Returns a list of ProgramWorkflows that are using a particular concept.
	 * 
	 * @param concept - The Concept being used.
	 * @return - A List of ProgramWorkflows
	 */
	public List<ProgramWorkflow> getProgramWorkflowsByConcept(Concept concept);
	
	/**
	 * Returns a list of ProgramWorkflowStates that are using a particular concept.
	 * 
	 * @param concept - The Concept being used.
	 * @return - A List of ProgramWorkflowStates
	 */
	public List<ProgramWorkflowState> getProgramWorkflowStatesByConcept(Concept concept);
}
