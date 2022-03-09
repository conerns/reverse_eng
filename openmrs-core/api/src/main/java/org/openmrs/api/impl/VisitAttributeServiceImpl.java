package org.openmrs.api.impl;

import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;
import org.openmrs.api.VisitAttributeService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.VisitAttributeDAO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class VisitAttributeServiceImpl implements VisitAttributeService {

	private VisitAttributeDAO visitAttributeDAO;

	public VisitAttributeDAO getVisitAttributeDAO() {
		return visitAttributeDAO;
	}

	public void setVisitAttributeDAO(VisitAttributeDAO visitAttributeDAO) {
		this.visitAttributeDAO = visitAttributeDAO;
	}

	@Override
	@Transactional(readOnly = true)
	public List<VisitAttributeType> getAllVisitAttributeTypes() {
		return visitAttributeDAO.getAllVisitAttributeTypes();
	}

	@Override
	@Transactional(readOnly = true)
	public VisitAttributeType getVisitAttributeType(Integer id) {
		return visitAttributeDAO.getVisitAttributeType(id);
	}

	@Override
	@Transactional(readOnly = true)
	public VisitAttributeType getVisitAttributeTypeByUuid(String uuid) {
		return visitAttributeDAO.getVisitAttributeTypeByUuid(uuid);
	}

	@Override
	public VisitAttributeType saveVisitAttributeType(VisitAttributeType visitAttributeType) {
		return visitAttributeDAO.saveVisitAttributeType(visitAttributeType);
	}

	@Override
	public VisitAttributeType retireVisitAttributeType(VisitAttributeType visitAttributeType, String reason) {
		return visitAttributeDAO.saveVisitAttributeType(visitAttributeType);
	}

	@Override
	public VisitAttributeType unretireVisitAttributeType(VisitAttributeType visitAttributeType) {
		return Context.getVisitAttributeService().saveVisitAttributeType(visitAttributeType);
	}

	@Override
	public void purgeVisitAttributeType(VisitAttributeType visitAttributeType) {
		visitAttributeDAO.deleteVisitAttributeType(visitAttributeType);
	}

	@Override
	@Transactional(readOnly = true)
	public VisitAttribute getVisitAttributeByUuid(String uuid) {
		return visitAttributeDAO.getVisitAttributeByUuid(uuid);
	}
	
}
