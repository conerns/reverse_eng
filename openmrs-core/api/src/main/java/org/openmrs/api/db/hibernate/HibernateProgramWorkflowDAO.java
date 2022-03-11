/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.api.db.hibernate;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.FlushMode;
import org.hibernate.type.StandardBasicTypes;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.ConceptStateConversion;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PatientProgramAttribute;
import org.openmrs.PatientState;
import org.openmrs.Program;
import org.openmrs.ProgramAttributeType;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.ProgramWorkflowDAO;
import org.openmrs.customdatatype.CustomDatatypeUtil;

/**
 * Hibernate specific ProgramWorkflow related functions.<br>
 * <br>
 * This class should not be used directly. All calls should go through the
 * {@link org.openmrs.api.ProgramWorkflowService} methods.
 *
 * @see org.openmrs.api.db.ProgramWorkflowDAO
 * @see org.openmrs.api.ProgramWorkflowService
 */
public class HibernateProgramWorkflowDAO implements ProgramWorkflowDAO {
	
	private SessionFactory sessionFactory;
	
	public HibernateProgramWorkflowDAO() {
	}
	
	/**
	 * Hibernate Session Factory
	 *
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	// **************************
	// PROGRAM
	// **************************
	
	/**
	 * @see org.openmrs.api.db.ProgramWorkflowDAO#saveProgram(org.openmrs.Program)
	 */
	@Override
	public Program saveProgram(Program program) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(program);
		return program;
	}
	
	/**
	 * @see org.openmrs.api.db.ProgramWorkflowDAO#getProgram(java.lang.Integer)
	 */
	@Override
	public Program getProgram(Integer programId) throws DAOException {
		return (Program) sessionFactory.getCurrentSession().get(Program.class, programId);
	}
	
	/**
	 * @see org.openmrs.api.db.ProgramWorkflowDAO#getAllPrograms(boolean)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Program> getAllPrograms(boolean includeRetired) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Program.class);
		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", false));
		}
		return criteria.list();
	}
	
	/**
	 * @see org.openmrs.api.db.ProgramWorkflowDAO#getProgramsByName(String, boolean)
	 */
	@Override
	public List<Program> getProgramsByName(String programName, boolean includeRetired) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Program.class);
		criteria.add(Restrictions.eq("name", programName));
		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", false));
		}
		
		@SuppressWarnings("unchecked")
		List<Program> list = criteria.list();
		
		return list;
	}
	
	/**
	 * @see org.openmrs.api.db.ProgramWorkflowDAO#findPrograms(java.lang.String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Program> findPrograms(String nameFragment) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Program.class, "program");
		criteria.add(Restrictions.ilike("name", nameFragment, MatchMode.ANYWHERE));
		criteria.addOrder(Order.asc("name"));
		return criteria.list();
	}
	
	/**
	 * @see org.openmrs.api.db.ProgramWorkflowDAO#deleteProgram(org.openmrs.Program)
	 */
	@Override
	public void deleteProgram(Program program) throws DAOException {
		sessionFactory.getCurrentSession().delete(program);
	}
	
	// **************************
	// PATIENT PROGRAM
	// **************************
	
	/**
	 * @see org.openmrs.api.db.ProgramWorkflowDAO#getProgramByUuid(java.lang.String)
	 */
	@Override
	public Program getProgramByUuid(String uuid) {
		return (Program) sessionFactory.getCurrentSession().createQuery("from Program p where p.uuid = :uuid").setString(
		    "uuid", uuid).uniqueResult();
	}
	
	/**
	 * @see org.openmrs.api.db.ProgramWorkflowDAO#getState(Integer)
	 */
	@Override
	public ProgramWorkflowState getState(Integer stateId) {
		return (ProgramWorkflowState) sessionFactory.getCurrentSession().get(ProgramWorkflowState.class, stateId);
	}
	
	/**
	 * @see org.openmrs.api.db.ProgramWorkflowDAO#getStateByUuid(java.lang.String)
	 */
	@Override
	public ProgramWorkflowState getStateByUuid(String uuid) {
		return (ProgramWorkflowState) sessionFactory.getCurrentSession().createQuery(
		    "from ProgramWorkflowState pws where pws.uuid = :uuid").setString("uuid", uuid).uniqueResult();
	}
	
	/**
	 * @see org.openmrs.api.db.ProgramWorkflowDAO#getWorkflow(Integer)
	 */
	@Override
	public ProgramWorkflow getWorkflow(Integer workflowId) {
		return (ProgramWorkflow) sessionFactory.getCurrentSession().get(ProgramWorkflow.class, workflowId);
	}
	
	/**
	 * @see org.openmrs.api.db.ProgramWorkflowDAO#getWorkflowByUuid(java.lang.String)
	 */
	@Override
	public ProgramWorkflow getWorkflowByUuid(String uuid) {
		return (ProgramWorkflow) sessionFactory.getCurrentSession().createQuery(
		    "from ProgramWorkflow pw where pw.uuid = :uuid").setString("uuid", uuid).uniqueResult();
	}
	
	/**
	 * @see org.openmrs.api.db.ProgramWorkflowDAO#getProgramsByConcept(org.openmrs.Concept)
	 */
	@Override
	public List<Program> getProgramsByConcept(Concept concept) {
		String pq = "select distinct p from Program p where p.concept = :concept";
		Query pquery = sessionFactory.getCurrentSession().createQuery(pq);
		pquery.setEntity("concept", concept);
		return pquery.list();
	}
	
	/**
	 * @see org.openmrs.api.db.ProgramWorkflowDAO#getProgramWorkflowsByConcept(org.openmrs.Concept)
	 */
	@Override
	public List<ProgramWorkflow> getProgramWorkflowsByConcept(Concept concept) {
		String wq = "select distinct w from ProgramWorkflow w where w.concept = :concept";
		Query wquery = sessionFactory.getCurrentSession().createQuery(wq);
		wquery.setEntity("concept", concept);
		return wquery.list();
	}
	
	/**
	 * @see org.openmrs.api.db.ProgramWorkflowDAO#getProgramWorkflowStatesByConcept(org.openmrs.Concept)
	 */
	@Override
	public List<ProgramWorkflowState> getProgramWorkflowStatesByConcept(Concept concept) {
		String sq = "select distinct s from ProgramWorkflowState s where s.concept = :concept";
		Query squery = sessionFactory.getCurrentSession().createQuery(sq);
		squery.setEntity("concept", concept);
		return squery.list();
	}
}
