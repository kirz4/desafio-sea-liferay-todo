<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/init.jsp" %>

<%@ page import="com.desafiosea.todo.model.Task" %>
<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.kernel.util.Validator" %>

<liferay-ui:error key="task-title-required" message="O título é obrigatório." />
<liferay-ui:error key="task-title-size" message="O título deve ter entre 3 e 100 caracteres." />
<liferay-ui:error key="task-description-size" message="A descrição deve ter no máximo 500 caracteres." />
<liferay-ui:error key="task-permission-denied" message="Você não tem permissão para editar esta tarefa." />
<liferay-ui:error key="task-add-error" message="Ocorreu um erro ao criar a tarefa." />
<liferay-ui:error key="task-update-error" message="Ocorreu um erro ao atualizar a tarefa." />

<%
Task task = (Task)request.getAttribute("task");

long taskId = ParamUtil.getLong(request, "taskId");
boolean editing = (task != null) || (taskId > 0);

String title = ParamUtil.getString(request, "title");
String description = ParamUtil.getString(request, "description");
String currentFilter = ParamUtil.getString(request, "filter", "all");

String doneParam = ParamUtil.getString(request, "done");
boolean done = "true".equals(doneParam) || "on".equals(doneParam);

if (task != null) {
	if (Validator.isBlank(title)) {
		title = task.getTitle();
	}

	if (Validator.isBlank(description)) {
		description = task.getDescription();
	}

	if (Validator.isBlank(doneParam)) {
		done = task.isDone();
	}

	taskId = task.getTaskId();
}
%>

<portlet:actionURL name="<%= editing ? "/task/update" : "/task/add" %>" var="taskActionURL" />

<portlet:renderURL var="viewTasksURL">
	<portlet:param name="mvcRenderCommandName" value="/task/view" />
	<portlet:param name="filter" value="<%= currentFilter %>" />
</portlet:renderURL>

<h2>
	<%= editing ? LanguageUtil.get(request, "task-edit-title") : LanguageUtil.get(request, "new-task") %>
</h2>

<aui:form action="<%= taskActionURL %>" method="post">
	<aui:input name="filter" type="hidden" value="<%= currentFilter %>" />

	<% if (editing) { %>
		<aui:input name="taskId" type="hidden" value="<%= taskId %>" />
	<% } %>

	<aui:input
		name="title"
		label="task-title"
		value="<%= title %>"
	/>

	<aui:input
		name="description"
		label="task-description"
		type="textarea"
		value="<%= description %>"
	/>

	<aui:input
		name="done"
		label="task-done"
		type="checkbox"
		checked="<%= done %>"
	/>

	<aui:button-row>
		<aui:button type="submit" value="task-save" />
		<a class="btn btn-secondary" href="<%= viewTasksURL.toString() %>">
			<liferay-ui:message key="task-cancel" />
		</a>
	</aui:button-row>
</aui:form>