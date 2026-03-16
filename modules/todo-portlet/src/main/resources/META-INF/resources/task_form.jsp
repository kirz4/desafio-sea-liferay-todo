<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/init.jsp" %>

<liferay-ui:error key="task-add-error" message="task-add-error" />

<portlet:actionURL name="/task/add" var="addTaskURL" />
<portlet:renderURL var="viewTasksURL">
	<portlet:param name="mvcRenderCommandName" value="/task/view" />
</portlet:renderURL>

<h2><liferay-ui:message key="new-task" /></h2>

<aui:form action="<%= addTaskURL %>" method="post">
	<aui:input name="title" label="task-title" />

	<aui:input name="description" label="task-description" type="textarea" />

	<aui:input name="done" label="task-done" type="checkbox" />

	<aui:button-row>
		<aui:button type="submit" value="task-save" />
		<aui:button type="button" value="task-cancel" onClick="<%= viewTasksURL.toString() %>" />
	</aui:button-row>
</aui:form>