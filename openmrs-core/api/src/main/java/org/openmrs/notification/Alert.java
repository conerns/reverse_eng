/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.notification;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.openmrs.Auditable;
import org.openmrs.AuditableOpenmrsObject;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.info.AuditableInfo;

/**
 * Alerts are the simplest form of communication. An Administrator (or script) sets the user or role
 * to attribute the alert to. Alerts are not intended to be sent from user to user and a user cannot
 * send a "reply alert"
 */
public class Alert extends AuditableOpenmrsObject implements Serializable {
	
	private static final long serialVersionUID = -507111111109152L;
	
	public static final int TEXT_MAX_LENGTH = 512;
	
	private Integer alertId;
	
	private String text;

	private Set<AlertRecipient> recipients;
	
	private Date dateToExpire;
	
	private Boolean satisfiedByAny = Boolean.FALSE;
	
	private Boolean alertRead = Boolean.FALSE;

	
	
	/**
	 * Default empty constructor
	 */
	public Alert() {
		auditableInfo = new AuditableInfo();
	}
	
	/**
	 * Initializes an alert with the given alert id
	 */
	public Alert(Integer alertId) {
		this.alertId = alertId;
		auditableInfo = new AuditableInfo();
	}
	
	/**
	 * Convenience constructor to create an alert with the given text and for the given users
	 *
	 * @param text String to display for the alert
	 * @param users Recipients of this alert
	 */
	public Alert(String text, Collection<User> users) {
		setText(text);
		for (User user : users) {
			addRecipient(user);
		}
		auditableInfo = new AuditableInfo();
	}
	
	/**
	 * Convenience constructor to create an alert with the given text and for the given users
	 *
	 * @param text String to display for the alert
	 * @param user Recipient of the alert
	 */
	public Alert(String text, User user) {
		setText(text);
		addRecipient(user);
		auditableInfo = new AuditableInfo();
	}
	
	/**
	 * @return Returns the alertId.
	 */
	public Integer getAlertId() {
		return alertId;
	}
	
	/**
	 * @param alertId The alertId to set.
	 */
	public void setAlertId(Integer alertId) {
		this.alertId = alertId;
	}
	
	/**
	 * @return Returns the date this alert expires
	 */
	public Date getDateToExpire() {
		return dateToExpire;
	}
	
	/**
	 * @param dateToExpire The date To Expire this alert
	 */
	public void setDateToExpire(Date dateToExpire) {
		this.dateToExpire = dateToExpire;
	}
	
	/**
	 * @return Returns the text.
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * @param text The text to set.
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	/**
	 * @see #isSatisfiedByAny()
	 */
	public Boolean getSatisfiedByAny() {
		return isSatisfiedByAny();
	}
	
	/**
	 * @return Returns the satisfiedByAny.
	 */
	public Boolean isSatisfiedByAny() {
		return satisfiedByAny;
	}
	
	/**
	 * @param satisfiedByAny The satisfiedByAny to set.
	 */
	public void setSatisfiedByAny(Boolean satisfiedByAny) {
		this.satisfiedByAny = satisfiedByAny;
	}
	
	/**
	 * @see #isAlertRead()
	 */
	public Boolean getAlertRead() {
		return isAlertRead();
	}
	
	/**
	 * @return Returns the alertRead.
	 */
	public Boolean isAlertRead() {
		return alertRead;
	}
	
	/**
	 * @param alertRead The alertRead to set.
	 */
	public void setAlertRead(Boolean alertRead) {
		this.alertRead = alertRead;
	}
	
	/**
	 * @return Returns the Recipients of this alert
	 */
	public Set<AlertRecipient> getRecipients() {
		return recipients;
	}
	
	/**
	 * @param recipients The recipients of this alert
	 */
	public void setRecipients(Set<AlertRecipient> recipients) {
		this.recipients = recipients;
	}
	
	/**
	 * Convenience method to add the given AlertRecipient to the list of recipients for this alert
	 *
	 * @param r AlertRecipient to add
	 */
	public void addRecipient(AlertRecipient r) {
		if (this.recipients == null) {
			this.recipients = new HashSet<>();
		}
		r.setAlert(this);
		// duplicates are avoided by depending on the .equals and .hashcode
		//  methods of Alert
		recipients.add(r);
	}
	
	/**
	 * Convenience method to add the given user to this list of recipients for this alert
	 *
	 * @param u User to add to list of recipients
	 */
	public void addRecipient(User u) {
		addRecipient(new AlertRecipient(u, false));
	}
	
	/**
	 * Convenience method to remove the given AlertRecipient from this Alert's list of recipients
	 *
	 * @param r user to remove from list of recipients
	 */
	public void removeRecipient(AlertRecipient r) {
		if (recipients != null) {
			recipients.remove(r);
		}
	}
	
	/**
	 * Convenience method to find the AlertRecipient object within this alert that corresponds to
	 * the given <code>recipient</code>
	 *
	 * @param recipient
	 * @return AlertRecipient
	 */
	public AlertRecipient getRecipient(User recipient) {
		if (getRecipients() != null) {
			for (AlertRecipient ar : recipients) {
				if (ar.getRecipient().equals(recipient)) {
					return ar;
				}
			}
		}
		return null;
	}
	
	/**
	 * Convenience method to mark this alert as read. In order to persist this change in the
	 * database, AlertService.saveAlert(Alert) will need to be called after this method is done.
	 *
	 * @return This alert (for chaining and one-liner purposes)
	 * @see org.openmrs.notification.AlertService#saveAlert(Alert)
	 */
	public Alert markAlertRead() {
		User authUser = Context.getAuthenticatedUser();
		
		if (authUser != null) {
			AlertRecipient ar = getRecipient(authUser);
			ar.setAlertRead(true);
			if (isSatisfiedByAny()) {
				setAlertRead(true);
			}
		}
		
		return this;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Alert: #" + alertId;
	}
	
	/**
	 * @since 1.5
	 * @see org.openmrs.OpenmrsObject#getId()
	 */
	@Override
	public Integer getId() {
		return getAlertId();
	}
	
	/**
	 * @since 1.5
	 * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
	 */
	@Override
	public void setId(Integer id) {
		setAlertId(id);
	}
	
}
