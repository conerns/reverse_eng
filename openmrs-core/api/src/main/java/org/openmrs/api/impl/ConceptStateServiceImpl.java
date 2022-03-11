package org.openmrs.api.impl;

import org.openmrs.Concept;
import org.openmrs.ConceptStateConversion;
import org.openmrs.PatientProgram;
import org.openmrs.ProgramWorkflow;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptStateService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.ConceptStateDAO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class ConceptStateServiceImpl extends BaseOpenmrsService implements ConceptStateService {
	
	protected ConceptStateDAO dao;

	public void setConceptStateDAO(ConceptStateDAO conceptStateDAO) {
		this.dao = conceptStateDAO;
	}

	/**
	 * @see org.openmrs.api.ProgramWorkflowService#saveConceptStateConversion(org.openmrs.ConceptStateConversion)
	 */
	@Override
	public ConceptStateConversion saveConceptStateConversion(ConceptStateConversion csc) throws APIException {
		if (csc.getConcept() == null || csc.getProgramWorkflow() == null || csc.getProgramWorkflowState() == null) {
			throw new APIException("ConceptStateConversion.requires", (Object[]) null);
		}
		return dao.saveConceptStateConversion(csc);
	}

	/**
	 * @see org.openmrs.api.ProgramWorkflowService#getConceptStateConversion(java.lang.Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public ConceptStateConversion getConceptStateConversion(Integer id) {
		return dao.getConceptStateConversion(id);
	}

	/**
	 * @see org.openmrs.api.ProgramWorkflowService#getAllConceptStateConversions()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ConceptStateConversion> getAllConceptStateConversions() throws APIException {
		return dao.getAllConceptStateConversions();
	}

	/**
	 * @see org.openmrs.api.ProgramWorkflowService#purgeConceptStateConversion(org.openmrs.ConceptStateConversion)
	 */
	@Override
	public void purgeConceptStateConversion(ConceptStateConversion conceptStateConversion) throws APIException {
		Context.getConceptStateService().purgeConceptStateConversion(conceptStateConversion, false);
	}

	/**
	 * @see org.openmrs.api.ProgramWorkflowService#purgeConceptStateConversion(org.openmrs.ConceptStateConversion,
	 *      boolean)
	 */
	@Override
	public void purgeConceptStateConversion(ConceptStateConversion conceptStateConversion, boolean cascade)
		throws APIException {
		dao.deleteConceptStateConversion(conceptStateConversion);
	}
	

	/**
	 * @see org.openmrs.api.ProgramWorkflowService#getConceptStateConversion(org.openmrs.ProgramWorkflow,
	 *      org.openmrs.Concept)
	 */
	@Override
	@Transactional(readOnly = true)
	public ConceptStateConversion getConceptStateConversion(ProgramWorkflow workflow, Concept trigger) {
		return dao.getConceptStateConversion(workflow, trigger);
	}
	
	/**
	 * @see org.openmrs.api.ProgramWorkflowService#getConceptStateConversionByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public ConceptStateConversion getConceptStateConversionByUuid(String uuid) {
		return dao.getConceptStateConversionByUuid(uuid);
	}
}
