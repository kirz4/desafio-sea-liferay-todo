<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/init.jsp" %>
<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>
<%@ page import="com.desafiosea.todo.model.Task" %>

<liferay-ui:error key="task-add-error" message="task-add-error" />
<liferay-ui:error key="task-update-error" message="task-update-error" />

<%
Task task = (Task)request.getAttribute("task");
boolean editing = task != null;
%>

<portlet:actionURL name="<%= editing ? "/task/update" : "/task/add" %>" var="taskActionURL" />
<portlet:renderURL var="viewTasksURL">
	<portlet:param name="mvcRenderCommandName" value="/task/view" />
</portlet:renderURL>

<h2>
	<%= editing ? LanguageUtil.get(request, "task-edit-title") : LanguageUtil.get(request, "new-task") %>
</h2>

<aui:form action="<%= taskActionURL %>" method="post">
	<% if (editing) { %>
		<aui:input name="taskId" type="hidden" value="<%= task.getTaskId() %>" />
	<% } %>

	<aui:input
		name="title"
		label="task-title"
		value="<%= editing ? task.getTitle() : "" %>"
	/>

	<aui:input
		name="description"
		label="task-description"
		type="textarea"
		value="<%= editing ? task.getDescription() : "" %>"
	/>

	<aui:input
		name="done"
		label="task-done"
		type="checkbox"
		checked="<%= editing && task.isDone() %>"
	/>

	<aui:button-row>
		<aui:button type="submit" value="task-save" />
		<aui:button type="button" value="task-cancel" onClick="<%= viewTasksURL.toString() %>" />
	</aui:button-row>
</aui:form>