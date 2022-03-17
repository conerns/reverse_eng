package org.openmrs.api;

import org.openmrs.CareSetting;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.db.CareSettingDAO;
import org.openmrs.util.PrivilegeConstants;

import java.util.List;

public interface CareSettingService extends OpenmrsService{
	
	public void setCareSettingDAO(CareSettingDAO dao);


	/**
	 * Retrieve care setting
	 *
	 * @param careSettingId
	 * @return the care setting
	 * @since 1.10
	 */
	@Authorized(PrivilegeConstants.GET_CARE_SETTINGS)
	public CareSetting getCareSetting(Integer careSettingId);

	/**
	 * Gets the CareSetting with the specified uuid
	 *
	 * @param uuid the uuid to match on
	 * @return CareSetting
	 * <strong>Should</strong> return the care setting with the specified uuid
	 */
	@Authorized(PrivilegeConstants.GET_CARE_SETTINGS)
	public CareSetting getCareSettingByUuid(String uuid);

	/**
	 * Gets the CareSetting with the specified name
	 *
	 * @param name the name to match on
	 * @return CareSetting
	 * <strong>Should</strong> return the care setting with the specified name
	 */
	@Authorized(PrivilegeConstants.GET_CARE_SETTINGS)
	public CareSetting getCareSettingByName(String name);

	/**
	 * Gets all non retired CareSettings if includeRetired is set to true otherwise retired ones are
	 * included too
	 *
	 * @param includeRetired specifies whether retired care settings should be returned or not
	 * @return A List of CareSettings
	 * <strong>Should</strong> return only un retired care settings if includeRetired is set to false
	 * <strong>Should</strong> return retired care settings if includeRetired is set to true
	 */
	@Authorized(PrivilegeConstants.GET_CARE_SETTINGS)
	public List<CareSetting> getCareSettings(boolean includeRetired);
}
