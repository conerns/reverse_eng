package org.openmrs.api.impl;

import org.openmrs.OrderAttribute;
import org.openmrs.OrderAttributeType;
import org.openmrs.api.APIException;
import org.openmrs.api.OrderAttributeService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.OrderAttributeDAO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class OrderAttributeServiceImpl implements OrderAttributeService {
	
	protected OrderAttributeDAO dao;

	@Override
	public void setOrderAttributeDAO(OrderAttributeDAO orderAttributeDAO) {
		dao = orderAttributeDAO;
	}


	/**
	 * @see org.openmrs.api.OrderService#getAllOrderAttributeTypes()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<OrderAttributeType> getAllOrderAttributeTypes() throws APIException {
		return dao.getAllOrderAttributeTypes();
	}

	/**
	 * @see org.openmrs.api.OrderService#getOrderAttributeTypeById(Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public OrderAttributeType getOrderAttributeTypeById(Integer id) throws APIException {
		return dao.getOrderAttributeTypeById(id);
	}

	/**
	 * @see org.openmrs.api.OrderService#getOrderAttributeTypeByUuid(String)
	 */
	@Override
	@Transactional(readOnly = true)
	public OrderAttributeType getOrderAttributeTypeByUuid(String uuid)throws APIException {
		return dao.getOrderAttributeTypeByUuid(uuid);
	}

	/**
	 * @see org.openmrs.api.OrderService#saveOrderAttributeType(OrderAttributeType)
	 */
	@Override
	public OrderAttributeType saveOrderAttributeType(OrderAttributeType orderAttributeType) throws APIException{
		return dao.saveOrderAttributeType(orderAttributeType);
	}

	/**
	 * @see org.openmrs.api.OrderService#retireOrderAttributeType(OrderAttributeType)
	 */
	@Override
	public OrderAttributeType retireOrderAttributeType(OrderAttributeType orderAttributeType, String reason)throws APIException {
		return Context.getOrderAttributeService().saveOrderAttributeType(orderAttributeType);
	}

	/**
	 * @see org.openmrs.api.OrderService#unretireOrderAttributeType(OrderAttributeType)
	 */
	@Override
	public OrderAttributeType unretireOrderAttributeType(OrderAttributeType orderAttributeType)throws APIException {
		return Context.getOrderAttributeService().saveOrderAttributeType(orderAttributeType);
	}

	/**
	 * @see org.openmrs.api.OrderService#purgeOrderAttributeType(OrderAttributeType)
	 */
	@Override
	public void purgeOrderAttributeType(OrderAttributeType orderAttributeType) throws APIException{
		dao.deleteOrderAttributeType(orderAttributeType);
	}

	/**
	 * @see org.openmrs.api.OrderService#getOrderAttributeTypeByName(String)
	 */
	@Override
	@Transactional(readOnly = true)
	public OrderAttributeType getOrderAttributeTypeByName(String orderAttributeTypeName)throws APIException {
		return dao.getOrderAttributeTypeByName(orderAttributeTypeName);
	}

	/**
	 * @see org.openmrs.api.OrderService#getOrderAttributeByUuid(String)
	 */
	@Override
	@Transactional(readOnly = true)
	public OrderAttribute getOrderAttributeByUuid(String uuid)throws APIException {
		return dao.getOrderAttributeByUuid(uuid);
	}

	@Override
	public void onStartup() {
		
	}

	@Override
	public void onShutdown() {

	}
}
