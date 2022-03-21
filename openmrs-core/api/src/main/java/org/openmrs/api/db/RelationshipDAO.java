package org.openmrs.api.db;

import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;

import java.util.Date;
import java.util.List;

public interface RelationshipDAO {


	/**
	 * @see org.openmrs.api.PersonService#getRelationship(java.lang.Integer)
	 */
	public Relationship getRelationship(Integer relationshipId) throws DAOException;

	/**
	 * @see org.openmrs.api.PersonService#getAllRelationships(boolean)
	 */
	public List<Relationship> getAllRelationships(boolean includeVoided) throws DAOException;

	/**
	 * @see org.openmrs.api.PersonService#getRelationshipType(java.lang.Integer)
	 */
	public RelationshipType getRelationshipType(Integer relationshipTypeId) throws DAOException;

	/**
	 * @see org.openmrs.api.PersonService#getRelationshipTypes(java.lang.String, java.lang.Boolean)
	 */
	public List<RelationshipType> getRelationshipTypes(String relationshipTypeName, Boolean preferred) throws DAOException;


	/**
	 * @see org.openmrs.api.PersonService#saveRelationship(org.openmrs.Relationship)
	 */
	public Relationship saveRelationship(Relationship relationship) throws DAOException;

	/**
	 * @see org.openmrs.api.PersonService#purgeRelationship(org.openmrs.Relationship)
	 */
	public void deleteRelationship(Relationship relationship) throws DAOException;

	/**
	 * @see org.openmrs.api.PersonService#getRelationships(org.openmrs.Person, org.openmrs.Person,
	 *      org.openmrs.RelationshipType)
	 */
	public List<Relationship> getRelationships(Person fromPerson, Person toPerson, RelationshipType relType)
		throws DAOException;

	/**
	 * @see org.openmrs.api.PersonService#getRelationships(org.openmrs.Person, org.openmrs.Person,
	 *      org.openmrs.RelationshipType, java.util.Date, java.util.Date)
	 */
	public List<Relationship> getRelationships(Person fromPerson, Person toPerson, RelationshipType relType,
											   Date startEffectiveDate, Date endEffectiveDate) throws DAOException;

	/**
	 * @see org.openmrs.api.PersonService#saveRelationshipType(org.openmrs.RelationshipType)
	 */
	public RelationshipType saveRelationshipType(RelationshipType relationshipType) throws DAOException;

	/**
	 * @see org.openmrs.api.PersonService#deleteRelationshipType(org.openmrs.RelationshipType)
	 */
	public void deleteRelationshipType(RelationshipType relationshipType) throws DAOException;


	/**
	 * @see org.openmrs.api.PersonService#getAllRelationshipTypes(boolean)
	 */
	public List<RelationshipType> getAllRelationshipTypes(boolean includeRetired);


	/**
	 * @param uuid
	 * @return relationship or null
	 */
	public Relationship getRelationshipByUuid(String uuid);

	/**
	 * @param uuid
	 * @return relationship type or null
	 */
	public RelationshipType getRelationshipTypeByUuid(String uuid);
}
