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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.openmrs.Address;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.LocationTag;
import org.openmrs.api.APIException;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.LocationDAO;
import org.openmrs.customdatatype.CustomDatatypeUtil;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Default implementation of the {@link LocationService}
 * <p>
 * This class should not be instantiated alone, get a service class from the Context:
 * Context.getLocationService();
 *
 * @see org.openmrs.api.context.Context
 * @see org.openmrs.api.LocationService
 * @see org.openmrs.Location
 */
@Transactional
public class LocationServiceImpl extends BaseOpenmrsService implements LocationService {
	
	private LocationDAO dao;
	
	/**
	 * @see org.openmrs.api.LocationService#setLocationDAO(org.openmrs.api.db.LocationDAO)
	 */
	@Override
	public void setLocationDAO(LocationDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @see org.openmrs.api.LocationService#saveLocation(org.openmrs.Location)
	 */
	@Override
	public Location saveLocation(Location location) throws APIException {
		if (location.getName() == null) {
			throw new APIException("Location.name.required", (Object[]) null);
		}
		
		// Check for transient tags. If found, try to match by name and overwrite, otherwise throw exception.
		if (location.getTags() != null) {
			for (LocationTag tag : location.getTags()) {
				
				// only check transient (aka non-precreated) location tags
				if (tag.getLocationTagId() == null) {
					if (!StringUtils.hasLength(tag.getName())) {
						throw new APIException("Location.tag.name.required", (Object[]) null);
					}
					
					LocationTag existing = Context.getLocationTagService().getLocationTagByName(tag.getName());
					if (existing != null) {
						location.removeTag(tag);
						location.addTag(existing);
					} else {
						throw new APIException("Location.cannot.add.transient.tags", (Object[]) null);
					}
				}
			}
		}
		
		CustomDatatypeUtil.saveAttributesIfNecessary(location);
		
		return dao.saveLocation(location);
	}
	
	/**
	 * @see org.openmrs.api.LocationService#getLocation(java.lang.Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public Location getLocation(Integer locationId) throws APIException {
		return dao.getLocation(locationId);
	}
	
	/**
	 * @see org.openmrs.api.LocationService#getLocation(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public Location getLocation(String name) throws APIException {
		return dao.getLocation(name);
	}
	
	/**
	 * @see org.openmrs.api.LocationService#getDefaultLocation()
	 */
	@Override
	@Transactional(readOnly = true)
	public Location getDefaultLocation() throws APIException {
		Location location = null;
		String locationGP = Context.getAdministrationService().getGlobalProperty(
		    OpenmrsConstants.GLOBAL_PROPERTY_DEFAULT_LOCATION_NAME);
		
		if (StringUtils.hasText(locationGP)) {
			location = Context.getLocationService().getLocation(locationGP);
		}

		if (location == null) {
			location = getDefaultLocation(null, locationGP);
		}
		
		// If neither exist, get the first available location
		if (location == null) {
			location = Context.getLocationService().getLocation(1);
		}
		
		return location;
	}

	private Location getDefaultLocation(Location location, String locationGP) {
		//Try to look up 'Unknown Location' in case the global property is something else
		Location result = getDefaultLocationFromSting(location, locationGP, "Unknown Location");

		// If Unknown Location does not exist, try Unknown if the global property was different
		if (result == null) {
			result = getDefaultLocationFromSting(location, locationGP, "Unknown");
		}

		return result;
	}

	private Location getDefaultLocationFromSting(Location location, String locationGP, String defaultLocation) {
		Location result = null;
		if (location == null && (!StringUtils.hasText(locationGP) || !defaultLocation.equalsIgnoreCase(locationGP))) {
			result = Context.getLocationService().getLocation(defaultLocation);
		}

		return result;
	}
	
	/**
	 * @see org.openmrs.api.LocationService#getLocationByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public Location getLocationByUuid(String uuid) throws APIException {
		return dao.getLocationByUuid(uuid);
	}
	

	
	/**
	 * @see org.openmrs.api.LocationService#getAllLocations()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Location> getAllLocations() throws APIException {
		return dao.getAllLocations(true);
	}
	
	/**
	 * @see org.openmrs.api.LocationService#getAllLocations(boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Location> getAllLocations(boolean includeRetired) throws APIException {
		return dao.getAllLocations(includeRetired);
	}
	
	/**
	 * @see org.openmrs.api.LocationService#getLocations(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Location> getLocations(String nameFragment) throws APIException {
		return Context.getLocationService().getLocations(nameFragment, null, null, false, null, null);
	}
	
	/**
	 * @see org.openmrs.api.LocationService#getLocationsByTag(LocationTag)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Location> getLocationsByTag(LocationTag tag) throws APIException {
		List<Location> locations = new ArrayList<>();
		
		for (Location l : dao.getAllLocations(false)) {
			if (l.getTags() != null && l.getTags().contains(tag)) {
				locations.add(l);
			}
		}
		
		return locations;
	}
	
	/**
	 * @see org.openmrs.api.LocationService#getLocationsHavingAllTags(List)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Location> getLocationsHavingAllTags(List<LocationTag> tags) throws APIException {
		return CollectionUtils.isEmpty(tags) ? getAllLocations(false) : dao.getLocationsHavingAllTags(tags);
	}
	
	/**
	 * @see org.openmrs.api.LocationService#getLocationsHavingAnyTag(List)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Location> getLocationsHavingAnyTag(List<LocationTag> tags) throws APIException {
		List<Location> locations = new ArrayList<>();
		
		for (Location loc : dao.getAllLocations(false)) {
			for (LocationTag t : tags) {
				if (loc.getTags().contains(t) && !locations.contains(loc)) {
					locations.add(loc);
				}
			}
		}
		
		return locations;
	}
	
	/**
	 * @see org.openmrs.api.LocationService#retireLocation(Location, String)
	 */
	@Override
	public Location retireLocation(Location location, String reason) throws APIException {
		location.setRetired(true);
		location.setRetireReason(reason);
		return Context.getLocationService().saveLocation(location);
	}
	
	/**
	 * @see org.openmrs.api.LocationService#unretireLocation(org.openmrs.Location)
	 */
	@Override
	public Location unretireLocation(Location location) throws APIException {
		location.setRetired(false);
		return Context.getLocationService().saveLocation(location);
	}
	
	/**
	 * @see org.openmrs.api.LocationService#purgeLocation(org.openmrs.Location)
	 */
	@Override
	public void purgeLocation(Location location) throws APIException {
		dao.deleteLocation(location);
	}
	
	
	/**
	 * @see org.openmrs.api.LocationService#getCountOfLocations(String, Boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public Integer getCountOfLocations(String nameFragment, Boolean includeRetired) {
		return OpenmrsUtil.convertToInteger(dao.getCountOfLocations(nameFragment, includeRetired));
	}
	
	/**
	 * @see LocationService#getLocations(String, org.openmrs.Location, java.util.Map, boolean,
	 *      Integer, Integer)
	 */
	@Override
	public List<Location> getLocations(String nameFragment, Location parent,
	        Map<LocationAttributeType, Object> attributeValues, boolean includeRetired, Integer start, Integer length) {
		
		Map<LocationAttributeType, String> serializedAttributeValues = CustomDatatypeUtil
		        .getValueReferences(attributeValues);
		
		return dao.getLocations(nameFragment, parent, serializedAttributeValues, includeRetired, start, length);
	}
	
	/**
	 * @see LocationService#getRootLocations(boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Location> getRootLocations(boolean includeRetired) throws APIException {
		return dao.getRootLocations(includeRetired);
	}
	

	
}
