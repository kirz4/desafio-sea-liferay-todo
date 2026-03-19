package com.desafiosea.todo.web.service;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

public interface TaskActionFeedbackService {

	void addError(ActionRequest actionRequest, String errorKey);

	void hideDefaultErrorMessage(ActionRequest actionRequest);

	void prepareFormRender(
		ActionRequest actionRequest, ActionResponse actionResponse);

	void prepareEditFormRender(
		ActionRequest actionRequest, ActionResponse actionResponse);

}