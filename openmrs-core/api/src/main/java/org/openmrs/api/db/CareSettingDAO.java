package org.openmrs.api.db;

import org.openmrs.CareSetting;

import java.util.List;

public interface CareSettingDAO {

	/**
	 * Get care setting by type
	 *
	 * @param careSettingId
	 * @return the care setting type
	 */
	public CareSetting getCareSetting(Integer careSettingId);

	/**
	 * @see org.openmrs.api.OrderService#getCareSettingByUuid(String)
	 */
	public CareSetting getCareSettingByUuid(String uuid);

	/**
	 * @see org.openmrs.api.OrderService#getCareSettingByName(String)
	 */
	public CareSetting getCareSettingByName(String name);

	/**
	 * @see org.openmrs.api.OrderService#getCareSettings(boolean)
	 */
	public List<CareSetting> getCareSettings(boolean includeRetired);
}
