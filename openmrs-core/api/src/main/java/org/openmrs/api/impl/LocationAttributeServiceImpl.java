package org.openmrs.api.impl;

import org.openmrs.LocationAttribute;
import org.openmrs.api.LocationAttributeService;
import org.openmrs.LocationAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.LocationAttributeDAO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class LocationAttributeServiceImpl implements LocationAttributeService {

	private LocationAttributeDAO dao;
	
	@Override
	public void setLocationAttributeDAO(LocationAttributeDAO dao) {
		this.dao = dao;
	}

	/**
	 * @see org.openmrs.api.LocationService#getAllLocationAttributeTypes()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<LocationAttributeType> getAllLocationAttributeTypes() {
		return dao.getAllLocationAttributeTypes();
	}

	/**
	 * @see org.openmrs.api.LocationService#getLocationAttributeType(java.lang.Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public LocationAttributeType getLocationAttributeType(Integer id) {
		return dao.getLocationAttributeType(id);
	}

	/**
	 * @see org.openmrs.api.LocationService#getLocationAttributeTypeByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public LocationAttributeType getLocationAttributeTypeByUuid(String uuid) {
		return dao.getLocationAttributeTypeByUuid(uuid);
	}

	/**
	 * @see org.openmrs.api.LocationService#saveLocationAttributeType(org.openmrs.LocationAttributeType)
	 */
	@Override
	public LocationAttributeType saveLocationAttributeType(LocationAttributeType locationAttributeType) {
		return dao.saveLocationAttributeType(locationAttributeType);
	}

	/**
	 * @see org.openmrs.api.LocationService#retireLocationAttributeType(org.openmrs.LocationAttributeType,
	 *      java.lang.String)
	 */
	@Override
	public LocationAttributeType retireLocationAttributeType(LocationAttributeType locationAttributeType, String reason) {
		return dao.saveLocationAttributeType(locationAttributeType);
	}

	/**
	 * @see org.openmrs.api.LocationService#unretireLocationAttributeType(org.openmrs.LocationAttributeType)
	 */
	@Override
	public LocationAttributeType unretireLocationAttributeType(LocationAttributeType locationAttributeType) {
		return Context.getLocationAttributeService().saveLocationAttributeType(locationAttributeType);
	}

	/**
	 * @see org.openmrs.api.LocationService#purgeLocationAttributeType(org.openmrs.LocationAttributeType)
	 */
	@Override
	public void purgeLocationAttributeType(LocationAttributeType locationAttributeType) {
		dao.deleteLocationAttributeType(locationAttributeType);
	}

	/**
	 * @see org.openmrs.api.LocationService#getLocationAttributeByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public LocationAttribute getLocationAttributeByUuid(String uuid) {
		return dao.getLocationAttributeByUuid(uuid);
	}


	/**
	 * @see org.openmrs.api.LocationService#getLocationAttributeTypeByName(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public LocationAttributeType getLocationAttributeTypeByName(String name) {
		return dao.getLocationAttributeTypeByName(name);
	}

	@Override
	public void onStartup() {

	}

	@Override
	public void onShutdown() {

	}
}
