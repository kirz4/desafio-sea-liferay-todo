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
		"mvc.command.name=/task/delete"
	},
	service = MVCActionCommand.class
)
public class DeleteTaskMVCActionCommand implements MVCActionCommand {

	@Override
	public boolean processAction(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		try {
			long taskId = ParamUtil.getLong(actionRequest, "taskId");

			_taskLocalService.deleteTaskById(taskId);

			return true;
		}
		catch (Exception e) {
			SessionErrors.add(actionRequest, "task-delete-error");
			return false;
		}
	}

	@Reference
	private TaskLocalService _taskLocalService;

}