package com.desafiosea.todo.web.action;

import com.desafiosea.todo.exception.TaskDescriptionSizeException;
import com.desafiosea.todo.exception.TaskTitleRequiredException;
import com.desafiosea.todo.exception.TaskTitleSizeException;
import com.desafiosea.todo.service.TaskLocalService;
import com.desafiosea.todo.web.constants.TodoPortletKeys;
import com.desafiosea.todo.web.service.TaskActionFeedbackService;
import com.desafiosea.todo.web.service.TaskImageUploadService;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	property = {
		"javax.portlet.name=" + TodoPortletKeys.TODO,
		"mvc.command.name=/task/add"
	},
	service = MVCActionCommand.class
)
public class AddTaskMVCActionCommand implements MVCActionCommand {

	@Override
	public boolean processAction(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		try {
			ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

			User user = themeDisplay.getUser();

			String title = _getString(actionRequest, "title");
			String description = _getString(actionRequest, "description");
			boolean done = _getBoolean(actionRequest, "done");
			long parentTaskId = _getLong(actionRequest, "parentTaskId", 0L);

			long fileEntryId = _taskImageUploadService.uploadTaskImage(
				actionRequest, themeDisplay);

			_taskLocalService.addTask(
				user.getUserId(),
				themeDisplay.getScopeGroupId(),
				title,
				description,
				done,
				fileEntryId,
				parentTaskId);

			return true;
		}
		catch (TaskTitleRequiredException e) {
			_taskActionFeedbackService.addError(
				actionRequest, "task-title-required");
		}
		catch (TaskTitleSizeException e) {
			_taskActionFeedbackService.addError(
				actionRequest, "task-title-size");
		}
		catch (TaskDescriptionSizeException e) {
			_taskActionFeedbackService.addError(
				actionRequest, "task-description-size");
		}
		catch (Exception e) {
			_taskActionFeedbackService.addError(
				actionRequest, "task-add-error");
		}

		_taskActionFeedbackService.hideDefaultErrorMessage(actionRequest);
		_taskActionFeedbackService.prepareFormRender(
			actionRequest, actionResponse);

		return true;
	}

	private boolean _getBoolean(ActionRequest actionRequest, String name) {
		String value = actionRequest.getParameter(name);

		return "true".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value);
	}

	private long _getLong(
		ActionRequest actionRequest, String name, long defaultValue) {

		String value = actionRequest.getParameter(name);

		if ((value == null) || value.trim().isEmpty()) {
			return defaultValue;
		}

		try {
			return Long.parseLong(value);
		}
		catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	private String _getString(ActionRequest actionRequest, String name) {
		String value = actionRequest.getParameter(name);

		return value == null ? "" : value;
	}

	@Reference
	private TaskActionFeedbackService _taskActionFeedbackService;

	@Reference
	private TaskImageUploadService _taskImageUploadService;

	@Reference
	private TaskLocalService _taskLocalService;
}