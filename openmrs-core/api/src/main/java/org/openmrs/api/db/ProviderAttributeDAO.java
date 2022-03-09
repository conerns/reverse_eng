package org.openmrs.api.db;

import org.openmrs.ProviderAttribute;
import org.openmrs.ProviderAttributeType;
import org.openmrs.api.ProviderService;

import java.util.List;

public interface ProviderAttributeDAO {

	List<ProviderAttributeType> getAllProviderAttributeTypes(boolean includeRetired);

	/**
	 * @see ProviderService#getProviderAttributeType(Integer)
	 */
	ProviderAttributeType getProviderAttributeType(Integer providerAttributeTypeId);

	/**
	 * @see ProviderService#getProviderAttributeTypeByUuid(String)
	 */
	ProviderAttributeType getProviderAttributeTypeByUuid(String uuid);

	/**
	 * @see ProviderService#saveProviderAttributeType(ProviderAttributeType)
	 */
	ProviderAttributeType saveProviderAttributeType(ProviderAttributeType providerAttributeType);

	/**
	 * @see ProviderService#purgeProviderAttributeType(ProviderAttributeType)
	 */
	void deleteProviderAttributeType(ProviderAttributeType providerAttributeType);

	/**
	 * @see ProviderService#getProviderAttribute(Integer)
	 */

	ProviderAttribute getProviderAttribute(Integer providerAttributeID);

	/**
	 * @see ProviderService#getProviderAttributeByUuid(String)
	 */
	ProviderAttribute getProviderAttributeByUuid(String uuid);
	
}
