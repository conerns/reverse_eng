package org.openmrs.api.db;

import org.openmrs.ConceptClass;
import org.openmrs.OrderType;

import java.util.List;

public interface OrderTypeDAO {

	/**
	 * @see org.openmrs.api.OrderService#getOrderTypeByName(String)
	 */
	public OrderType getOrderTypeByName(String orderTypeName);


	/**
	 * @see org.openmrs.api.OrderService#getOrderType
	 */
	public OrderType getOrderType(Integer orderTypeId);

	/**
	 * @see org.openmrs.api.OrderService#getOrderTypeByUuid
	 */
	public OrderType getOrderTypeByUuid(String uuid);

	/**
	 * @see org.openmrs.api.OrderService#getOrderTypes
	 */
	public List<OrderType> getOrderTypes(boolean includeRetired);

	/**
	 * @see org.openmrs.api.OrderService#getOrderTypeByConceptClass(org.openmrs.ConceptClass)
	 */
	public OrderType getOrderTypeByConceptClass(ConceptClass conceptClass);

	/**
	 * @see org.openmrs.api.OrderService#saveOrderType(org.openmrs.OrderType)
	 */
	public OrderType saveOrderType(OrderType orderType);

	/**
	 * @see org.openmrs.api.OrderService#purgeOrderType(org.openmrs.OrderType)
	 */
	public void purgeOrderType(OrderType orderType);

	/**
	 * @see org.openmrs.api.OrderService#getSubtypes(org.openmrs.OrderType, boolean)
	 */
	public List<OrderType> getOrderSubtypes(OrderType orderType, boolean includeRetired);

	/**
	 * Check whether give order type is used by any order
	 *
	 * @param orderType the order type to check the usage
	 * @return true if used else false
	 */
	public boolean isOrderTypeInUse(OrderType orderType);
}
