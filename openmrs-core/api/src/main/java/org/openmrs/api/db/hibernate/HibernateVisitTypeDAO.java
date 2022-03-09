package org.openmrs.api.db.hibernate;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.VisitType;
import org.openmrs.api.APIException;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.VisitTypeDAO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class HibernateVisitTypeDAO implements VisitTypeDAO {

	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}


	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<VisitType> getAllVisitTypes() throws APIException {
		return getCurrentSession().createCriteria(VisitType.class).list();
	}

	@Override
	public List<VisitType> getAllVisitTypes(boolean includeRetired) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(VisitType.class);
		return includeRetired ? criteria.list() : criteria.add(Restrictions.eq("retired", includeRetired)).list();
	}

	@Override
	@Transactional(readOnly = true)
	public VisitType getVisitType(Integer visitTypeId) {
		return (VisitType) sessionFactory.getCurrentSession().get(VisitType.class, visitTypeId);
	}

	@Override
	@Transactional(readOnly = true)
	public VisitType getVisitTypeByUuid(String uuid) {
		return (VisitType) sessionFactory.getCurrentSession().createQuery("from VisitType vt where vt.uuid = :uuid")
			.setString("uuid", uuid).uniqueResult();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<VisitType> getVisitTypes(String fuzzySearchPhrase) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(VisitType.class);
		criteria.add(Restrictions.ilike("name", fuzzySearchPhrase, MatchMode.ANYWHERE));
		criteria.addOrder(Order.asc("name"));
		return criteria.list();
	}

	@Override
	@Transactional
	public VisitType saveVisitType(VisitType visitType) {
		sessionFactory.getCurrentSession().saveOrUpdate(visitType);
		return visitType;
	}

	@Override
	@Transactional
	public void purgeVisitType(VisitType visitType) {
		sessionFactory.getCurrentSession().delete(visitType);
	}

}
