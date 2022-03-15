package org.openmrs.info;

import org.openmrs.Auditable;
import org.openmrs.User;

import java.util.Date;

public class AuditableInfo implements Auditable {
	
	private Integer auditableInfoId;
	
	private User creator;

	private Date dateCreated;

	private User changedBy;

	private Date dateChanged;
	
	
	public AuditableInfo(){}

	public Integer getAuditableInfoId() {
		return auditableInfoId;
	}

	public void setAuditableInfoId(Integer auditableInfoId) {
		this.auditableInfoId = auditableInfoId;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public User getChangedBy() {
		return changedBy;
	}

	public void setChangedBy(User changedBy) {
		this.changedBy = changedBy;
	}

	public Date getDateChanged() {
		return dateChanged;
	}

	public void setDateChanged(Date dateChanged) {
		this.dateChanged = dateChanged;
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
