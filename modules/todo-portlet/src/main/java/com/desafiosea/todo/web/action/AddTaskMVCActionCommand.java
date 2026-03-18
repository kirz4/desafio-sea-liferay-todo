package com.desafiosea.todo.web.action;

import com.desafiosea.todo.exception.TaskDescriptionSizeException;
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
		"mvc.command.name=/task/add"
	},
	service = MVCActionCommand.class
)
public class AddTaskMVCActionCommand implements MVCActionCommand {

	@Override
    public boolean processAction(
	ActionRequest actionRequest, ActionResponse actionResponse) {

	System.out.println("### ADD ACTION EXECUTOU ###");

	try {
		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		User user = themeDisplay.getUser();

		String title = ParamUtil.getString(actionRequest, "title");
		String description = ParamUtil.getString(actionRequest, "description");
		boolean done = ParamUtil.getBoolean(actionRequest, "done");

		System.out.println("### title=[" + title + "] description.length=" + description.length() + " ###");

		_taskLocalService.addTask(
			user.getUserId(), themeDisplay.getScopeGroupId(), title, description, done);

		System.out.println("### ADD SUCCESS ###");
		return true;
	}
	catch (TaskTitleRequiredException e) {
		System.out.println("### CAIU EM TaskTitleRequiredException ###");
		SessionErrors.add(actionRequest, "task-title-required");
	}
	catch (TaskTitleSizeException e) {
		System.out.println("### CAIU EM TaskTitleSizeException ###");
		SessionErrors.add(actionRequest, "task-title-size");
	}
	catch (TaskDescriptionSizeException e) {
		System.out.println("### CAIU EM TaskDescriptionSizeException ###");
		SessionErrors.add(actionRequest, "task-description-size");
	}
	catch (Exception e) {
		e.printStackTrace();
		System.out.println("### CAIU EM EXCEPTION GENERICA ###");
		SessionErrors.add(actionRequest, "task-add-error");
	}

	SessionMessages.add(
		actionRequest,
		PortalUtil.getPortletId(actionRequest) +
			SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);

	actionResponse.setRenderParameter("mvcRenderCommandName", "/task/form");
	actionResponse.setRenderParameter(
		"title", ParamUtil.getString(actionRequest, "title"));
	actionResponse.setRenderParameter(
		"description", ParamUtil.getString(actionRequest, "description"));
	actionResponse.setRenderParameter(
		"done", String.valueOf(ParamUtil.getBoolean(actionRequest, "done")));

	return true;
}

	@Reference
	private TaskLocalService _taskLocalService;

}