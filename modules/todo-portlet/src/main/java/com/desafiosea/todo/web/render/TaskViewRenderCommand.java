package com.desafiosea.todo.web.render;

import com.desafiosea.todo.model.Task;
import com.desafiosea.todo.service.TaskLocalService;
import com.desafiosea.todo.web.constants.TodoPortletKeys;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	property = {
		"javax.portlet.name=" + TodoPortletKeys.TODO,
		"mvc.command.name=/",
		"mvc.command.name=/task/view"
	},
	service = MVCRenderCommand.class
)
public class TaskViewRenderCommand implements MVCRenderCommand {

	@Override
	public String render(RenderRequest renderRequest, RenderResponse renderResponse) {
		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		User user = themeDisplay.getUser();

		List<Task> allTasks = Collections.emptyList();
		List<Task> doneTasks = new ArrayList<>();
		List<Task> pendingTasks = new ArrayList<>();

		String filter = ParamUtil.getString(renderRequest, "filter", "all");

		if (user != null) {
			allTasks = _taskLocalService.getTasksByUserId(user.getUserId());

			for (Task task : allTasks) {
				if (task.isDone()) {
					doneTasks.add(task);
				}
				else {
					pendingTasks.add(task);
				}
			}
		}

		renderRequest.setAttribute("tasks", allTasks);
		renderRequest.setAttribute("totalCount", allTasks.size());
		renderRequest.setAttribute("doneCount", doneTasks.size());
		renderRequest.setAttribute("pendingCount", pendingTasks.size());
		renderRequest.setAttribute("currentFilter", filter);

		return "/view.jsp";
	}

	@Reference
	private TaskLocalService _taskLocalService;

}