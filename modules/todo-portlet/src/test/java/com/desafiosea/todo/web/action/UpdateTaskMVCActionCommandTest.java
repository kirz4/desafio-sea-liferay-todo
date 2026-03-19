package com.desafiosea.todo.web.action;

import com.desafiosea.todo.exception.TaskDescriptionSizeException;
import com.desafiosea.todo.exception.TaskPermissionException;
import com.desafiosea.todo.exception.TaskTitleRequiredException;
import com.desafiosea.todo.exception.TaskTitleSizeException;
import com.desafiosea.todo.model.Task;
import com.desafiosea.todo.service.TaskLocalService;
import com.desafiosea.todo.web.service.TaskActionFeedbackService;
import com.desafiosea.todo.web.service.TaskImageUploadService;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import java.lang.reflect.Field;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class UpdateTaskMVCActionCommandTest {

	private UpdateTaskMVCActionCommand updateTaskMVCActionCommand;
	private TaskActionFeedbackService taskActionFeedbackService;
	private TaskImageUploadService taskImageUploadService;
	private TaskLocalService taskLocalService;

	private ActionRequest actionRequest;
	private ActionResponse actionResponse;
	private ThemeDisplay themeDisplay;
	private User user;
	private Task task;

	@Before
	public void setUp() throws Exception {
		updateTaskMVCActionCommand = new UpdateTaskMVCActionCommand();

		taskActionFeedbackService = Mockito.mock(TaskActionFeedbackService.class);
		taskImageUploadService = Mockito.mock(TaskImageUploadService.class);
		taskLocalService = Mockito.mock(TaskLocalService.class);

		actionRequest = Mockito.mock(ActionRequest.class);
		actionResponse = Mockito.mock(ActionResponse.class);
		themeDisplay = Mockito.mock(ThemeDisplay.class);
		user = Mockito.mock(User.class);
		task = Mockito.mock(Task.class);

		_inject(
			updateTaskMVCActionCommand, "_taskActionFeedbackService",
			taskActionFeedbackService);
		_inject(
			updateTaskMVCActionCommand, "_taskImageUploadService",
			taskImageUploadService);
		_inject(
			updateTaskMVCActionCommand, "_taskLocalService",
			taskLocalService);

		Mockito.when(actionRequest.getAttribute(WebKeys.THEME_DISPLAY)).thenReturn(
			themeDisplay);
		Mockito.when(themeDisplay.getUser()).thenReturn(user);
		Mockito.when(themeDisplay.getScopeGroupId()).thenReturn(20117L);
		Mockito.when(user.getUserId()).thenReturn(1L);

		Mockito.when(actionRequest.getParameter("taskId")).thenReturn("10");
		Mockito.when(actionRequest.getParameter("title")).thenReturn(
			"Tarefa atualizada");
		Mockito.when(actionRequest.getParameter("description")).thenReturn(
			"Nova descrição");
		Mockito.when(actionRequest.getParameter("done")).thenReturn("true");
		Mockito.when(actionRequest.getParameter("filter")).thenReturn("all");

		Mockito.when(taskLocalService.getTask(10L)).thenReturn(task);
		Mockito.when(task.getFileEntryId()).thenReturn(777L);
	}

	@Test
	public void shouldUpdateTaskSuccessfullyKeepingOldImage() throws Exception {
		Mockito.when(
			taskImageUploadService.uploadTaskImage(actionRequest, themeDisplay)
		).thenReturn(0L);

		boolean result = updateTaskMVCActionCommand.processAction(
			actionRequest, actionResponse);

		Assert.assertTrue(result);

		Mockito.verify(taskLocalService).updateTask(
			1L, 10L, "Tarefa atualizada", "Nova descrição", true, 777L);

		Mockito.verifyNoInteractions(taskActionFeedbackService);
	}

	@Test
	public void shouldUpdateTaskSuccessfullyWithNewImage() throws Exception {
		Mockito.when(
			taskImageUploadService.uploadTaskImage(actionRequest, themeDisplay)
		).thenReturn(999L);

		boolean result = updateTaskMVCActionCommand.processAction(
			actionRequest, actionResponse);

		Assert.assertTrue(result);

		Mockito.verify(taskLocalService).updateTask(
			1L, 10L, "Tarefa atualizada", "Nova descrição", true, 999L);

		Mockito.verifyNoInteractions(taskActionFeedbackService);
	}

	@Test
	public void shouldHandleTitleRequiredException() throws Exception {
		Mockito.when(
			taskImageUploadService.uploadTaskImage(actionRequest, themeDisplay)
		).thenReturn(0L);

		Mockito.doThrow(new TaskTitleRequiredException()).when(taskLocalService)
			.updateTask(
				Mockito.eq(1L),
				Mockito.eq(10L),
				Mockito.eq("Tarefa atualizada"),
				Mockito.eq("Nova descrição"),
				Mockito.eq(true),
				Mockito.anyLong());

		boolean result = updateTaskMVCActionCommand.processAction(
			actionRequest, actionResponse);

		Assert.assertTrue(result);

		Mockito.verify(taskActionFeedbackService).addError(
			actionRequest, "task-title-required");
		Mockito.verify(taskActionFeedbackService).hideDefaultErrorMessage(
			actionRequest);
		Mockito.verify(taskActionFeedbackService).prepareFormRender(
			actionRequest, actionResponse);
		Mockito.verify(actionResponse).setRenderParameter(
			"mvcRenderCommandName", "/task/edit-form");
		Mockito.verify(actionResponse).setRenderParameter("taskId", "10");
	}

	@Test
	public void shouldHandleTitleSizeException() throws Exception {
		Mockito.when(
			taskImageUploadService.uploadTaskImage(actionRequest, themeDisplay)
		).thenReturn(0L);

		Mockito.doThrow(new TaskTitleSizeException()).when(taskLocalService)
			.updateTask(
				Mockito.eq(1L),
				Mockito.eq(10L),
				Mockito.eq("Tarefa atualizada"),
				Mockito.eq("Nova descrição"),
				Mockito.eq(true),
				Mockito.anyLong());

		boolean result = updateTaskMVCActionCommand.processAction(
			actionRequest, actionResponse);

		Assert.assertTrue(result);

		Mockito.verify(taskActionFeedbackService).addError(
			actionRequest, "task-title-size");
		Mockito.verify(taskActionFeedbackService).hideDefaultErrorMessage(
			actionRequest);
		Mockito.verify(taskActionFeedbackService).prepareFormRender(
			actionRequest, actionResponse);
		Mockito.verify(actionResponse).setRenderParameter(
			"mvcRenderCommandName", "/task/edit-form");
		Mockito.verify(actionResponse).setRenderParameter("taskId", "10");
	}

	@Test
	public void shouldHandleDescriptionSizeException() throws Exception {
		Mockito.when(
			taskImageUploadService.uploadTaskImage(actionRequest, themeDisplay)
		).thenReturn(0L);

		Mockito.doThrow(new TaskDescriptionSizeException()).when(taskLocalService)
			.updateTask(
				Mockito.eq(1L),
				Mockito.eq(10L),
				Mockito.eq("Tarefa atualizada"),
				Mockito.eq("Nova descrição"),
				Mockito.eq(true),
				Mockito.anyLong());

		boolean result = updateTaskMVCActionCommand.processAction(
			actionRequest, actionResponse);

		Assert.assertTrue(result);

		Mockito.verify(taskActionFeedbackService).addError(
			actionRequest, "task-description-size");
		Mockito.verify(taskActionFeedbackService).hideDefaultErrorMessage(
			actionRequest);
		Mockito.verify(taskActionFeedbackService).prepareFormRender(
			actionRequest, actionResponse);
		Mockito.verify(actionResponse).setRenderParameter(
			"mvcRenderCommandName", "/task/edit-form");
		Mockito.verify(actionResponse).setRenderParameter("taskId", "10");
	}

	@Test
	public void shouldHandlePermissionException() throws Exception {
		Mockito.when(
			taskImageUploadService.uploadTaskImage(actionRequest, themeDisplay)
		).thenReturn(0L);

		Mockito.doThrow(new TaskPermissionException()).when(taskLocalService)
			.updateTask(
				Mockito.eq(1L),
				Mockito.eq(10L),
				Mockito.eq("Tarefa atualizada"),
				Mockito.eq("Nova descrição"),
				Mockito.eq(true),
				Mockito.anyLong());

		boolean result = updateTaskMVCActionCommand.processAction(
			actionRequest, actionResponse);

		Assert.assertTrue(result);

		Mockito.verify(taskActionFeedbackService).addError(
			actionRequest, "task-permission-denied");
		Mockito.verify(taskActionFeedbackService).hideDefaultErrorMessage(
			actionRequest);
		Mockito.verify(taskActionFeedbackService).prepareFormRender(
			actionRequest, actionResponse);
		Mockito.verify(actionResponse).setRenderParameter(
			"mvcRenderCommandName", "/task/edit-form");
		Mockito.verify(actionResponse).setRenderParameter("taskId", "10");
	}

	@Test
	public void shouldHandleGenericException() throws Exception {
		Mockito.when(
			taskImageUploadService.uploadTaskImage(actionRequest, themeDisplay)
		).thenReturn(0L);

		Mockito.doThrow(new RuntimeException("erro genérico")).when(taskLocalService)
			.updateTask(
				Mockito.eq(1L),
				Mockito.eq(10L),
				Mockito.eq("Tarefa atualizada"),
				Mockito.eq("Nova descrição"),
				Mockito.eq(true),
				Mockito.anyLong());

		boolean result = updateTaskMVCActionCommand.processAction(
			actionRequest, actionResponse);

		Assert.assertTrue(result);

		Mockito.verify(taskActionFeedbackService).addError(
			actionRequest, "task-update-error");
		Mockito.verify(taskActionFeedbackService).hideDefaultErrorMessage(
			actionRequest);
		Mockito.verify(taskActionFeedbackService).prepareFormRender(
			actionRequest, actionResponse);
		Mockito.verify(actionResponse).setRenderParameter(
			"mvcRenderCommandName", "/task/edit-form");
		Mockito.verify(actionResponse).setRenderParameter("taskId", "10");
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