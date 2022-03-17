package org.openmrs.api;

import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.OrderType;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.db.OrderTypeDAO;
import org.openmrs.util.PrivilegeConstants;

import java.util.List;

public interface OrderTypeService extends OpenmrsService{
	
	public void setOrderTypeDAO(OrderTypeDAO orderTypeDAO);


	/**
	 * Get OrderType by orderTypeId
	 *
	 * @param orderTypeId the orderTypeId to match on
	 * @since 1.10
	 * @return order type object associated with given id
	 * <strong>Should</strong> find order type object given valid id
	 * <strong>Should</strong> return null if no order type object found with given id
	 */
	@Authorized(PrivilegeConstants.GET_ORDER_TYPES)
	public OrderType getOrderType(Integer orderTypeId);

	/**
	 * Get OrderType by uuid
	 *
	 * @param uuid the uuid to match on
	 * @since 1.10
	 * @return order type object associated with given uuid
	 * <strong>Should</strong> find order type object given valid uuid
	 * <strong>Should</strong> return null if no order type object found with given uuid
	 */
	@Authorized(PrivilegeConstants.GET_ORDER_TYPES)
	public OrderType getOrderTypeByUuid(String uuid);

	/**
	 * Get all order types, if includeRetired is set to true then retired ones will be included
	 * otherwise not
	 *
	 * @param includeRetired boolean flag which indicate search needs to look at retired order types
	 *            or not
	 * <strong>Should</strong> get all order types if includeRetired is set to true
	 * <strong>Should</strong> get all non retired order types if includeRetired is set to false
	 * @return list of order types
	 * @since 1.10
	 */
	@Authorized(PrivilegeConstants.GET_ORDER_TYPES)
	public List<OrderType> getOrderTypes(boolean includeRetired);

	/**
	 * Creates or updates the given order type in the database
	 *
	 * @param orderType the order type to save
	 * @return the order type created/saved
	 * @since 1.10
	 * <strong>Should</strong> add a new order type to the database
	 * <strong>Should</strong> edit an existing order type
	 */
	@Authorized(PrivilegeConstants.MANAGE_ORDER_TYPES)
	public OrderType saveOrderType(OrderType orderType);

	/**
	 * Completely removes an order type from the database
	 *
	 * @param orderType the order type to purge
	 * @since 1.10
	 * <strong>Should</strong> delete order type if not in use
	 * <strong>Should</strong> not allow deleting an order type that is in use
	 */
	@Authorized(PrivilegeConstants.PURGE_ORDER_TYPES)
	public void purgeOrderType(OrderType orderType) throws APIException;

	/**
	 * Retires the given order type in the database
	 *
	 * @param orderType the order type to retire
	 * @param reason the retire reason
	 * @return the retired order type
	 * @since 1.10
	 * <strong>Should</strong> retire order type
	 */
	@Authorized(PrivilegeConstants.MANAGE_ORDER_TYPES)
	public OrderType retireOrderType(OrderType orderType, String reason);

	/**
	 * Restores an order type that was previously retired in the database
	 *
	 * @param orderType the order type to unretire
	 * @return the unretired order type
	 * @since 1.10
	 * <strong>Should</strong> unretire order type
	 */
	@Authorized(PrivilegeConstants.MANAGE_ORDER_TYPES)
	public OrderType unretireOrderType(OrderType orderType);

	/**
	 * Returns all descendants of a given order type for example Given TEST will get back LAB TEST
	 * and RADIOLOGY TEST; and Given LAB TEST, will might get back SEROLOGY, MICROBIOLOGY, and
	 * CHEMISTRY
	 *
	 * @param orderType the order type which needs to search for its' dependencies
	 * @param includeRetired boolean flag for include retired order types or not
	 * @return list of order type which matches the given order type
	 */
	@Authorized(PrivilegeConstants.GET_ORDER_TYPES)
	public List<OrderType> getSubtypes(OrderType orderType, boolean includeRetired);

	/**
	 * Gets the order type mapped to a given concept class
	 *
	 * @param conceptClass the concept class
	 * @return the matching order type
	 * @since 1.10
	 * <strong>Should</strong> get order type mapped to the given concept class
	 */
	@Authorized(PrivilegeConstants.GET_ORDER_TYPES)
	public OrderType getOrderTypeByConceptClass(ConceptClass conceptClass);

	/**
	 * Gets the order type mapped to a given concept
	 *
	 * @param concept the concept
	 * @return the matching order type
	 * @since 1.10
	 * <strong>Should</strong> get order type mapped to the given concept
	 */
	@Authorized(PrivilegeConstants.GET_ORDER_TYPES)
	public OrderType getOrderTypeByConcept(Concept concept);

	/**
	 * Gets OrderType that matches the specified name
	 *
	 * @param orderTypeName the name to match against
	 * @return OrderType
	 * @since 1.10
	 * <strong>Should</strong> return the order type that matches the specified name
	 */
	@Authorized(PrivilegeConstants.GET_ORDER_TYPES)
	public OrderType getOrderTypeByName(String orderTypeName);
}
