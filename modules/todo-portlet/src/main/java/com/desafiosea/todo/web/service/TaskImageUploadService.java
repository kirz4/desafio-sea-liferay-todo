package com.desafiosea.todo.web.service;

import com.liferay.portal.kernel.theme.ThemeDisplay;

import javax.portlet.ActionRequest;

public interface TaskImageUploadService {

	long uploadTaskImage(
		ActionRequest actionRequest, ThemeDisplay themeDisplay)
		throws Exception;

}