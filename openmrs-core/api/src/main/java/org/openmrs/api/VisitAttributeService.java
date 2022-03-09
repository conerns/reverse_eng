package org.openmrs.api;

import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;
import org.openmrs.annotation.Authorized;
import org.openmrs.util.PrivilegeConstants;

import java.util.List;

public interface VisitAttributeService {

	/**
	 * @return all {@link VisitAttributeType}s
	 * <strong>Should</strong> return all visit attribute types including retired ones
	 */
	@Authorized(PrivilegeConstants.GET_VISIT_ATTRIBUTE_TYPES)
	List<VisitAttributeType> getAllVisitAttributeTypes();

	/**
	 * @param id
	 * @return the {@link VisitAttributeType} with the given internal id
	 * <strong>Should</strong> return the visit attribute type with the given id
	 * <strong>Should</strong> return null if no visit attribute type exists with the given id
	 */
	@Authorized(PrivilegeConstants.GET_VISIT_ATTRIBUTE_TYPES)
	VisitAttributeType getVisitAttributeType(Integer id);

	/**
	 * @param uuid
	 * @return the {@link VisitAttributeType} with the given uuid
	 * <strong>Should</strong> return the visit attribute type with the given uuid
	 * <strong>Should</strong> return null if no visit attribute type exists with the given uuid
	 */
	@Authorized(PrivilegeConstants.GET_VISIT_ATTRIBUTE_TYPES)
	VisitAttributeType getVisitAttributeTypeByUuid(String uuid);

	/**
	 * Creates or updates the given visit attribute type in the database
	 *
	 * @param visitAttributeType
	 * @return the VisitAttributeType created/saved
	 * <strong>Should</strong> create a new visit attribute type
	 * <strong>Should</strong> edit an existing visit attribute type
	 */
	@Authorized(PrivilegeConstants.MANAGE_VISIT_ATTRIBUTE_TYPES)
	VisitAttributeType saveVisitAttributeType(VisitAttributeType visitAttributeType);

	/**
	 * Retires the given visit attribute type in the database
	 *
	 * @param visitAttributeType
	 * @return the visitAttribute retired
	 * <strong>Should</strong> retire a visit attribute type
	 */
	@Authorized(PrivilegeConstants.MANAGE_VISIT_ATTRIBUTE_TYPES)
	VisitAttributeType retireVisitAttributeType(VisitAttributeType visitAttributeType, String reason);

	/**
	 * Restores a visit attribute type that was previous retired in the database
	 *
	 * @param visitAttributeType
	 * @return the VisitAttributeType unretired
	 * <strong>Should</strong> unretire a retired visit attribute type
	 */
	@Authorized(PrivilegeConstants.MANAGE_VISIT_ATTRIBUTE_TYPES)
	VisitAttributeType unretireVisitAttributeType(VisitAttributeType visitAttributeType);

	/**
	 * Completely removes a visit attribute type from the database
	 *
	 * @param visitAttributeType
	 * <strong>Should</strong> completely remove a visit attribute type
	 */
	@Authorized(PrivilegeConstants.PURGE_VISIT_ATTRIBUTE_TYPES)
	void purgeVisitAttributeType(VisitAttributeType visitAttributeType);

	/**
	 * @param uuid
	 * @return the {@link VisitAttribute} with the given uuid
	 * <strong>Should</strong> get the visit attribute with the given uuid
	 * <strong>Should</strong> return null if no visit attribute has the given uuid
	 */
	@Authorized(PrivilegeConstants.GET_VISITS)
	VisitAttribute getVisitAttributeByUuid(String uuid);
	
}
