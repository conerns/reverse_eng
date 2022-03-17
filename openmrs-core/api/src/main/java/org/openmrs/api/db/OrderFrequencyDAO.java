package org.openmrs.api.db;

import org.openmrs.Concept;
import org.openmrs.OrderFrequency;

import java.util.List;
import java.util.Locale;

public interface OrderFrequencyDAO {
	
	/**
	 * @see org.openmrs.api.OrderService#getOrderFrequency
	 */
	public OrderFrequency getOrderFrequency(Integer orderFrequencyId);

	/**
	 * @see org.openmrs.api.OrderService#getOrderFrequencyByUuid
	 */
	public OrderFrequency getOrderFrequencyByUuid(String uuid);

	/**
	 * @see org.openmrs.api.OrderService#getOrderFrequencies(boolean)
	 */
	List<OrderFrequency> getOrderFrequencies(boolean includeRetired);

	/**
	 * @see org.openmrs.api.OrderService#getOrderFrequencies(String, java.util.Locale, boolean, boolean)
	 */
	public List<OrderFrequency> getOrderFrequencies(String searchPhrase, Locale locale, boolean exactLocale,
													boolean includeRetired);

	/**
	 * @see org.openmrs.api.OrderService#saveOrderFrequency(org.openmrs.OrderFrequency)
	 */
	public OrderFrequency saveOrderFrequency(OrderFrequency orderFrequency);

	/**
	 * @see org.openmrs.api.OrderService#purgeOrderFrequency(org.openmrs.OrderFrequency)
	 */
	public void purgeOrderFrequency(OrderFrequency orderFrequency);

	/**
	 * Checks if an order frequency is being referenced by any order
	 *
	 * @param orderFrequency the order frequency
	 * @return true if in use, else false
	 */
	public boolean isOrderFrequencyInUse(OrderFrequency orderFrequency);

	/**
	 * @see org.openmrs.api.OrderService#getOrderFrequencyByConcept
	 */
	public OrderFrequency getOrderFrequencyByConcept(Concept concept);
	
}
