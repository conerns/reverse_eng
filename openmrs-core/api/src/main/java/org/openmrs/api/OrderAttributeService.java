package org.openmrs.api;

import org.openmrs.OrderAttributeType;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.db.OrderAttributeDAO;
import org.openmrs.util.PrivilegeConstants;

import java.util.List;

public interface OrderAttributeService extends OpenmrsService {

	void setOrderAttributeDAO(OrderAttributeDAO orderAttributeDAO);

	/**
	 * Returns all order attribute types
	 *
	 * @return all {@link OrderAttributeType}s
	 * @since 2.5.0
	 * @should return all order attribute types including retired ones
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	List<OrderAttributeType> getAllOrderAttributeTypes() throws APIException;

	/**
	 * Fetches order attribute type using provided Id
	 *
	 * @param id The Id of the order attribute type to fetch from the database
	 * @return the {@link OrderAttributeType} with the given internal id
	 * @since 2.5.0
	 * @should return the order attribute type using the provided id
	 * @should return null if no order attribute type exists with the given id
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	OrderAttributeType getOrderAttributeTypeById(Integer orderAttributeTypeId) throws APIException;

	/**
	 * Fetches order attribute type using provided uuid
	 *
	 * @param uuid The uuid of the order attribute type to fetch from the database
	 * @return the {@link OrderAttributeType} with the given uuid
	 * @since 2.5.0
	 * @should return the order attribute type with the given uuid
	 * @should return null if no order attribute type exists with the given uuid
	 */
	OrderAttributeType getOrderAttributeTypeByUuid(String uuid) throws APIException;

	/**
	 * Creates or updates the given order attribute type in the database
	 *
	 * @param orderAttributeType The order attribute type to save in the database
	 * @return the order attribute type created or saved
	 * @since 2.5.0
	 * @should create a new order attribute type
	 * @should edit an existing order attribute type
	 */
	@Authorized({PrivilegeConstants.EDIT_ORDERS,PrivilegeConstants.ADD_ORDERS})
	OrderAttributeType saveOrderAttributeType(OrderAttributeType orderAttributeType) throws APIException;

	/**
	 * Retires the given order attribute type in the database
	 *
	 * @param orderAttributeType The order attribute type to retire
	 * @param reason The reason why the order attribute type is being retired
	 * @return the order attribute type retired
	 * @since 2.5.0
	 * @should retire an order attribute type
	 */
	@Authorized(PrivilegeConstants.MANAGE_ORDER_TYPES)
	OrderAttributeType retireOrderAttributeType(OrderAttributeType orderAttributeType, String reason) throws APIException;

	/**
	 * Restores an order attribute type that was previously retired in the database
	 *
	 * @param orderAttributeType The order attribute type to unretire
	 * @return the order attribute type unretired
	 * @since 2.5.0
	 * @should unretire an order attribute type
	 */
	@Authorized(PrivilegeConstants.MANAGE_ORDER_TYPES)
	OrderAttributeType unretireOrderAttributeType(OrderAttributeType orderAttributeType) throws APIException;

	/**
	 * Completely removes an order attribute type from the database
	 *
	 * @param orderAttributeType The order attribute type to purge
	 * @since 2.5.0
	 * @should completely remove an order attribute type
	 */
	@Authorized(PrivilegeConstants.PURGE_ORDERS)
	void purgeOrderAttributeType(OrderAttributeType orderAttributeType) throws APIException;

	/**
	 * Retrieves an order attribute type object based on the name provided
	 *
	 * @param orderAttributeTypeName The name of the order attribute type to fetch
	 * @return the {@link OrderAttributeType} with the specified name
	 * @since 2.5.0
	 * @should return the order attribute type with the specified name
	 * @should return null if no order attribute type exists with the specified name
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	OrderAttributeType getOrderAttributeTypeByName(String orderAttributeTypeName) throws APIException;

	/**
	 * Fetches a given order attribute using the provided uuid
	 *
	 * @param uuid The uuid of the order attribute to fetch
	 * @return the {@link org.openmrs.OrderAttribute} with the given uuid
	 * @since 2.5.0
	 * @should get the order attribute with the given uuid
	 * @should return null if no order attribute has the given uuid
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	org.openmrs.OrderAttribute getOrderAttributeByUuid(String uuid) throws APIException;
}
