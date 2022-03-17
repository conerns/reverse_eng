/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.api.db;

import java.util.Date;
import java.util.List;
import org.openmrs.CareSetting;
import org.openmrs.Order;
import org.openmrs.Encounter;
import org.openmrs.Concept;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.parameter.OrderSearchCriteria;

/**
 * Order-related database functions
 * <p>
 * This class should never be used directly. It should only be used through the
 * {@link org.openmrs.api.OrderService}
 * 
 * @see org.openmrs.api.OrderService
 */
public interface OrderDAO {
	
	/**
	 * @see org.openmrs.api.OrderService#saveOrder(org.openmrs.Order, org.openmrs.api.OrderContext)
	 */
	public Order saveOrder(Order order) throws DAOException;
	
	/**
	 * @see org.openmrs.api.OrderService#purgeOrder(Order)
	 */
	public void deleteOrder(Order order) throws DAOException;
	
	/**
	 * @see org.openmrs.api.OrderService#getOrder(Integer)
	 */
	public Order getOrder(Integer orderId) throws DAOException;
	
	/**
	 * This searches for orders given the parameters. Most arguments are optional (nullable). If
	 * multiple arguments are given, the returned orders will match on all arguments. The orders are
	 * sorted by startDate with the latest coming first
	 * 
	 * @param orderType The type of Order to get
	 * @param patients The patients to get orders for
	 * @param concepts The concepts in order.getConcept to get orders for
	 * @param orderers The orderers to match on
	 * @param encounters The encounters that the orders are assigned to
	 * @return list of Orders matching the parameters
	 */
	public List<Order> getOrders(OrderType orderType, List<Patient> patients, List<Concept> concepts, List<User> orderers,
	        List<Encounter> encounters);
	
	/**
	 * @see org.openmrs.api.OrderService#getOrders(org.openmrs.Patient, org.openmrs.CareSetting,
	 *      org.openmrs.OrderType, boolean)
	 */
	public List<Order> getOrders(Patient patient, CareSetting careSetting, List<OrderType> orderTypes,
	        boolean includeVoided, boolean includeDiscontinuationOrders);

	/**
	 * @see org.openmrs.api.OrderService#getOrders(OrderSearchCriteria)
	 */
	public List<Order> getOrders(OrderSearchCriteria orderSearchCriteria);
	
	/**
	 * @param uuid
	 * @return order or null
	 */
	public Order getOrderByUuid(String uuid);
	
	/**
	 * Delete Obs that references an order
	 */
	public void deleteObsThatReference(Order order);
	
	/**
	 * @see org.openmrs.api.OrderService#getOrderByOrderNumber(java.lang.String)
	 */
	public Order getOrderByOrderNumber(String orderNumber);
	
	/**
	 * Gets the next available order number seed
	 * 
	 * @return the order number seed
	 */
	public Long getNextOrderNumberSeedSequenceValue();
	
	/**
	 * @see org.openmrs.api.OrderService#getActiveOrders(org.openmrs.Patient, org.openmrs.OrderType,
	 *      org.openmrs.CareSetting, java.util.Date)
	 */
	public List<Order> getActiveOrders(Patient patient, List<OrderType> orderTypes, CareSetting careSetting, Date asOfDate);
	
	
	/**
	 * @see org.openmrs.api.OrderService#getDiscontinuationOrder(Order)
	 */
	public Order getDiscontinuationOrder(Order order);
	
	/**
	 * @see org.openmrs.api.OrderService#getRevisionOrder(org.openmrs.Order)
	 */
	public Order getRevisionOrder(Order order) throws APIException;
	
	/**
	 * Get the fresh order from the database
	 *
	 * @param order the order to get from the database
	 * @param isOrderADrugOrder is the order a previous order
	 * @return a list of orders from the database
	 */
	public List<Object[]> getOrderFromDatabase(Order order, boolean isOrderADrugOrder) throws APIException;


}
