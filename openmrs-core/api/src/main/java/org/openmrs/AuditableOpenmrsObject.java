package org.openmrs;

import org.openmrs.info.AuditableInfo;

public abstract class AuditableOpenmrsObject extends BaseOpenmrsObject {
	
	protected AuditableInfo auditableInfo;

	public AuditableInfo getAuditableInfo() {
		return auditableInfo;
	}

	public void setAuditableInfo(AuditableInfo auditableInfo) {
		this.auditableInfo = auditableInfo;
	}

	

	@Override
	public Integer getId() {
		return null;
	}

	@Override
	public void setId(Integer id) {

	}

	@Override
	public String getUuid() {
		return null;
	}

	@Override
	public void setUuid(String uuid) {

	}
}
