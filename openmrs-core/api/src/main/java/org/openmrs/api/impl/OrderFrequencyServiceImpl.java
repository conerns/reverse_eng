package org.openmrs.api.impl;

import org.openmrs.Concept;
import org.openmrs.OrderFrequency;
import org.openmrs.api.APIException;
import org.openmrs.api.CannotDeleteObjectInUseException;
import org.openmrs.api.OrderFrequencyService;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.OrderFrequencyDAO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

public class OrderFrequencyServiceImpl implements OrderFrequencyService {
	
	protected OrderFrequencyDAO dao;

	@Override
	public void setOrderFrequencyDAO(OrderFrequencyDAO orderFrequencyDAO) {
		dao = orderFrequencyDAO;
	}


	/**
	 * @see OrderService#getOrderFrequency(Integer)
	 */
	@Override
	public OrderFrequency getOrderFrequency(Integer orderFrequencyId) {
		return dao.getOrderFrequency(orderFrequencyId);
	}

	/**
	 * @see OrderService#getOrderFrequencyByUuid(String)
	 */
	@Override
	public OrderFrequency getOrderFrequencyByUuid(String uuid) {
		return dao.getOrderFrequencyByUuid(uuid);
	}

	/**
	 * @see OrderService#getOrderFrequencies(boolean)
	 */
	@Override
	public List<OrderFrequency> getOrderFrequencies(boolean includeRetired) {
		return dao.getOrderFrequencies(includeRetired);
	}

	/**
	 * @see OrderService#getOrderFrequencies(String, java.util.Locale, boolean, boolean)
	 */
	@Override
	public List<OrderFrequency> getOrderFrequencies(String searchPhrase, Locale locale, boolean exactLocale,
													boolean includeRetired) {
		if (searchPhrase == null) {
			throw new IllegalArgumentException("searchPhrase is required");
		}
		return dao.getOrderFrequencies(searchPhrase, locale, exactLocale, includeRetired);
	}


	/**
	 * @see org.openmrs.api.OrderService#saveOrderFrequency(org.openmrs.OrderFrequency)
	 */
	@Override
	public OrderFrequency saveOrderFrequency(OrderFrequency orderFrequency) throws APIException {
		return dao.saveOrderFrequency(orderFrequency);
	}

	/**
	 * @see org.openmrs.api.OrderService#retireOrderFrequency(org.openmrs.OrderFrequency,
	 *      java.lang.String)
	 */
	@Override
	public OrderFrequency retireOrderFrequency(OrderFrequency orderFrequency, String reason) {
		return dao.saveOrderFrequency(orderFrequency);
	}

	/**
	 * @see org.openmrs.api.OrderService#unretireOrderFrequency(org.openmrs.OrderFrequency)
	 */
	@Override
	public OrderFrequency unretireOrderFrequency(OrderFrequency orderFrequency) {
		return Context.getOrderFrequencyService().saveOrderFrequency(orderFrequency);
	}

	/**
	 * @see org.openmrs.api.OrderService#purgeOrderFrequency(org.openmrs.OrderFrequency)
	 */
	@Override
	public void purgeOrderFrequency(OrderFrequency orderFrequency) {

		if (dao.isOrderFrequencyInUse(orderFrequency)) {
			throw new CannotDeleteObjectInUseException("Order.frequency.cannot.delete", (Object[]) null);
		}

		dao.purgeOrderFrequency(orderFrequency);
	}

	/**
	 * @see org.openmrs.api.OrderService#getOrderFrequencyByConcept(org.openmrs.Concept)
	 */
	@Override
	@Transactional(readOnly = true)
	public OrderFrequency getOrderFrequencyByConcept(Concept concept) {
		return dao.getOrderFrequencyByConcept(concept);
	}

	@Override
	public void onStartup() {
		
	}

	@Override
	public void onShutdown() {

	}
}
