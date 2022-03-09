package org.openmrs.api;

import org.openmrs.ProviderAttribute;
import org.openmrs.ProviderAttributeType;

import java.util.List;

public interface ProviderAttributeService {


	/**
	 * Gets all provider attribute types including retired provider attribute types. This method
	 * delegates to the #getAllProviderAttributeTypes(boolean) method
	 *
	 * @return a list of provider attribute type objects.
	 * <strong>Should</strong> get all provider attribute types including retired by default
	 */
	public List<ProviderAttributeType> getAllProviderAttributeTypes();

	/**
	 * Gets all provider attribute types optionally including retired provider attribute types.
	 *
	 * @param includeRetired boolean value to indicate whether to include retired records or not
	 * @return a list of provider attribute type objects.
	 * <strong>Should</strong> get all provider attribute types excluding retired
	 * <strong>Should</strong> get all provider attribute types including retired
	 */
	public List<ProviderAttributeType> getAllProviderAttributeTypes(boolean includeRetired);

	/**
	 * Gets a provider attribute type by it's id
	 *
	 * @param providerAttributeTypeId the provider attribute type id
	 * @return the provider type attribute by it's id
	 * <strong>Should</strong> get provider attribute type for the given id
	 */
	public ProviderAttributeType getProviderAttributeType(Integer providerAttributeTypeId);

	/**
	 * Get a provider attribute type by it's uuid
	 *
	 * @param uuid the uuid of the provider attribute type
	 * @return the provider attribute type for the given uuid
	 * <strong>Should</strong> get the provider attribute type by it's uuid
	 */
	public ProviderAttributeType getProviderAttributeTypeByUuid(String uuid);

	/**
	 * Get a provider attribute by it's providerAttributeID
	 *
	 * @param providerAttributeID the provider attribute ID of the providerAttribute
	 * @return the provider attribute for the given providerAttributeID
	 * <strong>Should</strong> get the provider attribute by it's providerAttributeID
	 */
	public ProviderAttribute getProviderAttribute(Integer providerAttributeID);

	/**
	 * Get a provider attribute by it's providerAttributeUuid
	 *
	 * @param uuid the provider attribute uuid of the providerAttribute
	 * @return the provider attribute for the given providerAttributeUuid
	 * <strong>Should</strong> get the provider attribute by it's providerAttributeUuid
	 */
	public ProviderAttribute getProviderAttributeByUuid(String uuid);

	/**
	 * Save the provider attribute type
	 *
	 * @param providerAttributeType the provider attribute type to be saved
	 * @return the saved provider attribute type
	 * <strong>Should</strong> save the provider attribute type
	 */
	public ProviderAttributeType saveProviderAttributeType(ProviderAttributeType providerAttributeType);

	/**
	 * Retire a provider attribute type
	 *
	 * @param providerAttributeType the provider attribute type to be retired
	 * @param reason for retiring the provider attribute type
	 * @return the retired provider attribute type
	 * <strong>Should</strong> retire provider type attribute
	 */
	public ProviderAttributeType retireProviderAttributeType(ProviderAttributeType providerAttributeType, String reason);

	/**
	 * Un-Retire a provider attribute type
	 *
	 * @param providerAttributeType the provider type attribute to unretire
	 * @return the unretire provider attribute type
	 * <strong>Should</strong> unretire a provider attribute type
	 */
	public ProviderAttributeType unretireProviderAttributeType(ProviderAttributeType providerAttributeType);

	/**
	 * Deletes a provider attribute type
	 *
	 * @param providerAttributeType provider attribute type to be deleted
	 * <strong>Should</strong> delete a provider attribute type
	 */
	public void purgeProviderAttributeType(ProviderAttributeType providerAttributeType);
	
}
