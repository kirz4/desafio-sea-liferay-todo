package com.desafiosea.todo.web.render;

import com.desafiosea.todo.model.Task;
import com.desafiosea.todo.service.TaskLocalService;
import com.desafiosea.todo.web.constants.TodoPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.ParamUtil;

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
			Task task = _taskLocalService.getTask(taskId);

			renderRequest.setAttribute("task", task);
		}
		catch (Exception e) {
			renderRequest.setAttribute("task", null);
		}

		return "/task_form.jsp";
	}

	@Reference
	private TaskLocalService _taskLocalService;

}