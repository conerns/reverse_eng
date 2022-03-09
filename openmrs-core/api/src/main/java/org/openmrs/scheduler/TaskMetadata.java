package org.openmrs.scheduler;

import org.openmrs.BaseChangeableOpenmrsMetadata;

import java.util.Date;

public class TaskMetadata extends BaseChangeableOpenmrsMetadata {

	private Integer id;
	
	// Scheduling metadata
	private Date startTime;

	private Date lastExecutionTime;

	private Long repeatInterval;

	private Boolean startOnStartup;

	private String startTimePattern;

	
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getLastExecutionTime() {
		return lastExecutionTime;
	}

	public void setLastExecutionTime(Date lastExecutionTime) {
		this.lastExecutionTime = lastExecutionTime;
	}

	public Long getRepeatInterval() {
		return repeatInterval;
	}

	public void setRepeatInterval(Long repeatInterval) {
		this.repeatInterval = repeatInterval;
	}

	public Boolean getStartOnStartup() {
		return startOnStartup;
	}

	public void setStartOnStartup(Boolean startOnStartup) {
		this.startOnStartup = startOnStartup;
	}

	public String getStartTimePattern() {
		return startTimePattern;
	}

	public void setStartTimePattern(String startTimePattern) {
		this.startTimePattern = startTimePattern;
	}

	@Override
	public Integer getId() {
		return null;
	}

	@Override
	public void setId(Integer id) {

	}
}
