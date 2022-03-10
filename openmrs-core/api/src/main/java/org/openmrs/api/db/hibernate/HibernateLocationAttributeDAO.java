package org.openmrs.api.db.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.api.db.LocationAttributeDAO;

import java.util.List;

public class HibernateLocationAttributeDAO implements LocationAttributeDAO {

	private SessionFactory sessionFactory;

	/**
	 * @see org.openmrs.api.db.LocationDAO#setSessionFactory(org.hibernate.SessionFactory)
	 */
	@Override
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}


	/**
	 * @see org.openmrs.api.db.LocationDAO#getAllLocationAttributeTypes()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<LocationAttributeType> getAllLocationAttributeTypes() {
		return sessionFactory.getCurrentSession().createCriteria(LocationAttributeType.class).list();
	}

	/**
	 * @see org.openmrs.api.db.LocationDAO#getLocationAttributeType(java.lang.Integer)
	 */
	@Override
	public LocationAttributeType getLocationAttributeType(Integer id) {
		return (LocationAttributeType) sessionFactory.getCurrentSession().get(LocationAttributeType.class, id);
	}

	/**
	 * @see org.openmrs.api.db.LocationDAO#getLocationAttributeTypeByUuid(java.lang.String)
	 */
	@Override
	public LocationAttributeType getLocationAttributeTypeByUuid(String uuid) {
		return (LocationAttributeType) sessionFactory.getCurrentSession().createCriteria(LocationAttributeType.class).add(
			Restrictions.eq("uuid", uuid)).uniqueResult();
	}

	/**
	 * @see org.openmrs.api.db.LocationDAO#saveLocationAttributeType(org.openmrs.LocationAttributeType)
	 */
	@Override
	public LocationAttributeType saveLocationAttributeType(LocationAttributeType locationAttributeType) {
		sessionFactory.getCurrentSession().saveOrUpdate(locationAttributeType);
		return locationAttributeType;
	}

	/**
	 * @see org.openmrs.api.db.LocationDAO#deleteLocationAttributeType(org.openmrs.LocationAttributeType)
	 */
	@Override
	public void deleteLocationAttributeType(LocationAttributeType locationAttributeType) {
		sessionFactory.getCurrentSession().delete(locationAttributeType);
	}

	/**
	 * @see org.openmrs.api.db.LocationDAO#getLocationAttributeByUuid(java.lang.String)
	 */
	@Override
	public LocationAttribute getLocationAttributeByUuid(String uuid) {
		return (LocationAttribute) sessionFactory.getCurrentSession().createCriteria(LocationAttribute.class).add(
			Restrictions.eq("uuid", uuid)).uniqueResult();
	}

	/**
	 * @see org.openmrs.api.db.LocationDAO#getLocationAttributeTypeByName(java.lang.String)
	 */
	@Override
	public LocationAttributeType getLocationAttributeTypeByName(String name) {
		return (LocationAttributeType) sessionFactory.getCurrentSession().createCriteria(LocationAttributeType.class).add(
			Restrictions.eq("name", name)).uniqueResult();
	}
}
