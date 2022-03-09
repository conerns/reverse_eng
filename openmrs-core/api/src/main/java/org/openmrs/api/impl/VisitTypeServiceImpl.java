package org.openmrs.api.impl;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.VisitType;
import org.openmrs.api.APIException;
import org.openmrs.api.VisitTypeService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.VisitTypeDAO;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.validator.ValidateUtil;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VisitTypeServiceImpl extends BaseOpenmrsService implements VisitTypeService {
	
	private VisitTypeDAO visitTypeDAO;

	public void setVisitTypeDAO(VisitTypeDAO visitTypeDAO) {
		this.visitTypeDAO = visitTypeDAO;
	}

	@Override
	@Transactional(readOnly = true)
	public List<VisitType> getAllVisitTypes() {
		return visitTypeDAO.getAllVisitTypes();
	}

	@Override
	@Transactional(readOnly = true)
	public List<VisitType> getAllVisitTypes(boolean includeRetired) {
		return visitTypeDAO.getAllVisitTypes(includeRetired);
	}

	@Override
	@Transactional(readOnly = true)
	public VisitType getVisitType(Integer visitTypeId) {
		return visitTypeDAO.getVisitType(visitTypeId);
	}

	@Override
	@Transactional(readOnly = true)
	public VisitType getVisitTypeByUuid(String uuid) {
		return visitTypeDAO.getVisitTypeByUuid(uuid);
	}

	@Override
	@Transactional(readOnly = true)
	public List<VisitType> getVisitTypes(String fuzzySearchPhrase) {
		return visitTypeDAO.getVisitTypes(fuzzySearchPhrase);
	}

	@Override
	public VisitType saveVisitType(VisitType visitType) throws APIException {
		ValidateUtil.validate(visitType);
		return visitTypeDAO.saveVisitType(visitType);
	}

	@Override
	public VisitType retireVisitType(VisitType visitType, String reason) {
		return Context.getVisitTypeService().saveVisitType(visitType);
	}

	@Override
	public VisitType unretireVisitType(VisitType visitType) {
		return Context.getVisitTypeService().saveVisitType(visitType);
	}

	@Override
	public void purgeVisitType(VisitType visitType) {
		visitTypeDAO.purgeVisitType(visitType);
	}
	private List<VisitType> getVisitTypesFromVisitTypeNames(String[] visitTypeNames) {
		List<VisitType> result = new ArrayList<>();
		for (VisitType visitType : Context.getVisitTypeService().getAllVisitTypes()) {
			if (ArrayUtils.contains(visitTypeNames, visitType.getName().toLowerCase())) {
				result.add(visitType);
			}
		}
		return result;
	}

	public List<VisitType> getVisitTypesToStop() {
		String gpValue = Context.getAdministrationService().getGlobalProperty(OpenmrsConstants.GP_VISIT_TYPES_TO_AUTO_CLOSE);
		if (StringUtils.isBlank(gpValue)) {
			return Collections.emptyList();
		} else {
			String[] visitTypeNames = getVisitTypeNamesFromGlobalPropertyValue(gpValue);
			return getVisitTypesFromVisitTypeNames(visitTypeNames);
		}
	}

	public String[] getVisitTypeNamesFromGlobalPropertyValue(String commaSeparatedNames) {
		String[] result = StringUtils.split(commaSeparatedNames.trim(), ",");
		for (int i = 0; i < result.length; i++) {
			String currName = result[i];
			result[i] = currName.trim().toLowerCase();
		}
		return result;
	}
}
