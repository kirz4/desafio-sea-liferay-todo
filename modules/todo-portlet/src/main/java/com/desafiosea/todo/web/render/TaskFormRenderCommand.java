package com.desafiosea.todo.web.render;

import com.desafiosea.todo.web.constants.TodoPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

@Component(
	property = {
		"javax.portlet.name=" + TodoPortletKeys.TODO,
		"mvc.command.name=/task/form"
	},
	service = MVCRenderCommand.class
)
public class TaskFormRenderCommand implements MVCRenderCommand {

	@Override
	public String render(RenderRequest renderRequest, RenderResponse renderResponse) {
		return "/task_form.jsp";
	}

}