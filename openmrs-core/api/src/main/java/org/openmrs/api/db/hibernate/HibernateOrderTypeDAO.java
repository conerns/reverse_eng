package org.openmrs.api.db.hibernate;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.ConceptClass;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.api.db.OrderDAO;
import org.openmrs.api.db.OrderTypeDAO;

import java.util.List;

public class HibernateOrderTypeDAO implements OrderTypeDAO {

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
	 * @see OrderDAO#getOrderTypeByName
	 */
	@Override
	public OrderType getOrderTypeByName(String orderTypeName) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OrderType.class);
		criteria.add(Restrictions.eq("name", orderTypeName));
		return (OrderType) criteria.uniqueResult();
	}


	/**
	 * @see org.openmrs.api.db.OrderDAO#getOrderType(Integer)
	 */
	@Override
	public OrderType getOrderType(Integer orderTypeId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OrderType.class);
		criteria.add(Restrictions.eq("orderTypeId", orderTypeId));
		return (OrderType) criteria.uniqueResult();
	}

	/**
	 * @see org.openmrs.api.db.OrderDAO#getOrderTypeByUuid(String)
	 */
	@Override
	public OrderType getOrderTypeByUuid(String uuid) {
		return (OrderType) sessionFactory.getCurrentSession().createQuery("from OrderType o where o.uuid = :uuid")
			.setString("uuid", uuid).uniqueResult();
	}

	/**
	 * @see org.openmrs.api.db.OrderDAO#getOrderTypes(boolean)
	 */
	@Override
	public List<OrderType> getOrderTypes(boolean includeRetired) {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(OrderType.class);
		if (!includeRetired) {
			c.add(Restrictions.eq("retired", false));
		}
		return c.list();
	}

	/**
	 * @see org.openmrs.api.db.OrderDAO#getOrderTypeByConceptClass(org.openmrs.ConceptClass)
	 */
	@Override
	public OrderType getOrderTypeByConceptClass(ConceptClass conceptClass) {
		return (OrderType) sessionFactory.getCurrentSession().createQuery(
				"from OrderType where :conceptClass in elements(conceptClasses)").setParameter("conceptClass", conceptClass)
			.uniqueResult();
	}

	/**
	 * @see org.openmrs.api.OrderService#saveOrderType(org.openmrs.OrderType)
	 */
	@Override
	public OrderType saveOrderType(OrderType orderType) {
		sessionFactory.getCurrentSession().saveOrUpdate(orderType);
		return orderType;
	}

	/**
	 * @see org.openmrs.api.OrderService#purgeOrderType(org.openmrs.OrderType)
	 */
	@Override
	public void purgeOrderType(OrderType orderType) {
		sessionFactory.getCurrentSession().delete(orderType);
	}

	/**
	 * @see org.openmrs.api.OrderService#getSubtypes(org.openmrs.OrderType, boolean)
	 */
	@Override
	public List<OrderType> getOrderSubtypes(OrderType orderType, boolean includeRetired) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OrderType.class);
		criteria.add(Restrictions.eq("parent", orderType));
		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", false));
		}
		return criteria.list();
	}

	@Override
	public boolean isOrderTypeInUse(OrderType orderType) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Order.class);
		criteria.add(Restrictions.eq("orderType", orderType));
		return !criteria.list().isEmpty();
	}
}
