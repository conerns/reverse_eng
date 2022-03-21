/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.api;

import java.util.List;
import java.util.Set;

import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.db.PersonDAO;
import org.openmrs.person.PersonMergeLog;
import org.openmrs.serialization.SerializationException;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsConstants.PERSON_TYPE;
import org.openmrs.util.PrivilegeConstants;

/**
 * Contains methods pertaining to Persons in the system Use:<br>
 * 
 * <pre>
 * 
 * 
 * 
 * 
 * List&lt;Person&gt; personObjects = Context.getPersonService().getAllPersons();
 * </pre>
 * 
 * @see org.openmrs.api.context.Context
 * @see org.openmrs.Patient
 */
public interface PersonService extends OpenmrsService {
	
	/**
	 * These enumerations are used when determining which person attr types to display. If listing
	 * off a lot of patients/users, one set of types are shown. When only displaying one
	 * patient/user, another type is shown.
	 */
	public static enum ATTR_VIEW_TYPE {
		/**
		 * Attributes to be shown when listing off multiple patients or users
		 */
		LISTING,
		
		/**
		 * Attributes to be shown when only showing one patient or user
		 */
		VIEWING,
		
		/**
		 * Attributes to be shown in the header
		 */
		HEADER,
		
	}
	
	/**
	 * Sets the DAO for this service. This is done through spring injection
	 * 
	 * @param dao DAO for this service
	 */
	public void setPersonDAO(PersonDAO dao);
	
	/**
	 * Find a similar person given the attributes. This does a very loose lookup with the
	 * <code>nameSearch</code> parameter. This does a very loose lookup on <code>birthyear</code> as
	 * well. Any person with a null/missing birthdate is included and anyone with a birthyear
	 * plus/minus one year from the given <code>birthyear</code> is also included
	 * 
	 * @param nameSearch string to search the person's name for
	 * @param birthyear the year of birth to restrict
	 * @param gender The gender field to search on (Typically just "M" or "F")
	 * @return Set&lt;Person&gt; object with all people matching criteria
	 * @throws APIException
	 * <strong>Should</strong> accept greater than three names
	 * <strong>Should</strong> match single search to any name part
	 * <strong>Should</strong> match two word search to any name part
	 * <strong>Should</strong> match three word search to any name part
	 * <strong>Should</strong> match search to familyName2
	 */
	// TODO: make gender a (definable?) constant
	@Authorized( { PrivilegeConstants.GET_PERSONS })
	public Set<Person> getSimilarPeople(String nameSearch, Integer birthyear, String gender) throws APIException;
		
	/**
	 * Find a person matching the <tt>searchPhrase</tt> search string
	 * 
	 * @param searchPhrase person name to match on
	 * @param dead if true will return only dead patients, if false will return only alive patients,
	 *            if null will return both
	 * @return list of person objects matches the parameters
	 * <strong>Should</strong> match search to familyName2
	 */
	@Authorized( { PrivilegeConstants.GET_PERSONS })
	public List<Person> getPeople(String searchPhrase, Boolean dead) throws APIException;
	
	@Authorized( { PrivilegeConstants.GET_PERSONS })
	public List<Person> getPeople(String searchPhrase, Boolean dead, Boolean voided) throws APIException;
		
	/**
	 * Save the given person attribute type in the database. <br>
	 * If the given type's Id is not empty, then also need to change any global property which is in
	 * {@link OpenmrsConstants#GLOBAL_PROPERTIES_OF_PERSON_ATTRIBUTES} and reference this given
	 * type, prior to saving this given type. <br>
	 * 
	 * @param type
	 * @return the saved person attribute type
	 * @throws APIException
	 * <strong>Should</strong> set the date created and creator on new
	 * <strong>Should</strong> set the date changed and changed by on update
	 * <strong>Should</strong> update any global property which reference this type
	 * <strong>Should</strong> throw an error when trying to save person attribute type while person attribute types are locked
	 */
	@Authorized( { PrivilegeConstants.MANAGE_PERSON_ATTRIBUTE_TYPES })
	public PersonAttributeType savePersonAttributeType(PersonAttributeType type) throws APIException;
	
	/**
	 * Retire a Person Attribute Type
	 * 
	 * @param type
	 * @param retiredReason
	 * <strong>Should</strong> throw an error when trying to retire person attribute type while person attribute types are locked
	 */
	@Authorized( { PrivilegeConstants.MANAGE_PERSON_ATTRIBUTE_TYPES })
	public PersonAttributeType retirePersonAttributeType(PersonAttributeType type, String retiredReason) throws APIException;
	
	
	/**
	 * Purges a PersonAttribute type from the database (cannot be undone)
	 * 
	 * @param type type to be purged from the database
	 * @throws APIException
	 * <strong>Should</strong> delete person attribute type from database
	 * <strong>Should</strong> throw an error when trying to delete person attribute type while person attribute types are locked
	 */
	@Authorized( { PrivilegeConstants.PURGE_PERSON_ATTRIBUTE_TYPES })
	public void purgePersonAttributeType(PersonAttributeType type) throws APIException;
	
	/**
	 * Unretires a PersonAttribute type from the database (can be undone)
	 * 
	 * @param type type to be restored from the database
	 * @throws APIException
	 * <strong>Should</strong> restore person attribute type from database
	 * <strong>Should</strong> throw an error when trying to unretire person attribute type while person attribute types are locked
	 */
	
	@Authorized( { PrivilegeConstants.MANAGE_PERSON_ATTRIBUTE_TYPES })
	public void unretirePersonAttributeType(PersonAttributeType type) throws APIException;
	
	/**
	 * Effectively removes this person from the system. Voids Patient and retires Users as well.
	 * 
	 * @param person person to be voided
	 * @param reason reason for voiding person
	 * @return the person that was voided
	 * <strong>Should</strong> return voided person with given reason
	 * <strong>Should</strong> void patient
	 * <strong>Should</strong> retire users
	 */
	@Authorized( { PrivilegeConstants.EDIT_PERSONS })
	public Person voidPerson(Person person, String reason) throws APIException;
	
	/**
	 * Effectively resurrects this person in the db. Unvoids Patient as well.
	 * 
	 * @param person person to be revived
	 * @return the person that was unvoided
	 * <strong>Should</strong> unvoid the given person
	 * <strong>Should</strong> unvoid patient
	 * <strong>Should</strong> not unretire users
	 */
	@Authorized( { PrivilegeConstants.EDIT_PERSONS })
	public Person unvoidPerson(Person person) throws APIException;
		
	/**
	 * Get all PersonAttributeTypes in the database
	 * 
	 * @see #getAllPersonAttributeTypes(boolean)
	 * @return All person attribute types including the retired ones
	 * <strong>Should</strong> return all person attribute types including retired
	 */
	@Authorized( { PrivilegeConstants.GET_PERSON_ATTRIBUTE_TYPES })
	public List<PersonAttributeType> getAllPersonAttributeTypes() throws APIException;
	
	/**
	 * Get all PersonAttributeTypes in the database with the option of including the retired types
	 * 
	 * @param includeRetired boolean - include retired attribute types as well?
	 * @return List&lt;PersonAttributeType&gt; object of all PersonAttributeTypes, possibly including
	 *         retired ones
	 * <strong>Should</strong> return all person attribute types including retired when include retired is true
	 * <strong>Should</strong> return all person attribute types excluding retired when include retired is false
	 */
	@Authorized( { PrivilegeConstants.GET_PERSON_ATTRIBUTE_TYPES })
	public List<PersonAttributeType> getAllPersonAttributeTypes(boolean includeRetired) throws APIException;
	
	/**
	 * Find person attribute types matching the given parameters. Retired types are included in the
	 * results
	 * 
	 * @param exactName (optional) The name of type
	 * @param format (optional) The format for this type
	 * @param foreignKey (optional) The foreign key
	 * @param searchable (optional) if true only returns searchable types, if false returns only
	 *            nonsearchable and if null returns all
	 * @return list of PersonAttributeTypes matching the given parameters
	 * @throws APIException
	 * <strong>Should</strong> return person attribute types matching given parameters
	 * <strong>Should</strong> return empty list when no person attribute types match given parameters
	 */
	@Authorized( { PrivilegeConstants.GET_PERSON_ATTRIBUTE_TYPES })
	public List<PersonAttributeType> getPersonAttributeTypes(String exactName, String format, Integer foreignKey,
	        Boolean searchable) throws APIException;
	
	/**
	 * Get the PersonAttributeType given the type's PersonAttributeTypeId
	 * 
	 * @param typeId PersonAttributeType.personAttributeTypeId to match on
	 * @return the type matching this id or null if none was found
	 * <strong>Should</strong> return null when no person attribute with the given id exist
	 */
	@Authorized( { PrivilegeConstants.GET_PERSON_ATTRIBUTE_TYPES })
	public PersonAttributeType getPersonAttributeType(Integer typeId) throws APIException;
	
	/**
	 * Gets a person attribute type with the given uuid.
	 * 
	 * @param uuid the universally unique identifier to lookup
	 * @return a person attribute type with the given uuid
	 * <strong>Should</strong> find object given valid uuid
	 * <strong>Should</strong> return null if no object found with given uuid
	 */
	@Authorized( { PrivilegeConstants.GET_PERSON_ATTRIBUTE_TYPES })
	public PersonAttributeType getPersonAttributeTypeByUuid(String uuid);
	
	/**
	 * Get a PersonAttribute from the database with the given PersonAttributeid
	 * 
	 * @param id the PersonAttribute.personAttributeId to match on
	 * @return the matching PersonAttribute or null if none was found
	 * @throws APIException
	 * <strong>Should</strong> return null when PersonAttribute with given id does not exist
	 * <strong>Should</strong> return person attribute when PersonAttribute with given id does exist
	 */
	@Authorized( { PrivilegeConstants.GET_PERSON_ATTRIBUTE_TYPES })
	public PersonAttribute getPersonAttribute(Integer id) throws APIException;
	
	/**
	 * Get the PersonAttributeType given the type's name
	 * 
	 * @param typeName
	 * @return the PersonAttributeType that has the given name or null if none found
	 * <strong>Should</strong> return person attribute type when name matches given typeName
	 * <strong>Should</strong> return null when no person attribute type match given typeName
	 */
	@Authorized( { PrivilegeConstants.GET_PERSON_ATTRIBUTE_TYPES })
	public PersonAttributeType getPersonAttributeTypeByName(String typeName) throws APIException;
	
	
	/**
	 * Creates or updates a Person in the database
	 * 
	 * @param person person to be created or updated
	 * @return person who was created or updated
	 * @throws APIException
	 * <strong>Should</strong> create new object when person id is null
	 * <strong>Should</strong> update existing object when person id is not null
	 * <strong>Should</strong> set the preferred name and address if none is specified
	 * <strong>Should</strong> not set the preferred name and address if they already exist
	 * <strong>Should</strong> not set a voided name or address as preferred
	 */
	@Authorized( { PrivilegeConstants.ADD_PERSONS, PrivilegeConstants.EDIT_PERSONS })
	public Person savePerson(Person person) throws APIException;
	
	/**
	 * Purges a person from the database (cannot be undone)
	 * 
	 * @param person person to be purged from the database
	 * @throws APIException
	 * <strong>Should</strong> delete person from the database
	 */
	@Authorized( { PrivilegeConstants.PURGE_PERSONS })
	public void purgePerson(Person person) throws APIException;
	
	/**
	 * Get Person by its UUID
	 * 
	 * @param uuid
	 * @return person or null
	 * <strong>Should</strong> find object given valid uuid
	 * <strong>Should</strong> return null if no object found with given uuid
	 */
	@Authorized( { PrivilegeConstants.GET_PERSONS })
	public Person getPersonByUuid(String uuid) throws APIException;
	
	/**
	 * Get PersonAddress by its UUID
	 * 
	 * @param uuid
	 * @return person address or null
	 * <strong>Should</strong> find object given valid uuid
	 * <strong>Should</strong> return null if no object found with given uuid
	 */
	@Authorized( { PrivilegeConstants.GET_PERSONS })
	public PersonAddress getPersonAddressByUuid(String uuid) throws APIException;
	
	/**
	 * Get PersonAttribute by its UUID
	 * 
	 * @param uuid
	 * @return person attribute or null
	 * <strong>Should</strong> find object given valid uuid
	 * <strong>Should</strong> return null if no object found with given uuid
	 */
	@Authorized( { PrivilegeConstants.GET_PERSONS })
	public PersonAttribute getPersonAttributeByUuid(String uuid) throws APIException;
	
	/**
	 * Get PersonName by its personNameId
	 * 
	 * @param personNameId
	 * @return person name or null
	 * <strong>Should</strong> find PersonName given valid personNameId
	 * <strong>Should</strong> return null if no object found with given personNameId
	 */
	@Authorized( { PrivilegeConstants.GET_PERSONS })
	PersonName getPersonName(Integer personNameId);
	
	/**
	 * Get PersonName by its UUID
	 * 
	 * @param uuid
	 * @return person name or null
	 * <strong>Should</strong> find object given valid uuid
	 * <strong>Should</strong> return null if no object found with given uuid
	 */
	@Authorized( { PrivilegeConstants.GET_PERSONS })
	public PersonName getPersonNameByUuid(String uuid) throws APIException;
	
	/**
	 * Gets a person by internal id
	 * 
	 * @param personId internal identifier of person to get
	 * @return Person person with given internal identifier
	 * @throws APIException
	 * <strong>Should</strong> return null when no person has the given id
	 */
	@Authorized( { PrivilegeConstants.GET_PERSONS })
	public Person getPerson(Integer personId) throws APIException;
	
	// this has anonymous access because its cached into generic js files
	public List<PersonAttributeType> getPersonAttributeTypes(PERSON_TYPE personType, ATTR_VIEW_TYPE viewType)
	        throws APIException;
	
	/**
	 * Voids the given PersonName, effectively deleting the name, from the end-user's point of view.
	 * 
	 * @param personName PersonName to void
	 * @param voidReason String reason the personName is being voided.
	 * @return the newly saved personName
	 * @throws APIException
	 * <strong>Should</strong> void personName with the given reason
	 */
	@Authorized( { PrivilegeConstants.EDIT_PERSONS })
	public PersonName voidPersonName(PersonName personName, String voidReason);
	
	/**
	 * Unvoid PersonName in the database, effectively marking this as a valid personName again
	 * 
	 * @param personName PersonName to unvoid
	 * @return the newly unvoided personName
	 * @throws APIException
	 * <strong>Should</strong> unvoid voided personName
	 */
	@Authorized( { PrivilegeConstants.EDIT_PERSONS })
	public PersonName unvoidPersonName(PersonName personName) throws APIException;
	
	/**
	 * Inserts or updates the given personName object in the database
	 * 
	 * @param personName to be created or updated
	 * @return personName that was created or updated
	 * @throws APIException
	 * <strong>Should</strong> fail if you try to void the last non voided name
	 */
	@Authorized( { PrivilegeConstants.EDIT_PERSONS })
	public PersonName savePersonName(PersonName personName);
	
	/**
	 * Parses a name into a PersonName (separate Given, Middle, and Family names)
	 * 
	 * @param name person name to be parsed
	 * @return parsed person name
	 * <strong>Should</strong> parse two person name with comma
	 * <strong>Should</strong> parse two person name without comma
	 * <strong>Should</strong> not fail when ending with whitespace
	 * <strong>Should</strong> not fail when ending with a comma
	 * <strong>Should</strong> parse four person name
	 */
	public PersonName parsePersonName(String name) throws APIException;
	
	/**
	 * Builds the serialized data from
	 * {@link org.openmrs.person.PersonMergeLog#getPersonMergeLogData}, sets the mergedData String,
	 * and the creator and date if null. It then saves the <code>PersonMergeLog</code> object to the
	 * model.
	 * 
	 * @param personMergeLog the <code>PersonMergeLog</code> object to save.
	 * @return the persisted <code>PersonMergeLog</code> object
	 * @see org.openmrs.person.PersonMergeLog
	 * @see org.openmrs.api.handler.OpenmrsObjectSaveHandler
	 * <strong>Should</strong> require PersonMergeLogData
	 * <strong>Should</strong> require winner
	 * <strong>Should</strong> require loser
	 * <strong>Should</strong> set date created if null
	 * <strong>Should</strong> set creator if null
	 * <strong>Should</strong> serialize PersonMergeLogData
	 * <strong>Should</strong> save PersonMergeLog
	 */
	public PersonMergeLog savePersonMergeLog(PersonMergeLog personMergeLog) throws SerializationException, APIException;
	
	/**
	 * Gets a PersonMergeLog object from the model using the UUID identifier. Deserializes the
	 * 
	 * @param uuid
	 * @param deserialize
	 * @return person merge log object
	 * @throws SerializationException
	 * @throws APIException
	 * <strong>Should</strong> require uuid
	 * <strong>Should</strong> retrieve personMergeLog without deserializing data
	 * <strong>Should</strong> retrieve personMergeLog and deserialize data
	 */
	public PersonMergeLog getPersonMergeLogByUuid(String uuid, boolean deserialize) throws SerializationException,
	        APIException;
	
	/**
	 * Gets all the <code>PersonMergeLog</code> objects from the model
	 * 
	 * @return list of PersonMergeLog objects
	 * @throws SerializationException
	 * <strong>Should</strong> retrieve all PersonMergeLogs from the model
	 * <strong>Should</strong> retrieve all PersonMergeLogs and deserialize them
	 */
	public List<PersonMergeLog> getAllPersonMergeLogs(boolean deserialize) throws SerializationException;
	
	/**
	 * Gets <code>PersonMergeLog</code> objects by winning person p. Useful for to getting all persons merged into p.
	 * @param person the winning person
	 * @return List of <code>PersonMergeLog</code> objects
	 * @throws SerializationException
	 * <strong>Should</strong> retrieve PersonMergeLogs by winner
	 */
	public List<PersonMergeLog> getWinningPersonMergeLogs(Person person, boolean deserialize) throws SerializationException;
	
	/**
	 * Gets the <code>PersonMergeLog</code> where person p is the loser. Useful for getting the person that p was merged into.
	 * @param person the losing person
	 * @return The <code>PersonMergeLog</code> object
	 * @throws SerializationException
	 * <strong>Should</strong> find PersonMergeLog by loser
	 */
	public PersonMergeLog getLosingPersonMergeLog(Person person, boolean deserialize) throws SerializationException;
	
	/**
	 * Voids the given PersonAddress, effectively deleting the personAddress, from the end-user's
	 * point of view.
	 * 
	 * @param personAddress PersonAddress to void
	 * @param voidReason String reason the personAddress is being voided.
	 * @return the newly saved personAddress
	 * @throws APIException
	 * <strong>Should</strong> void personAddress with the given reason
	 */
	@Authorized( { PrivilegeConstants.EDIT_PERSONS })
	public PersonAddress voidPersonAddress(PersonAddress personAddress, String voidReason);
	
	/**
	 * Unvoid PersonAddress in the database, effectively marking this as a valid PersonAddress again
	 * 
	 * @param personAddress PersonAddress to unvoid
	 * @return the newly unvoided personAddress
	 * @throws APIException
	 * <strong>Should</strong> unvoid voided personAddress
	 */
	@Authorized( { PrivilegeConstants.EDIT_PERSONS })
	public PersonAddress unvoidPersonAddress(PersonAddress personAddress) throws APIException;
	
	/**
	 * Inserts or updates the given personAddress object in the database
	 * 
	 * @param personAddress PersonAddress to be created or updated
	 * @return personAddress that was created or updated
	 * @since 1.9
	 */
	@Authorized( { PrivilegeConstants.EDIT_PERSONS })
	public PersonAddress savePersonAddress(PersonAddress personAddress);
	
	/**
	 * Check if the person attribute types are locked, and if they are throws an exception during manipulation of a person attribute type
	 * 
	 * @throws PersonAttributeTypeLockedException
	 */
	public void checkIfPersonAttributeTypesAreLocked() throws PersonAttributeTypeLockedException;
}
