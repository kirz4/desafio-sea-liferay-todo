package com.desafiosea.todo.service.impl;

import com.desafiosea.todo.model.Task;
import com.desafiosea.todo.service.base.TaskLocalServiceBaseImpl;

import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	property = "model.class.name=com.desafiosea.todo.model.Task",
	service = AopService.class
)
public class TaskLocalServiceImpl extends TaskLocalServiceBaseImpl {

	public Task addTask(
		long userId, long groupId, String title, String description,
		boolean done) throws PortalException {

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

		task.setTitle(title);
		task.setDescription(description);
		task.setDone(done);

		return addTask(task);
	}

	public Task updateTask(
		long taskId, String title, String description, boolean done)
		throws PortalException {

		Task task = getTask(taskId);

		task.setTitle(title);
		task.setDescription(description);
		task.setDone(done);
		task.setModifiedDate(new Date());

		return updateTask(task);
	}

	public Task deleteTaskById(long taskId) throws PortalException {
		Task task = getTask(taskId);

		return deleteTask(task);
	}

	public Task toggleTaskStatus(long taskId) throws PortalException {
	Task task = getTask(taskId);

	task.setDone(!task.isDone());
	task.setModifiedDate(new Date());

	return updateTask(task);
	}

	public List<Task> getTasksByUserId(long userId) {
		return taskPersistence.findByUserId(userId);
	}

	@Reference
	private CounterLocalService _counterLocalService;

	@Reference
	private UserLocalService _userLocalService;

}