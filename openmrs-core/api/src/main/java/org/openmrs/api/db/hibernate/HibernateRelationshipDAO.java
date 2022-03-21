package org.openmrs.api.db.hibernate;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StringType;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.Person;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.RelationshipDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

public class HibernateRelationshipDAO implements RelationshipDAO {

	private static final Logger log = LoggerFactory.getLogger(HibernateRelationshipDAO.class);

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
	 * @see org.openmrs.api.RelationshipService#getRelationship(java.lang.Integer)
	 * @see org.openmrs.api.db.RelationshipDAO#getRelationship(java.lang.Integer)
	 */
	@Override
	public Relationship getRelationship(Integer relationshipId) throws DAOException {

		return (Relationship) sessionFactory.getCurrentSession()
			.get(Relationship.class, relationshipId);
	}

	/**
	 * @see org.openmrs.api.RelationshipService#getAllRelationships(boolean)
	 * @see org.openmrs.api.db.RelationshipDAO#getAllRelationships(boolean)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Relationship> getAllRelationships(boolean includeVoided) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Relationship.class, "r");

		if (!includeVoided) {
			criteria.add(Restrictions.eq("voided", false));
		}

		return criteria.list();
	}

	/**
	 * @see org.openmrs.api.RelationshipService#getRelationships(org.openmrs.Person, org.openmrs.Person,
	 *      org.openmrs.RelationshipType)
	 * @see org.openmrs.api.db.RelationshipDAO#getRelationships(org.openmrs.Person, org.openmrs.Person,
	 *      org.openmrs.RelationshipType)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Relationship> getRelationships(Person fromPerson, Person toPerson, RelationshipType relType) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Relationship.class, "r");

		if (fromPerson != null) {
			criteria.add(Restrictions.eq("personA", fromPerson));
		}
		if (toPerson != null) {
			criteria.add(Restrictions.eq("personB", toPerson));
		}
		if (relType != null) {
			criteria.add(Restrictions.eq("relationshipType", relType));
		}

		criteria.add(Restrictions.eq("voided", false));

		return criteria.list();
	}

	/**
	 * @see org.openmrs.api.RelationshipService#getRelationships(org.openmrs.Person, org.openmrs.Person,
	 *      org.openmrs.RelationshipType, java.util.Date, java.util.Date)
	 * @see org.openmrs.api.db.RelationshipDAO#getRelationships(org.openmrs.Person, org.openmrs.Person,
	 *      org.openmrs.RelationshipType, java.util.Date, java.util.Date)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Relationship> getRelationships(Person fromPerson, Person toPerson, RelationshipType relType,
											   Date startEffectiveDate, Date endEffectiveDate) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Relationship.class, "r");

		if (fromPerson != null) {
			criteria.add(Restrictions.eq("personA", fromPerson));
		}
		if (toPerson != null) {
			criteria.add(Restrictions.eq("personB", toPerson));
		}
		if (relType != null) {
			criteria.add(Restrictions.eq("relationshipType", relType));
		}
		if (startEffectiveDate != null) {
			criteria.add(Restrictions.disjunction().add(
				Restrictions.and(Restrictions.le("startDate", startEffectiveDate), Restrictions.ge("endDate",
					startEffectiveDate))).add(
				Restrictions.and(Restrictions.le("startDate", startEffectiveDate), Restrictions.isNull("endDate"))).add(
				Restrictions.and(Restrictions.isNull("startDate"), Restrictions.ge("endDate", startEffectiveDate))).add(
				Restrictions.and(Restrictions.isNull("startDate"), Restrictions.isNull("endDate"))));
		}
		if (endEffectiveDate != null) {
			criteria.add(Restrictions.disjunction().add(
				Restrictions.and(Restrictions.le("startDate", endEffectiveDate), Restrictions
					.ge("endDate", endEffectiveDate))).add(
				Restrictions.and(Restrictions.le("startDate", endEffectiveDate), Restrictions.isNull("endDate"))).add(
				Restrictions.and(Restrictions.isNull("startDate"), Restrictions.ge("endDate", endEffectiveDate))).add(
				Restrictions.and(Restrictions.isNull("startDate"), Restrictions.isNull("endDate"))));
		}
		criteria.add(Restrictions.eq("voided", false));

		return criteria.list();
	}

	/**
	 * @see org.openmrs.api.RelationshipService#getRelationshipType(java.lang.Integer)
	 * @see org.openmrs.api.db.RelationshipDAO#getRelationshipType(java.lang.Integer)
	 */
	@Override
	public RelationshipType getRelationshipType(Integer relationshipTypeId) throws DAOException {

		return (RelationshipType) sessionFactory.getCurrentSession().get(
			RelationshipType.class, relationshipTypeId);
	}

	/**
	 * @see org.openmrs.api.RelationshipService#getRelationshipTypes(java.lang.String, java.lang.Boolean)
	 * @see org.openmrs.api.db.RelationshipDAO#getRelationshipTypes(java.lang.String, java.lang.Boolean)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<RelationshipType> getRelationshipTypes(String relationshipTypeName, Boolean preferred) throws DAOException {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(RelationshipType.class);
		criteria.add(Restrictions.sqlRestriction("CONCAT(a_Is_To_B, CONCAT('/', b_Is_To_A)) like (?)", relationshipTypeName,
			new StringType()));

		if (preferred != null) {
			criteria.add(Restrictions.eq("preferred", preferred));
		}

		return criteria.list();
	}

	/**
	 * @see org.openmrs.api.RelationshipService#saveRelationshipType(org.openmrs.RelationshipType)
	 * @see org.openmrs.api.db.RelationshipDAO#saveRelationshipType(org.openmrs.RelationshipType)
	 */
	@Override
	public RelationshipType saveRelationshipType(RelationshipType relationshipType) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(relationshipType);
		return relationshipType;
	}

	/**
	 * @see org.openmrs.api.RelationshipService#purgeRelationshipType(org.openmrs.RelationshipType)
	 * @see org.openmrs.api.db.RelationshipDAO#deleteRelationshipType(org.openmrs.RelationshipType)
	 */
	@Override
	public void deleteRelationshipType(RelationshipType relationshipType) throws DAOException {
		sessionFactory.getCurrentSession().delete(relationshipType);
	}

	/**
	 * @see org.openmrs.api.RelationshipService#saveRelationship(org.openmrs.Relationship)
	 * @see org.openmrs.api.db.RelationshipDAO#saveRelationship(org.openmrs.Relationship)
	 */
	@Override
	public Relationship saveRelationship(Relationship relationship) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(relationship);
		return relationship;
	}

	/**
	 * @see org.openmrs.api.RelationshipService#purgeRelationship(org.openmrs.Relationship)
	 * @see org.openmrs.api.db.RelationshipDAO#deleteRelationship(org.openmrs.Relationship)
	 */
	@Override
	public void deleteRelationship(Relationship relationship) throws DAOException {
		sessionFactory.getCurrentSession().delete(relationship);
	}

	/**
	 * Used by deletePerson, deletePatient, and deleteUser to remove all properties of a person
	 * before deleting them.
	 *
	 * @param sessionFactory the session factory from which to pull the current session
	 * @param person the person to delete
	 */


	/**
	 * @see org.openmrs.api.db.RelationshipDAO#getRelationshipByUuid(java.lang.String)
	 */
	@Override
	public Relationship getRelationshipByUuid(String uuid) {
		return (Relationship) sessionFactory.getCurrentSession().createQuery("from Relationship r where r.uuid = :uuid")
			.setString("uuid", uuid).uniqueResult();
	}

	/**
	 * @see org.openmrs.api.db.RelationshipDAO#getRelationshipTypeByUuid(java.lang.String)
	 */
	@Override
	public RelationshipType getRelationshipTypeByUuid(String uuid) {
		return (RelationshipType) sessionFactory.getCurrentSession().createQuery(
			"from RelationshipType rt where rt.uuid = :uuid").setString("uuid", uuid).uniqueResult();
	}

	/**
	 * @see org.openmrs.api.db.RelationshipDAO#getAllRelationshipTypes(boolean)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<RelationshipType> getAllRelationshipTypes(boolean includeRetired) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(RelationshipType.class);
		criteria.addOrder(Order.asc("weight"));

		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", false));
		}

		return criteria.list();
	}
}
