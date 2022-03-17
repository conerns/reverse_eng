package org.openmrs.api.db.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.OrderAttribute;
import org.openmrs.OrderAttributeType;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.OrderAttributeDAO;

import java.util.List;

public class HibernateOrderAttributeDAO implements OrderAttributeDAO {


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
	 * @param uuid The uuid associated with the order attribute to retrieve.
	 * @see org.openmrs.api.db.OrderDAO#getOrderAttributeByUuid(String)
	 */
	@Override
	public OrderAttribute getOrderAttributeByUuid(String uuid) throws DAOException {
		return (OrderAttribute) sessionFactory.getCurrentSession()
			.createQuery("from OrderAttribute a where a.uuid = :uuid")
			.setString("uuid", uuid).uniqueResult();
	}

	/**
	 * @see org.openmrs.api.db.OrderDAO#getAllOrderAttributeTypes()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<OrderAttributeType> getAllOrderAttributeTypes() throws DAOException {
		return sessionFactory.getCurrentSession().createCriteria(OrderAttributeType.class).list();
	}

	/**
	 * @param orderAttributeTypeId The orderAttributeTypeId for the order attribute type to retrieve.
	 * @see org.openmrs.api.db.OrderDAO#getOrderAttributeTypeById(Integer)
	 */
	@Override
	public OrderAttributeType getOrderAttributeTypeById(Integer orderAttributeTypeId) throws DAOException {
		return sessionFactory.getCurrentSession().get(OrderAttributeType.class, orderAttributeTypeId);
	}

	/**
	 * @param uuid The uuid associated with the order attribute type to retrieve
	 * @see org.openmrs.api.db.OrderDAO#getOrderAttributeTypeByUuid(String)
	 */
	@Override
	public OrderAttributeType getOrderAttributeTypeByUuid(String uuid) throws DAOException {
		return (OrderAttributeType) sessionFactory.getCurrentSession().createCriteria(OrderAttributeType.class)
			.add(Restrictions.eq("uuid", uuid)).uniqueResult();
	}

	/**
	 * @param orderAttributeType The orderAttributeType to save
	 * @see org.openmrs.api.db.OrderDAO#saveOrderAttributeType(OrderAttributeType)
	 */
	@Override
	public OrderAttributeType saveOrderAttributeType(OrderAttributeType orderAttributeType) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(orderAttributeType);
		return orderAttributeType;
	}

	/**
	 * @param orderAttributeType The orderAttributeType to retire
	 * @see org.openmrs.api.db.OrderDAO#purgeOrderAttributeType(OrderAttributeType)
	 */
	@Override
	public void deleteOrderAttributeType(OrderAttributeType orderAttributeType) throws DAOException {
		sessionFactory.getCurrentSession().delete(orderAttributeType);
	}

	/**
	 * @param name The name of the order attribute type to retrieve
	 * @see org.openmrs.api.db.OrderDAO#getOrderAttributeTypeByName(String)
	 */
	@Override
	public OrderAttributeType getOrderAttributeTypeByName(String name) throws DAOException {
		return (OrderAttributeType) sessionFactory.getCurrentSession().createCriteria(OrderAttributeType.class)
			.add(Restrictions.eq("name", name)).uniqueResult();
	}
}
