package org.openmrs.api;

import org.openmrs.LocationTag;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.db.LocationTagDAO;
import org.openmrs.util.PrivilegeConstants;

import java.util.List;

public interface LocationTagService extends OpenmrsService{



	/**
	 * Set the data access object that the service will use to interact with the database. This is
	 * set by spring in the applicationContext-service.xml file
	 *
	 * @param dao
	 */
	public void setLocationTagDAO(LocationTagDAO dao);
	
	/**
	 * Save location tag to database (create if new or update if changed)
	 *
	 * @param tag is the tag to be saved to the database
	 * <strong>Should</strong> throw APIException if tag has no name
	 * <strong>Should</strong> return saved object
	 * <strong>Should</strong> update location tag successfully
	 * <strong>Should</strong> create location tag successfully
	 * <strong>Should</strong> throw exception if tag name is null
	 * @since 1.5
	 */
	@Authorized( { PrivilegeConstants.MANAGE_LOCATION_TAGS })
	public LocationTag saveLocationTag(LocationTag tag) throws APIException;

	/**
	 * Returns a location tag given that locations primary key <code>locationTagId</code>. A null
	 * value is returned if no tag exists with this ID.
	 *
	 * @param locationTagId integer primary key of the location tag to find
	 * @return LocationTag object that has LocationTag.locationTagId = <code>locationTagId</code>
	 *         passed in.
	 * <strong>Should</strong> return null when no location tag match given id
	 * @since 1.5
	 */
	@Authorized( { PrivilegeConstants.GET_LOCATIONS })
	public LocationTag getLocationTag(Integer locationTagId) throws APIException;

	/**
	 * Returns a location tag given the location's exact name (tag). A null value is returned if
	 * there is no tag with this name.
	 *
	 * @param tag the exact name of the tag to match on
	 * @return LocationTag matching the name to LocationTag.tag
	 * <strong>Should</strong> get location tag by name
	 * <strong>Should</strong> return null when no location tag match given name
	 * @since 1.5
	 */
	@Authorized( { PrivilegeConstants.GET_LOCATIONS })
	public LocationTag getLocationTagByName(String tag) throws APIException;

	/**
	 * Returns all location tags, includes retired location tags. This method delegates to the
	 * #getAllLocationTags(boolean) method.
	 *
	 * @return location tags that are in the database
	 * <strong>Should</strong> return all location tags including retired
	 * @since 1.5
	 */
	@Authorized( { PrivilegeConstants.GET_LOCATIONS })
	public List<LocationTag> getAllLocationTags() throws APIException;

	/**
	 * Returns all location tags.
	 *
	 * @param includeRetired whether or not to include retired location tags
	 * <strong>Should</strong> return all location tags if includeRetired is true
	 * <strong>Should</strong> return only unretired location tags if includeRetired is false
	 * @since 1.5
	 */
	@Authorized( { PrivilegeConstants.GET_LOCATIONS })
	public List<LocationTag> getAllLocationTags(boolean includeRetired) throws APIException;

	/**
	 * Returns location tags that match the beginning of the given string. A null list will never be
	 * returned. An empty list will be returned if there are no tags. Search is case insensitive.
	 * matching this <code>search</code>
	 *
	 * @param search is the string used to search for tags
	 * <strong>Should</strong> return empty list when no location tag match given search string
	 * @since 1.5
	 */
	@Authorized( { PrivilegeConstants.GET_LOCATIONS })
	public List<LocationTag> getLocationTags(String search) throws APIException;

	/**
	 * Retire the given location tag. This effectively removes the tag from circulation or use.
	 *
	 * @param tag location tag to be retired
	 * @param reason is the reason why the location tag is being retired
	 * <strong>Should</strong> retire location tag successfully
	 * <strong>Should</strong> retire location tag with given reason
	 * @since 1.5
	 */
	@Authorized( { PrivilegeConstants.MANAGE_LOCATION_TAGS })
	public LocationTag retireLocationTag(LocationTag tag, String reason) throws APIException;

	/**
	 * Unretire the given location tag. This restores a previously retired tag back into circulation
	 * and use.
	 *
	 * @param tag
	 * @return the newly unretired location tag
	 * @throws APIException
	 * <strong>Should</strong> unretire retired location tag
	 * @since 1.5
	 */
	@Authorized( { PrivilegeConstants.MANAGE_LOCATION_TAGS })
	public LocationTag unretireLocationTag(LocationTag tag) throws APIException;

	/**
	 * Completely remove a location tag from the database (not reversible).
	 *
	 * @param tag the LocationTag to clean out of the database.
	 * <strong>Should</strong> delete location tag
	 * @since 1.5
	 */
	@Authorized( { PrivilegeConstants.PURGE_LOCATION_TAGS })
	public void purgeLocationTag(LocationTag tag) throws APIException;
	
	/**
	 * Returns a location tag by uuid
	 *
	 * @param uuid is the uuid of the desired location tag
	 * @return location tag with the given uuid
	 * <strong>Should</strong> find object given valid uuid
	 * <strong>Should</strong> return null if no object found with given uuid
	 */
	@Authorized( { PrivilegeConstants.GET_LOCATIONS })
	public LocationTag getLocationTagByUuid(String uuid) throws APIException;
}
