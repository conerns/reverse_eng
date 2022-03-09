package org.openmrs.api.impl;

import org.openmrs.ProviderAttribute;
import org.openmrs.ProviderAttributeType;
import org.openmrs.api.ProviderAttributeService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.ProviderAttributeDAO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class ProviderAttributeServiceImpl implements ProviderAttributeService {
	
	private ProviderAttributeDAO dao;

	public void setProviderAttributeDAO(ProviderAttributeDAO dao) {
		this.dao = dao;
	}


	/**
	 * @see org.openmrs.api.ProviderService#getAllProviderAttributeTypes()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ProviderAttributeType> getAllProviderAttributeTypes() {
		return dao.getAllProviderAttributeTypes(true);
	}

	/**
	 * @see org.openmrs.api.ProviderService#getAllProviderAttributeTypes(boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ProviderAttributeType> getAllProviderAttributeTypes(boolean includeRetired) {
		return dao.getAllProviderAttributeTypes(includeRetired);
	}

	/**
	 * @see org.openmrs.api.ProviderService#getProviderAttributeType(java.lang.Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public ProviderAttributeType getProviderAttributeType(Integer providerAttributeTypeId) {
		return dao.getProviderAttributeType(providerAttributeTypeId);
	}

	/**
	 * @see org.openmrs.api.ProviderService#getProviderAttributeTypeByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public ProviderAttributeType getProviderAttributeTypeByUuid(String uuid) {
		return dao.getProviderAttributeTypeByUuid(uuid);
	}

	/**
	 * @see org.openmrs.api.ProviderService#getProviderAttribute(java.lang.Integer)
	 */

	@Override
	@Transactional(readOnly = true)
	public ProviderAttribute getProviderAttribute(Integer providerAttributeID) {
		return dao.getProviderAttribute(providerAttributeID);
	}

	/**
	 * @see org.openmrs.api.ProviderService#getProviderAttributeByUuid(String)
	 */

	@Override
	@Transactional(readOnly = true)
	public ProviderAttribute getProviderAttributeByUuid(String uuid) {
		return dao.getProviderAttributeByUuid(uuid);
	}

	/**
	 * @see org.openmrs.api.ProviderService#saveProviderAttributeType(org.openmrs.ProviderAttributeType)
	 */
	@Override
	public ProviderAttributeType saveProviderAttributeType(ProviderAttributeType providerAttributeType) {
		return dao.saveProviderAttributeType(providerAttributeType);
	}

	/**
	 * @see org.openmrs.api.ProviderService#retireProviderAttributeType(org.openmrs.ProviderAttributeType,
	 *      java.lang.String)
	 */
	@Override
	public ProviderAttributeType retireProviderAttributeType(ProviderAttributeType providerAttributeType, String reason) {
		return Context.getProviderAttributeService().saveProviderAttributeType(providerAttributeType);
	}

	/**
	 * @see org.openmrs.api.ProviderService#unretireProviderAttributeType(org.openmrs.ProviderAttributeType)
	 */
	@Override
	public ProviderAttributeType unretireProviderAttributeType(ProviderAttributeType providerAttributeType) {
		return Context.getProviderAttributeService().saveProviderAttributeType(providerAttributeType);
	}

	/**
	 * @see org.openmrs.api.ProviderService#purgeProviderAttributeType(org.openmrs.ProviderAttributeType)
	 */
	@Override
	public void purgeProviderAttributeType(ProviderAttributeType providerAttributeType) {
		dao.deleteProviderAttributeType(providerAttributeType);
	}
}
