/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.desafiosea.todo.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link Task}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see Task
 * @generated
 */
public class TaskWrapper
	extends BaseModelWrapper<Task> implements ModelWrapper<Task>, Task {

	public TaskWrapper(Task task) {
		super(task);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("taskId", getTaskId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("title", getTitle());
		attributes.put("description", getDescription());
		attributes.put("done", isDone());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long taskId = (Long)attributes.get("taskId");

		if (taskId != null) {
			setTaskId(taskId);
		}

		Long groupId = (Long)attributes.get("groupId");

		if (groupId != null) {
			setGroupId(groupId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long userId = (Long)attributes.get("userId");

		if (userId != null) {
			setUserId(userId);
		}

		String userName = (String)attributes.get("userName");

		if (userName != null) {
			setUserName(userName);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		String title = (String)attributes.get("title");

		if (title != null) {
			setTitle(title);
		}

		String description = (String)attributes.get("description");

		if (description != null) {
			setDescription(description);
		}

		Boolean done = (Boolean)attributes.get("done");

		if (done != null) {
			setDone(done);
		}
	}

	@Override
	public Task cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this task.
	 *
	 * @return the company ID of this task
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this task.
	 *
	 * @return the create date of this task
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the description of this task.
	 *
	 * @return the description of this task
	 */
	@Override
	public String getDescription() {
		return model.getDescription();
	}

	/**
	 * Returns the done of this task.
	 *
	 * @return the done of this task
	 */
	@Override
	public boolean getDone() {
		return model.getDone();
	}

	/**
	 * Returns the group ID of this task.
	 *
	 * @return the group ID of this task
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the modified date of this task.
	 *
	 * @return the modified date of this task
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the primary key of this task.
	 *
	 * @return the primary key of this task
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the task ID of this task.
	 *
	 * @return the task ID of this task
	 */
	@Override
	public long getTaskId() {
		return model.getTaskId();
	}

	/**
	 * Returns the title of this task.
	 *
	 * @return the title of this task
	 */
	@Override
	public String getTitle() {
		return model.getTitle();
	}

	/**
	 * Returns the user ID of this task.
	 *
	 * @return the user ID of this task
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this task.
	 *
	 * @return the user name of this task
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this task.
	 *
	 * @return the user uuid of this task
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns <code>true</code> if this task is done.
	 *
	 * @return <code>true</code> if this task is done; <code>false</code> otherwise
	 */
	@Override
	public boolean isDone() {
		return model.isDone();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the company ID of this task.
	 *
	 * @param companyId the company ID of this task
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this task.
	 *
	 * @param createDate the create date of this task
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the description of this task.
	 *
	 * @param description the description of this task
	 */
	@Override
	public void setDescription(String description) {
		model.setDescription(description);
	}

	/**
	 * Sets whether this task is done.
	 *
	 * @param done the done of this task
	 */
	@Override
	public void setDone(boolean done) {
		model.setDone(done);
	}

	/**
	 * Sets the group ID of this task.
	 *
	 * @param groupId the group ID of this task
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the modified date of this task.
	 *
	 * @param modifiedDate the modified date of this task
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the primary key of this task.
	 *
	 * @param primaryKey the primary key of this task
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the task ID of this task.
	 *
	 * @param taskId the task ID of this task
	 */
	@Override
	public void setTaskId(long taskId) {
		model.setTaskId(taskId);
	}

	/**
	 * Sets the title of this task.
	 *
	 * @param title the title of this task
	 */
	@Override
	public void setTitle(String title) {
		model.setTitle(title);
	}

	/**
	 * Sets the user ID of this task.
	 *
	 * @param userId the user ID of this task
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this task.
	 *
	 * @param userName the user name of this task
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this task.
	 *
	 * @param userUuid the user uuid of this task
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected TaskWrapper wrap(Task task) {
		return new TaskWrapper(task);
	}

}