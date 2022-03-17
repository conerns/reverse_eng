package org.openmrs.api.db;

import org.openmrs.*;

import java.util.List;

public interface OrderGroupDAO {

	/**
	 * Saves an orderGroup to the database
	 *
	 * @param orderGroup
	 * @return an orderGroup
	 * @throws DAOException
	 */
	public OrderGroup saveOrderGroup(OrderGroup orderGroup) throws DAOException;

	/**
	 * @see org.openmrs.api.OrderService#getOrderGroupByUuid(String)
	 */
	public OrderGroup getOrderGroupByUuid(String uuid) throws DAOException;

	/**
	 * @see org.openmrs.api.OrderService#getOrderGroup(Integer)
	 */
	public OrderGroup getOrderGroupById(Integer orderGroupId) throws DAOException;

	/**
	 * @see org.openmrs.api.OrderService#getOrderGroupsByPatient(Patient)
	 */
	public List<OrderGroup> getOrderGroupsByPatient(Patient patient) throws DAOException;

	/**
	 * @see org.openmrs.api.OrderService#getOrderGroupsByEncounter(Encounter)
	 */
	public List<OrderGroup> getOrderGroupsByEncounter(Encounter encounter) throws DAOException;

	/**
	 * @see  org.openmrs.api.OrderService#getOrderGroupAttributeByUuid(String)
	 */
	public OrderGroupAttribute getOrderGroupAttributeByUuid(String uuid) throws DAOException;

	/**
	 * @see org.openmrs.api.OrderService#getAllOrderGroupAttributeTypes()
	 */
	public List<OrderGroupAttributeType> getAllOrderGroupAttributeTypes()throws DAOException;

	/**
	 * @see org.openmrs.api.OrderService#getOrderGroupAttributeType(Integer)
	 */
	public OrderGroupAttributeType getOrderGroupAttributeType(Integer orderGroupAttributeTypeId)throws DAOException;

	/**
	 * @see org.openmrs.api.OrderService#getOrderGroupAttributeTypeByUuid(String)
	 */
	public OrderGroupAttributeType getOrderGroupAttributeTypeByUuid(String uuid)throws DAOException;

	/**
	 * @see org.openmrs.api.OrderService#saveOrderGroupAttributeType(OrderGroupAttributeType)
	 */
	public OrderGroupAttributeType  saveOrderGroupAttributeType(OrderGroupAttributeType orderGroupAttributeType)throws DAOException;

	/**
	 * @see org.openmrs.api.OrderService#purgeOrderGroupAttributeType(OrderGroupAttributeType)
	 */
	public  void deleteOrderGroupAttributeType(OrderGroupAttributeType orderGroupAttributeType)throws DAOException;

	/**
	 * @see org.openmrs.api.OrderService#getOrderGroupAttributeTypeByName(String)
	 */
	public OrderGroupAttributeType getOrderGroupAttributeTypeByName(String name)throws DAOException;
}
