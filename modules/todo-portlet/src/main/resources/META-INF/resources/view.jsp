<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/init.jsp" %>

<%@ page import="com.desafiosea.todo.model.Task" %>
<%@ page import="java.util.List" %>
<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>

<portlet:renderURL var="newTaskURL">
	<portlet:param name="mvcRenderCommandName" value="/task/form" />
</portlet:renderURL>

<%
List<Task> tasks = (List<Task>)request.getAttribute("tasks");
%>

<h2><liferay-ui:message key="my-tasks" /></h2>

<p>
	<aui:button type="button" value="new-task-button" onClick="<%= newTaskURL.toString() %>" />
</p>

<% if (tasks == null || tasks.isEmpty()) { %>
	<p><liferay-ui:message key="no-tasks" /></p>
<% } else { %>
	<ul>
		<% for (Task task : tasks) { %>
			<li>
				<strong><%= task.getTitle() %></strong>
				- <%= task.getDescription() %>
				- <%= task.isDone() ? LanguageUtil.get(request, "status-done") : LanguageUtil.get(request, "status-pending") %>
			</li>
		<% } %>
	</ul>
<% } %>