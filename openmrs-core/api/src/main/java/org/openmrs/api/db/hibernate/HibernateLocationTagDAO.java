package org.openmrs.api.db.hibernate;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.db.LocationTagDAO;

import java.util.List;

public class HibernateLocationTagDAO implements LocationTagDAO {

	private SessionFactory sessionFactory;

	/**
	 * @see org.openmrs.api.db.LocationDAO#setSessionFactory(org.hibernate.SessionFactory)
	 */
	@Override
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * @see org.openmrs.api.db.LocationDAO#saveLocation(org.openmrs.Location)
	 */
	@Override
	public LocationTag saveLocationTag(LocationTag tag) {
		sessionFactory.getCurrentSession().saveOrUpdate(tag);
		return tag;
	}

	/**
	 * @see org.openmrs.api.db.LocationDAO#getLocationTag(java.lang.Integer)
	 */
	@Override
	public LocationTag getLocationTag(Integer locationTagId) {
		return (LocationTag) sessionFactory.getCurrentSession().get(LocationTag.class, locationTagId);
	}

	/**
	 * @see org.openmrs.api.db.LocationDAO#getLocationTagByName(java.lang.String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public LocationTag getLocationTagByName(String tag) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LocationTag.class).add(
			Restrictions.eq("name", tag));

		List<LocationTag> tags = criteria.list();
		if (null == tags || tags.isEmpty()) {
			return null;
		}
		return tags.get(0);
	}

	/**
	 * @see org.openmrs.api.db.LocationDAO#getAllLocationTags(boolean)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<LocationTag> getAllLocationTags(boolean includeRetired) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LocationTag.class);
		if (!includeRetired) {
			criteria.add(Restrictions.like("retired", false));
		}
		criteria.addOrder(Order.asc("name"));
		return criteria.list();
	}

	/**
	 * @see org.openmrs.api.db.LocationDAO#getLocationTags(String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<LocationTag> getLocationTags(String search) {
		return sessionFactory.getCurrentSession().createCriteria(LocationTag.class)
			// 'ilike' case insensitive search
			.add(Restrictions.ilike("name", search, MatchMode.START)).addOrder(Order.asc("name")).list();
	}

	/**
	 * @see org.openmrs.api.db.LocationDAO#deleteLocationTag(org.openmrs.LocationTag)
	 */
	@Override
	public void deleteLocationTag(LocationTag tag) {
		sessionFactory.getCurrentSession().delete(tag);
	}


	/**
	 * @see org.openmrs.api.db.LocationDAO#getLocationTagByUuid(java.lang.String)
	 */
	@Override
	public LocationTag getLocationTagByUuid(String uuid) {
		return (LocationTag) sessionFactory.getCurrentSession().createQuery("from LocationTag where uuid = :uuid")
			.setString("uuid", uuid).uniqueResult();
	}
}
