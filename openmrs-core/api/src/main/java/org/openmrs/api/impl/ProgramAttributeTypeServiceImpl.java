package org.openmrs.api.impl;

import org.openmrs.ProgramAttributeType;
import org.openmrs.api.ProgramAttributeTypeService;
import org.openmrs.api.db.ProgramAttributeTypeDAO;

import java.util.List;

public class ProgramAttributeTypeServiceImpl extends BaseOpenmrsService implements ProgramAttributeTypeService {
	
	protected ProgramAttributeTypeDAO programAttributeTypeDAO;

	public void setProgramAttributeTypeDAO(ProgramAttributeTypeDAO programAttributeTypeDAO) {
		this.programAttributeTypeDAO = programAttributeTypeDAO;
	}

	@Override
	public List<ProgramAttributeType> getAllProgramAttributeTypes() {
		return programAttributeTypeDAO.getAllProgramAttributeTypes();
	}

	@Override
	public ProgramAttributeType getProgramAttributeType(Integer id) {
		return programAttributeTypeDAO.getProgramAttributeType(id);
	}

	@Override
	public ProgramAttributeType getProgramAttributeTypeByUuid(String uuid) {
		return programAttributeTypeDAO.getProgramAttributeTypeByUuid(uuid);
	}

	@Override
	public ProgramAttributeType saveProgramAttributeType(ProgramAttributeType type) {
		return programAttributeTypeDAO.saveProgramAttributeType(type);
	}

	@Override
	public void purgeProgramAttributeType(ProgramAttributeType type) {
		programAttributeTypeDAO.purgeProgramAttributeType(type);
	}
}
