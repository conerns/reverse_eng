package org.openmrs.notification;

import org.openmrs.Auditable;
import org.openmrs.User;

import java.util.Date;

public abstract class AuditableOpenmrsObject implements Auditable {
	
	private User creator;

	private Date dateCreated;

	private User changedBy;

	private Date dateChanged;

	@Override
	public User getCreator() {
		return creator;
	}

	@Override
	public void setCreator(User creator) {
		this.creator = creator;
	}

	@Override
	public Date getDateCreated() {
		return dateCreated;
	}

	@Override
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	@Override
	public User getChangedBy() {
		return changedBy;
	}

	@Override
	public void setChangedBy(User changedBy) {
		this.changedBy = changedBy;
	}

	@Override
	public Date getDateChanged() {
		return dateChanged;
	}

	@Override
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
