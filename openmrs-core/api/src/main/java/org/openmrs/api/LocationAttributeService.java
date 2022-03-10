package org.openmrs.api;

import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.db.LocationAttributeDAO;
import org.openmrs.util.PrivilegeConstants;

import java.util.List;

public interface LocationAttributeService extends OpenmrsService{

	public void setLocationAttributeDAO(LocationAttributeDAO dao);
	
	/**
	 * @return all {@link LocationAttributeType}s
	 * @since 1.9
	 * <strong>Should</strong> return all location attribute types including retired ones
	 */
	@Authorized(PrivilegeConstants.GET_LOCATION_ATTRIBUTE_TYPES)
	List<LocationAttributeType> getAllLocationAttributeTypes();

	/**
	 * @param id
	 * @return the {@link LocationAttributeType} with the given internal id
	 * @since 1.9
	 * <strong>Should</strong> return the location attribute type with the given id
	 * <strong>Should</strong> return null if no location attribute type exists with the given id
	 */
	@Authorized(PrivilegeConstants.GET_LOCATION_ATTRIBUTE_TYPES)
	LocationAttributeType getLocationAttributeType(Integer id);

	/**
	 * @param uuid
	 * @return the {@link LocationAttributeType} with the given uuid
	 * @since 1.9
	 * <strong>Should</strong> return the location attribute type with the given uuid
	 * <strong>Should</strong> return null if no location attribute type exists with the given uuid
	 */
	@Authorized(PrivilegeConstants.GET_LOCATION_ATTRIBUTE_TYPES)
	LocationAttributeType getLocationAttributeTypeByUuid(String uuid);

	/**
	 * Creates or updates the given location attribute type in the database
	 *
	 * @param locationAttributeType
	 * @return the LocationAttributeType created/saved
	 * @since 1.9
	 * <strong>Should</strong> create a new location attribute type
	 * <strong>Should</strong> edit an existing location attribute type
	 */
	@Authorized(PrivilegeConstants.MANAGE_LOCATION_ATTRIBUTE_TYPES)
	LocationAttributeType saveLocationAttributeType(LocationAttributeType locationAttributeType);

	/**
	 * Retires the given location attribute type in the database
	 *
	 * @param locationAttributeType
	 * @return the locationAttribute retired
	 * @since 1.9
	 * <strong>Should</strong> retire a location attribute type
	 */
	@Authorized(PrivilegeConstants.MANAGE_LOCATION_ATTRIBUTE_TYPES)
	LocationAttributeType retireLocationAttributeType(LocationAttributeType locationAttributeType, String reason);

	/**
	 * Restores a location attribute type that was previous retired in the database
	 *
	 * @param locationAttributeType
	 * @return the LocationAttributeType unretired
	 * @since 1.9
	 * <strong>Should</strong> unretire a retired location attribute type
	 */
	@Authorized(PrivilegeConstants.MANAGE_LOCATION_ATTRIBUTE_TYPES)
	LocationAttributeType unretireLocationAttributeType(LocationAttributeType locationAttributeType);

	/**
	 * Completely removes a location attribute type from the database
	 *
	 * @param locationAttributeType
	 * @since 1.9
	 * <strong>Should</strong> completely remove a location attribute type
	 */
	@Authorized(PrivilegeConstants.PURGE_LOCATION_ATTRIBUTE_TYPES)
	void purgeLocationAttributeType(LocationAttributeType locationAttributeType);

	/**
	 * @param uuid
	 * @return the {@link LocationAttribute} with the given uuid
	 * @since 1.9
	 * <strong>Should</strong> get the location attribute with the given uuid
	 * <strong>Should</strong> return null if no location attribute has the given uuid
	 */
	@Authorized(PrivilegeConstants.GET_LOCATIONS)
	LocationAttribute getLocationAttributeByUuid(String uuid);

	/**
	 * Retrieves a LocationAttributeType object based on the name provided
	 *
	 * @param locationAttributeTypeName
	 * @return the {@link LocationAttributeType} with the specified name
	 * @since 1.10.0
	 * <strong>Should</strong> return the location attribute type with the specified name
	 * <strong>Should</strong> return null if no location attribute type exists with the specified name
	 */
	@Authorized(PrivilegeConstants.GET_LOCATION_ATTRIBUTE_TYPES)
	LocationAttributeType getLocationAttributeTypeByName(String locationAttributeTypeName);
}
