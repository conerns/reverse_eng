package org.openmrs.api;

import org.openmrs.VisitType;
import org.openmrs.annotation.Authorized;
import org.openmrs.util.PrivilegeConstants;

import java.util.List;

public interface VisitTypeService {

	/**
	 * Gets all visit types.
	 *
	 * @return a list of visit type objects.
	 * <strong>Should</strong> get all visit types
	 */
	@Authorized( { PrivilegeConstants.GET_VISIT_TYPES })
	List<VisitType> getAllVisitTypes();

	public List<VisitType> getVisitTypesToStop();

	/**
	 * Get all visit types based on includeRetired flag
	 *
	 * @param includeRetired
	 * @return List of all visit types
	 * @since 1.9
	 * <strong>Should</strong> get all visit types based on include retired flag.
	 */
	@Authorized( { PrivilegeConstants.MANAGE_VISIT_TYPES })
	public List<VisitType> getAllVisitTypes(boolean includeRetired);

	/**
	 * Gets a visit type by its visit type id.
	 *
	 * @param visitTypeId the visit type id.
	 * @return the visit type object found with the given id, else null.
	 * <strong>Should</strong> get correct visit type
	 */
	@Authorized( { PrivilegeConstants.GET_VISIT_TYPES })
	VisitType getVisitType(Integer visitTypeId);

	/**
	 * Gets a visit type by its UUID.
	 *
	 * @param uuid the visit type UUID.
	 * @return the visit type object found with the given uuid, else null.
	 * <strong>Should</strong> get correct visit type
	 */
	@Authorized( { PrivilegeConstants.GET_VISIT_TYPES })
	VisitType getVisitTypeByUuid(String uuid);

	/**
	 * Gets all visit types whose names are similar to or contain the given search phrase.
	 *
	 * @param fuzzySearchPhrase the search phrase to use.
	 * @return a list of all visit types with names similar to or containing the given phrase
	 * <strong>Should</strong> get correct visit types
	 */
	@Authorized( { PrivilegeConstants.GET_VISIT_TYPES })
	List<VisitType> getVisitTypes(String fuzzySearchPhrase);

	/**
	 * Creates or updates the given visit type in the database.
	 *
	 * @param visitType the visit type to create or update.
	 * @return the created or updated visit type.
	 * <strong>Should</strong> save new visit type
	 * <strong>Should</strong> save edited visit type
	 * <strong>Should</strong> throw error when name is null
	 * <strong>Should</strong> throw error when name is empty string
	 */
	@Authorized( { PrivilegeConstants.MANAGE_VISIT_TYPES })
	VisitType saveVisitType(VisitType visitType) throws APIException;

	/**
	 * Retires a given visit type.
	 *
	 * @param visitType the visit type to retire.
	 * @param reason the reason why the visit type is retired.
	 * @return the visit type that has been retired.
	 * <strong>Should</strong> retire given visit type
	 */
	@Authorized( { PrivilegeConstants.MANAGE_VISIT_TYPES })
	VisitType retireVisitType(VisitType visitType, String reason);

	/**
	 * Unretires a visit type.
	 *
	 * @param visitType the visit type to unretire.
	 * @return the unretired visit type
	 * <strong>Should</strong> unretire given visit type
	 */
	@Authorized( { PrivilegeConstants.MANAGE_VISIT_TYPES })
	VisitType unretireVisitType(VisitType visitType);

	/**
	 * Completely removes a visit type from the database. This is not reversible.
	 *
	 * @param visitType the visit type to delete from the database.
	 * <strong>Should</strong> delete given visit type
	 */
	@Authorized( { PrivilegeConstants.MANAGE_VISIT_TYPES })
	void purgeVisitType(VisitType visitType);
}
