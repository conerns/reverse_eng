package org.openmrs.api.impl;

import org.openmrs.LocationTag;
import org.openmrs.api.APIException;
import org.openmrs.api.LocationTagService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.LocationDAO;
import org.openmrs.api.db.LocationTagDAO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

public class LocationTagServiceImpl implements LocationTagService {

	private LocationTagDAO dao;

	@Override
	public void setLocationTagDAO(LocationTagDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @see org.openmrs.api.LocationService#getLocationTagByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public LocationTag getLocationTagByUuid(String uuid) throws APIException {
		return dao.getLocationTagByUuid(uuid);
	}



	/**
	 * @see org.openmrs.api.LocationService#saveLocationTag(org.openmrs.LocationTag)
	 */
	@Override
	public LocationTag saveLocationTag(LocationTag tag) throws APIException {
		return dao.saveLocationTag(tag);
	}

	/**
	 * @see org.openmrs.api.LocationService#getLocationTag(java.lang.Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public LocationTag getLocationTag(Integer locationTagId) throws APIException {
		return dao.getLocationTag(locationTagId);
	}

	/**
	 * @see org.openmrs.api.LocationService#getLocationTagByName(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public LocationTag getLocationTagByName(String tag) throws APIException {
		return dao.getLocationTagByName(tag);
	}

	/**
	 * @see org.openmrs.api.LocationService#getAllLocationTags()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<LocationTag> getAllLocationTags() throws APIException {
		return dao.getAllLocationTags(true);
	}

	/**
	 * @see org.openmrs.api.LocationService#getAllLocationTags(boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<LocationTag> getAllLocationTags(boolean includeRetired) throws APIException {
		return dao.getAllLocationTags(includeRetired);
	}

	/**
	 * @see org.openmrs.api.LocationService#getLocationTags(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<LocationTag> getLocationTags(String search) throws APIException {
		if (StringUtils.isEmpty(search)) {
			return Context.getLocationTagService().getAllLocationTags(true);
		}

		return dao.getLocationTags(search);
	}

	/**
	 * @see org.openmrs.api.LocationService#retireLocationTag(LocationTag, String)
	 */
	@Override
	public LocationTag retireLocationTag(LocationTag tag, String reason) throws APIException {
		if (tag.getRetired()) {
			return tag;
		} else {
			if (reason == null) {
				throw new APIException("Location.retired.reason.required", (Object[]) null);
			}
			tag.setRetired(true);
			tag.setRetireReason(reason);
			tag.setRetiredBy(Context.getAuthenticatedUser());
			tag.setDateRetired(new Date());
			return Context.getLocationTagService().saveLocationTag(tag);
		}
	}

	/**
	 * @see org.openmrs.api.LocationService#unretireLocationTag(org.openmrs.LocationTag)
	 */
	@Override
	public LocationTag unretireLocationTag(LocationTag tag) throws APIException {
		tag.setRetired(false);
		tag.setRetireReason(null);
		tag.setRetiredBy(null);
		tag.setDateRetired(null);
		return Context.getLocationTagService().saveLocationTag(tag);
	}

	/**
	 * @see org.openmrs.api.LocationService#purgeLocationTag(org.openmrs.LocationTag)
	 */
	@Override
	public void purgeLocationTag(LocationTag tag) throws APIException {
		dao.deleteLocationTag(tag);
	}

	@Override
	public void onStartup() {

	}

	@Override
	public void onShutdown() {

	}
}
