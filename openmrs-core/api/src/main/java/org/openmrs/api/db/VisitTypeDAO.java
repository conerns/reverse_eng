package org.openmrs.api.db;

import org.openmrs.VisitType;
import org.openmrs.api.APIException;

import java.util.List;

public interface VisitTypeDAO {

	List<VisitType> getAllVisitTypes() throws APIException;

	public List<VisitType> getAllVisitTypes(boolean includeRetired) throws DAOException;

	VisitType getVisitType(Integer visitTypeId);

	VisitType getVisitTypeByUuid(String uuid);

	List<VisitType> getVisitTypes(String fuzzySearchPhrase);

	VisitType saveVisitType(VisitType visitType);

	void purgeVisitType(VisitType visitType);
}
