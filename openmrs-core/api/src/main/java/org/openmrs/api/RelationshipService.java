package org.openmrs.api;

import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.annotation.Authorized;
import org.openmrs.util.PrivilegeConstants;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface RelationshipService extends OpenmrsService{


	/**
	 * Get relationship by internal relationship identifier
	 *
	 * @param relationshipId
	 * @return Relationship the relationship to match on or null if none found
	 * @throws APIException
	 * <strong>Should</strong> return relationship with given id
	 * <strong>Should</strong> return null when relationship with given id does not exist
	 */
	@Authorized( { PrivilegeConstants.GET_RELATIONSHIPS })
	public Relationship getRelationship(Integer relationshipId) throws APIException;

	/**
	 * Get Relationship by its UUID
	 *
	 * @param uuid
	 * @return relationship or null
	 * <strong>Should</strong> find object given valid uuid
	 * <strong>Should</strong> return null if no object found with given uuid
	 */
	@Authorized( { PrivilegeConstants.GET_RELATIONSHIPS })
	public Relationship getRelationshipByUuid(String uuid) throws APIException;

	/**
	 * Get list of relationships that are not voided
	 *
	 * @return non-voided Relationship list
	 * @throws APIException
	 * @return list of all unvoided relationship
	 * <strong>Should</strong> return all unvoided relationships
	 */
	@Authorized( { PrivilegeConstants.GET_RELATIONSHIPS })
	public List<Relationship> getAllRelationships() throws APIException;

	/**
	 * Get list of relationships optionally including the voided ones or not
	 *
	 * @param includeVoided true/false whether to include the voided relationships
	 * @return non-voided Relationship list
	 * @throws APIException
	 * <strong>Should</strong> return all relationship including voided when include voided equals true
	 * <strong>Should</strong> return all relationship excluding voided when include voided equals false
	 */
	@Authorized( { PrivilegeConstants.GET_RELATIONSHIPS })
	public List<Relationship> getAllRelationships(boolean includeVoided) throws APIException;

	/**
	 * Get list of relationships that include Person in person_id or relative_id Does not include
	 * voided relationships
	 *
	 * @param p person object listed on either side of the relationship
	 * @return Relationship list
	 * @throws APIException
	 * <strong>Should</strong> only get unvoided relationships
	 * <strong>Should</strong> fetch relationships associated with the given person
	 * <strong>Should</strong> fetch unvoided relationships only
	 */
	@Authorized( { PrivilegeConstants.GET_RELATIONSHIPS })
	public List<Relationship> getRelationshipsByPerson(Person p) throws APIException;

	/**
	 * Get list of relationships that include Person in person_id or relative_id. Does not include
	 * voided relationships. Accepts an effectiveDate parameter which, if supplied, will limit the
	 * returned relationships to those that were active on the given date. Such active relationships
	 * include those that have a startDate that is null or less than or equal to the effectiveDate,
	 * and that have an endDate that is null or greater than or equal to the effectiveDate.
	 *
	 * @param p person object listed on either side of the relationship
	 * @param effectiveDate effective date of relationship
	 * @return Relationship list
	 * @throws APIException
	 * <strong>Should</strong> only get unvoided relationships
	 * <strong>Should</strong> only get unvoided relationships regardless of effective date
	 * <strong>Should</strong> fetch relationships associated with the given person
	 * <strong>Should</strong> fetch relationships that were active during effectiveDate
	 */
	@Authorized( { PrivilegeConstants.GET_RELATIONSHIPS })
	public List<Relationship> getRelationshipsByPerson(Person p, Date effectiveDate) throws APIException;

	/**
	 * Get relationships stored in the database that
	 *
	 * @param fromPerson (optional) Person to in the person_id column
	 * @param toPerson (optional) Person in the relative_id column
	 * @param relType (optional) The RelationshipType to match
	 * @return relationships matching the given parameters
	 * @throws APIException
	 * <strong>Should</strong> fetch relationships matching the given from person
	 * <strong>Should</strong> fetch relationships matching the given to person
	 * <strong>Should</strong> fetch relationships matching the given rel type
	 * <strong>Should</strong> return empty list when no relationship matching given parameters exist
	 */
	@Authorized( { PrivilegeConstants.GET_RELATIONSHIPS })
	public List<Relationship> getRelationships(Person fromPerson, Person toPerson, RelationshipType relType)
		throws APIException;

	/**
	 * Get relationships stored in the database that are active on the passed date
	 *
	 * @param fromPerson (optional) Person to in the person_id column
	 * @param toPerson (optional) Person in the relative_id column
	 * @param relType (optional) The RelationshipType to match
	 * @param effectiveDate (optional) The date during which the relationship was effective
	 * @return relationships matching the given parameters
	 * @throws APIException
	 * <strong>Should</strong> fetch relationships matching the given from person
	 * <strong>Should</strong> fetch relationships matching the given to person
	 * <strong>Should</strong> fetch relationships matching the given rel type
	 * <strong>Should</strong> return empty list when no relationship matching given parameters exist
	 * <strong>Should</strong> fetch relationships that were active during effectiveDate
	 */
	@Authorized( { PrivilegeConstants.GET_RELATIONSHIPS })
	public List<Relationship> getRelationships(Person fromPerson, Person toPerson, RelationshipType relType,
											   Date effectiveDate) throws APIException;

	/**
	 * Get relationships stored in the database that were active during the specified date range
	 *
	 * @param fromPerson (optional) Person to in the person_id column
	 * @param toPerson (optional) Person in the relative_id column
	 * @param relType (optional) The RelationshipType to match
	 * @param startEffectiveDate (optional) The date during which the relationship was effective
	 *            (lower bound)
	 * @param endEffectiveDate (optional) The date during which the relationship was effective
	 *            (upper bound)
	 * @return relationships matching the given parameters
	 * @throws APIException
	 * <strong>Should</strong> fetch relationships matching the given from person
	 * <strong>Should</strong> fetch relationships matching the given to person
	 * <strong>Should</strong> fetch relationships matching the given rel type
	 * <strong>Should</strong> return empty list when no relationship matching given parameters exist
	 * <strong>Should</strong> fetch relationships that were active during the specified date range
	 */
	@Authorized( { PrivilegeConstants.GET_RELATIONSHIPS })
	public List<Relationship> getRelationships(Person fromPerson, Person toPerson, RelationshipType relType,
											   Date startEffectiveDate, Date endEffectiveDate) throws APIException;

	/**
	 * Get all relationshipTypes Includes retired relationship types
	 *
	 * @return relationshipType list
	 * @throws APIException
	 * <strong>Should</strong> return all relationship types
	 */
	@Authorized( { PrivilegeConstants.GET_RELATIONSHIP_TYPES })
	public List<RelationshipType> getAllRelationshipTypes() throws APIException;

	/**
	 * Get all relationshipTypes with the option of including the retired types
	 *
	 * @param includeRetired boolean - include retired relationshipTypes as well?
	 * @return relationshipType list
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_RELATIONSHIP_TYPES })
	public List<RelationshipType> getAllRelationshipTypes(boolean includeRetired) throws APIException;

	/**
	 * Get relationshipType by internal identifier
	 *
	 * @param relationshipTypeId
	 * @return relationshipType with given internal identifier or null if none found
	 * @throws APIException
	 * <strong>Should</strong> return relationship type with the given relationship type id
	 * <strong>Should</strong> return null when no relationship type matches given relationship type id
	 */
	@Authorized( { PrivilegeConstants.GET_RELATIONSHIP_TYPES })
	public RelationshipType getRelationshipType(Integer relationshipTypeId) throws APIException;

	/**
	 * Gets the relationship type with the given uuid.
	 *
	 * @param uuid
	 * @return relationship type or null
	 * @throws APIException
	 * <strong>Should</strong> find object given valid uuid
	 * <strong>Should</strong> return null if no object found with given uuid
	 */
	@Authorized( { PrivilegeConstants.GET_RELATIONSHIP_TYPES })
	public RelationshipType getRelationshipTypeByUuid(String uuid) throws APIException;

	/**
	 * Find relationshipType by exact name match
	 *
	 * @param relationshipTypeName name to match on
	 * @return RelationshipType with given name or null if none found
	 * @throws APIException
	 * <strong>Should</strong> return null when no relationship type match the given name
	 */
	@Authorized( { PrivilegeConstants.GET_RELATIONSHIP_TYPES })
	public RelationshipType getRelationshipTypeByName(String relationshipTypeName) throws APIException;

	/**
	 * Find relationshipTypes by exact name match and/or preferred status
	 *
	 * @param relationshipTypeName name to match on
	 * @param preferred if true, returns on preferred types, if false returns only the nonpreferred
	 *            types. if null returns both
	 * @return RelationshipTypes with given name and preferred status
	 * @throws APIException
	 * <strong>Should</strong> return list of preferred relationship type matching given name
	 * <strong>Should</strong> return empty list when no preferred relationship type match the given name
	 */
	@Authorized( { PrivilegeConstants.GET_RELATIONSHIP_TYPES })
	public List<RelationshipType> getRelationshipTypes(String relationshipTypeName, Boolean preferred) throws APIException;

	/**
	 * Get relationshipTypes by searching through the names and loosely matching to the given
	 * searchString
	 *
	 * @param searchString string to match to a relationship type name
	 * @return list of relationship types or empty list if none found
	 * @throws APIException
	 * <strong>Should</strong> return empty list when no relationship type match the search string
	 */
	@Authorized( { PrivilegeConstants.GET_RELATIONSHIP_TYPES })
	public List<RelationshipType> getRelationshipTypes(String searchString) throws APIException;

	/**
	 * Create or update a relationship between people. Saves the given <code>relationship</code> to
	 * the database
	 *
	 * @param relationship relationship to be created or updated
	 * @return relationship that was created or updated
	 * @throws APIException
	 * <strong>Should</strong> create new object when relationship id is null
	 * <strong>Should</strong> update existing object when relationship id is not null
	 */
	@Authorized( { PrivilegeConstants.ADD_RELATIONSHIPS, PrivilegeConstants.EDIT_RELATIONSHIPS })
	public Relationship saveRelationship(Relationship relationship) throws APIException;

	/**
	 * Purges a relationship from the database (cannot be undone)
	 *
	 * @param relationship relationship to be purged from the database
	 * @throws APIException
	 * <strong>Should</strong> delete relationship from the database
	 */
	@Authorized( { PrivilegeConstants.PURGE_RELATIONSHIPS })
	public void purgeRelationship(Relationship relationship) throws APIException;

	/**
	 * Voids the given Relationship, effectively removing it from openmrs.
	 *
	 * @param relationship Relationship to void
	 * @param voidReason String reason the relationship is being voided.
	 * @return the newly saved relationship
	 * @throws APIException
	 * <strong>Should</strong> void relationship with the given reason
	 */
	@Authorized( { PrivilegeConstants.DELETE_RELATIONSHIPS })
	public Relationship voidRelationship(Relationship relationship, String voidReason) throws APIException;

	/**
	 * Unvoid Relationship in the database, effectively marking this as a valid relationship again
	 *
	 * @param relationship Relationship to unvoid
	 * @return the newly unvoided relationship
	 * @throws APIException
	 * <strong>Should</strong> unvoid voided relationship
	 */
	@Authorized( { PrivilegeConstants.EDIT_RELATIONSHIPS })
	public Relationship unvoidRelationship(Relationship relationship) throws APIException;


	/**
	 * Inserts or updates the given relationship type object in the database
	 *
	 * @param relationshipType type to be created or updated
	 * @return relationship type that was created or updated
	 * @throws APIException
	 * <strong>Should</strong> create new object when relationship type id is null
	 * <strong>Should</strong> update existing object when relationship type id is not null
	 * <strong>Should</strong> fail if the description is not specified
	 */
	@Authorized( { PrivilegeConstants.MANAGE_RELATIONSHIP_TYPES })
	public RelationshipType saveRelationshipType(RelationshipType relationshipType) throws APIException;

	/**
	 * Purge relationship type from the database (cannot be undone)
	 *
	 * @param relationshipType relationship type to be purged
	 * @throws APIException
	 * <strong>Should</strong> delete relationship type from the database
	 */
	@Authorized( { PrivilegeConstants.PURGE_RELATIONSHIP_TYPES })
	public void purgeRelationshipType(RelationshipType relationshipType) throws APIException;

	/**
	 * Gets the types defined for the given person type (person, user, patient) and the given type
	 * of view (one person vs many person objects)
	 *
	 * @param personType PERSON, USER, PATIENT, or null. Both PERSON and null mean to return attr
	 *            types for both patients and users
	 * @param viewType whether this is a listing or viewing or null for both listing and viewing
	 * @return list of PersonAttributeTypes that should be displayed
	 */


	/**
	 * Retire a Person Relationship Type
	 *
	 * @param type
	 * @param retiredReason
	 */
	@Authorized( { PrivilegeConstants.MANAGE_RELATIONSHIP_TYPES })
	public RelationshipType retireRelationshipType(RelationshipType type, String retiredReason) throws APIException;


	/**
	 * Get all relationships for a given type of relationship mapped from the personA to all of the
	 * personB's
	 *
	 * @param relationshipType type of relationship for which to retrieve all relationships
	 * @return all relationships for the given type of relationship
	 * @throws APIException
	 * <strong>Should</strong> return empty map when no relationship has the matching relationship type
	 */
	@Authorized( { PrivilegeConstants.GET_RELATIONSHIPS })
	public Map<Person, List<Person>> getRelationshipMap(RelationshipType relationshipType) throws APIException;

	/**
	 * Unretire a Person Relationship Type
	 *
	 * @param relationshipType retiredReason
	 * @since 1.9
	 */
	@Authorized( { PrivilegeConstants.MANAGE_RELATIONSHIP_TYPES })
	public RelationshipType unretireRelationshipType(RelationshipType relationshipType);
}
