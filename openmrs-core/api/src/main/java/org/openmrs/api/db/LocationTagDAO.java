package org.openmrs.api.db;

import org.hibernate.SessionFactory;
import org.openmrs.LocationTag;

import java.util.List;

public interface LocationTagDAO {

	/**
	 * Set the Hibernate SessionFactory to connect to the database.
	 *
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory);

	/**
	 * Create or update a location tag.
	 *
	 * @param tag
	 * @return the saved <code>LocationTag</code>
	 */
	public LocationTag saveLocationTag(LocationTag tag);

	/**
	 * Get a location tag by <code>locationTagId</code>
	 *
	 * @param locationTagId Internal <code>Integer</code> identifier of the tag to get
	 * @return the requested <code>LocationTag</code>
	 */
	public LocationTag getLocationTag(Integer locationTagId);

	/**
	 * Get a location tag by name
	 *
	 * @param tag String representation of the <code>LocationTag</code> to get
	 * @return the requested <code>LocationTag</code>
	 */
	public LocationTag getLocationTagByName(String tag);

	/**
	 * Get all location tags
	 *
	 * @param includeRetired boolean - include retired tags as well?
	 * @return List&lt;LocationTag&gt; object with all <code>LocationTag</code>s, possibly included
	 *         retired ones
	 */
	public List<LocationTag> getAllLocationTags(boolean includeRetired);

	/**
	 * Find all location tags with matching names.
	 *
	 * @param search name to search
	 * @return List&lt;LocationTag&gt; with all matching <code>LocationTags</code>
	 */
	public List<LocationTag> getLocationTags(String search);

	/**
	 * Completely remove the location tag from the database.
	 *
	 * @param tag The <code>LocationTag</code> to delete
	 */
	public void deleteLocationTag(LocationTag tag);

	/**
	 * @param uuid
	 * @return location tag or null
	 */
	public LocationTag getLocationTagByUuid(String uuid);
}
