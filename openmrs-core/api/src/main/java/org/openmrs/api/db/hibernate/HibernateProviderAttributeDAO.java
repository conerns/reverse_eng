package org.openmrs.api.db.hibernate;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.ProviderAttribute;
import org.openmrs.ProviderAttributeType;
import org.openmrs.api.db.ProviderAttributeDAO;

import java.util.List;

public class HibernateProviderAttributeDAO implements ProviderAttributeDAO {

	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}


	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	/**
	 * @see org.openmrs.api.db.ProviderDAO#getProviderAttribute(Integer)
	 */
	@Override
	public ProviderAttribute getProviderAttribute(Integer providerAttributeID) {
		return (ProviderAttribute) getSession().get(ProviderAttribute.class, providerAttributeID);
	}

	/**
	 * @see org.openmrs.api.db.ProviderDAO#getProviderAttributeByUuid(String)
	 */

	@Override
	public ProviderAttribute getProviderAttributeByUuid(String uuid) {
		return getByUuid(uuid, ProviderAttribute.class);
	}
	
	/* (non-Javadoc)
	 * @see org.openmrs.api.db.ProviderDAO#getAllProviderAttributeTypes(boolean)
	 */
	@Override
	public List<ProviderAttributeType> getAllProviderAttributeTypes(boolean includeRetired) {
		return getAll(includeRetired, ProviderAttributeType.class);
	}
	
	private <T> List<T> getAll(boolean includeRetired, Class<T> clazz) {
		Criteria criteria = getSession().createCriteria(clazz);
		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", false));
		} else {
			//push retired Provider to the end of the returned list
			criteria.addOrder(Order.asc("retired"));
		}
		criteria.addOrder(Order.asc("name"));
		return criteria.list();
	}

	private <T> T getByUuid(String uuid, Class<T> clazz) {
		Criteria criteria = getSession().createCriteria(clazz);
		criteria.add(Restrictions.eq("uuid", uuid));
		return (T) criteria.uniqueResult();
	}

	/* (non-Javadoc)
	 * @see org.openmrs.api.db.ProviderDAO#getProviderAttributeType(java.lang.Integer)
	 */
	@Override
	public ProviderAttributeType getProviderAttributeType(Integer providerAttributeTypeId) {
		return (ProviderAttributeType) getSession().get(ProviderAttributeType.class, providerAttributeTypeId);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.api.db.ProviderDAO#getProviderAttributeTypeByUuid(java.lang.String)
	 */
	@Override
	public ProviderAttributeType getProviderAttributeTypeByUuid(String uuid) {
		return getByUuid(uuid, ProviderAttributeType.class);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.api.db.ProviderDAO#saveProviderAttributeType(org.openmrs.ProviderAttributeType)
	 */
	@Override
	public ProviderAttributeType saveProviderAttributeType(ProviderAttributeType providerAttributeType) {
		getSession().saveOrUpdate(providerAttributeType);
		return providerAttributeType;
	}

	/* (non-Javadoc)
	 * @see org.openmrs.api.db.ProviderDAO#deleteProviderAttributeType(org.openmrs.ProviderAttributeType)
	 */
	@Override
	public void deleteProviderAttributeType(ProviderAttributeType providerAttributeType) {
		getSession().delete(providerAttributeType);
	}
}
