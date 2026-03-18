package com.desafiosea.todo.web.render;

import com.desafiosea.todo.model.Task;
import com.desafiosea.todo.service.TaskLocalService;
import com.desafiosea.todo.web.constants.TodoPortletKeys;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		List<Task> rootTasks = Collections.emptyList();
		List<Task> doneTasks = new ArrayList<>();
		List<Task> pendingTasks = new ArrayList<>();

		Map<Long, List<Task>> subtasksByParentTaskId = new HashMap<>();
		Map<Long, String> taskImageUrls = new HashMap<>();

		String filter = ParamUtil.getString(renderRequest, "filter", "all");

		if (user != null) {
			long userId = user.getUserId();

			allTasks = _taskLocalService.getTasksByUserId(userId);
			rootTasks = _taskLocalService.getRootTasksByUserId(userId);

			for (Task task : allTasks) {
				if (task.isDone()) {
					doneTasks.add(task);
				}
				else {
					pendingTasks.add(task);
				}

				if (task.getFileEntryId() > 0) {
					try {
						FileEntry fileEntry = _dlAppLocalService.getFileEntry(
							task.getFileEntryId());

						String imageURL = _dlURLHelper.getPreviewURL(
							fileEntry, fileEntry.getFileVersion(), themeDisplay, "");

						taskImageUrls.put(task.getTaskId(), imageURL);
					}
					catch (Exception e) {
					}
				}
			}

			for (Task rootTask : rootTasks) {
				List<Task> subtasks = _taskLocalService.getSubtasksByParentTaskId(
					userId, rootTask.getTaskId());

				subtasksByParentTaskId.put(rootTask.getTaskId(), subtasks);
			}
		}

		renderRequest.setAttribute("rootTasks", rootTasks);
		renderRequest.setAttribute("subtasksByParentTaskId", subtasksByParentTaskId);
		renderRequest.setAttribute("taskImageUrls", taskImageUrls);
		renderRequest.setAttribute("totalCount", allTasks.size());
		renderRequest.setAttribute("doneCount", doneTasks.size());
		renderRequest.setAttribute("pendingCount", pendingTasks.size());
		renderRequest.setAttribute("currentFilter", filter);

		return "/view.jsp";
	}

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private TaskLocalService _taskLocalService;

}