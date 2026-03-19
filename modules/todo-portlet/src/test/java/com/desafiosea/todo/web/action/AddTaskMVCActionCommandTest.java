package com.desafiosea.todo.web.action;

import com.desafiosea.todo.exception.TaskDescriptionSizeException;
import com.desafiosea.todo.exception.TaskTitleRequiredException;
import com.desafiosea.todo.exception.TaskTitleSizeException;
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

public class AddTaskMVCActionCommandTest {

	private AddTaskMVCActionCommand addTaskMVCActionCommand;
	private TaskActionFeedbackService taskActionFeedbackService;
	private TaskImageUploadService taskImageUploadService;
	private TaskLocalService taskLocalService;

	private ActionRequest actionRequest;
	private ActionResponse actionResponse;
	private ThemeDisplay themeDisplay;
	private User user;

	@Before
	public void setUp() throws Exception {
		addTaskMVCActionCommand = new AddTaskMVCActionCommand();

		taskActionFeedbackService = Mockito.mock(TaskActionFeedbackService.class);
		taskImageUploadService = Mockito.mock(TaskImageUploadService.class);
		taskLocalService = Mockito.mock(TaskLocalService.class);

		actionRequest = Mockito.mock(ActionRequest.class);
		actionResponse = Mockito.mock(ActionResponse.class);
		themeDisplay = Mockito.mock(ThemeDisplay.class);
		user = Mockito.mock(User.class);

		_inject(
			addTaskMVCActionCommand, "_taskActionFeedbackService",
			taskActionFeedbackService);
		_inject(
			addTaskMVCActionCommand, "_taskImageUploadService",
			taskImageUploadService);
		_inject(addTaskMVCActionCommand, "_taskLocalService", taskLocalService);

		Mockito.when(actionRequest.getAttribute(WebKeys.THEME_DISPLAY)).thenReturn(
			themeDisplay);
		Mockito.when(themeDisplay.getUser()).thenReturn(user);
		Mockito.when(themeDisplay.getScopeGroupId()).thenReturn(20117L);
		Mockito.when(user.getUserId()).thenReturn(1L);

		Mockito.when(actionRequest.getParameter("title")).thenReturn(
			"Minha tarefa");
		Mockito.when(actionRequest.getParameter("description")).thenReturn(
			"Descrição");
		Mockito.when(actionRequest.getParameter("done")).thenReturn("true");
		Mockito.when(actionRequest.getParameter("parentTaskId")).thenReturn("99");
	}

	@Test
	public void shouldAddTaskSuccessfullyWithoutImage() throws Exception {
		Mockito.when(
			taskImageUploadService.uploadTaskImage(actionRequest, themeDisplay)
		).thenReturn(0L);

		boolean result = addTaskMVCActionCommand.processAction(
			actionRequest, actionResponse);

		Assert.assertTrue(result);

		Mockito.verify(taskLocalService).addTask(
			1L, 20117L, "Minha tarefa", "Descrição", true, 0L, 99L);

		Mockito.verifyNoInteractions(taskActionFeedbackService);
	}

	@Test
	public void shouldAddTaskSuccessfullyWithImage() throws Exception {
		Mockito.when(
			taskImageUploadService.uploadTaskImage(actionRequest, themeDisplay)
		).thenReturn(555L);

		boolean result = addTaskMVCActionCommand.processAction(
			actionRequest, actionResponse);

		Assert.assertTrue(result);

		Mockito.verify(taskLocalService).addTask(
			1L, 20117L, "Minha tarefa", "Descrição", true, 555L, 99L);

		Mockito.verifyNoInteractions(taskActionFeedbackService);
	}

	@Test
	public void shouldHandleTitleRequiredException() throws Exception {
		Mockito.when(
			taskImageUploadService.uploadTaskImage(actionRequest, themeDisplay)
		).thenReturn(0L);

		Mockito.doThrow(new TaskTitleRequiredException()).when(taskLocalService)
			.addTask(1L, 20117L, "Minha tarefa", "Descrição", true, 0L, 99L);

		boolean result = addTaskMVCActionCommand.processAction(
			actionRequest, actionResponse);

		Assert.assertTrue(result);

		Mockito.verify(taskActionFeedbackService).addError(
			actionRequest, "task-title-required");
		Mockito.verify(taskActionFeedbackService).hideDefaultErrorMessage(
			actionRequest);
		Mockito.verify(taskActionFeedbackService).prepareFormRender(
			actionRequest, actionResponse);
	}

	@Test
	public void shouldHandleTitleSizeException() throws Exception {
		Mockito.when(
			taskImageUploadService.uploadTaskImage(actionRequest, themeDisplay)
		).thenReturn(0L);

		Mockito.doThrow(new TaskTitleSizeException()).when(taskLocalService)
			.addTask(1L, 20117L, "Minha tarefa", "Descrição", true, 0L, 99L);

		boolean result = addTaskMVCActionCommand.processAction(
			actionRequest, actionResponse);

		Assert.assertTrue(result);

		Mockito.verify(taskActionFeedbackService).addError(
			actionRequest, "task-title-size");
		Mockito.verify(taskActionFeedbackService).hideDefaultErrorMessage(
			actionRequest);
		Mockito.verify(taskActionFeedbackService).prepareFormRender(
			actionRequest, actionResponse);
	}

	@Test
	public void shouldHandleDescriptionSizeException() throws Exception {
		Mockito.when(
			taskImageUploadService.uploadTaskImage(actionRequest, themeDisplay)
		).thenReturn(0L);

		Mockito.doThrow(new TaskDescriptionSizeException()).when(taskLocalService)
			.addTask(1L, 20117L, "Minha tarefa", "Descrição", true, 0L, 99L);

		boolean result = addTaskMVCActionCommand.processAction(
			actionRequest, actionResponse);

		Assert.assertTrue(result);

		Mockito.verify(taskActionFeedbackService).addError(
			actionRequest, "task-description-size");
		Mockito.verify(taskActionFeedbackService).hideDefaultErrorMessage(
			actionRequest);
		Mockito.verify(taskActionFeedbackService).prepareFormRender(
			actionRequest, actionResponse);
	}

	@Test
	public void shouldHandleGenericException() throws Exception {
		Mockito.when(
			taskImageUploadService.uploadTaskImage(actionRequest, themeDisplay)
		).thenReturn(0L);

		Mockito.doThrow(new RuntimeException("erro genérico")).when(
			taskLocalService
		).addTask(1L, 20117L, "Minha tarefa", "Descrição", true, 0L, 99L);

		boolean result = addTaskMVCActionCommand.processAction(
			actionRequest, actionResponse);

		Assert.assertTrue(result);

		Mockito.verify(taskActionFeedbackService).addError(
			actionRequest, "task-add-error");
		Mockito.verify(taskActionFeedbackService).hideDefaultErrorMessage(
			actionRequest);
		Mockito.verify(taskActionFeedbackService).prepareFormRender(
			actionRequest, actionResponse);
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