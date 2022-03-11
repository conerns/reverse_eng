package org.openmrs.api.db;

import org.openmrs.PatientProgramAttribute;
import org.openmrs.ProgramAttributeType;

import java.util.List;

public interface ProgramAttributeTypeDAO {


	public List<ProgramAttributeType> getAllProgramAttributeTypes();

	public ProgramAttributeType getProgramAttributeType(Integer var1);

	public ProgramAttributeType getProgramAttributeTypeByUuid(String var1);

	public ProgramAttributeType saveProgramAttributeType(ProgramAttributeType var1);

	public void purgeProgramAttributeType(ProgramAttributeType var1);
}
