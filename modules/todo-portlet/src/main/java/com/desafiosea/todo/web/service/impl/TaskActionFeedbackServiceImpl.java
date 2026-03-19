package com.desafiosea.todo.web.service.impl;

import com.desafiosea.todo.web.service.TaskActionFeedbackService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.PortalUtil;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;

@Component(service = TaskActionFeedbackService.class)
public class TaskActionFeedbackServiceImpl implements TaskActionFeedbackService {

	@Override
	public void addError(ActionRequest actionRequest, String errorKey) {
		SessionErrors.add(actionRequest, errorKey);
	}

	@Override
	public void hideDefaultErrorMessage(ActionRequest actionRequest) {
		SessionMessages.add(
			actionRequest,
			PortalUtil.getPortletId(actionRequest) +
				SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
	}

	@Override
	public void prepareFormRender(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		actionResponse.setRenderParameter("mvcRenderCommandName", "/task/form");
		actionResponse.setRenderParameter(
			"title", _getString(actionRequest, "title"));
		actionResponse.setRenderParameter(
			"description", _getString(actionRequest, "description"));
		actionResponse.setRenderParameter(
			"done", String.valueOf(_getBoolean(actionRequest, "done")));
		actionResponse.setRenderParameter(
			"filter", _getStringOrDefault(actionRequest, "filter", "all"));
		actionResponse.setRenderParameter(
			"parentTaskId",
			String.valueOf(_getLong(actionRequest, "parentTaskId", 0L)));
	}

	@Override
	public void prepareEditFormRender(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		actionResponse.setRenderParameter("mvcRenderCommandName", "/task/edit-form");
		actionResponse.setRenderParameter(
			"taskId", String.valueOf(_getLong(actionRequest, "taskId", 0L)));
		actionResponse.setRenderParameter(
			"title", _getString(actionRequest, "title"));
		actionResponse.setRenderParameter(
			"description", _getString(actionRequest, "description"));
		actionResponse.setRenderParameter(
			"done", String.valueOf(_getBoolean(actionRequest, "done")));
		actionResponse.setRenderParameter(
			"filter", _getStringOrDefault(actionRequest, "filter", "all"));
	}

	private boolean _getBoolean(ActionRequest actionRequest, String name) {
		String value = actionRequest.getParameter(name);

		return "true".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value);
	}

	private long _getLong(
		ActionRequest actionRequest, String name, long defaultValue) {

		String value = actionRequest.getParameter(name);

		if ((value == null) || value.trim().isEmpty()) {
			return defaultValue;
		}

		try {
			return Long.parseLong(value);
		}
		catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	private String _getString(ActionRequest actionRequest, String name) {
		String value = actionRequest.getParameter(name);

		return value == null ? "" : value;
	}

	private String _getStringOrDefault(
		ActionRequest actionRequest, String name, String defaultValue) {

		String value = actionRequest.getParameter(name);

		if ((value == null) || value.isEmpty()) {
			return defaultValue;
		}

		return value;
	}
}