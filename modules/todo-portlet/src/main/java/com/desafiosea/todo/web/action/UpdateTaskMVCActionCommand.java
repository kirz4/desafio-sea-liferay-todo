package com.desafiosea.todo.web.action;

import com.desafiosea.todo.service.TaskLocalService;
import com.desafiosea.todo.web.constants.TodoPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;

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
			long taskId = ParamUtil.getLong(actionRequest, "taskId");
			String title = ParamUtil.getString(actionRequest, "title");
			String description = ParamUtil.getString(actionRequest, "description");
			boolean done = ParamUtil.getBoolean(actionRequest, "done");

			_taskLocalService.updateTask(taskId, title, description, done);

			return true;
		}
		catch (Exception e) {
			SessionErrors.add(actionRequest, "task-update-error");
			return false;
		}
	}

	@Reference
	private TaskLocalService _taskLocalService;

}