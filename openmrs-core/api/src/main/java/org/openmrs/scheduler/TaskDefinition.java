/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.scheduler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.openmrs.BaseChangeableOpenmrsMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the metadata for a task that can be scheduled.
 */
public class TaskDefinition extends BaseChangeableOpenmrsMetadata {
	
	private static final Logger log = LoggerFactory.getLogger(TaskDefinition.class);
	
	// Task metadata
	private Integer id;
	
	// This class must implement the schedulable interface or it will fail to start
	private String taskClass;
	
	private Task taskInstance = null;
	
	// Relationships
	private Map<String, String> properties;

	private Boolean started;
	
	private TaskMetadata taskMetadata;
	
	/**
	 * Default no-arg public constructor
	 */
	public TaskDefinition() {
		this.started = Boolean.FALSE; // default
		// as we have a default date populated
		this.properties = new HashMap<>();
		
		taskMetadata = new TaskMetadata();
		taskMetadata.setStartTime(new Date());
		
	}

	public TaskMetadata getTaskMetadata() {
		return taskMetadata;
	}

	public void setTaskMetadata(TaskMetadata taskMetadata) {
		this.taskMetadata = taskMetadata;
	}

	/**
	 * Public constructor
	 */
	public TaskDefinition(Integer id, String name, String description, String taskClass) {
		this();
		log.debug("Creating taskconfig: " + id);
		this.id = id;
		setName(name);
		setDescription(description);
		this.taskClass = taskClass;

		taskMetadata = new TaskMetadata();
	}
	
	/**
	 * Get the task identifier.
	 * 
	 * @return <code>Integer</code> identifier of the task
	 */
	@Override
	public Integer getId() {
		return this.id;
	}
	
	/**
	 * Set the task identifier.
	 * 
	 * @param id
	 */
	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	/**
	 * Get the data map used to provide the task with runtime data.
	 * 
	 * @return the data map
	 */
	public Map<String, String> getProperties() {
		return this.properties;
	}
	
	/**
	 * Set the properties of the task. This overrides any properties previously set with the
	 * setProperty(String, String) method.
	 * 
	 * @param properties <code>Map&lt;String, String&gt;</code> of the properties to set
	 */
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
	
	/**
	 * Get the schedulable object to be executed.
	 * 
	 * @return the schedulable object
	 */
	public String getTaskClass() {
		return this.taskClass;
	}
	
	/**
	 * Set the schedulable object to be executed.
	 * 
	 * @param taskClass <code>String</code> taskClass of a schedulable object
	 */
	public void setTaskClass(String taskClass) {
		this.taskClass = taskClass;
	}
	
	/**
	 * Gets the flag that indicates whether the task has been started.
	 */
	public Boolean getStarted() {
		return this.started;
	}
	
	/**
	 * Sets the flag that indicates whether the task has been started.
	 */
	public void setStarted(Boolean started) {
		this.started = started;
	}
	
	/**
	 * Get task configuration property.
	 * 
	 * @param key the <code>String</code> key of the property to get
	 * @return the <code>String</code> value for the given key
	 */
	public String getProperty(String key) {
		return this.properties.get(key);
	}
	
	/**
	 * Set task configuration property. Only supports strings at the moment.
	 * 
	 * @param key the <code>String</code> key of the property to set
	 * @param value the <code>String</code> value of the property to set
	 */
	public void setProperty(String key, String value) {
		this.properties.put(key, value);
	}
	
	/**
	 * Convenience method that asks SchedulerUtil for it's next execution time.
	 * 
	 * @return the <code>Date</code> of the next execution
	 */
	public Date getNextExecutionTime() {
		return SchedulerUtil.getNextExecution(this);
	}
	
	/**
	 * Convenience method to calculate the seconds until the next execution time.
	 * 
	 * @return the number of seconds until the next execution
	 */
	public long getSecondsUntilNextExecutionTime() {
		return (getNextExecutionTime().getTime() - System.currentTimeMillis()) / 1000;
		
	}
	
	// ==================================   Metadata ============================
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[TaskDefinition " + " id=" + getId() + " name=" + getName() + " class=" + getTaskClass() + " startTime="
		        + taskMetadata.getStartTime() + " repeatInterval=" + taskMetadata.getRepeatInterval() + " secondsUntilNext="
		        + this.getSecondsUntilNextExecutionTime() + "]";
	}
	
	/**
	 * Gets the runnable task instance associated with this definition.
	 * 
	 * @return related task, or null if none instantiated (definition hasn't been scheduled)
	 */
	public Task getTaskInstance() {
		return taskInstance;
	}
	
	/**
	 * Sets the runnable task instance associated with this definition. This should be set by the
	 * scheduler which instantiates the task.
	 * 
	 * @param taskInstance
	 */
	public void setTaskInstance(Task taskInstance) {
		this.taskInstance = taskInstance;
	}
}
