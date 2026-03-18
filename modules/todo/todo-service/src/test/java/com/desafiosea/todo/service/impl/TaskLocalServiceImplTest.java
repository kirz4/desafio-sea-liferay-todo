package com.desafiosea.todo.service.impl;

import com.desafiosea.todo.exception.TaskDescriptionSizeException;
import com.desafiosea.todo.exception.TaskPermissionException;
import com.desafiosea.todo.exception.TaskTitleRequiredException;
import com.desafiosea.todo.exception.TaskTitleSizeException;
import com.desafiosea.todo.model.Task;
import com.desafiosea.todo.model.impl.TaskImpl;
import com.desafiosea.todo.service.persistence.TaskPersistence;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class TaskLocalServiceImplTest {

	private TaskLocalServiceImpl taskLocalService;
	private CounterLocalService counterLocalService;
	private UserLocalService userLocalService;
	private TaskPersistence taskPersistence;

	@Before
	public void setUp() throws Exception {
		taskLocalService = Mockito.spy(new TaskLocalServiceImpl());

		counterLocalService = Mockito.mock(CounterLocalService.class);
		userLocalService = Mockito.mock(UserLocalService.class);
		taskPersistence = Mockito.mock(TaskPersistence.class);

		_inject(taskLocalService, "_counterLocalService", counterLocalService);
		_inject(taskLocalService, "_userLocalService", userLocalService);
		_inject(taskLocalService, "taskPersistence", taskPersistence);
	}

	@Test
	public void shouldAddRootTaskSuccessfully() throws Exception {
		User user = Mockito.mock(User.class);

		Mockito.when(userLocalService.getUser(1L)).thenReturn(user);
		Mockito.when(user.getCompanyId()).thenReturn(200L);
		Mockito.when(user.getFullName()).thenReturn("Lucas");
		Mockito.when(counterLocalService.increment(Task.class.getName())).thenReturn(10L);

		Task task = new TaskImpl();
		task.setTaskId(10L);

		Mockito.doReturn(task).when(taskLocalService).createTask(10L);
		Mockito.doReturn(task).when(taskLocalService).addTask(task);

		Task result = taskLocalService.addTask(
			1L, 300L, "Minha tarefa", "Descrição", false, 500L);

		Assert.assertNotNull(result);
		Assert.assertEquals(10L, result.getTaskId());
		Assert.assertEquals(300L, result.getGroupId());
		Assert.assertEquals(200L, result.getCompanyId());
		Assert.assertEquals(1L, result.getUserId());
		Assert.assertEquals("Lucas", result.getUserName());
		Assert.assertEquals("Minha tarefa", result.getTitle());
		Assert.assertEquals("Descrição", result.getDescription());
		Assert.assertFalse(result.isDone());
		Assert.assertEquals(500L, result.getFileEntryId());
		Assert.assertEquals(0L, result.getParentTaskId());
		Assert.assertNotNull(result.getCreateDate());
		Assert.assertNotNull(result.getModifiedDate());
	}

	@Test
	public void shouldTrimTitleAndDescriptionWhenAddingTask() throws Exception {
		User user = Mockito.mock(User.class);

		Mockito.when(userLocalService.getUser(1L)).thenReturn(user);
		Mockito.when(user.getCompanyId()).thenReturn(200L);
		Mockito.when(user.getFullName()).thenReturn("Lucas");
		Mockito.when(counterLocalService.increment(Task.class.getName())).thenReturn(20L);

		Task task = new TaskImpl();
		task.setTaskId(20L);

		Mockito.doReturn(task).when(taskLocalService).createTask(20L);
		Mockito.doReturn(task).when(taskLocalService).addTask(task);

		Task result = taskLocalService.addTask(
			1L, 300L, "  Minha tarefa  ", "  Descrição  ", false, 0L);

		Assert.assertEquals("Minha tarefa", result.getTitle());
		Assert.assertEquals("Descrição", result.getDescription());
	}

	@Test
	public void shouldNormalizeNullDescriptionWhenAddingTask() throws Exception {
		User user = Mockito.mock(User.class);

		Mockito.when(userLocalService.getUser(1L)).thenReturn(user);
		Mockito.when(user.getCompanyId()).thenReturn(200L);
		Mockito.when(user.getFullName()).thenReturn("Lucas");
		Mockito.when(counterLocalService.increment(Task.class.getName())).thenReturn(21L);

		Task task = new TaskImpl();
		task.setTaskId(21L);

		Mockito.doReturn(task).when(taskLocalService).createTask(21L);
		Mockito.doReturn(task).when(taskLocalService).addTask(task);

		Task result = taskLocalService.addTask(
			1L, 300L, "Tarefa", null, false, 0L);

		Assert.assertEquals("", result.getDescription());
	}

	@Test
	public void shouldAddSubtaskSuccessfully() throws Exception {
		User user = Mockito.mock(User.class);

		Mockito.when(userLocalService.getUser(1L)).thenReturn(user);
		Mockito.when(user.getCompanyId()).thenReturn(200L);
		Mockito.when(user.getFullName()).thenReturn("Lucas");
		Mockito.when(counterLocalService.increment(Task.class.getName())).thenReturn(11L);

		Task task = new TaskImpl();
		task.setTaskId(11L);

		Mockito.doReturn(task).when(taskLocalService).createTask(11L);
		Mockito.doReturn(task).when(taskLocalService).addTask(task);

		Task result = taskLocalService.addTask(
			1L, 300L, "Subtarefa", "Desc", false, 0L, 99L);

		Assert.assertEquals(11L, result.getTaskId());
		Assert.assertEquals(99L, result.getParentTaskId());
	}

	@Test(expected = TaskTitleRequiredException.class)
	public void shouldThrowWhenTitleIsBlank() throws Exception {
		taskLocalService.addTask(1L, 300L, "", "Descrição", false, 0L);
	}

	@Test(expected = TaskTitleSizeException.class)
	public void shouldThrowWhenTitleIsTooShort() throws Exception {
		taskLocalService.addTask(1L, 300L, "ab", "Descrição", false, 0L);
	}

	@Test(expected = TaskTitleSizeException.class)
	public void shouldThrowWhenTitleIsTooLong() throws Exception {
		StringBuilder longTitle = new StringBuilder();

		for (int i = 0; i < 101; i++) {
			longTitle.append("a");
		}

		taskLocalService.addTask(1L, 300L, longTitle.toString(), "Descrição", false, 0L);
	}

	@Test(expected = TaskDescriptionSizeException.class)
	public void shouldThrowWhenDescriptionIsTooLong() throws Exception {
		StringBuilder longDescription = new StringBuilder();

		for (int i = 0; i < 501; i++) {
			longDescription.append("a");
		}

		taskLocalService.addTask(
			1L, 300L, "Título válido", longDescription.toString(), false, 0L);
	}

	@Test
	public void shouldUpdateTaskSuccessfully() throws Exception {
		Task task = new TaskImpl();
		task.setTaskId(50L);
		task.setUserId(1L);
		task.setTitle("Antigo");
		task.setDescription("Antiga");
		task.setDone(false);
		task.setFileEntryId(0L);

		Mockito.doReturn(task).when(taskLocalService).getTask(50L);
		Mockito.doReturn(task).when(taskLocalService).updateTask(task);

		Task result = taskLocalService.updateTask(
			1L, 50L, "Novo título", "Nova descrição", true, 77L);

		Assert.assertEquals("Novo título", result.getTitle());
		Assert.assertEquals("Nova descrição", result.getDescription());
		Assert.assertTrue(result.isDone());
		Assert.assertEquals(77L, result.getFileEntryId());
		Assert.assertNotNull(result.getModifiedDate());
	}

	@Test
	public void shouldNormalizeNullDescriptionWhenUpdatingTask() throws Exception {
		Task task = new TaskImpl();
		task.setTaskId(51L);
		task.setUserId(1L);
		task.setTitle("Antigo");
		task.setDescription("Antiga");

		Mockito.doReturn(task).when(taskLocalService).getTask(51L);
		Mockito.doReturn(task).when(taskLocalService).updateTask(task);

		Task result = taskLocalService.updateTask(
			1L, 51L, "Novo título", null, false, 0L);

		Assert.assertEquals("", result.getDescription());
	}

	@Test(expected = TaskPermissionException.class)
	public void shouldNotUpdateTaskFromAnotherUser() throws Exception {
		Task task = new TaskImpl();
		task.setTaskId(50L);
		task.setUserId(999L);

		Mockito.doReturn(task).when(taskLocalService).getTask(50L);

		taskLocalService.updateTask(
			1L, 50L, "Novo título", "Nova descrição", true, 77L);
	}

	@Test
	public void shouldToggleTaskStatus() throws Exception {
		Task task = new TaskImpl();
		task.setTaskId(70L);
		task.setUserId(1L);
		task.setDone(false);

		Mockito.doReturn(task).when(taskLocalService).getTask(70L);
		Mockito.doReturn(task).when(taskLocalService).updateTask(task);

		Task result = taskLocalService.toggleTaskStatus(1L, 70L);

		Assert.assertTrue(result.isDone());
	}

	@Test
	public void shouldToggleTaskStatusTwice() throws Exception {
		Task task = new TaskImpl();
		task.setTaskId(71L);
		task.setUserId(1L);
		task.setDone(false);

		Mockito.doReturn(task).when(taskLocalService).getTask(71L);
		Mockito.doReturn(task).when(taskLocalService).updateTask(task);

		Task firstToggle = taskLocalService.toggleTaskStatus(1L, 71L);
		Assert.assertTrue(firstToggle.isDone());

		Task secondToggle = taskLocalService.toggleTaskStatus(1L, 71L);
		Assert.assertFalse(secondToggle.isDone());
	}

	@Test(expected = TaskPermissionException.class)
	public void shouldNotToggleTaskFromAnotherUser() throws Exception {
		Task task = new TaskImpl();
		task.setTaskId(90L);
		task.setUserId(999L);
		task.setDone(false);

		Mockito.doReturn(task).when(taskLocalService).getTask(90L);

		taskLocalService.toggleTaskStatus(1L, 90L);
	}

	@Test
	public void shouldDeleteTaskAndItsSubtasks() throws Exception {
		Task parent = new TaskImpl();
		parent.setTaskId(10L);
		parent.setUserId(1L);

		Task child1 = new TaskImpl();
		child1.setTaskId(11L);
		child1.setUserId(1L);

		Task child2 = new TaskImpl();
		child2.setTaskId(12L);
		child2.setUserId(1L);

		Mockito.doReturn(parent).when(taskLocalService).getTask(10L);
		Mockito.when(taskPersistence.findByU_P(1L, 10L)).thenReturn(
			Arrays.asList(child1, child2));
		Mockito.doReturn(child1).when(taskLocalService).deleteTask(child1);
		Mockito.doReturn(child2).when(taskLocalService).deleteTask(child2);
		Mockito.doReturn(parent).when(taskLocalService).deleteTask(parent);

		Task result = taskLocalService.deleteTaskById(1L, 10L);

		Assert.assertEquals(10L, result.getTaskId());

		Mockito.verify(taskLocalService).deleteTask(child1);
		Mockito.verify(taskLocalService).deleteTask(child2);
		Mockito.verify(taskLocalService).deleteTask(parent);
	}

	@Test
	public void shouldDeleteTaskWithoutSubtasks() throws Exception {
		Task parent = new TaskImpl();
		parent.setTaskId(30L);
		parent.setUserId(1L);

		Mockito.doReturn(parent).when(taskLocalService).getTask(30L);
		Mockito.when(taskPersistence.findByU_P(1L, 30L)).thenReturn(Collections.emptyList());
		Mockito.doReturn(parent).when(taskLocalService).deleteTask(parent);

		Task result = taskLocalService.deleteTaskById(1L, 30L);

		Assert.assertEquals(30L, result.getTaskId());
		Mockito.verify(taskLocalService).deleteTask(parent);
	}

	@Test(expected = TaskPermissionException.class)
	public void shouldNotDeleteTaskFromAnotherUser() throws Exception {
		Task task = new TaskImpl();
		task.setTaskId(31L);
		task.setUserId(999L);

		Mockito.doReturn(task).when(taskLocalService).getTask(31L);

		taskLocalService.deleteTaskById(1L, 31L);
	}

	@Test
	public void shouldGetTasksByUserId() {
		List<Task> expected = Collections.singletonList(new TaskImpl());

		Mockito.when(taskPersistence.findByUserId(1L)).thenReturn(expected);

		List<Task> result = taskLocalService.getTasksByUserId(1L);

		Assert.assertEquals(expected, result);
	}

	@Test
	public void shouldGetRootTasksByUserId() {
		List<Task> expected = Collections.singletonList(new TaskImpl());

		Mockito.when(taskPersistence.findByU_P(1L, 0L)).thenReturn(expected);

		List<Task> result = taskLocalService.getRootTasksByUserId(1L);

		Assert.assertEquals(expected, result);
	}

	@Test
	public void shouldReturnEmptyListWhenNoRootTasksExist() {
		Mockito.when(taskPersistence.findByU_P(1L, 0L)).thenReturn(Collections.emptyList());

		List<Task> result = taskLocalService.getRootTasksByUserId(1L);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void shouldGetSubtasksByParentTaskId() {
		List<Task> expected = Collections.singletonList(new TaskImpl());

		Mockito.when(taskPersistence.findByU_P(1L, 99L)).thenReturn(expected);

		List<Task> result = taskLocalService.getSubtasksByParentTaskId(1L, 99L);

		Assert.assertEquals(expected, result);
	}

	private void _inject(Object target, String fieldName, Object value)
		throws Exception {

		Field field = null;
		Class<?> clazz = target.getClass();

		while (clazz != null) {
			try {
				field = clazz.getDeclaredField(fieldName);
				break;
			}
			catch (NoSuchFieldException e) {
				clazz = clazz.getSuperclass();
			}
		}

		if (field == null) {
			throw new NoSuchFieldException(fieldName);
		}

		field.setAccessible(true);
		field.set(target, value);
	}
}