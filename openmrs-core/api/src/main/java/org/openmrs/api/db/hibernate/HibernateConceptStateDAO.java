package org.openmrs.api.db.hibernate;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Concept;
import org.openmrs.ConceptStateConversion;
import org.openmrs.ProgramWorkflow;
import org.openmrs.api.db.ConceptStateDAO;
import org.openmrs.api.db.DAOException;

import java.util.List;

public class HibernateConceptStateDAO implements ConceptStateDAO {

	private SessionFactory sessionFactory;

	/**
	 * Hibernate Session Factory
	 *
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * @see org.openmrs.api.db.ProgramWorkflowDAO#saveConceptStateConversion(org.openmrs.ConceptStateConversion)
	 */
	@Override
	public ConceptStateConversion saveConceptStateConversion(ConceptStateConversion csc) throws DAOException {
		if (csc.getConceptStateConversionId() == null) {
			sessionFactory.getCurrentSession().save(csc);
		} else {
			sessionFactory.getCurrentSession().merge(csc);
		}
		return csc;
	}

	/**
	 * @see org.openmrs.api.db.ProgramWorkflowDAO#getAllConceptStateConversions()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<ConceptStateConversion> getAllConceptStateConversions() throws DAOException {
		return sessionFactory.getCurrentSession().createCriteria(ConceptStateConversion.class).list();
	}

	/**
	 * @see org.openmrs.api.db.ProgramWorkflowDAO#getConceptStateConversion(java.lang.Integer)
	 */
	@Override
	public ConceptStateConversion getConceptStateConversion(Integer conceptStateConversionId) {
		return (ConceptStateConversion) sessionFactory.getCurrentSession().get(ConceptStateConversion.class,
			conceptStateConversionId);
	}

	/**
	 * @see org.openmrs.api.db.ProgramWorkflowDAO#deleteConceptStateConversion(org.openmrs.ConceptStateConversion)
	 */
	@Override
	public void deleteConceptStateConversion(ConceptStateConversion csc) {
		sessionFactory.getCurrentSession().delete(csc);
	}

	/**
	 * @see org.openmrs.api.db.ProgramWorkflowDAO#getConceptStateConversion(org.openmrs.ProgramWorkflow,
	 *      org.openmrs.Concept)
	 */
	@Override
	public ConceptStateConversion getConceptStateConversion(ProgramWorkflow workflow, Concept trigger) {
		ConceptStateConversion csc = null;

		if (workflow != null && trigger != null) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ConceptStateConversion.class, "csc");
			criteria.add(Restrictions.eq("csc.programWorkflow", workflow));
			criteria.add(Restrictions.eq("csc.concept", trigger));
			csc = (ConceptStateConversion) criteria.uniqueResult();
		}

		return csc;
	}

	/**
	 * @see org.openmrs.api.db.ProgramWorkflowDAO#getConceptStateConversionByUuid(java.lang.String)
	 */
	@Override
	public ConceptStateConversion getConceptStateConversionByUuid(String uuid) {
		return (ConceptStateConversion) sessionFactory.getCurrentSession().createQuery(
			"from ConceptStateConversion csc where csc.uuid = :uuid").setString("uuid", uuid).uniqueResult();
	}
}
