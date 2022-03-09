/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.api.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;
import org.openmrs.VisitType;
import org.openmrs.api.APIException;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.VisitDAO;
import org.openmrs.customdatatype.CustomDatatypeUtil;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.PrivilegeConstants;
import org.openmrs.validator.ValidateUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of the {@link VisitService}. This class should not be used on its own. The
 * current OpenMRS implementation should be fetched from the Context.
 *
 * @since 1.9
 */
@Transactional
public class VisitServiceImpl extends BaseOpenmrsService implements VisitService {
	
	private VisitDAO dao;
	
	/**
	 * Method used to inject the visit data access object.
	 *
	 * @param dao the visit data access object.
	 */
	public void setVisitDAO(VisitDAO dao) {
		this.dao = dao;
	}
	
	public VisitDAO getVisitDAO() {
		return dao;
	}
	
	
	
	/**
	 * @see org.openmrs.api.VisitService#getAllVisits()
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Visit> getAllVisits() throws APIException {
		return dao.getVisits(null, null, null, null, null, null, null, null, null, true, false);
	}
	
	/**
	 * @see org.openmrs.api.VisitService#getVisit(java.lang.Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public Visit getVisit(Integer visitId) throws APIException {
		return dao.getVisit(visitId);
	}
	
	/**
	 * @see org.openmrs.api.VisitService#getVisitByUuid(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public Visit getVisitByUuid(String uuid) throws APIException {
		return dao.getVisitByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.api.VisitService#saveVisit(org.openmrs.Visit)
	 */
	@Override
	public Visit saveVisit(Visit visit) throws APIException {
		if (visit.getVisitId() == null) {
			Context.requirePrivilege(PrivilegeConstants.ADD_VISITS);
		} else {
			Context.requirePrivilege(PrivilegeConstants.EDIT_VISITS);
		}
		
		CustomDatatypeUtil.saveAttributesIfNecessary(visit);
		return dao.saveVisit(visit);
	}
	
	/**
	 * @see org.openmrs.api.VisitService#endVisit(org.openmrs.Visit, java.util.Date)
	 */
	@Override
	public Visit endVisit(Visit visit, Date stopDate) {
		if (stopDate == null) {
			stopDate = new Date();
		}
		
		visit.setStopDatetime(stopDate);
		
		return Context.getVisitService().saveVisit(visit);
	}
	
	/**
	 * @see org.openmrs.api.VisitService#voidVisit(org.openmrs.Visit, java.lang.String)
	 */
	@Override
	public Visit voidVisit(Visit visit, String reason) throws APIException {
		return dao.saveVisit(visit);
	}
	
	/**
	 * @see org.openmrs.api.VisitService#unvoidVisit(org.openmrs.Visit)
	 */
	@Override
	public Visit unvoidVisit(Visit visit) throws APIException {
		return Context.getVisitService().saveVisit(visit);
	}
	
	/**
	 * @see org.openmrs.api.VisitService#purgeVisit(org.openmrs.Visit)
	 */
	@Override
	public void purgeVisit(Visit visit) throws APIException {
		if (visit.getVisitId() == null) {
			return;
		}
		if (!Context.getEncounterService().getEncountersByVisit(visit, true).isEmpty()) {
			throw new APIException("Visit.purge.inUse", (Object[]) null);
		}
		dao.deleteVisit(visit);
	}
	
	/**
	 * @see org.openmrs.api.VisitService#getVisits(Collection, Collection, Collection, Collection, Date, Date, Date, Date, Map, boolean, boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Visit> getVisits(Collection<VisitType> visitTypes, Collection<Patient> patients,
	        Collection<Location> locations, Collection<Concept> indications, Date minStartDatetime, Date maxStartDatetime,
	        Date minEndDatetime, Date maxEndDatetime, Map<VisitAttributeType, Object> attributeValues,
	        boolean includeInactive, boolean includeVoided) throws APIException {
		
		Map<VisitAttributeType, String> serializedAttributeValues = CustomDatatypeUtil.getValueReferences(attributeValues);
		return dao.getVisits(visitTypes, patients, locations, indications, minStartDatetime, maxStartDatetime,
		    minEndDatetime, maxEndDatetime, serializedAttributeValues, includeInactive, includeVoided);
	}
	
	/**
	 * @see org.openmrs.api.VisitService#getVisitsByPatient(org.openmrs.Patient)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Visit> getVisitsByPatient(Patient patient) throws APIException {
		//Don't bother to hit the database
		if (patient == null || patient.getId() == null) {
			return Collections.emptyList();
		}
		
		return Context.getVisitService().getVisits(null, Collections.singletonList(patient), null, null, null, null, null,
		    null, null, true, false);
	}
	
	/**
	 * @see org.openmrs.api.VisitService#getActiveVisitsByPatient(org.openmrs.Patient)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Visit> getActiveVisitsByPatient(Patient patient) throws APIException {
		return Context.getVisitService().getVisitsByPatient(patient, false, false);
	}
	
	/**
	 * @see org.openmrs.api.VisitService#getVisitsByPatient(org.openmrs.Patient, boolean, boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Visit> getVisitsByPatient(Patient patient, boolean includeInactive, boolean includeVoided)
	        throws APIException {
		if (patient == null || patient.getId() == null) {
			return Collections.emptyList();
		}
		
		return dao.getVisits(null, Collections.singletonList(patient), null, null, null, null, null, null, null,
		    includeInactive, includeVoided);
	}
	
	/**
	 * @see org.openmrs.api.VisitService#stopVisits(Date)
	 */
	@Override
	public void stopVisits(Date maximumStartDate) {
		
		final List<VisitType> visitTypesToStop = Context.getVisitTypeService().getVisitTypesToStop();
		
		if (maximumStartDate == null) {
			maximumStartDate = new Date();
		}
			
		if (visitTypesToStop.isEmpty()) {
			return;
		}
		
		int counter = 0;
		Date stopDate = new Date();
		Visit nextVisit = dao.getNextVisit(null, visitTypesToStop, maximumStartDate);
		while (nextVisit != null) {
			nextVisit.setStopDatetime(stopDate);
			dao.saveVisit(nextVisit);
			if (counter++ > 50) {
				//ensure changes are persisted to DB before reclaiming memory
				Context.flushSession();
				Context.clearSession();
				counter = 0;
			}
					
		nextVisit = dao.getNextVisit(nextVisit, visitTypesToStop, maximumStartDate);
		}
	}
	

	


}
