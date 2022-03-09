package org.openmrs.api.db.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;
import org.openmrs.api.db.VisitAttributeDAO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class HibernateVisitAttributeDAO implements VisitAttributeDAO {
	
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}



	/**
	 * @see org.openmrs.api.db.VisitDAO#getAllVisitAttributeTypes()
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public List<VisitAttributeType> getAllVisitAttributeTypes() {
		return getCurrentSession().createCriteria(VisitAttributeType.class).list();
	}

	/**
	 * @see org.openmrs.api.db.VisitDAO#getVisitAttributeType(java.lang.Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public VisitAttributeType getVisitAttributeType(Integer id) {
		return (VisitAttributeType) getCurrentSession().get(VisitAttributeType.class, id);
	}

	/**
	 * @see org.openmrs.api.db.VisitDAO#getVisitAttributeTypeByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public VisitAttributeType getVisitAttributeTypeByUuid(String uuid) {
		return (VisitAttributeType) getCurrentSession().createCriteria(VisitAttributeType.class).add(
			Restrictions.eq("uuid", uuid)).uniqueResult();
	}

	/**
	 * @see org.openmrs.api.db.VisitDAO#saveVisitAttributeType(org.openmrs.VisitAttributeType)
	 */
	@Override
	@Transactional
	public VisitAttributeType saveVisitAttributeType(VisitAttributeType visitAttributeType) {
		getCurrentSession().saveOrUpdate(visitAttributeType);
		return visitAttributeType;
	}

	/**
	 * @see org.openmrs.api.db.VisitDAO#deleteVisitAttributeType(org.openmrs.VisitAttributeType)
	 */
	@Override
	@Transactional
	public void deleteVisitAttributeType(VisitAttributeType visitAttributeType) {
		getCurrentSession().delete(visitAttributeType);
	}

	/**
	 * @see org.openmrs.api.db.VisitDAO#getVisitAttributeByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public VisitAttribute getVisitAttributeByUuid(String uuid) {
		return (VisitAttribute) getCurrentSession().createCriteria(VisitAttribute.class).add(Restrictions.eq("uuid", uuid))
			.uniqueResult();
	}
	
}
