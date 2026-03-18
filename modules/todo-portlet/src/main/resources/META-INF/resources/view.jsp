<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/init.jsp" %>

<%@ page import="com.desafiosea.todo.model.Task" %>
<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>
<%@ page import="java.util.List" %>

<liferay-ui:error key="task-permission-denied" message="Você não tem permissão para modificar esta tarefa." />
<liferay-ui:error key="task-delete-error" message="Ocorreu um erro ao deletar a tarefa." />
<liferay-ui:error key="task-toggle-error" message="Ocorreu um erro ao alterar o status da tarefa." />
<liferay-ui:error key="task-update-error" message="Ocorreu um erro ao atualizar a tarefa." />

<portlet:renderURL var="newTaskURL">
	<portlet:param name="mvcRenderCommandName" value="/task/form" />
</portlet:renderURL>

<%
List<Task> tasks = (List<Task>)request.getAttribute("tasks");
%>

<h2><liferay-ui:message key="my-tasks" /></h2>

<p>
	<a class="btn btn-primary" href="<%= newTaskURL.toString() %>">
		<liferay-ui:message key="new-task-button" />
	</a>
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

				<portlet:renderURL var="editTaskURL">
					<portlet:param name="mvcRenderCommandName" value="/task/edit-form" />
					<portlet:param name="taskId" value="<%= String.valueOf(task.getTaskId()) %>" />
				</portlet:renderURL>

				<portlet:actionURL name="/task/toggle-status" var="toggleTaskStatusURL">
					<portlet:param name="taskId" value="<%= String.valueOf(task.getTaskId()) %>" />
				</portlet:actionURL>

				<portlet:actionURL name="/task/delete" var="deleteTaskURL">
					<portlet:param name="taskId" value="<%= String.valueOf(task.getTaskId()) %>" />
				</portlet:actionURL>

				<a class="btn btn-secondary" href="<%= editTaskURL.toString() %>">
					<liferay-ui:message key="task-edit" />
				</a>

				<aui:form action="<%= toggleTaskStatusURL %>" method="post" cssClass="d-inline">
					<aui:button
						type="submit"
						value="<%= task.isDone() ? "task-mark-pending" : "task-mark-done" %>"
					/>
				</aui:form>

				<aui:form action="<%= deleteTaskURL %>" method="post" cssClass="d-inline">
					<aui:button
						type="submit"
						value="task-delete"
					/>
				</aui:form>
			</li>
		<% } %>
	</ul>
<% } %>