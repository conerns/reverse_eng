package org.openmrs.api;

import org.openmrs.Concept;
import org.openmrs.OrderFrequency;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.db.OrderFrequencyDAO;
import org.openmrs.util.PrivilegeConstants;

import java.util.List;
import java.util.Locale;

public interface OrderFrequencyService extends OpenmrsService{
	
	void setOrderFrequencyDAO(OrderFrequencyDAO orderFrequencyDAO);


	/**
	 * Creates or updates the given order frequency in the database
	 *
	 * @param orderFrequency the order frequency to save
	 * @return the order frequency created/saved
	 * @since 1.10
	 * <strong>Should</strong> add a new order frequency to the database
	 * <strong>Should</strong> edit an existing order frequency that is not in use
	 * <strong>Should</strong> not allow editing an existing order frequency that is in use
	 */
	@Authorized(PrivilegeConstants.MANAGE_ORDER_FREQUENCIES)
	public OrderFrequency saveOrderFrequency(OrderFrequency orderFrequency) throws APIException;

	/**
	 * Retires the given order frequency in the database
	 *
	 * @param orderFrequency the order frequency to retire
	 * @param reason the retire reason
	 * @return the retired order frequency
	 * @since 1.10
	 * <strong>Should</strong> retire given order frequency
	 */
	@Authorized(PrivilegeConstants.MANAGE_ORDER_FREQUENCIES)
	public OrderFrequency retireOrderFrequency(OrderFrequency orderFrequency, String reason);

	/**
	 * Restores an order frequency that was previously retired in the database
	 *
	 * @param orderFrequency the order frequency to unretire
	 * @return the unretired order frequency
	 * @since 1.10
	 * <strong>Should</strong> unretire given order frequency
	 */
	@Authorized(PrivilegeConstants.MANAGE_ORDER_FREQUENCIES)
	public OrderFrequency unretireOrderFrequency(OrderFrequency orderFrequency);

	/**
	 * Completely removes an order frequency from the database
	 *
	 * @param orderFrequency the order frequency to purge
	 * @since 1.10
	 * <strong>Should</strong> delete given order frequency
	 * <strong>Should</strong> not allow deleting an order frequency that is in use
	 */
	@Authorized(PrivilegeConstants.PURGE_ORDER_FREQUENCIES)
	public void purgeOrderFrequency(OrderFrequency orderFrequency) throws APIException;


	/**
	 * Gets OrderFrequency that matches the specified orderFrequencyId
	 *
	 * @param orderFrequencyId the id to match against
	 * @return OrderFrequency
	 * @since 1.10
	 * <strong>Should</strong> return the order frequency that matches the specified id
	 */
	@Authorized(PrivilegeConstants.GET_ORDER_FREQUENCIES)
	public OrderFrequency getOrderFrequency(Integer orderFrequencyId);

	/**
	 * Gets OrderFrequency that matches the specified uuid
	 *
	 * @param uuid the uuid to match against
	 * @return OrderFrequency
	 * @since 1.10
	 * <strong>Should</strong> return the order frequency that matches the specified uuid
	 */
	@Authorized(PrivilegeConstants.GET_ORDER_FREQUENCIES)
	public OrderFrequency getOrderFrequencyByUuid(String uuid);

	/**
	 * Gets an OrderFrequency that matches the specified concept
	 *
	 * @param concept the concept to match against
	 * @return OrderFrequency
	 * @since 1.10
	 * <strong>Should</strong> return the order frequency that matches the specified concept
	 */
	@Authorized(PrivilegeConstants.GET_ORDER_FREQUENCIES)
	public OrderFrequency getOrderFrequencyByConcept(Concept concept);

	/**
	 * Gets all order frequencies
	 *
	 * @return List&lt;OrderFrequency&gt;
	 * @since 1.10
	 * @param includeRetired specifies whether retired ones should be included or not
	 * <strong>Should</strong> return only non retired order frequencies if includeRetired is set to false
	 * <strong>Should</strong> return all the order frequencies if includeRetired is set to true
	 */
	@Authorized(PrivilegeConstants.GET_ORDER_FREQUENCIES)
	public List<OrderFrequency> getOrderFrequencies(boolean includeRetired);

	/**
	 * Gets all non retired order frequencies associated to concepts that match the specified search
	 * phrase
	 *
	 * @param searchPhrase The string to match on
	 * @param locale The locale to match on when searching in associated concept names
	 * @param exactLocale If false then order frequencies associated to concepts with names in a
	 *            broader locale will be matched e.g in case en_GB is passed in then en will be
	 *            matched
	 * @param includeRetired Specifies if retired order frequencies that match should be included or
	 *            not
	 * @return List&lt;OrderFrequency&gt;
	 * @since 1.10
	 * <strong>Should</strong> get non retired frequencies with names matching the phrase if includeRetired is false
	 * <strong>Should</strong> include retired frequencies if includeRetired is set to true
	 * <strong>Should</strong> get frequencies with names that match the phrase and locales if exact locale is false
	 * <strong>Should</strong> get frequencies with names that match the phrase and locale if exact locale is true
	 * <strong>Should</strong> return unique frequencies
	 * <strong>Should</strong> reject a null search phrase
	 */
	@Authorized(PrivilegeConstants.GET_ORDER_FREQUENCIES)
	public List<OrderFrequency> getOrderFrequencies(String searchPhrase, Locale locale, boolean exactLocale,
													boolean includeRetired);
}
