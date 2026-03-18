package com.desafiosea.todo.web.action;

import com.desafiosea.todo.exception.TaskDescriptionSizeException;
import com.desafiosea.todo.exception.TaskPermissionException;
import com.desafiosea.todo.exception.TaskTitleRequiredException;
import com.desafiosea.todo.exception.TaskTitleSizeException;
import com.desafiosea.todo.service.TaskLocalService;
import com.desafiosea.todo.web.constants.TodoPortletKeys;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	property = {
		"javax.portlet.name=" + TodoPortletKeys.TODO,
		"mvc.command.name=/task/update"
	},
	service = MVCActionCommand.class
)
public class UpdateTaskMVCActionCommand implements MVCActionCommand {

	@Override
	public boolean processAction(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		try {
			ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

			User user = themeDisplay.getUser();

			long taskId = ParamUtil.getLong(actionRequest, "taskId");
			String title = ParamUtil.getString(actionRequest, "title");
			String description = ParamUtil.getString(actionRequest, "description");
			boolean done = ParamUtil.getBoolean(actionRequest, "done");

			_taskLocalService.updateTask(
				user.getUserId(), taskId, title, description, done);

			return true;
		}
		catch (TaskTitleRequiredException e) {
			SessionErrors.add(actionRequest, "task-title-required");
		}
		catch (TaskTitleSizeException e) {
			SessionErrors.add(actionRequest, "task-title-size");
		}
		catch (TaskDescriptionSizeException e) {
			SessionErrors.add(actionRequest, "task-description-size");
		}
		catch (TaskPermissionException e) {
			SessionErrors.add(actionRequest, "task-permission-denied");
		}
		catch (Exception e) {
			SessionErrors.add(actionRequest, "task-update-error");
		}

		SessionMessages.add(
			actionRequest,
			PortalUtil.getPortletId(actionRequest) +
				SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);

		actionResponse.setRenderParameter("mvcRenderCommandName", "/task/edit-form");
		actionResponse.setRenderParameter(
			"taskId", String.valueOf(ParamUtil.getLong(actionRequest, "taskId")));
		actionResponse.setRenderParameter(
			"title", ParamUtil.getString(actionRequest, "title"));
		actionResponse.setRenderParameter(
			"description", ParamUtil.getString(actionRequest, "description"));
		actionResponse.setRenderParameter(
			"done", String.valueOf(ParamUtil.getBoolean(actionRequest, "done")));
		actionResponse.setRenderParameter(
			"filter", ParamUtil.getString(actionRequest, "filter", "all"));

		return true;
	}

	@Reference
	private TaskLocalService _taskLocalService;

}