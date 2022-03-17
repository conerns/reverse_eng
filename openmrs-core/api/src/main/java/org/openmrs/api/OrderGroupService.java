package org.openmrs.api;

import org.openmrs.*;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.db.OrderGroupDAO;
import org.openmrs.util.PrivilegeConstants;

import java.util.List;

public interface OrderGroupService extends OpenmrsService{
	
	public void setOrderGroupDAO(OrderGroupDAO orderGroupDAO);
	

	/**
	 * Fetches the OrderGroup By Uuid.
	 *
	 * @param uuid Uuid Of the OrderGroup
	 * @return saved OrderGroup
	 * @since 1.12
	 * @throws APIException
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	public OrderGroup getOrderGroupByUuid(String uuid) throws APIException;

	/**
	 * Fetches the OrderGroup by Id.
	 *
	 * @param orderGroupId Id of the OrderGroup
	 * @return saved OrderGroup
	 * @since 1.12
	 * @throws APIException
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	public OrderGroup getOrderGroup(Integer orderGroupId) throws APIException;

	/**
	 * Saves the orderGroup. It also saves the list of orders that are present within the
	 * orderGroup.
	 *
	 * @param orderGroup the orderGroup to be saved
	 * @since 1.12
	 * @throws APIException
	 */
	@Authorized({ PrivilegeConstants.EDIT_ORDERS, PrivilegeConstants.ADD_ORDERS })
	public OrderGroup saveOrderGroup(OrderGroup orderGroup) throws APIException;

	/**
	 * Fetches all order groups for the specified patient
	 *
	 * @param patient the patient to match on
	 * @return list of matching OrderGroups
	 * @since 2.4.0
	 * @throws APIException
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	public List<OrderGroup> getOrderGroupsByPatient(Patient patient) throws APIException;

	/**
	 * Fetches all order groups for the specified encounter
	 *
	 * @param encounter the encounter to match on
	 * @return list of matching OrderGroups
	 * @since 2.4.0
	 * @throws APIException
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	public List<OrderGroup> getOrderGroupsByEncounter(Encounter encounter) throws APIException;

	/**
	 * Returns all order group attribute types
	 *
	 * @return all {@link OrderGroupAttributeType}s
	 * @should return all order group attribute types including retired ones
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	List<OrderGroupAttributeType> getAllOrderGroupAttributeTypes() throws APIException;

	/**
	 * Fetches order group attribute type using provided Id
	 *
	 * @param id The Id of the order group attribute type to fetch from the database
	 * @return the {@link OrderGroupAttributeType} with the given internal id
	 * @should return the order group attribute type using the provided id
	 * @should return null if no order group attribute type exists with the given id
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	OrderGroupAttributeType getOrderGroupAttributeType(Integer orderGroupAttributeTypeId) throws APIException;

	/**
	 * Fetches  order group attribute type using provided uuid
	 *
	 * @param uuid The uuid of the order group attribute type to fetch from the database
	 * @return the {@link OrderGroupAttributeType} with the given uuid
	 * @should return the order group attribute type with the given uuid
	 * @should return null if no order group attribute type exists with the given uuid
	 */
	OrderGroupAttributeType getOrderGroupAttributeTypeByUuid(String uuid) throws APIException;

	/**
	 * Creates or updates the given order group attribute type in the database
	 *
	 * @param orderGroupAttributeType The order group attribute type to save in the database
	 * @return the order group attribute type created or saved
	 * @should create a new order group attribute type
	 * @should edit an existing order group attribute type
	 */
	@Authorized({PrivilegeConstants.EDIT_ORDERS,PrivilegeConstants.ADD_ORDERS})
	OrderGroupAttributeType saveOrderGroupAttributeType(OrderGroupAttributeType orderGroupAttributeType) throws APIException;

	/**
	 * Retires the given order group attribute type in the database
	 *
	 * @param orderGroupAttributeType The order group attribute type to retire
	 * @param reason The reason why the order group attribute type is being retired
	 * @return the order group attribute type retired
	 * @should retire an order group attribute type
	 */
	@Authorized(PrivilegeConstants.MANAGE_ORDER_TYPES)
	OrderGroupAttributeType retireOrderGroupAttributeType(OrderGroupAttributeType orderGroupAttributeType, String reason) throws APIException;

	/**
	 * Restores an order group attribute type that was previously retired in the database
	 *
	 * @param orderGroupAttributeType The order group attribute type to unretire
	 * @return the order group attribute type unretired
	 * @should unretire an order group attribute type
	 */
	@Authorized(PrivilegeConstants.MANAGE_ORDER_TYPES)
	OrderGroupAttributeType unretireOrderGroupAttributeType(OrderGroupAttributeType orderGroupAttributeType) throws APIException;

	/**
	 * Completely removes an order group attribute type from the database
	 *
	 * @param orderGroupAttributeType The order group attribute type to purge
	 * @should completely remove an order group attribute type
	 */
	@Authorized(PrivilegeConstants.PURGE_ORDERS)
	void purgeOrderGroupAttributeType(OrderGroupAttributeType orderGroupAttributeType) throws APIException;

	/**
	 * Retrieves an order group attribute type object based on the name provided
	 *
	 * @param orderGroupAttributeTypeName The name of the order group attribute type to fetch
	 * @return the {@link OrderGroupAttributeType} with the specified name
	 * @should return the order group attribute type with the specified name
	 * @should return null if no order group attribute type exists with the specified name
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	OrderGroupAttributeType getOrderGroupAttributeTypeByName(String orderGroupAttributeTypeName) throws APIException;

	/**
	 * Fetches a given order group attribute using the provided uuid
	 *
	 * @param uuid The uuid of the order group attribute to fetch
	 * @return the {@link OrderGroupAttribute} with the given uuid
	 * @since 2.4.0
	 * @should get the order group attribute with the given uuid
	 * @should return null if no order group attribute has the given uuid
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	OrderGroupAttribute getOrderGroupAttributeByUuid(String uuid) throws APIException;
}
