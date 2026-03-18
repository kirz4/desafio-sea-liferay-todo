package com.desafiosea.todo.web.action;

import com.desafiosea.todo.exception.TaskDescriptionSizeException;
import com.desafiosea.todo.exception.TaskPermissionException;
import com.desafiosea.todo.exception.TaskTitleRequiredException;
import com.desafiosea.todo.exception.TaskTitleSizeException;
import com.desafiosea.todo.model.Task;
import com.desafiosea.todo.service.TaskLocalService;
import com.desafiosea.todo.web.constants.TodoPortletKeys;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.File;

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

			Task task = _taskLocalService.getTask(taskId);

			long fileEntryId = _uploadTaskImage(actionRequest, themeDisplay);

			if (fileEntryId == 0L) {
				fileEntryId = task.getFileEntryId();
			}

			_taskLocalService.updateTask(
				user.getUserId(), taskId, title, description, done, fileEntryId);

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

	private long _uploadTaskImage(
		ActionRequest actionRequest, ThemeDisplay themeDisplay) throws Exception {

		UploadPortletRequest uploadPortletRequest =
			PortalUtil.getUploadPortletRequest(actionRequest);

		File file = uploadPortletRequest.getFile("taskImage");
		String fileName = uploadPortletRequest.getFileName("taskImage");
		String contentType = uploadPortletRequest.getContentType("taskImage");

		if ((file == null) || (file.length() == 0) || (fileName == null) || fileName.isEmpty()) {
			return 0L;
		}

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			ActionRequest.class.getName(), actionRequest);
		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null,
			themeDisplay.getUserId(),
			themeDisplay.getScopeGroupId(),
			0,
			fileName,
			contentType != null ? contentType : ContentTypes.APPLICATION_OCTET_STREAM,
			fileName,
			null,
			"Tarefa - imagem anexada",
			"",
			file,
			null,
			null,
			null,
			serviceContext);

		return fileEntry.getFileEntryId();
	}

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private TaskLocalService _taskLocalService;

}