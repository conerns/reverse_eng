package org.openmrs.api.db;

import org.openmrs.OrderAttribute;
import org.openmrs.OrderAttributeType;
import org.openmrs.api.db.DAOException;

import java.util.List;

public interface OrderAttributeDAO {

	/**
	 * @see org.openmrs.api.OrderService#getOrderAttributeByUuid(String)
	 */
	OrderAttribute getOrderAttributeByUuid(String uuid) throws DAOException;

	/**
	 * @see org.openmrs.api.OrderService#getAllOrderAttributeTypes()
	 */
	List<OrderAttributeType> getAllOrderAttributeTypes()throws DAOException;

	/**
	 * @see org.openmrs.api.OrderService#getOrderAttributeTypeById(Integer)
	 */
	OrderAttributeType getOrderAttributeTypeById(Integer orderAttributeTypeId)throws DAOException;

	/**
	 * @see org.openmrs.api.OrderService#getOrderAttributeTypeByUuid(String)
	 */
	OrderAttributeType getOrderAttributeTypeByUuid(String uuid)throws DAOException;

	/**
	 * @see org.openmrs.api.OrderService#saveOrderAttributeType(OrderAttributeType)
	 */
	OrderAttributeType saveOrderAttributeType(OrderAttributeType orderAttributeType)throws DAOException;

	/**
	 * @see org.openmrs.api.OrderService#purgeOrderAttributeType(OrderAttributeType)
	 */
	void deleteOrderAttributeType(OrderAttributeType orderAttributeType)throws DAOException;

	/**
	 * @see org.openmrs.api.OrderService#getOrderAttributeTypeByName(String)
	 */
	OrderAttributeType getOrderAttributeTypeByName(String name)throws DAOException;
}
