package org.openmrs.api.impl;

import org.openmrs.*;
import org.openmrs.api.APIException;
import org.openmrs.api.OrderGroupService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.OrderGroupDAO;
import org.openmrs.customdatatype.CustomDatatypeUtil;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public class OrderGroupServiceImpl implements OrderGroupService {
	
	protected OrderGroupDAO dao;

	@Override
	public void setOrderGroupDAO(OrderGroupDAO orderGroupDAO) {
		dao = orderGroupDAO;
	}


	/**
	 * @see org.openmrs.api.OrderService#saveOrderGroup(org.openmrs.OrderGroup)
	 */
	@Override
	public OrderGroup saveOrderGroup(OrderGroup orderGroup) throws APIException {
		if (orderGroup.getId() == null) {
			// an OrderGroup requires an encounter, which has a patient, so it
			// is odd that OrderGroup has a patient field. There is no obvious
			// reason why they should ever be different.
			orderGroup.setPatient(orderGroup.getEncounter().getPatient());
			CustomDatatypeUtil.saveAttributesIfNecessary(orderGroup);
			dao.saveOrderGroup(orderGroup);
		}
		List<Order> orders = orderGroup.getOrders();
		for (Order order : orders) {
			if (order.getId() == null) {
				order.setEncounter(orderGroup.getEncounter());
				Context.getOrderService().saveOrder(order, null);
			}
		}
		Set<OrderGroup> nestedGroups = orderGroup.getNestedOrderGroups();
		if (nestedGroups != null) {
			for (OrderGroup nestedGroup : nestedGroups) {
				Context.getOrderGroupService().saveOrderGroup(nestedGroup);
			}
		}
		return orderGroup;
	}
	
	@Override
	@Transactional(readOnly = true)
	public OrderGroup getOrderGroupByUuid(String uuid) throws APIException {
		return dao.getOrderGroupByUuid(uuid);
	}

	@Override
	@Transactional(readOnly = true)
	public OrderGroup getOrderGroup(Integer orderGroupId) throws APIException {
		return dao.getOrderGroupById(orderGroupId);
	}

	@Override
	public List<OrderGroup> getOrderGroupsByPatient(Patient patient) throws APIException {
		return dao.getOrderGroupsByPatient(patient);
	}

	@Override
	public List<OrderGroup> getOrderGroupsByEncounter(Encounter encounter) throws APIException {
		return dao.getOrderGroupsByEncounter(encounter);
	}

	/**
	 * @see org.openmrs.api.OrderService#getOrderGroupAttributeTypes()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<OrderGroupAttributeType> getAllOrderGroupAttributeTypes() throws APIException {
		return dao.getAllOrderGroupAttributeTypes();
	}

	/**
	 * @see org.openmrs.api.OrderService#getOrderGroupAttributeTypeById()
	 */
	@Override
	@Transactional(readOnly = true)
	public OrderGroupAttributeType getOrderGroupAttributeType(Integer id) throws APIException {
		return dao.getOrderGroupAttributeType(id);
	}

	/**
	 * @see org.openmrs.api.OrderService#getOrderGroupAttributeTypeByUuid()
	 */
	@Override
	@Transactional(readOnly = true)
	public OrderGroupAttributeType getOrderGroupAttributeTypeByUuid(String uuid)throws APIException {
		return dao.getOrderGroupAttributeTypeByUuid(uuid);
	}

	/**
	 * @see org.openmrs.api.OrderService#saveOrderGroupAttributeType()
	 */
	@Override
	public OrderGroupAttributeType saveOrderGroupAttributeType(OrderGroupAttributeType orderGroupAttributeType) throws APIException{
		return dao.saveOrderGroupAttributeType(orderGroupAttributeType);
	}

	/**
	 * @see org.openmrs.api.OrderService#retireOrderGroupAttributeType()
	 */
	@Override
	public OrderGroupAttributeType retireOrderGroupAttributeType(OrderGroupAttributeType orderGroupAttributeType, String reason)throws APIException {
		return Context.getOrderGroupService().saveOrderGroupAttributeType(orderGroupAttributeType);
	}

	/**
	 * @see org.openmrs.api.OrderService#unretireOrderGroupAttributeType()
	 */
	@Override
	public OrderGroupAttributeType unretireOrderGroupAttributeType(OrderGroupAttributeType orderGroupAttributeType)throws APIException {
		return Context.getOrderGroupService().saveOrderGroupAttributeType(orderGroupAttributeType);
	}

	/**
	 * @see org.openmrs.api.OrderService#purgeOrderGroupAttributeType()
	 */
	@Override
	public void purgeOrderGroupAttributeType(OrderGroupAttributeType orderGroupAttributeType) throws APIException{
		dao.deleteOrderGroupAttributeType(orderGroupAttributeType);
	}

	/**
	 * @see org.openmrs.api.OrderService#getOrderGroupAttributeTypeByName()
	 */
	@Override
	@Transactional(readOnly = true)
	public OrderGroupAttributeType getOrderGroupAttributeTypeByName(String orderGroupAttributeTypeName)throws APIException {
		return dao.getOrderGroupAttributeTypeByName(orderGroupAttributeTypeName);
	}

	/**
	 * @see org.openmrs.api.OrderService#getOrderGroupAttributeByUuid()
	 */
	@Override
	@Transactional(readOnly = true)
	public OrderGroupAttribute getOrderGroupAttributeByUuid(String uuid)throws APIException {
		return dao.getOrderGroupAttributeByUuid(uuid);
	}

	@Override
	public void onStartup() {
		
	}

	@Override
	public void onShutdown() {

	}
}
