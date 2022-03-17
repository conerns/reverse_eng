package org.openmrs.api.db.hibernate;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.CareSetting;
import org.openmrs.api.db.CareSettingDAO;
import org.openmrs.api.db.OrderDAO;

import java.util.List;

public class HibernateCareSettingDAO implements CareSettingDAO {
	
	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;

	/**
	 * Set session factory
	 *
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}


	/**
	 * @see org.openmrs.api.db.OrderDAO#getCareSetting(Integer)
	 */
	@Override
	public CareSetting getCareSetting(Integer careSettingId) {
		return (CareSetting) sessionFactory.getCurrentSession().get(CareSetting.class, careSettingId);
	}

	/**
	 * @see OrderDAO#getCareSettingByUuid(String)
	 */
	@Override
	public CareSetting getCareSettingByUuid(String uuid) {
		return (CareSetting) sessionFactory.getCurrentSession().createQuery("from CareSetting cs where cs.uuid = :uuid")
			.setString("uuid", uuid).uniqueResult();
	}

	/**
	 * @see OrderDAO#getCareSettingByName(String)
	 */
	@Override
	public CareSetting getCareSettingByName(String name) {
		return (CareSetting) sessionFactory.getCurrentSession().createCriteria(CareSetting.class).add(
			Restrictions.ilike("name", name)).uniqueResult();
	}

	/**
	 * @see OrderDAO#getCareSettings(boolean)
	 */
	@Override
	public List<CareSetting> getCareSettings(boolean includeRetired) {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(CareSetting.class);
		if (!includeRetired) {
			c.add(Restrictions.eq("retired", false));
		}
		return c.list();
	}
}
