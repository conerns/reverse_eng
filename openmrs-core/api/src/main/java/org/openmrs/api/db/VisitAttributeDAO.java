package org.openmrs.api.db;

import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;
import org.openmrs.api.VisitService;

import java.util.List;

public interface VisitAttributeDAO {

	/**
	 * @see VisitService#getAllVisitAttributeTypes()
	 */
	List<VisitAttributeType> getAllVisitAttributeTypes();

	/**
	 * @see VisitService#getVisitAttributeType(Integer)
	 */
	VisitAttributeType getVisitAttributeType(Integer id);

	/**
	 * @see VisitService#getVisitAttributeTypeByUuid(String)
	 */
	VisitAttributeType getVisitAttributeTypeByUuid(String uuid);

	/**
	 * @see VisitService#saveVisitAttributeType(VisitAttributeType)
	 */
	VisitAttributeType saveVisitAttributeType(VisitAttributeType visitAttributeType);

	/**
	 * Completely removes a visit attribute type from the database
	 *
	 * @param visitAttributeType
	 */
	void deleteVisitAttributeType(VisitAttributeType visitAttributeType);

	/**
	 * @see VisitService#getVisitAttributeByUuid(String)
	 */
	VisitAttribute getVisitAttributeByUuid(String uuid);
	
}
