/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.api.db.hibernate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.LocationAttributeType;
import org.openmrs.LocationTag;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.LocationDAO;

/**
 * Hibernate location-related database functions
 */
public class HibernateLocationDAO implements LocationDAO {
	
	private SessionFactory sessionFactory;
	
	/**
	 * @see org.openmrs.api.db.LocationDAO#setSessionFactory(org.hibernate.SessionFactory)
	 */
	@Override
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * @see org.openmrs.api.db.LocationDAO#getLocationByUuid(java.lang.String)
	 */
	@Override
	public Location getLocationByUuid(String uuid) {
		return (Location) sessionFactory.getCurrentSession().createQuery("from Location l where l.uuid = :uuid").setString(
			"uuid", uuid).uniqueResult();
	}
	/**
	 * @see org.openmrs.api.db.LocationDAO#saveLocation(org.openmrs.Location)
	 */
	@Override
	public Location saveLocation(Location location) {
		if (location.getChildLocations() != null && location.getLocationId() != null) {
			// hibernate has a problem updating child collections
			// if the parent object was already saved so we do it
			// explicitly here
			for (Location child : location.getChildLocations()) {
				if (child.getLocationId() == null) {
					saveLocation(child);
				}
			}
		}
		
		sessionFactory.getCurrentSession().saveOrUpdate(location);
		return location;
	}
	
	/**
	 * @see org.openmrs.api.db.LocationDAO#getLocation(java.lang.Integer)
	 */
	@Override
	public Location getLocation(Integer locationId) {
		return (Location) sessionFactory.getCurrentSession().get(Location.class, locationId);
	}
	
	/**
	 * @see org.openmrs.api.db.LocationDAO#getLocation(java.lang.String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Location getLocation(String name) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Location.class).add(
		    Restrictions.eq("name", name));
		
		List<Location> locations = criteria.list();
		if (null == locations || locations.isEmpty()) {
			return null;
		}
		return locations.get(0);
	}
	
	/**
	 * @see org.openmrs.api.db.LocationDAO#getAllLocations(boolean)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Location> getAllLocations(boolean includeRetired) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Location.class);
		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", false));
		} else {
			//push retired locations to the end of the returned list
			criteria.addOrder(Order.asc("retired"));
		}
		criteria.addOrder(Order.asc("name"));
		return criteria.list();
	}
	
	/**
	 * @see org.openmrs.api.db.LocationDAO#deleteLocation(org.openmrs.Location)
	 */
	@Override
	public void deleteLocation(Location location) {
		sessionFactory.getCurrentSession().delete(location);
	}
	
	
	/**
	 * @see org.openmrs.api.db.LocationDAO#getCountOfLocations(String, Boolean)
	 */
	@Override
	public Long getCountOfLocations(String nameFragment, Boolean includeRetired) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Location.class);
		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", false));
		}
		
		if (StringUtils.isNotBlank(nameFragment)) {
			criteria.add(Restrictions.ilike("name", nameFragment, MatchMode.START));
		}
		
		criteria.setProjection(Projections.rowCount());
		
		return (Long) criteria.uniqueResult();
	}
	
	/**
	 * @see LocationDAO#getLocations(String, org.openmrs.Location, java.util.Map, boolean, Integer, Integer)
	 */
	@Override
	public List<Location> getLocations(String nameFragment, Location parent,
	        Map<LocationAttributeType, String> serializedAttributeValues, boolean includeRetired, Integer start,
	        Integer length) {
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Location.class);
		
		if (StringUtils.isNotBlank(nameFragment)) {
			criteria.add(Restrictions.ilike("name", nameFragment, MatchMode.START));
		}
		
		if (parent != null) {
			criteria.add(Restrictions.eq("parentLocation", parent));
		}
		
		if (serializedAttributeValues != null) {
			HibernateUtil.addAttributeCriteria(criteria, serializedAttributeValues);
		}
		
		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", false));
		}
		
		criteria.addOrder(Order.asc("name"));
		if (start != null) {
			criteria.setFirstResult(start);
		}
		if (length != null && length > 0) {
			criteria.setMaxResults(length);
		}
		
		return criteria.list();
	}
	
	/**
	 * @see LocationDAO#getRootLocations(boolean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Location> getRootLocations(boolean includeRetired) throws DAOException {
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Location.class);
		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", false));
		}
		
		criteria.add(Restrictions.isNull("parentLocation"));
		
		criteria.addOrder(Order.asc("name"));
		return criteria.list();
	}
	
	
	/**
	 * @see org.openmrs.api.db.LocationDAO#getLocationsHavingAllTags(java.util.List)
	 */
	@Override
	public List<Location> getLocationsHavingAllTags(List<LocationTag> tags) {
		tags.removeAll(Collections.singleton(null));
		
		DetachedCriteria numberOfMatchingTags = DetachedCriteria.forClass(Location.class, "alias").createAlias("alias.tags",
		    "locationTag").add(Restrictions.in("locationTag.locationTagId", getLocationTagIds(tags))).setProjection(
		    Projections.rowCount()).add(Restrictions.eqProperty("alias.locationId", "outer.locationId"));
		
		return sessionFactory.getCurrentSession().createCriteria(Location.class, "outer").add(
		    Restrictions.eq("retired", false)).add(Subqueries.eq(Long.valueOf(tags.size()), numberOfMatchingTags)).list();
	}
	
	/**
	 * Extract locationTagIds from the list of LocationTag objects provided.
	 *
	 * @param tags
	 * @return
	 */
	private List<Integer> getLocationTagIds(List<LocationTag> tags) {
		List<Integer> locationTagIds = new ArrayList<>();
		for (LocationTag tag : tags) {
			locationTagIds.add(tag.getLocationTagId());
		}
		return locationTagIds;
	}
}
