package com.desafiosea.todo.web.render;

import com.desafiosea.todo.exception.TaskPermissionException;
import com.desafiosea.todo.model.Task;
import com.desafiosea.todo.service.TaskLocalService;
import com.desafiosea.todo.web.constants.TodoPortletKeys;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	property = {
		"javax.portlet.name=" + TodoPortletKeys.TODO,
		"mvc.command.name=/task/edit-form"
	},
	service = MVCRenderCommand.class
)
public class EditTaskRenderCommand implements MVCRenderCommand {

	@Override
	public String render(RenderRequest renderRequest, RenderResponse renderResponse) {
		long taskId = ParamUtil.getLong(renderRequest, "taskId");

		try {
			ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

			User user = themeDisplay.getUser();

			Task task = _taskLocalService.getTask(taskId);

			if (task.getUserId() != user.getUserId()) {
				SessionErrors.add(renderRequest, TaskPermissionException.class);
				renderRequest.setAttribute(
					"tasks", _taskLocalService.getTasksByUserId(user.getUserId()));

				return "/view.jsp";
			}

			renderRequest.setAttribute("task", task);

			return "/task_form.jsp";
		}
		catch (Exception e) {
			SessionErrors.add(renderRequest, "task-update-error");

			ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

			if (themeDisplay != null && themeDisplay.getUser() != null) {
				renderRequest.setAttribute(
					"tasks",
					_taskLocalService.getTasksByUserId(themeDisplay.getUserId()));
			}

			return "/view.jsp";
		}
	}

	@Reference
	private TaskLocalService _taskLocalService;

}