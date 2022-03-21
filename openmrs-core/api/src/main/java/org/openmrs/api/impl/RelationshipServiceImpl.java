package org.openmrs.api.impl;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.APIException;
import org.openmrs.api.RelationshipService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.RelationshipDAO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

public class RelationshipServiceImpl implements RelationshipService {
	
	private RelationshipDAO dao;
	
	public void setRelationshipDAO(RelationshipDAO dao){
		this.dao = dao;
	}
	
	/**
	 * @see org.openmrs.api.RelationshipService#getRelationship(java.lang.Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public Relationship getRelationship(Integer relationshipId) throws APIException {
		return dao.getRelationship(relationshipId);
	}

	/**
	 * @see org.openmrs.api.RelationshipService#getRelationshipType(java.lang.Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public RelationshipType getRelationshipType(Integer relationshipTypeId) throws APIException {
		return dao.getRelationshipType(relationshipTypeId);
	}

	/**
	 * @see org.openmrs.api.RelationshipService#getRelationshipTypeByName(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public RelationshipType getRelationshipTypeByName(String relationshipTypeName) throws APIException {
		List<RelationshipType> types = dao.getRelationshipTypes(relationshipTypeName, null);

		if (types.isEmpty()) {
			return null;
		} else {
			return types.get(0);
		}
	}


	/**
	 * @see org.openmrs.api.RelationshipService#getAllRelationships()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Relationship> getAllRelationships() throws APIException {
		return Context.getRelationshipService().getAllRelationships(false);
	}

	/**
	 * @see org.openmrs.api.RelationshipService#getAllRelationships(boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Relationship> getAllRelationships(boolean includeVoided) throws APIException {
		return dao.getAllRelationships(includeVoided);
	}

	/**
	 * @see org.openmrs.api.RelationshipService#getRelationships(org.openmrs.Person, org.openmrs.Person,
	 *      org.openmrs.RelationshipType)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Relationship> getRelationships(Person fromPerson, Person toPerson, RelationshipType relType)
		throws APIException {
		return dao.getRelationships(fromPerson, toPerson, relType);
	}

	/**
	 * @see org.openmrs.api.RelationshipService#getRelationships(org.openmrs.Person, org.openmrs.Person,
	 *      org.openmrs.RelationshipType, java.util.Date)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Relationship> getRelationships(Person fromPerson, Person toPerson, RelationshipType relType,
											   Date effectiveDate) throws APIException {
		return dao.getRelationships(fromPerson, toPerson, relType, effectiveDate, null);
	}

	/**
	 * @see org.openmrs.api.RelationshipService#getRelationships(org.openmrs.Person, org.openmrs.Person,
	 *      org.openmrs.RelationshipType, java.util.Date, java.util.Date)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Relationship> getRelationships(Person fromPerson, Person toPerson, RelationshipType relType,
											   Date startEffectiveDate, Date endEffectiveDate) throws APIException {
		return dao.getRelationships(fromPerson, toPerson, relType, startEffectiveDate, endEffectiveDate);
	}

	/**
	 * @see org.openmrs.api.RelationshipService#getRelationshipsByPerson(org.openmrs.Person)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Relationship> getRelationshipsByPerson(Person p) throws APIException {

		// search both the left side and the right side of the relationship
		// for this person
		List<Relationship> rels = Context.getRelationshipService().getRelationships(p, null, null);
		rels.addAll(Context.getRelationshipService().getRelationships(null, p, null));

		return rels;
	}

	/**
	 * @see org.openmrs.api.RelationshipService#getRelationshipsByPerson(org.openmrs.Person,
	 *      java.util.Date)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Relationship> getRelationshipsByPerson(Person p, Date effectiveDate) throws APIException {

		// search both the left side and the right side of the relationship
		// for this person
		List<Relationship> rels = Context.getRelationshipService().getRelationships(p, null, null, effectiveDate);
		rels.addAll(Context.getRelationshipService().getRelationships(null, p, null, effectiveDate));

		return rels;
	}

	/**
	 * @see org.openmrs.api.RelationshipService#purgeRelationship(org.openmrs.Relationship)
	 */
	@Override
	public void purgeRelationship(Relationship relationship) throws APIException {
		dao.deleteRelationship(relationship);
	}

	/**
	 * @see org.openmrs.api.RelationshipService#saveRelationship(org.openmrs.Relationship)
	 */
	@Override
	public Relationship saveRelationship(Relationship relationship) throws APIException {
		if (relationship.getPersonA().equals(relationship.getPersonB())) {
			throw new APIException("Person.cannot.same", (Object[]) null);
		}

		return dao.saveRelationship(relationship);
	}

	/**
	 * @see org.openmrs.api.RelationshipService#voidRelationship(org.openmrs.Relationship,
	 *      java.lang.String)
	 */
	@Override
	public Relationship voidRelationship(Relationship relationship, String voidReason) throws APIException {
		if (relationship.getVoided()) {
			return relationship;
		}

		relationship.setVoided(true);
		if (relationship.getVoidedBy() == null) {
			relationship.setVoidedBy(Context.getAuthenticatedUser());
		}
		if (voidReason != null) {
			relationship.setVoidReason(voidReason);
		}
		relationship.setDateVoided(new Date());

		return Context.getRelationshipService().saveRelationship(relationship);
	}

	/**
	 * @see org.openmrs.api.RelationshipService#unvoidRelationship(org.openmrs.Relationship)
	 */
	@Override
	public Relationship unvoidRelationship(Relationship relationship) throws APIException {
		relationship.setVoided(false);
		relationship.setVoidedBy(null);
		relationship.setDateVoided(null);
		relationship.setVoidReason(null);

		return Context.getRelationshipService().saveRelationship(relationship);
	}

	/**
	 * @see org.openmrs.api.RelationshipService#getAllRelationshipTypes()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<RelationshipType> getAllRelationshipTypes() throws APIException {
		return Context.getRelationshipService().getAllRelationshipTypes(false);
	}

	/**
	 * @see org.openmrs.api.RelationshipService#getRelationshipTypes(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<RelationshipType> getRelationshipTypes(String searchString) throws APIException {

		return Context.getRelationshipService().getRelationshipTypes(searchString, null);
	}

	/**
	 * @see org.openmrs.api.RelationshipService#getRelationshipTypes(java.lang.String, java.lang.Boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<RelationshipType> getRelationshipTypes(String relationshipTypeName, Boolean preferred) throws APIException {
		Assert.hasText(relationshipTypeName, "The search string cannot be empty");

		return dao.getRelationshipTypes(relationshipTypeName, preferred);
	}

	/**
	 * @see org.openmrs.api.RelationshipService#purgeRelationshipType(org.openmrs.RelationshipType)
	 */
	@Override
	public void purgeRelationshipType(RelationshipType relationshipType) throws APIException {
		dao.deleteRelationshipType(relationshipType);
	}

	/**
	 * @see org.openmrs.api.RelationshipService#saveRelationshipType(org.openmrs.RelationshipType)
	 */
	@Override
	public RelationshipType saveRelationshipType(RelationshipType relationshipType) throws APIException {
		if (StringUtils.isBlank(relationshipType.getDescription())) {
			throw new APIException("error.required",
				new Object[] { Context.getMessageSourceService().getMessage("general.description") });
		}

		return dao.saveRelationshipType(relationshipType);
	}


	/**
	 * @see org.openmrs.api.RelationshipService#getRelationshipMap(org.openmrs.RelationshipType)
	 */
	@Override
	@Transactional(readOnly = true)
	public Map<Person, List<Person>> getRelationshipMap(RelationshipType relType) throws APIException {

		// get all relationships with this type
		List<Relationship> relationships = Context.getRelationshipService().getRelationships(null, null, relType);

		// the map to return
		Map<Person, List<Person>> ret = new HashMap<>();

		if (relationships != null) {
			for (Relationship rel : relationships) {
				Person from = rel.getPersonA();
				Person to = rel.getPersonB();

				List<Person> relList = ret.get(from);
				if (relList == null) {
					relList = new ArrayList<>();
				}
				relList.add(to);

				ret.put(from, relList);
			}
		}

		return ret;
	}


	/**
	 * @see org.openmrs.api.RelationshipService#getRelationshipByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public Relationship getRelationshipByUuid(String uuid) throws APIException {
		return dao.getRelationshipByUuid(uuid);
	}

	/**
	 * @see org.openmrs.api.RelationshipService#getRelationshipTypeByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public RelationshipType getRelationshipTypeByUuid(String uuid) throws APIException {
		return dao.getRelationshipTypeByUuid(uuid);
	}

	/**
	 * @see org.openmrs.api.RelationshipService#getAllRelationshipTypes(boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<RelationshipType> getAllRelationshipTypes(boolean includeRetired) throws APIException {
		return dao.getAllRelationshipTypes(includeRetired);
	}

	/**
	 * @see org.openmrs.api.RelationshipService#retireRelationshipType(org.openmrs.RelationshipType,
	 *      java.lang.String)
	 */
	@Override
	public RelationshipType retireRelationshipType(RelationshipType type, String retiredReason) throws APIException {
		if (retiredReason == null || retiredReason.length() < 1) {
			throw new APIException("Relationship.retiring.reason.required", (Object[]) null);
		}

		type.setRetired(true);
		type.setRetiredBy(Context.getAuthenticatedUser());
		type.setDateRetired(new Date());
		type.setRetireReason(retiredReason);
		return Context.getRelationshipService().saveRelationshipType(type);
	}

	/**
	 * @see org.openmrs.api.RelationshipService#unretireRelationshipType(org.openmrs.RelationshipType)
	 */
	@Override
	public RelationshipType unretireRelationshipType(RelationshipType relationshipType) {
		relationshipType.setRetired(false);
		relationshipType.setRetiredBy(null);
		relationshipType.setDateRetired(null);
		relationshipType.setRetireReason(null);
		return Context.getRelationshipService().saveRelationshipType(relationshipType);
	}

	
	
	@Override
	public void onStartup() {
		
	}

	@Override
	public void onShutdown() {

	}
}
