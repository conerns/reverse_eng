package org.openmrs.api.db.hibernate;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.*;
import org.openmrs.api.APIException;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.OrderDAO;
import org.openmrs.api.db.OrderGroupDAO;

import java.util.List;

public class HibernateOrderGroupDAO implements OrderGroupDAO {

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
	 * @see OrderDAO#saveOrderGroup(OrderGroup)
	 */
	@Override
	public OrderGroup saveOrderGroup(OrderGroup orderGroup) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(orderGroup);
		return orderGroup;
	}

	/**
	 * @see OrderDAO#getOrderGroupByUuid(String)
	 * @see org.openmrs.api.OrderService#getOrderGroupByUuid(String)
	 */
	@Override
	public OrderGroup getOrderGroupByUuid(String uuid) throws DAOException {
		return (OrderGroup) sessionFactory.getCurrentSession().createQuery("from OrderGroup o where o.uuid = :uuid")
			.setString("uuid", uuid).uniqueResult();
	}

	/**
	 * @see OrderDAO#getOrderGroupById(Integer)
	 * @see org.openmrs.api.OrderService#getOrderGroup(Integer)
	 */
	@Override
	public OrderGroup getOrderGroupById(Integer orderGroupId) throws DAOException {
		return (OrderGroup) sessionFactory.getCurrentSession().get(OrderGroup.class, orderGroupId);
	}


	/**
	 * @see OrderDAO#getOrderGroupsByPatient(Patient)
	 */
	@Override
	public List<OrderGroup> getOrderGroupsByPatient(Patient patient) throws DAOException {
		if (patient == null) {
			throw new APIException("Patient cannot be null");
		}
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OrderGroup.class);
		criteria.add(Restrictions.eq("patient", patient));
		return criteria.list();
	}

	/**
	 * @see OrderDAO#getOrderGroupsByEncounter(Encounter)
	 */
	@Override
	public List<OrderGroup> getOrderGroupsByEncounter(Encounter encounter) throws DAOException {
		if (encounter == null) {
			throw new APIException("Encounter cannot be null");
		}
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OrderGroup.class);
		criteria.add(Restrictions.eq("encounter", encounter));
		return criteria.list();
	}

	/**
	 * @see org.openmrs.api.db.OrderDAO#getAllOrderGroupAttributeTypes()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<OrderGroupAttributeType> getAllOrderGroupAttributeTypes() throws DAOException{
		return sessionFactory.getCurrentSession().createCriteria(OrderGroupAttributeType.class).list();
	}

	/**
	 * @see org.openmrs.api.db.OrderDAO#getOrderGroupAttributeType(java.lang.Integer)
	 */
	@Override
	public OrderGroupAttributeType getOrderGroupAttributeType(Integer orderGroupAttributeTypeId) throws DAOException{
		return sessionFactory.getCurrentSession().get(OrderGroupAttributeType.class, orderGroupAttributeTypeId);
	}

	/**
	 * @see org.openmrs.api.db.OrderDAO#getOrderGroupAttributeTypeByUuid(java.lang.String)
	 */
	@Override
	public OrderGroupAttributeType getOrderGroupAttributeTypeByUuid(String uuid) throws DAOException{
		return (OrderGroupAttributeType) sessionFactory.getCurrentSession().createCriteria(OrderGroupAttributeType.class).add(
			Restrictions.eq("uuid", uuid)).uniqueResult();
	}

	/**
	 * @see org.openmrs.api.db.OrderDAO#saveOrderGroupAttributeType(org.openmrs.OrderGroupAttributeType)
	 */
	@Override
	public OrderGroupAttributeType saveOrderGroupAttributeType(OrderGroupAttributeType orderGroupAttributeType)throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(orderGroupAttributeType);
		return orderGroupAttributeType;
	}

	/**
	 * @see org.openmrs.api.db.OrderDAO#deleteOrderGroupAttributeType(org.openmrs.OrderGroupAttributeType)
	 */
	@Override
	public void deleteOrderGroupAttributeType(OrderGroupAttributeType orderGroupAttributeType) throws DAOException{
		sessionFactory.getCurrentSession().delete(orderGroupAttributeType);
	}

	/**
	 * @see org.openmrs.api.db.OrderDAO#getOrderGroupAttributeByUuid(String)
	 */
	@Override
	public OrderGroupAttribute getOrderGroupAttributeByUuid(String uuid)  throws DAOException{
		return (OrderGroupAttribute) sessionFactory.getCurrentSession().createQuery("from OrderGroupAttribute d where d.uuid = :uuid")
			.setString("uuid", uuid).uniqueResult();
	}

	/**
	 * @see org.openmrs.api.db.OrderDAO#getOrderGroupAttributeTypeByName(String)
	 */
	@Override
	public OrderGroupAttributeType getOrderGroupAttributeTypeByName(String name) throws DAOException{
		return (OrderGroupAttributeType) sessionFactory.getCurrentSession().createCriteria(OrderGroupAttributeType.class).add(
			Restrictions.eq("name", name)).uniqueResult();
	}
}
