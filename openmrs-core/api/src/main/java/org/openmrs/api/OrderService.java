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

import org.openmrs.OrderAttribute;
import org.openmrs.OrderAttributeType;
import org.openmrs.Provider;
import org.openmrs.OrderGroup;
import org.openmrs.OrderGroupAttribute;
import org.openmrs.OrderGroupAttributeType;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.CareSetting;
import org.openmrs.OrderType;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.Encounter;
import org.openmrs.OrderFrequency;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.db.OrderDAO;
import org.openmrs.parameter.OrderSearchCriteria;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.PrivilegeConstants;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Contains methods pertaining to creating/deleting/voiding Orders
 */
public interface OrderService extends OpenmrsService {
	
	public static final String PARALLEL_ORDERS = "PARALLEL_ORDERS";
	
	/**
	 * Setter for the Order data access object. The dao is used for saving and getting orders
	 * to/from the database
	 * 
	 * @param dao The data access object to use
	 */
	public void setOrderDAO(OrderDAO dao);
	
	/**
	 * Save or update the given <code>order</code> in the database. If the OrderType for the order
	 * is not specified, then it will be set to the one set on the OrderContext if any, if none
	 * exists on the orderContext, then it will be set to the one associated to the ConceptClass of
	 * the ordered concept otherwise the save fails. If the CareSetting field of the order is not
	 * specified then it will default to the one set on the passed in OrderContext if any otherwise
	 * the save fails.
	 * 
	 * @param order the Order to save
	 * @param orderContext the OrderContext object
	 * @return the Order that was saved
	 * @throws APIException
	 * <strong>Should</strong> not save order if order doesnt validate
	 * <strong>Should</strong> discontinue existing active order if new order being saved with action to discontinue
	 * <strong>Should</strong> pass if the existing drug order matches the concept and drug of the DC order
	 * <strong>Should</strong> fail if the existing drug order matches the concept and not drug of the DC order
	 * <strong>Should</strong> discontinue previousOrder if it is not already discontinued
	 * <strong>Should</strong> fail if concept in previous order does not match this concept
	 * <strong>Should</strong> not allow editing an existing order
	 * <strong>Should</strong> not allow revising a voided order
	 * <strong>Should</strong> not allow revising a stopped order
	 * <strong>Should</strong> not allow revising an expired order
	 * <strong>Should</strong> not allow revising an order with no previous order
	 * <strong>Should</strong> save a revised order
	 * <strong>Should</strong> save a revised order for a scheduled order which is not started
	 * <strong>Should</strong> set order number specified in the context if specified
	 * <strong>Should</strong> set the order number returned by the configured generator
	 * <strong>Should</strong> set order type if null but mapped to the concept class
	 * <strong>Should</strong> fail if order type is null and not mapped to the concept class
	 * <strong>Should</strong> default to care setting and order type defined in the order context if null
	 * <strong>Should</strong> not allow changing the patient of the previous order when revising an order
	 * <strong>Should</strong> not allow changing the careSetting of the previous order when revising an order
	 * <strong>Should</strong> not allow changing the concept of the previous order when revising an order
	 * <strong>Should</strong> not allow changing the drug of the previous drug order when revising an order
	 * <strong>Should</strong> fail if concept in previous order does not match that of the revised order
	 * <strong>Should</strong> fail if the existing drug order matches the concept and not drug of the revised order
	 * <strong>Should</strong> fail if the order type of the previous order does not match
	 * <strong>Should</strong> fail if the java type of the previous order does not match
	 * <strong>Should</strong> fail if the careSetting of the previous order does not match
	 * <strong>Should</strong> set concept for drug orders if null
	 * <strong>Should</strong> pass for a discontinuation order with no previous order
	 * <strong>Should</strong> fail if an active drug order for the same concept and care setting exists
	 * <strong>Should</strong> pass if an active test order for the same concept and care setting exists
	 * <strong>Should</strong> pass if an active order for the same concept exists in a different care setting
	 * <strong>Should</strong> set Order type of Drug Order to drug order if not set and concept not mapped
	 * <strong>Should</strong> set Order type of Test Order to test order if not set and concept not mapped
	 * <strong>Should</strong> throw AmbiguousOrderException if an active drug order for the same drug formulation
	 *         exists
	 * <strong>Should</strong> pass if an active order for the same concept exists in a different care setting
	 * <strong>Should</strong> fail for revision order if an active drug order for the same concept and care
	 *         settings exists
	 * <strong>Should</strong> pass for revision order if an active test order for the same concept and care
	 *         settings exists
	 * <strong>Should</strong> roll the autoExpireDate to the end of the day if it has no time component
	 * <strong>Should</strong> not change the autoExpireDate if it has a time component
	 * <strong>Should</strong> throw AmbiguousOrderException if disconnecting multiple active orders for the given
	 *         concept
	 * <strong>Should</strong> throw AmbiguousOrderException if disconnecting multiple active drug orders with the
	 *         same drug
	 */
	@Authorized({ PrivilegeConstants.EDIT_ORDERS, PrivilegeConstants.ADD_ORDERS })
	public Order saveOrder(Order order, OrderContext orderContext) throws APIException;
	
	/**
	 * Save or update the given retrospective <code>order</code> in the database. If the OrderType
	 * for the order is not specified, then it will be set to the one set on the OrderContext if
	 * any, if none exists on the orderContext, then it will be set to the one associated to the
	 * ConceptClass of the ordered concept otherwise the save fails. If the CareSetting field of the
	 * order is not specified then it will default to the one set on the passed in OrderContext if
	 * any otherwise the save fails. Retrospective entry of orders can affect downstream systems
	 * that acts on orders created. Orders cannot be stopped if they are already stopped in
	 * retrospective entry.
	 *
	 * @param order the Order to save
	 * @param orderContext the OrderContext object
	 * @return the Order that was saved
	 * @throws APIException
	 * @see #saveOrder(Order, OrderContext)
	 */
	@Authorized({ PrivilegeConstants.EDIT_ORDERS, PrivilegeConstants.ADD_ORDERS })
	public Order saveRetrospectiveOrder(Order order, OrderContext orderContext);
	
	/**
	 * Completely delete an order from the database. This should not typically be used unless
	 * desperately needed. Most orders should just be voided. See {@link #voidOrder(Order, String)}
	 * 
	 * @param order The Order to remove from the system
	 * @throws APIException
	 * <strong>Should</strong> delete order from the database
	 */
	@Authorized(PrivilegeConstants.PURGE_ORDERS)
	public void purgeOrder(Order order) throws APIException;
	
	/**
	 * Completely delete an order from the database. This should not typically be used unless
	 * desperately needed. Most orders should just be voided. See {@link #voidOrder(Order, String)}
	 * This method is different from purgeOrder(Order order) above: If param cascade is false will
	 * completely delete an order from the database period If param cascade is true will completely
	 * delete an order from the database and delete any Obs that references the Order.
	 * 
	 * @param order The Order to remove from the system
	 * @param cascade
	 * @throws APIException
	 * @since 1.9.4
	 * <strong>Should</strong> delete any Obs associated to the order when cascade is true
	 */
	@Authorized(PrivilegeConstants.PURGE_ORDERS)
	public void purgeOrder(Order order, boolean cascade) throws APIException;
	
	/**
	 * Mark an order as voided. This functionally removes the Order from the system while keeping a
	 * semblance
	 * 
	 * @param voidReason String reason
	 * @param order Order to void
	 * @return the Order that was voided
	 * @throws APIException
	 * <strong>Should</strong> void an order
	 * <strong>Should</strong> unset dateStopped of the previous order if the specified order is a discontinuation
	 * <strong>Should</strong> unset dateStopped of the previous order if the specified order is a revision
	 */
	@Authorized(PrivilegeConstants.DELETE_ORDERS)
	public Order voidOrder(Order order, String voidReason) throws APIException;
	
	/**
	 * Get order by internal primary key identifier
	 * 
	 * @param orderId internal order identifier
	 * @return order with given internal identifier
	 * @throws APIException
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	public Order getOrder(Integer orderId) throws APIException;
	
	/**
	 * Get Order by its UUID
	 * 
	 * @param uuid
	 * @return order or null
	 * <strong>Should</strong> find object given valid uuid
	 * <strong>Should</strong> return null if no object found with given uuid
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	public Order getOrderByUuid(String uuid) throws APIException;
	
	/**
	 * Get discontinuation order for the given order, it is the un voided discontinuation order with
	 * a previousOrder that matches the specified order.
	 * 
	 * @param order
	 * @return the discontinuation order or null if none
	 * @throws APIException
	 * @since 1.10
	 * <strong>Should</strong> return discontinuation order if order has been discontinued
	 * <strong>Should</strong> return null if order has not been discontinued
	 * <strong>Should</strong> return null if dc order is voided
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	public Order getDiscontinuationOrder(Order order) throws APIException;
	
	/**
	 * Get revision order for the given order, it is the order with the changes that was created as
	 * a replacement for the specified order. In other words, it is the un voided revise order with
	 * a previousOrder that matches the specified order.
	 * 
	 * @param order
	 * @return the revision order or null if none
	 * @throws APIException
	 * @since 1.10
	 * <strong>Should</strong> return revision order if order has been revised
	 * <strong>Should</strong> return null if order has not been revised
	 * <strong>Should</strong> not return a voided revision order
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	public Order getRevisionOrder(Order order) throws APIException;
	
	/**
	 * Gets all Orders that match the specified parameters excluding discontinuation orders
	 * 
	 * @param patient the patient to match on
	 * @param careSetting the CareSetting to match on
	 * @param orderType The OrderType to match on
	 * @param includeVoided Specifies whether voided orders should be included or not
	 * @return list of Orders matching the parameters
	 * @since 1.10
	 * <strong>Should</strong> fail if patient is null
	 * <strong>Should</strong> fail if careSetting is null
	 * <strong>Should</strong> get the orders that match all the arguments
	 * <strong>Should</strong> get all unvoided matches if includeVoided is set to false
	 * <strong>Should</strong> include voided matches if includeVoided is set to true
	 * <strong>Should</strong> include orders for sub types if order type is specified
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	public List<Order> getOrders(Patient patient, CareSetting careSetting, OrderType orderType, boolean includeVoided);
	
	/**
	 * Gets all orders for the specified patient including discontinuation orders
	 * 
	 * @param patient the patient to match on
	 * @return list of matching {@link org.openmrs.Order}
	 * @since 1.10
	 * <strong>Should</strong> fail if patient is null
	 * <strong>Should</strong> get all the orders for the specified patient
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	public List<Order> getAllOrdersByPatient(Patient patient);

	/**
	 * Get all orders that match a variety of (nullable) criteria contained in the parameter object.
	 * Each extra value for a parameter that is provided acts as an "and" and will reduce the number of results returned
	 *
	 * @param orderSearchCriteria the object containing search parameters
	 * @return a list of orders matching the search criteria
	 * @since 2.2
	 * <strong>Should</strong> get the order matching the search criteria
	 */
	@Authorized( { PrivilegeConstants.GET_ORDERS })
	public List<Order> getOrders(OrderSearchCriteria orderSearchCriteria);
	
	/**
	 * Unvoid order record. Reverse a previous call to {@link #voidOrder(Order, String)}
	 * 
	 * @param order order to be unvoided
	 * @return the Order that was unvoided
	 * <strong>Should</strong> unvoid an order
	 * <strong>Should</strong> stop the previous order if the specified order is a discontinuation
	 * <strong>Should</strong> stop the previous order if the specified order is a revision
	 * <strong>Should</strong> fail for a discontinuation order if the previousOrder is inactive
	 * <strong>Should</strong> fail for a revise order if the previousOrder is inactive
	 */
	@Authorized(PrivilegeConstants.DELETE_ORDERS)
	public Order unvoidOrder(Order order) throws APIException;
	
	/**
	 * Updates the fulfillerStatus of an order and the related comment and finally persists it
	 *
	 * @param order order whose fulfillerStatus should be changed
	 * @param orderFulfillerStatus describes the new Order.FulfillerStatus the order should be set to
	 * @param fullFillerComment is a string which describes a comment that is set while changing the FulfillerStatus               
	 * @return the Order that is updated with an according fulfillerStatus and fulFillerComment
	 * <strong>Should</strong> set the new fulfillerStatus
	 * <strong>Should</strong> set the new fulFillerComment
	 * <strong>Should</strong> not update fulfillerStatus or fulFillerComment if null passed in to that field
	 * <strong>Should</strong> save the changed order
	 */
	@Authorized(PrivilegeConstants.EDIT_ORDERS)
	public Order updateOrderFulfillerStatus(Order order, Order.FulfillerStatus orderFulfillerStatus, String fullFillerComment);
	
	/**
	 * Updates the fulfillerStatus of an order and the related comment and finally persists it
	 *
	 * @param order order whose fulfillerStatus should be changed
	 * @param orderFulfillerStatus describes the new Order.FulfillerStatus the order should be set to
	 * @param fullFillerComment is a string which describes a comment that is set while changing the FulfillerStatus 
	 * @param accessionNumber is the accession number to set             
	 * @return the Order that is updated with an according fulfillerStatus and fulFillerComment and accession number
	 * <strong>Should</strong> set the new fulfillerStatus
	 * <strong>Should</strong> set the new fulFillerComment
	 * <strong>Should</strong> set the new accessionNumber
	 *  <strong>Should</strong> not update fulfillerStatus or fulFillerComment or accessionNumber if null passed in to that field
	 * <strong>Should</strong> save the changed order
	 */
	@Authorized(PrivilegeConstants.EDIT_ORDERS)
	public Order updateOrderFulfillerStatus(Order order, Order.FulfillerStatus orderFulfillerStatus, String fullFillerComment, String accessionNumber);
	
	/**
	 * Gets the order identified by a given order number
	 * 
	 * @param orderNumber the order number
	 * @return the order object
	 * <strong>Should</strong> find object given valid order number
	 * <strong>Should</strong> return null if no object found with given order number
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	public Order getOrderByOrderNumber(String orderNumber);
	
	/**
	 * Gets all Order objects that use this Concept for a given patient. Orders will be returned in
	 * the order in which they occurred, i.e. sorted by startDate starting with the latest
	 * 
	 * @param patient the patient.
	 * @param concept the concept.
	 * @return the list of orders.
	 * <strong>Should</strong> return orders with the given concept
	 * <strong>Should</strong> return empty list for concept without orders
	 * <strong>Should</strong> reject a null patient
	 * <strong>Should</strong> reject a null concept
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	public List<Order> getOrderHistoryByConcept(Patient patient, Concept concept);
	
	/**
	 * Gets the next available order number seed
	 * 
	 * @return the order number seed
	 */
	@Authorized(PrivilegeConstants.ADD_ORDERS)
	public Long getNextOrderNumberSeedSequenceValue();
	
	/**
	 * Gets the order matching the specified order number and its previous orders in the ordering
	 * they occurred, i.e if this order has a previous order, fetch it and if it also has a previous
	 * order then fetch it until the original one with no previous order is reached
	 * 
	 * @param orderNumber the order number whose history to get
	 * @return a list of orders for given order number
	 * <strong>Should</strong> return all order history for given order number
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	public List<Order> getOrderHistoryByOrderNumber(String orderNumber);
	
	/**
	 * Gets all active orders for the specified patient matching the specified CareSetting,
	 * OrderType as of the specified date. Below is the criteria for determining an active order:
	 * <pre>
	 * - Not voided
	 * - Not a discontinuation Order i.e one where action != Action#DISCONTINUE
	 * - dateActivated is before or equal to asOfDate
	 * - dateStopped and autoExpireDate are both null OR if it has dateStopped, then it should be
	 * after asOfDate OR if it has autoExpireDate, then it should be after asOfDate. NOTE: If both
	 * dateStopped and autoExpireDate are set then dateStopped wins because an order can never
	 * expire and then stopped later i.e. you stop an order that hasn't yet expired
	 * </pre>
	 * 
	 * @param patient the patient
	 * @param orderType The OrderType to match
	 * @param careSetting the care setting, returns all ignoring care setting if value is null
	 * @param asOfDate defaults to current time
	 * @return all active orders for given patient parameters
	 * @since 1.10
	 * <strong>Should</strong> return all active orders for the specified patient
	 * <strong>Should</strong> return all active orders for the specified patient and care setting
	 * <strong>Should</strong> return all active drug orders for the specified patient
	 * <strong>Should</strong> return all active test orders for the specified patient
	 * <strong>Should</strong> fail if patient is null
	 * <strong>Should</strong> return active orders as of the specified date
	 * <strong>Should</strong> return all orders if no orderType is specified
	 * <strong>Should</strong> include orders for sub types if order type is specified
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	public List<Order> getActiveOrders(Patient patient, OrderType orderType, CareSetting careSetting, Date asOfDate);
	
	
	
	
	/**
	 * Discontinues an order. Creates a new order that discontinues the orderToDiscontinue
	 * 
	 * @param orderToDiscontinue
	 * @param reasonCoded
	 * @param discontinueDate
	 * @param orderer
	 * @param encounter
	 * @return the new order that discontinued orderToDiscontinue
	 * @throws APIException if the <code>action</code> of orderToDiscontinue is
	 *             <code>Order.Action.DISCONTINUE</code>
	 * @since 1.10
	 * <strong>Should</strong> set correct attributes on the discontinue and discontinued orders
	 * <strong>Should</strong> pass for an active order which is scheduled and not started as of discontinue date
	 * <strong>Should</strong> not pass for a discontinuation order
	 * <strong>Should</strong> fail for a stopped order
	 * <strong>Should</strong> fail for an expired order
	 * <strong>Should</strong> reject a future discontinueDate
	 * <strong>Should</strong> not pass for a discontinued order
	 */
	@Authorized({ PrivilegeConstants.ADD_ORDERS, PrivilegeConstants.EDIT_ORDERS })
	public Order discontinueOrder(Order orderToDiscontinue, Concept reasonCoded, Date discontinueDate, Provider orderer,
	        Encounter encounter);
	
	/**
	 * Discontinues an order. Creates a new order that discontinues the orderToDiscontinue.
	 * 
	 * @param orderToDiscontinue
	 * @param reasonNonCoded
	 * @param discontinueDate
	 * @param orderer
	 * @param encounter
	 * @return the new order that discontinued orderToDiscontinue
	 * @throws APIException if the <code>action</code> of orderToDiscontinue is
	 *             <code>Order.Action.DISCONTINUE</code>
	 * @since 1.10
	 * <strong>Should</strong> populate correct attributes on the discontinue and discontinued orders
	 * <strong>Should</strong> pass for an active order which is scheduled and not started as of discontinue date
	 * <strong>Should</strong> fail for a discontinuation order
	 * <strong>Should</strong> fail if discontinueDate is in the future
	 * <strong>Should</strong> fail for a voided order
	 * <strong>Should</strong> fail for a discontinued order
	 */
	@Authorized({ PrivilegeConstants.ADD_ORDERS, PrivilegeConstants.EDIT_ORDERS })
	public Order discontinueOrder(Order orderToDiscontinue, String reasonNonCoded, Date discontinueDate, Provider orderer,
	        Encounter encounter);
	
	
	
	

}
