package org.openmrs.api.db.hibernate;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.DistinctRootEntityResultTransformer;
import org.openmrs.Concept;
import org.openmrs.Order;
import org.openmrs.OrderFrequency;
import org.openmrs.api.db.OrderDAO;
import org.openmrs.api.db.OrderFrequencyDAO;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class HibernateOrderFrequencyDAO implements OrderFrequencyDAO {

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
	 * @see OrderDAO#getOrderFrequency
	 */
	@Override
	public OrderFrequency getOrderFrequency(Integer orderFrequencyId) {
		return (OrderFrequency) sessionFactory.getCurrentSession().get(OrderFrequency.class, orderFrequencyId);
	}

	/**
	 * @see OrderDAO#getOrderFrequencyByUuid
	 */
	@Override
	public OrderFrequency getOrderFrequencyByUuid(String uuid) {
		return (OrderFrequency) sessionFactory.getCurrentSession().createQuery("from OrderFrequency o where o.uuid = :uuid")
			.setString("uuid", uuid).uniqueResult();
	}

	/**
	 * @see OrderDAO#getOrderFrequencies(boolean)
	 */
	@Override
	public List<OrderFrequency> getOrderFrequencies(boolean includeRetired) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OrderFrequency.class);
		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", false));
		}
		return criteria.list();
	}

	/**
	 * @see OrderDAO#getOrderFrequencies(String, java.util.Locale, boolean, boolean)
	 */
	@Override
	public List<OrderFrequency> getOrderFrequencies(String searchPhrase, Locale locale, boolean exactLocale,
													boolean includeRetired) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OrderFrequency.class, "orderFreq");
		criteria.setResultTransformer(DistinctRootEntityResultTransformer.INSTANCE);

		//match on the concept names of the concepts
		criteria.createAlias("orderFreq.concept", "concept");
		criteria.createAlias("concept.names", "conceptName");
		criteria.add(Restrictions.ilike("conceptName.name", searchPhrase, MatchMode.ANYWHERE));
		if (locale != null) {
			List<Locale> locales = new ArrayList<>(2);
			locales.add(locale);
			//look in the broader locale too if exactLocale is false e.g en for en_GB
			if (!exactLocale && StringUtils.isNotBlank(locale.getCountry())) {
				locales.add(new Locale(locale.getLanguage()));
			}
			criteria.add(Restrictions.in("conceptName.locale", locales));
		}

		if (!includeRetired) {
			criteria.add(Restrictions.eq("orderFreq.retired", false));
		}

		return criteria.list();
	}

	/**
	 * @see org.openmrs.api.db.OrderDAO#saveOrderFrequency(org.openmrs.OrderFrequency)
	 */
	@Override
	public OrderFrequency saveOrderFrequency(OrderFrequency orderFrequency) {
		sessionFactory.getCurrentSession().saveOrUpdate(orderFrequency);
		return orderFrequency;
	}

	/**
	 * @see org.openmrs.api.db.OrderDAO#purgeOrderFrequency(org.openmrs.OrderFrequency)
	 */
	@Override
	public void purgeOrderFrequency(OrderFrequency orderFrequency) {
		sessionFactory.getCurrentSession().delete(orderFrequency);
	}

	/**
	 * @see org.openmrs.api.db.OrderDAO#isOrderFrequencyInUse(org.openmrs.OrderFrequency)
	 */
	@Override
	public boolean isOrderFrequencyInUse(OrderFrequency orderFrequency) {

		Set<EntityType<?>> entities = sessionFactory.getMetamodel().getEntities();

		for (EntityType<?> entityTpe : entities) {
			Class<?> entityClass = entityTpe.getJavaType();
			if (Order.class.equals(entityClass)) {
				//ignore the org.openmrs.Order class itself
				continue;
			}

			if (!Order.class.isAssignableFrom(entityClass)) {
				//not a sub class of Order
				continue;
			}

			for (Attribute<?,?> attribute : entityTpe.getDeclaredAttributes()) {
				if (attribute.getJavaType().equals(OrderFrequency.class)) {
					Criteria criteria = sessionFactory.getCurrentSession().createCriteria(entityClass);
					criteria.add(Restrictions.eq(attribute.getName(), orderFrequency));
					criteria.setMaxResults(1);
					if (!criteria.list().isEmpty()) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * @see org.openmrs.api.db.OrderDAO#getOrderFrequencyByConcept(org.openmrs.Concept)
	 */
	@Override
	public OrderFrequency getOrderFrequencyByConcept(Concept concept) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OrderFrequency.class);
		criteria.add(Restrictions.eq("concept", concept));
		return (OrderFrequency) criteria.uniqueResult();
	}
}
