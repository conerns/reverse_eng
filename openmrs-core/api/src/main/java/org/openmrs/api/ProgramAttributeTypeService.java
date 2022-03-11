package org.openmrs.api;

import org.openmrs.ProgramAttributeType;
import org.openmrs.annotation.Authorized;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProgramAttributeTypeService extends OpenmrsService{


	@Transactional(readOnly = true)
	@Authorized({"Get Patient Program Attribute Types"})
	public List<ProgramAttributeType> getAllProgramAttributeTypes();

	@Transactional(readOnly = true)
	@Authorized({"Get Patient Program Attribute Types"})
	public ProgramAttributeType getProgramAttributeType(Integer var1);

	@Transactional(readOnly = true)
	@Authorized({"Get Patient Program Attribute Types"})
	public ProgramAttributeType getProgramAttributeTypeByUuid(String var1);

	@Authorized({"Manage Patient Program Attribute Types"})
	public ProgramAttributeType saveProgramAttributeType(ProgramAttributeType var1);

	@Authorized({"Purge Patient Program Attribute Types"})
	public void purgeProgramAttributeType(ProgramAttributeType var1);
}
