package org.openmrs.api.db;

import org.hibernate.SessionFactory;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.api.LocationService;

import java.util.List;

public interface LocationAttributeDAO {

	/**
	 * Set the Hibernate SessionFactory to connect to the database.
	 *
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory);


	/**
	 * @see LocationService#getAllLocationAttributeTypes()
	 */
	public List<LocationAttributeType> getAllLocationAttributeTypes();

	/**
	 * @see LocationService#getLocationAttributeType(Integer)
	 */
	public LocationAttributeType getLocationAttributeType(Integer id);

	/**
	 * @see LocationService#getLocationAttributeTypeByUuid(String)
	 */
	public LocationAttributeType getLocationAttributeTypeByUuid(String uuid);

	/**
	 * @see LocationService#saveLocationAttributeType(LocationAttributeType)
	 */
	public LocationAttributeType saveLocationAttributeType(LocationAttributeType locationAttributeType);

	/**
	 * @see LocationService#purgeLocationAttributeType(LocationAttributeType)
	 */
	public void deleteLocationAttributeType(LocationAttributeType locationAttributeType);

	/**
	 * @see LocationService#getLocationAttributeByUuid(String)
	 */
	public LocationAttribute getLocationAttributeByUuid(String uuid);

	/**
	 * @see LocationService#getLocationAttributeTypeByName(String)
	 */
	public LocationAttributeType getLocationAttributeTypeByName(String name);
	
}
