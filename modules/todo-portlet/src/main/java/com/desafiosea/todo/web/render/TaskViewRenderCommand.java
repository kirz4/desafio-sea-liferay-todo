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
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		User user = themeDisplay.getUser();

		String filter = ParamUtil.getString(renderRequest, "filter", "all");

		List<Task> rootTasks = _taskLocalService.getRootTasksByUserId(
			user.getUserId());

		Map<Long, List<Task>> subtasksByParentTaskId = new HashMap<>();
		Map<Long, String> taskImageUrls = new HashMap<>();

		int totalCount = 0;
		int pendingCount = 0;
		int doneCount = 0;

		for (Task rootTask : rootTasks) {
			totalCount++;

			if (rootTask.isDone()) {
				doneCount++;
			}
			else {
				pendingCount++;
			}

			_addTaskImageUrl(rootTask, taskImageUrls, themeDisplay);

			List<Task> subtasks = _taskLocalService.getSubtasksByParentTaskId(
				user.getUserId(), rootTask.getTaskId());

			subtasksByParentTaskId.put(rootTask.getTaskId(), subtasks);

			for (Task subtask : subtasks) {
				totalCount++;

				if (subtask.isDone()) {
					doneCount++;
				}
				else {
					pendingCount++;
				}

				_addTaskImageUrl(subtask, taskImageUrls, themeDisplay);
			}
		}

		renderRequest.setAttribute("rootTasks", rootTasks);
		renderRequest.setAttribute(
			"subtasksByParentTaskId", subtasksByParentTaskId);
		renderRequest.setAttribute("taskImageUrls", taskImageUrls);
		renderRequest.setAttribute("totalCount", totalCount);
		renderRequest.setAttribute("pendingCount", pendingCount);
		renderRequest.setAttribute("doneCount", doneCount);
		renderRequest.setAttribute("currentFilter", filter);

		return "/view.jsp";
	}

	private void _addTaskImageUrl(
		Task task, Map<Long, String> taskImageUrls, ThemeDisplay themeDisplay) {

		if (task.getFileEntryId() <= 0) {
			return;
		}

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

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private TaskLocalService _taskLocalService;

}