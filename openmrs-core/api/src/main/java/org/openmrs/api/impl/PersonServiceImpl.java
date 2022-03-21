/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.api.impl;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.GlobalProperty;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PersonAttributeTypeLockedException;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.PersonDAO;
import org.openmrs.person.PersonMergeLog;
import org.openmrs.person.PersonMergeLogData;
import org.openmrs.serialization.SerializationException;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsConstants.PERSON_TYPE;
import org.openmrs.validator.ValidateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Default implementation of the PersonService
 * <p>
 * Which implementation to use is determined by Spring. See the spring application context file in
 * /metadata/api/spring/applicatContext-service.xml
 *
 * @see PersonService
 * @see org.openmrs.api.context.Context
 */
@Transactional
public class PersonServiceImpl extends BaseOpenmrsService implements PersonService {
	
	private static final Logger log = LoggerFactory.getLogger(PersonServiceImpl.class);
	
	private PersonDAO dao;
	
	/**
	 * @see org.openmrs.api.PersonService#setPersonDAO(org.openmrs.api.db.PersonDAO)
	 */
	@Override
	public void setPersonDAO(PersonDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @see org.openmrs.api.PersonService#getSimilarPeople(java.lang.String, java.lang.Integer,
	 *      java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public Set<Person> getSimilarPeople(String name, Integer birthyear, String gender) throws APIException {
		return dao.getSimilarPeople(name, birthyear, gender);
	}
	
	/**
	 * @see org.openmrs.api.PersonService#getPeople(String, Boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Person> getPeople(String searchPhrase, Boolean dead) throws APIException {
		
		return dao.getPeople(searchPhrase, dead);
	}
	
	@Override
	public List<Person> getPeople(String searchPhrase, Boolean dead, Boolean voided) throws APIException {
		
		return dao.getPeople(searchPhrase, dead, voided);
	}
	
	/**
	 * @see org.openmrs.api.PersonService#getAllPersonAttributeTypes()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<PersonAttributeType> getAllPersonAttributeTypes() throws APIException {
		return Context.getPersonService().getAllPersonAttributeTypes(true);
	}
	
	/**
	 * @see org.openmrs.api.PersonService#getAllPersonAttributeTypes(boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<PersonAttributeType> getAllPersonAttributeTypes(boolean includeRetired) throws APIException {
		return dao.getAllPersonAttributeTypes(includeRetired);
	}
	
	/**
	 * @see org.openmrs.api.PersonService#getPersonAttributeTypeByName(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public PersonAttributeType getPersonAttributeTypeByName(String typeName) throws APIException {
		List<PersonAttributeType> types = Context.getPersonService().getPersonAttributeTypes(typeName, null, null, null);
		
		if (types.isEmpty()) {
			return null;
		} else {
			return types.get(0);
		}
	}
	
	/**
	 * @see org.openmrs.api.PersonService#purgePersonAttributeType(org.openmrs.PersonAttributeType)
	 */
	@Override
	public void purgePersonAttributeType(PersonAttributeType type) throws APIException {
		checkIfPersonAttributeTypesAreLocked();
		dao.deletePersonAttributeType(type);
	}
	
	/**
	 * @see org.openmrs.api.PersonService#savePersonAttributeType(org.openmrs.PersonAttributeType)
	 */
	@Override
	public PersonAttributeType savePersonAttributeType(PersonAttributeType type) throws APIException {
		checkIfPersonAttributeTypesAreLocked();
		
		if (type.getSortWeight() == null) {
			List<PersonAttributeType> allTypes = Context.getPersonService().getAllPersonAttributeTypes();
			if (!allTypes.isEmpty()) {
				type.setSortWeight(allTypes.get(allTypes.size() - 1).getSortWeight() + 1);
			} else {
				type.setSortWeight(1.0);
			}
		}
		
		boolean updateExisting = false;
		
		if (type.getId() != null) {
			updateExisting = true;
			
			String oldTypeName = dao.getSavedPersonAttributeTypeName(type);
			String newTypeName = type.getName();
			
			if (!oldTypeName.equals(newTypeName)) {
				List<GlobalProperty> props = new ArrayList<>();
				
				AdministrationService as = Context.getAdministrationService();
				
				for (String propName : OpenmrsConstants.GLOBAL_PROPERTIES_OF_PERSON_ATTRIBUTES) {
					props.add(as.getGlobalPropertyObject(propName));
				}
				
				for (GlobalProperty prop : props) {
					if (prop != null) {
						String propVal = prop.getPropertyValue();
						if (propVal != null && propVal.contains(oldTypeName)) {
							prop.setPropertyValue(propVal.replaceFirst(oldTypeName, newTypeName));
							as.saveGlobalProperty(prop);
						}
					}
				}
			}
		}
		
		PersonAttributeType attributeType = dao.savePersonAttributeType(type);
		
		if (updateExisting ) {
			Boolean oldSearchable = dao.getSavedPersonAttributeTypeSearchable(type);
			if (oldSearchable == null || !oldSearchable.equals(type.getSearchable())) {
				//we need to update index searchable property has changed
				Context.updateSearchIndexForType(PersonAttribute.class);
			}
		}
		
		return attributeType;
	}
	
	/**
	 * @see org.openmrs.api.PersonService#retirePersonAttributeType(PersonAttributeType, String)
	 */
	@Override
	public PersonAttributeType retirePersonAttributeType(PersonAttributeType type, String retiredReason)
	        throws APIException {
		checkIfPersonAttributeTypesAreLocked();
		if (retiredReason == null || retiredReason.length() < 1) {
			throw new APIException("Person.retiring.reason.required", (Object[]) null);
		}
		
		type.setRetired(true);
		type.setRetiredBy(Context.getAuthenticatedUser());
		type.setRetireReason(retiredReason);
		type.setDateRetired(new Date());
		
		return dao.savePersonAttributeType(type);
	}
	
	/**
	 * @see org.openmrs.api.PersonService#getPersonAttributeTypes(java.lang.String,
	 *      java.lang.String, java.lang.Integer, java.lang.Boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<PersonAttributeType> getPersonAttributeTypes(String exactName, String format, Integer foreignKey,
	        Boolean searchable) throws APIException {
		return dao.getPersonAttributeTypes(exactName, format, foreignKey, searchable);
	}
	
	@Override
	public void unretirePersonAttributeType(PersonAttributeType type) throws APIException {
		checkIfPersonAttributeTypesAreLocked();
		type.setRetired(false);
		type.setDateRetired(null);
		type.setRetiredBy(null);
		type.setRetireReason(null);
		Context.getPersonService().savePersonAttributeType(type);

	}
	
	/**
	 * @see org.openmrs.api.PersonService#getPersonAttributeType(java.lang.Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public PersonAttributeType getPersonAttributeType(Integer typeId) {
		return dao.getPersonAttributeType(typeId);
	}
	
	/**
	 * @see org.openmrs.api.PersonService#getPersonAttribute(java.lang.Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public PersonAttribute getPersonAttribute(Integer id) {
		return dao.getPersonAttribute(id);
	}
	
	
	
	/**
	 * @see org.openmrs.api.PersonService#purgePerson(org.openmrs.Person)
	 */
	@Override
	public void purgePerson(Person person) throws APIException {
		dao.deletePerson(person);
	}
	
	/**
	 * @see org.openmrs.api.PersonService#savePerson(org.openmrs.Person)
	 */
	@Override
	public Person savePerson(Person person) throws APIException {
		setPreferredPersonName(person);
		setPreferredPersonAddress(person);
		return dao.savePerson(person);
	}

	private void setPreferredPersonName(Person person) {
		PersonName preferredName = null;
		PersonName possiblePreferredName = person.getPersonName();
		if (possiblePreferredName != null && possiblePreferredName.getPreferred() && !possiblePreferredName.getVoided()) {
			preferredName = possiblePreferredName;
		}

		for (PersonName name : person.getNames()) {
			if (preferredName == null && !name.getVoided()) {
				name.setPreferred(true);
				preferredName = name;
				continue;
			}

			if (!name.equals(preferredName)) {
				name.setPreferred(false);
			}
		}
	}
	
	private void setPreferredPersonAddress(Person person) {
		PersonAddress preferredAddress = null;
		PersonAddress possiblePreferredAddress = person.getPersonAddress();
		if (possiblePreferredAddress != null && possiblePreferredAddress.getPreferred()
				&& !possiblePreferredAddress.getVoided()) {
			preferredAddress = possiblePreferredAddress;
		}

		for (PersonAddress address : person.getAddresses()) {
			if (preferredAddress == null && !address.getVoided()) {
				address.setPreferred(true);
				preferredAddress = address;
				continue;
			}

			if (!address.equals(preferredAddress)) {
				address.setPreferred(false);
			}
		}
	}
	
	/**
	 * @see org.openmrs.api.PersonService#voidPerson(org.openmrs.Person, java.lang.String)
	 */
	@Override
	public Person voidPerson(Person person, String reason) throws APIException {
		if (person == null) {
			return null;
		}
		
		return dao.savePerson(person);
	}
	
	/**
	 * @see org.openmrs.api.PersonService#unvoidPerson(org.openmrs.Person)
	 */
	@Override
	public Person unvoidPerson(Person person) throws APIException {
		if (person == null) {
			return null;
		}
		
		return Context.getPersonService().savePerson(person);
	}
	
	/**
	 * @see org.openmrs.api.PersonService#getPerson(java.lang.Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public Person getPerson(Integer personId) throws APIException {
		if (personId == null) {
			return null;
		}
		return dao.getPerson(personId);
	}
	
	
	/**
	 * @see org.openmrs.api.PersonService#getPersonAttributeTypes(org.openmrs.util.OpenmrsConstants.PERSON_TYPE,
	 *      org.openmrs.api.PersonService.ATTR_VIEW_TYPE)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<PersonAttributeType> getPersonAttributeTypes(PERSON_TYPE personType, ATTR_VIEW_TYPE viewType)
	        throws APIException {
		
		if (viewType == null) {
			return Context.getPersonService().getAllPersonAttributeTypes();
		}
		
		List<String> attrNames = getAttributeTypesFromGlobalProperties(viewType, personType);
		List<PersonAttributeType> result = new ArrayList<>();
		for (String nameOrId : attrNames) {
			if (nameOrId.matches("\\d")) {
				result.add(getPersonAttributeType(Integer.valueOf(nameOrId)));
			} else {
				result.add(getPersonAttributeTypeByName(nameOrId));
			}
		}
		return result;
	}

	private List<String> getAttributeTypesFromGlobalProperties(ATTR_VIEW_TYPE viewType, PERSON_TYPE personType) {
		List<String> result = new ArrayList<>();
		
		if (viewType == ATTR_VIEW_TYPE.LISTING) {
			result = combineAttributes(OpenmrsConstants.GLOBAL_PROPERTY_PATIENT_LISTING_ATTRIBUTES, OpenmrsConstants.GLOBAL_PROPERTY_USER_LISTING_ATTRIBUTES, personType);
		} else if (viewType == ATTR_VIEW_TYPE.VIEWING) {
			result = combineAttributes(OpenmrsConstants.GLOBAL_PROPERTY_PATIENT_VIEWING_ATTRIBUTES, OpenmrsConstants.GLOBAL_PROPERTY_USER_VIEWING_ATTRIBUTES, personType);
		} else if (viewType == ATTR_VIEW_TYPE.HEADER) {
			result = combineAttributes(OpenmrsConstants.GLOBAL_PROPERTY_PATIENT_HEADER_ATTRIBUTES, OpenmrsConstants.GLOBAL_PROPERTY_USER_HEADER_ATTRIBUTES, personType);
		} else {
			log.error(MarkerFactory.getMarker("FATAL"), "Should not be here.");
		}
		
		List<String> attrTypes = new ArrayList<>();
		for (String res : result) {
			for (String attrType : res.split(",")) {
				if (attrType != null) {
					attrType = attrType.trim();
					if (!attrType.isEmpty()) {
						attrTypes.add(attrType);
					}
				}
			}
		}
		
		return attrTypes;
	}

	private List<String> combineAttributes(String patientAttributeProperty, String userAttributeProperty, PERSON_TYPE personType) {
		List<String> result = new ArrayList<>();
		
		if (personType == null || personType == PERSON_TYPE.PERSON) {
			result = getGlobalProperties(patientAttributeProperty, userAttributeProperty);
		} else if (personType == PERSON_TYPE.PATIENT) {
			result = getGlobalProperties(patientAttributeProperty);
		} else if (personType == PERSON_TYPE.USER) {
			result = getGlobalProperties(userAttributeProperty);
		} else {
			log.error(MarkerFactory.getMarker("FATAL"), "Should not be here.");
		}
		return result;
	}
	
	private List<String> getGlobalProperties(String... properties) {
		List<String> result = new ArrayList<>();
		AdministrationService as = Context.getAdministrationService();
		for (String p : properties) {
			String id = as.getGlobalProperty(p, "");
			if (StringUtils.isNotBlank(id)) {
				result.add(id.trim());
			}
		}
		return result;
	}

	/**
	 * @see org.openmrs.api.PersonService#parsePersonName(java.lang.String)
	 */
	@Override
	public PersonName parsePersonName(String name) throws APIException {
		// strip beginning/ending whitespace
		name = name.trim();
		
		// trim off all trailing commas
		while (name.endsWith(",")) {
			name = name.substring(0, name.length() - 1);
		}
		
		String firstName = name;
		String middleName = "";
		String lastName = "";
		String lastName2 = null;
		
		if (name.contains(",")) {
			
			String[] names = name.split(",");
			
			// trim whitespace on each part of the name
			for (int x = 0; x < names.length; x++) {
				names[x] = names[x].trim();
			}
			
			String[] firstNames = names[1].split(" ");
			if (firstNames.length == 2) {
				// user entered "Smith, John Adam"
				lastName = names[0];
				firstName = firstNames[0];
				middleName = firstNames[1];
			} else {
				// user entered "Smith, John"
				firstName = names[1];
				lastName = names[0];
			}
		} else if (name.contains(" ")) {
			String[] names = name.split(" ");
			if (names.length == 4) {
				// user entered "John Adam Smith"
				firstName = names[0];
				middleName = names[1];
				lastName = names[2];
				lastName2 = names[3];
			} else if (names.length == 3) {
				// user entered "John Adam Smith"
				firstName = names[0];
				middleName = names[1];
				lastName = names[2];
			} else {
				// user entered "John Smith"
				firstName = names[0];
				lastName = names[1];
			}
		}
		
		PersonName pn = new PersonName(firstName, middleName, lastName);
		pn.setFamilyName2(lastName2);
		
		return pn;
	}
	
	/**
	 * @see org.openmrs.api.PersonService#voidPersonName(org.openmrs.PersonName, String)
	 */
	@Override
	public PersonName voidPersonName(PersonName personName, String voidReason) throws APIException {
		return Context.getPersonService().savePersonName(personName);
	}
	
	/**
	 * @see org.openmrs.api.PersonService#unvoidPersonName(org.openmrs.PersonName)
	 */
	@Override
	public PersonName unvoidPersonName(PersonName personName) throws APIException {
		return Context.getPersonService().savePersonName(personName);
		
	}
	
	/**
	 * @see org.openmrs.api.PersonService#savePersonName(org.openmrs.PersonName)
	 */
	@Override
	public PersonName savePersonName(PersonName personName) throws APIException {
		ValidateUtil.validate(personName.getPerson());
		return dao.savePersonName(personName);
	}
	
	
	/**
	 * @see org.openmrs.api.PersonService#getPersonAttributeTypeByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public PersonAttributeType getPersonAttributeTypeByUuid(String uuid) {
		return dao.getPersonAttributeTypeByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.api.PersonService#getPersonByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public Person getPersonByUuid(String uuid) throws APIException {
		return dao.getPersonByUuid(uuid);
	}
	
	@Override
	@Transactional(readOnly = true)
	public PersonAddress getPersonAddressByUuid(String uuid) throws APIException {
		return dao.getPersonAddressByUuid(uuid);
	}
	
	@Override
	@Transactional(readOnly = true)
	public PersonAttribute getPersonAttributeByUuid(String uuid) throws APIException {
		return dao.getPersonAttributeByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.api.PersonService#getPersonName(java.lang.Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public PersonName getPersonName(Integer personNameId) {
		return dao.getPersonName(personNameId);
	}
	
	/**
	 * @see org.openmrs.api.PersonService#getPersonNameByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public PersonName getPersonNameByUuid(String uuid) throws APIException {
		return dao.getPersonNameByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.api.PersonService#savePersonMergeLog(PersonMergeLog)
	 */
	@Override
	public PersonMergeLog savePersonMergeLog(PersonMergeLog personMergeLog) throws SerializationException, APIException {
		//verify required fields
		if (Context.getSerializationService().getDefaultSerializer() == null) {
			throw new APIException("serializer.default.not.found", (Object[]) null);
		}
		log.debug("Auditing merging of non-preferred person " + personMergeLog.getLoser().getUuid()
		        + " with preferred person " + personMergeLog.getWinner().getId());
		//populate the mergedData XML from the PersonMergeLogData object
		String serialized = Context.getSerializationService().getDefaultSerializer()
		        .serialize(personMergeLog.getPersonMergeLogData());
		personMergeLog.setSerializedMergedData(serialized);
		log.debug(serialized);
		//save the bean to the database
		return dao.savePersonMergeLog(personMergeLog);
	}
	
	/**
	 * @see org.openmrs.api.PersonService#getPersonMergeLogByUuid(String, boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public PersonMergeLog getPersonMergeLogByUuid(String uuid, boolean deserialize)
	        throws SerializationException, APIException {
		if (uuid == null) {
			throw new APIException("uuid.cannot.null", (Object[]) null);
		}
		PersonMergeLog personMergeLog = dao.getPersonMergeLogByUuid(uuid);
		//deserialize if requested
		if (deserialize) {
			deserialize(personMergeLog);
		}
		return personMergeLog;
	}
	
	/**
	 * Deserializes a List of <code>PersonMErgeLog</code> objects
	 *
	 * @param lst the List of <code> PersonMergeLog</code> objects to deserialize
	 * @throws SerializationException
	 */
	private void deserializeList(List<PersonMergeLog> lst) throws SerializationException {
		for (PersonMergeLog personMergeLog : lst) {
			deserialize(personMergeLog);
		}
	}
	
	/**
	 * Deserializes a <code>PersonMErgeLog</code> object
	 *
	 * @param personMergeLog the <code> PersonMergeLog</code> object to deserialize
	 * @throws SerializationException
	 */
	private void deserialize(PersonMergeLog personMergeLog) throws SerializationException {
		PersonMergeLogData data = Context.getSerializationService().getDefaultSerializer()
		        .deserialize(personMergeLog.getSerializedMergedData(), PersonMergeLogData.class);
		personMergeLog.setPersonMergeLogData(data);
	}
	
	/**
	 * @see org.openmrs.api.PersonService#getAllPersonMergeLogs(boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<PersonMergeLog> getAllPersonMergeLogs(boolean deserialize) throws SerializationException {
		List<PersonMergeLog> lst = dao.getAllPersonMergeLogs();
		//deserialize if requested
		if (deserialize) {
			deserializeList(lst);
		}
		return lst;
	}
	
	/**
	 * @see org.openmrs.api.PersonService#getWinningPersonMergeLogs(Person, boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<PersonMergeLog> getWinningPersonMergeLogs(Person person, boolean deserialize) throws SerializationException {
		List<PersonMergeLog> lst = dao.getWinningPersonMergeLogs(person);
		if (deserialize) {
			deserializeList(lst);
		}
		return lst;
	}
	
	/**
	 * @see org.openmrs.api.PersonService#getLosingPersonMergeLog(Person, boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public PersonMergeLog getLosingPersonMergeLog(Person person, boolean deserialize) throws SerializationException {
		PersonMergeLog personMergeLog = dao.getLosingPersonMergeLogs(person);
		if (deserialize) {
			deserialize(personMergeLog);
		}
		return personMergeLog;
	}
	
	
	/**
	 * @see org.openmrs.api.PersonService#voidPersonAddress(org.openmrs.PersonAddress, String)
	 */
	@Override
	public PersonAddress voidPersonAddress(PersonAddress personAddress, String voidReason) {
		return Context.getPersonService().savePersonAddress(personAddress);
	}
	
	/**
	 * @see org.openmrs.api.PersonService#unvoidPersonAddress(org.openmrs.PersonAddress)
	 */
	@Override
	public PersonAddress unvoidPersonAddress(PersonAddress personAddress) throws APIException {
		return Context.getPersonService().savePersonAddress(personAddress);
	}
	
	/**
	 * @see org.openmrs.api.PersonService#savePersonAddress(org.openmrs.PersonAddress)
	 */
	@Override
	public PersonAddress savePersonAddress(PersonAddress personAddress) {
		return dao.savePersonAddress(personAddress);
	}
	
	@Override
	public void checkIfPersonAttributeTypesAreLocked() {
		String locked = Context.getAdministrationService()
		        .getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_PERSON_ATRIBUTE_TYPES_LOCKED, "false");
		if (Boolean.valueOf(locked)) {
			throw new PersonAttributeTypeLockedException();
		}
	}
}
