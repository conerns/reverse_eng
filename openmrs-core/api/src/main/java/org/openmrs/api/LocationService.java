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
import java.util.Map;

import org.openmrs.Address;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.LocationTag;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.db.LocationDAO;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.PrivilegeConstants;

/**
 * API methods for managing Locations <br>
 * <br>
 * Example Usage: <br>
 * <code>
 *   List&lt;Location&gt; locations = Context.getLocationService().getAllLocations();
 * </code>
 * 
 * @see org.openmrs.api.context.Context
 * @see org.openmrs.Location
 */
public interface LocationService extends OpenmrsService {
	
	/**
	 * Set the data access object that the service will use to interact with the database. This is
	 * set by spring in the applicationContext-service.xml file
	 * 
	 * @param dao
	 */
	public void setLocationDAO(LocationDAO dao);
	
	/**
	 * Save location to database (create if new or update if changed)
	 * 
	 * @param location is the location to be saved to the database
	 * <strong>Should</strong> throw APIException if location has no name
	 * <strong>Should</strong> overwrite transient tag if tag with same name exists
	 * <strong>Should</strong> throw APIException if transient tag is not found
	 * <strong>Should</strong> return saved object
	 * <strong>Should</strong> remove location tag from location
	 * <strong>Should</strong> add location tag to location
	 * <strong>Should</strong> remove child location from location
	 * <strong>Should</strong> cascade save to child location from location
	 * <strong>Should</strong> update location successfully
	 * <strong>Should</strong> create location successfully
	 */
	@Authorized( { PrivilegeConstants.MANAGE_LOCATIONS })
	public Location saveLocation(Location location) throws APIException;
	
	/**
	 * Returns a location given that locations primary key <code>locationId</code> A null value is
	 * returned if no location exists with this location.
	 * 
	 * @param locationId integer primary key of the location to find
	 * @return Location object that has location.locationId = <code>locationId</code> passed in.
	 * <strong>Should</strong> return null when no location match given location id
	 */
	@Authorized( { PrivilegeConstants.GET_LOCATIONS })
	public Location getLocation(Integer locationId) throws APIException;
	
	/**
	 * Returns a location given the location's exact <code>name</code> A null value is returned if
	 * there is no location with this name
	 * 
	 * @param name the exact name of the location to match on
	 * @return Location matching the <code>name</code> to Location.name
	 * <strong>Should</strong> return null when no location match given location name
	 */
	@Authorized( { PrivilegeConstants.GET_LOCATIONS })
	public Location getLocation(String name) throws APIException;
	
	/**
	 * Returns the default location for this implementation.
	 * 
	 * @return The default location for this implementation.
	 * <strong>Should</strong> return default location for the implementation
	 * <strong>Should</strong> return Unknown Location if the global property is something else that doesnot exist
	 */
	@Authorized( { PrivilegeConstants.GET_LOCATIONS })
	public Location getDefaultLocation() throws APIException;
	
	/**
	 * Returns a location by uuid
	 * 
	 * @param uuid is the uuid of the desired location
	 * @return location with the given uuid
	 * <strong>Should</strong> find object given valid uuid
	 * <strong>Should</strong> return null if no object found with given uuid
	 */
	@Authorized( { PrivilegeConstants.GET_LOCATIONS })
	public Location getLocationByUuid(String uuid) throws APIException;
	
	
	
	/**
	 * Returns all locations, includes retired locations. This method delegates to the
	 * #getAllLocations(boolean) method
	 * 
	 * @return locations that are in the database
	 * <strong>Should</strong> return all locations including retired
	 */
	@Authorized( { PrivilegeConstants.GET_LOCATIONS })
	public List<Location> getAllLocations() throws APIException;
	
	/**
	 * Returns all locations.
	 * 
	 * @param includeRetired whether or not to include retired locations
	 * <strong>Should</strong> return all locations when includeRetired is true
	 * <strong>Should</strong> return only unretired locations when includeRetires is false
	 */
	@Authorized( { PrivilegeConstants.GET_LOCATIONS })
	public List<Location> getAllLocations(boolean includeRetired) throws APIException;
	
	/**
	 * Returns locations that match the beginning of the given string. A null list will never be
	 * returned. An empty list will be returned if there are no locations. Search is case
	 * insensitive. matching this <code>nameFragment</code>
	 * 
	 * @param nameFragment is the string used to search for locations
	 * <strong>Should</strong> return empty list when no location match the name fragment
	 */
	@Authorized( { PrivilegeConstants.GET_LOCATIONS })
	public List<Location> getLocations(String nameFragment) throws APIException;
	
	/**
	 * Gets the locations matching the specified arguments. A null list will never be returned. An empty list will be
	 * returned if there are no locations. Search is case insensitive. matching this <code>nameFragment</code>. If start
	 * and length are not specified, then all matches are returned.
	 *
	 * @param nameFragment    is the string used to search for locations
	 * @param parent          only return children of this parent
	 * @param attributeValues the attribute values
	 * @param includeRetired  specifies if retired locations should also be returned
	 * @param start           the beginning index
	 * @param length          the number of matching locations to return
	 * @return the list of locations
	 * <strong>Should</strong> return empty list when no location has matching attribute values
	 * <strong>Should</strong> get locations having all matching attribute values
	 * @since 1.10
	 */
	@Authorized( { PrivilegeConstants.GET_LOCATIONS })
	public List<Location> getLocations(String nameFragment, Location parent,
	        Map<LocationAttributeType, Object> attributeValues, boolean includeRetired, Integer start, Integer length)
	        throws APIException;
	
	/**
	 * Returns locations that contain the given tag.
	 * 
	 * @param tag LocationTag criterion
	 * <strong>Should</strong> get locations by tag
	 * <strong>Should</strong> return empty list when no locations has the given tag
	 * @since 1.5
	 */
	@Authorized( { PrivilegeConstants.GET_LOCATIONS })
	public List<Location> getLocationsByTag(LocationTag tag) throws APIException;
	
	/**
	 * Returns locations that are mapped to all given tags.
	 * 
	 * @param tags Set of LocationTag criteria
	 * <strong>Should</strong> get locations having all tags
	 * <strong>Should</strong> return all unretired locations given an empty tag list
	 * @since 1.5
	 */
	@Authorized( { PrivilegeConstants.GET_LOCATIONS })
	public List<Location> getLocationsHavingAllTags(List<LocationTag> tags) throws APIException;
	
	/**
	 * Returns locations that are mapped to any of the given tags.
	 * 
	 * @param tags Set of LocationTag criteria
	 * <strong>Should</strong> get locations having any tag
	 * <strong>Should</strong> return empty list when no location has the given tags
	 * <strong>Should</strong> return empty list when given an empty tag list
	 * @since 1.5
	 */
	@Authorized( { PrivilegeConstants.GET_LOCATIONS })
	public List<Location> getLocationsHavingAnyTag(List<LocationTag> tags) throws APIException;
	
	/**
	 * Retires the given location. This effectively removes the location from circulation or use.
	 * 
	 * @param location location to be retired
	 * @param reason is the reason why the location is being retired
	 * <strong>Should</strong> retire location successfully
	 */
	@Authorized( { PrivilegeConstants.MANAGE_LOCATIONS })
	public Location retireLocation(Location location, String reason) throws APIException;
	
	/**
	 * Unretire the given location. This restores a previously retired location back into
	 * circulation and use.
	 * 
	 * @param location
	 * @return the newly unretired location
	 * @throws APIException
	 * <strong>Should</strong> unretire retired location
	 */
	@Authorized( { PrivilegeConstants.MANAGE_LOCATIONS })
	public Location unretireLocation(Location location) throws APIException;
	
	/**
	 * Completely remove a location from the database (not reversible) This method delegates to
	 * #purgeLocation(location, boolean) method
	 * 
	 * @param location the Location to clean out of the database.
	 * <strong>Should</strong> delete location successfully
	 */
	@Authorized( { PrivilegeConstants.PURGE_LOCATIONS })
	public void purgeLocation(Location location) throws APIException;
	
	
	/**
	 * Return the number of all locations that start with the given name fragment, if the name
	 * fragment is null or an empty string, then the number of all locations will be returned
	 * 
	 * @param nameFragment is the string used to search for locations
	 * @param includeRetired Specifies if retired locations should be counted or ignored
	 * @return the number of all locations starting with the given nameFragment
	 * @since 1.8
	 */
	@Authorized( { PrivilegeConstants.GET_LOCATIONS })
	public Integer getCountOfLocations(String nameFragment, Boolean includeRetired);
	
	/**
	 * Returns all root locations (i.e. those who have no parentLocation), optionally including
	 * retired ones.
	 * 
	 * @param includeRetired
	 * @return return all root locations depends on includeRetired
	 * <strong>Should</strong> return all root locations when includeRetired is true
	 * <strong>Should</strong> return only unretired root locations when includeRetired is false
	 * @since 1.9
	 */
	@Authorized( { PrivilegeConstants.GET_LOCATIONS })
	public List<Location> getRootLocations(boolean includeRetired);
	
	
	
}
