package org.openmrs.api.impl;

import org.openmrs.CareSetting;
import org.openmrs.api.CareSettingService;
import org.openmrs.api.db.CareSettingDAO;

import java.util.List;

public class CareSettingServiceImpl implements CareSettingService {
	
	protected CareSettingDAO dao;

	@Override
	public void setCareSettingDAO(CareSettingDAO careSettingDAO) {
		dao = careSettingDAO;
	}


	/**
	 * @see org.openmrs.api.OrderService#getCareSetting(Integer)
	 */
	@Override
	public CareSetting getCareSetting(Integer careSettingId) {
		return dao.getCareSetting(careSettingId);
	}

	/**
	 * @see org.openmrs.api.OrderService#getCareSettingByUuid(String)
	 */
	@Override
	public CareSetting getCareSettingByUuid(String uuid) {
		return dao.getCareSettingByUuid(uuid);
	}

	/**
	 * @see org.openmrs.api.OrderService#getCareSettingByName(String)
	 */
	@Override
	public CareSetting getCareSettingByName(String name) {
		return dao.getCareSettingByName(name);
	}

	/**
	 * @see org.openmrs.api.OrderService#getCareSettings(boolean)
	 */
	@Override
	public List<CareSetting> getCareSettings(boolean includeRetired) {
		return dao.getCareSettings(includeRetired);
	}

	@Override
	public void onStartup() {
		
	}

	@Override
	public void onShutdown() {

	}
}
