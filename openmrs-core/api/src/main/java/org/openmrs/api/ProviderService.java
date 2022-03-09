/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.ProviderAttribute;
import org.openmrs.ProviderAttributeType;
import org.openmrs.annotation.Authorized;
import org.openmrs.annotation.Handler;
import org.openmrs.util.PrivilegeConstants;

/**
 * This service contains methods relating to providers.
 * 
 * @since 1.9
 */
@Handler(supports = Provider.class)
public interface ProviderService extends OpenmrsService {
	
	/**
	 * Gets all providers. includes retired Provider.This method delegates to the
	 * #getAllProviders(boolean) method
	 * 
	 * @return a list of provider objects.
	 * <strong>Should</strong> get all providers
	 */
	
	@Authorized( { PrivilegeConstants.GET_PROVIDERS })
	public List<Provider> getAllProviders();
	
	/**
	 * Gets all Provider
	 * 
	 * @param includeRetired - whether or not to include retired Provider
	 * <strong>Should</strong> get all providers that are unretired
	 */
	@Authorized( { PrivilegeConstants.GET_PROVIDERS })
	public List<Provider> getAllProviders(boolean includeRetired);
	
	/**
	 * Retires a given Provider
	 * 
	 * @param provider provider to retire
	 * @param reason reason why the provider is retired
	 * <strong>Should</strong> retire a provider
	 */
	@Authorized( { PrivilegeConstants.MANAGE_PROVIDERS })
	public void retireProvider(Provider provider, String reason);
	
	/**
	 * Unretire a given Provider
	 * 
	 * @param provider provider to unretire
	 * <strong>Should</strong> unretire a provider
	 */
	@Authorized( { PrivilegeConstants.MANAGE_PROVIDERS })
	public Provider unretireProvider(Provider provider);
	
	/**
	 * Deletes a given Provider
	 * 
	 * @param provider provider to be deleted
	 * <strong>Should</strong> delete a provider
	 */
	@Authorized( { PrivilegeConstants.PURGE_PROVIDERS })
	public void purgeProvider(Provider provider);
	
	/**
	 * Gets a provider by its provider id
	 * 
	 * @param providerId the provider id
	 * @return the provider by it's id
	 * <strong>Should</strong> get provider given ID
	 */
	@Authorized( { PrivilegeConstants.GET_PROVIDERS })
	public Provider getProvider(Integer providerId);
	
	/**
	 * @param provider
	 * @return the Provider object after saving it in the database
	 * <strong>Should</strong> save a Provider with Person alone
	 * <strong>Should</strong> not save a Provider person being null
	 */
	@Authorized( { PrivilegeConstants.MANAGE_PROVIDERS })
	public Provider saveProvider(Provider provider);
	
	/**
	 * @param uuid
	 * @return the Provider object having the given uuid
	 * <strong>Should</strong> get provider given Uuid
	 */
	@Authorized( { PrivilegeConstants.GET_PROVIDERS })
	public Provider getProviderByUuid(String uuid);
	
	/**
	 * Gets the Providers for the given person.
	 * 
	 * @param person
	 * @return providers or empty collection
	 * <strong>Should</strong> return providers for given person
	 * <strong>Should</strong> fail if person is null
	 */
	@Authorized( { PrivilegeConstants.GET_PROVIDERS })
	public Collection<Provider> getProvidersByPerson(Person person);
	
	/**
	 * Gets the Providers for the given person including or excluding retired.
	 * 
	 * @param person
	 * @param includeRetired
	 * @return providers or empty collection
	 * <strong>Should</strong> return all providers by person including retired if includeRetired is true
	 * <strong>Should</strong> return all providers by person and exclude retired if includeRetired is false
	 * <strong>Should</strong> fail if person is null
	 * @since 1.10, 1.9.1
	 */
	@Authorized( { PrivilegeConstants.GET_PROVIDERS })
	public Collection<Provider> getProvidersByPerson(Person person, boolean includeRetired);
	
	/**
	 * @param query
	 * @param start
	 * @param length
	 * @param attributes
	 * @param includeRetired
	 * @return the list of Providers given the query , current page and page length
	 * <strong>Should</strong> fetch provider with given identifier with case in sensitive
	 * <strong>Should</strong> fetch provider with given name with case in sensitive
	 * <strong>Should</strong> fetch provider by matching query string with any unVoided PersonName's Given Name
	 * <strong>Should</strong> fetch provider by matching query string with any unVoided PersonName's middleName
	 * <strong>Should</strong> fetch provider by matching query string with any unVoided Person's familyName
	 * <strong>Should</strong> not fetch provider if the query string matches with any voided Person name for that
	 *         Provider
	 * <strong>Should</strong> get all visits with given attribute values
	 * <strong>Should</strong> not find any visits if none have given attribute values
	 * <strong>Should</strong> return all providers if query is empty
	 * <strong>Should</strong> find provider by identifier
	 */
	@Authorized( { PrivilegeConstants.GET_PROVIDERS })
	public List<Provider> getProviders(String query, Integer start, Integer length,
	        Map<ProviderAttributeType, Object> attributes, boolean includeRetired);
	
	/**
	 * @param query
	 * @param start
	 * @param length
	 * @param attributes
	 * @return the list of Providers given the query , current page and page length
	 * <strong>Should</strong> fetch provider with given identifier with case in sensitive
	 * <strong>Should</strong> fetch provider with given name with case in sensitive
	 * <strong>Should</strong> fetch provider by matching query string with any unVoided PersonName's Given Name
	 * <strong>Should</strong> fetch provider by matching query string with any unVoided PersonName's middleName
	 * <strong>Should</strong> fetch provider by matching query string with any unVoided Person's familyName
	 * <strong>Should</strong> not fetch provider if the query string matches with any voided Person name for that
	 *         Provider
	 * <strong>Should</strong> get all visits with given attribute values
	 * <strong>Should</strong> not find any visits if none have given attribute values
	 * <strong>Should</strong> return all providers if query is empty
	 * <strong>Should</strong> return retired providers
	 */
	@Authorized( { PrivilegeConstants.GET_PROVIDERS })
	public List<Provider> getProviders(String query, Integer start, Integer length,
	        Map<ProviderAttributeType, Object> attributes);
	
	/**
	 * @param query
	 * @return Count-Integer
	 * <strong>Should</strong> exclude retired providers
	 */
	@Authorized( { PrivilegeConstants.GET_PROVIDERS })
	public Integer getCountOfProviders(String query);
	
	/**
	 * Gets the count of providers with a person name or identifier or name that matches the
	 * specified query
	 * 
	 * @param query the text to match
	 * @param includeRetired specifies whether retired providers should be include or not
	 * @return Count-Integer
	 * <strong>Should</strong> fetch number of provider matching given query
	 * <strong>Should</strong> include retired providers if includeRetired is set to true
	 * @since 1.9.4
	 */
	@Authorized( { PrivilegeConstants.GET_PROVIDERS })
	public Integer getCountOfProviders(String query, boolean includeRetired);
	
	/**
	 * Checks if the identifier for the specified provider is unique
	 * 
	 * @param provider the provider whose identifier to check
	 * @return true if the identifier is unique otherwise false
	 * @throws APIException
	 * <strong>Should</strong> return false if the identifier is a duplicate
	 * <strong>Should</strong> return true if the identifier is null
	 * <strong>Should</strong> return true if the identifier is a blank string
	 */
	@Authorized( { PrivilegeConstants.GET_PROVIDERS })
	public boolean isProviderIdentifierUnique(Provider provider) throws APIException;
	
	/**
	 * Gets a provider with a matching identifier, this method performs a case insensitive search
	 * 
	 * @param identifier the identifier to match against
	 * @return a {@link Provider}
	 * <strong>Should</strong> get a provider matching the specified identifier ignoring case
	 */
	@Authorized( { PrivilegeConstants.GET_PROVIDERS })
	public Provider getProviderByIdentifier(String identifier);
	
	/**
	 * Gets the unknown provider account, i.e. the provider account that matches the uuid specified
	 * as the value for the global property
	 * {@link org.openmrs.util.OpenmrsConstants#GP_UNKNOWN_PROVIDER_UUID}
	 * 
	 * @return a {@link Provider}
	 * @since 1.10
	 * <strong>Should</strong> get the unknown provider account
	 */
	@Authorized( { PrivilegeConstants.GET_PROVIDERS })
	public Provider getUnknownProvider();
}
