package com.desafiosea.todo.web.action;

import com.desafiosea.todo.exception.TaskPermissionException;
import com.desafiosea.todo.service.TaskLocalService;
import com.desafiosea.todo.web.constants.TodoPortletKeys;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	property = {
		"javax.portlet.name=" + TodoPortletKeys.TODO,
		"mvc.command.name=/task/delete"
	},
	service = MVCActionCommand.class
)
public class DeleteTaskMVCActionCommand implements MVCActionCommand {

	@Override
	public boolean processAction(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		try {
			ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

			User user = themeDisplay.getUser();

			long taskId = ParamUtil.getLong(actionRequest, "taskId");

			_taskLocalService.deleteTaskById(user.getUserId(), taskId);

			return true;
		}
		catch (TaskPermissionException e) {
			SessionErrors.add(actionRequest, TaskPermissionException.class);
		}
		catch (Exception e) {
			SessionErrors.add(actionRequest, "task-delete-error");
		}

		SessionMessages.add(
			actionRequest,
			actionRequest.getPortletSession().getPortletContext().getPortletContextName() +
				SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);

		actionResponse.setRenderParameter("mvcRenderCommandName", "/task/view");

		return false;
	}

	@Reference
	private TaskLocalService _taskLocalService;

}