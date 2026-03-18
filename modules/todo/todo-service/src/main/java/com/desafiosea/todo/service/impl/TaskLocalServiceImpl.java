package com.desafiosea.todo.service.impl;

import com.desafiosea.todo.exception.TaskDescriptionSizeException;
import com.desafiosea.todo.exception.TaskPermissionException;
import com.desafiosea.todo.exception.TaskTitleRequiredException;
import com.desafiosea.todo.exception.TaskTitleSizeException;
import com.desafiosea.todo.model.Task;
import com.desafiosea.todo.service.base.TaskLocalServiceBaseImpl;

import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Validator;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	property = "model.class.name=com.desafiosea.todo.model.Task",
	service = AopService.class
)
public class TaskLocalServiceImpl extends TaskLocalServiceBaseImpl {

	private static final int TITLE_MIN_LENGTH = 3;
	private static final int TITLE_MAX_LENGTH = 100;
	private static final int DESCRIPTION_MAX_LENGTH = 500;

	public Task addTask(
		long userId, long groupId, String title, String description,
		boolean done, long fileEntryId) throws PortalException {

		validate(title, description);

		User user = _userLocalService.getUser(userId);

		long taskId = _counterLocalService.increment(Task.class.getName());

		Task task = createTask(taskId);

		Date now = new Date();

		task.setGroupId(groupId);
		task.setCompanyId(user.getCompanyId());
		task.setUserId(userId);
		task.setUserName(user.getFullName());
		task.setCreateDate(now);
		task.setModifiedDate(now);

		task.setTitle(title.trim());
		task.setDescription(normalizeDescription(description));
		task.setDone(done);
		task.setFileEntryId(fileEntryId);

		return addTask(task);
	}

	public Task updateTask(
		long userId, long taskId, String title, String description,
		boolean done, long fileEntryId) throws PortalException {

		validate(title, description);

		Task task = getTask(taskId);

		validateOwnership(userId, task);

		task.setTitle(title.trim());
		task.setDescription(normalizeDescription(description));
		task.setDone(done);
		task.setFileEntryId(fileEntryId);
		task.setModifiedDate(new Date());

		return updateTask(task);
	}

	public Task deleteTaskById(long userId, long taskId) throws PortalException {
		Task task = getTask(taskId);

		validateOwnership(userId, task);

		return deleteTask(task);
	}

	public Task toggleTaskStatus(long userId, long taskId) throws PortalException {
		Task task = getTask(taskId);

		validateOwnership(userId, task);

		task.setDone(!task.isDone());
		task.setModifiedDate(new Date());

		return updateTask(task);
	}

	public List<Task> getTasksByUserId(long userId) {
		return taskPersistence.findByUserId(userId);
	}

	private void validate(String title, String description) throws PortalException {
		if (Validator.isBlank(title)) {
			throw new TaskTitleRequiredException();
		}

		String normalizedTitle = title.trim();

		if (normalizedTitle.length() < TITLE_MIN_LENGTH ||
			normalizedTitle.length() > TITLE_MAX_LENGTH) {

			throw new TaskTitleSizeException();
		}

		String normalizedDescription = normalizeDescription(description);

		if (normalizedDescription.length() > DESCRIPTION_MAX_LENGTH) {
			throw new TaskDescriptionSizeException();
		}
	}

	private void validateOwnership(long userId, Task task) throws PortalException {
		if (task.getUserId() != userId) {
			throw new TaskPermissionException();
		}
	}

	private String normalizeDescription(String description) {
		if (Validator.isNull(description)) {
			return "";
		}

		return description.trim();
	}

	@Reference
	private CounterLocalService _counterLocalService;

	@Reference
	private UserLocalService _userLocalService;

}