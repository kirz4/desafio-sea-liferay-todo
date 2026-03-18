<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/init.jsp" %>

<%@ page import="com.desafiosea.todo.model.Task" %>
<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>
<%@ page import="java.util.List" %>

<liferay-ui:error key="task-permission-denied" message="Você não tem permissão para modificar esta tarefa." />
<liferay-ui:error key="task-delete-error" message="Ocorreu um erro ao deletar a tarefa." />
<liferay-ui:error key="task-toggle-error" message="Ocorreu um erro ao alterar o status da tarefa." />
<liferay-ui:error key="task-update-error" message="Ocorreu um erro ao atualizar a tarefa." />

<%
List<Task> tasks = (List<Task>)request.getAttribute("tasks");
int totalCount = (Integer)request.getAttribute("totalCount");
int pendingCount = (Integer)request.getAttribute("pendingCount");
int doneCount = (Integer)request.getAttribute("doneCount");
String currentFilter = (String)request.getAttribute("currentFilter");

if (currentFilter == null || currentFilter.isEmpty()) {
	currentFilter = "all";
}

String namespace = renderResponse.getNamespace();
%>

<portlet:renderURL var="newTaskURL">
	<portlet:param name="mvcRenderCommandName" value="/task/form" />
</portlet:renderURL>

<h2><liferay-ui:message key="my-tasks" /></h2>

<input type="hidden" id="<%= namespace %>currentFilter" value="<%= currentFilter %>" />

<p>
	<a
		class="btn btn-primary"
		id="<%= namespace %>newTaskLink"
		href="<%= newTaskURL.toString() %>&<%= namespace %>filter=<%= currentFilter %>"
	>
		<liferay-ui:message key="new-task-button" />
	</a>
</p>

<div class="task-tabs" id="<%= namespace %>taskTabs">
	<button type="button" class="task-tab" data-filter="all">
		Todas (<%= totalCount %>)
	</button>

	<button type="button" class="task-tab" data-filter="pending">
		Pendentes (<%= pendingCount %>)
	</button>

	<button type="button" class="task-tab" data-filter="done">
		Concluídas (<%= doneCount %>)
	</button>
</div>

<% if (tasks == null || tasks.isEmpty()) { %>
	<p class="task-empty"><liferay-ui:message key="no-tasks" /></p>
<% } else { %>
	<ul class="task-list" id="<%= namespace %>taskList">
		<% for (Task task : tasks) { %>
			<li class="task-item" data-status="<%= task.isDone() ? "done" : "pending" %>">
				<strong><%= task.getTitle() %></strong>
				- <%= task.getDescription() %>
				- <span class="task-status"><%= task.isDone() ? LanguageUtil.get(request, "status-done") : LanguageUtil.get(request, "status-pending") %></span>

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

				<div class="task-actions">
					<a
						class="btn btn-secondary task-edit-link"
						href="<%= editTaskURL.toString() %>&<%= namespace %>filter=<%= currentFilter %>"
					>
						<liferay-ui:message key="task-edit" />
					</a>

					<aui:form action="<%= toggleTaskStatusURL %>" method="post" cssClass="d-inline task-filter-form">
						<aui:input name="filter" type="hidden" value="<%= currentFilter %>" cssClass="task-filter-input" />
						<aui:button
							type="submit"
							value="<%= task.isDone() ? "task-mark-pending" : "task-mark-done" %>"
						/>
					</aui:form>

					<aui:form action="<%= deleteTaskURL %>" method="post" cssClass="d-inline task-filter-form">
						<aui:input name="filter" type="hidden" value="<%= currentFilter %>" cssClass="task-filter-input" />
						<aui:button
							type="submit"
							value="task-delete"
						/>
					</aui:form>
				</div>
			</li>
		<% } %>
	</ul>
<% } %>

<script>
(function() {
	const namespace = '<%= namespace %>';
	const tabsContainer = document.getElementById(namespace + 'taskTabs');
	const taskList = document.getElementById(namespace + 'taskList');
	const currentFilterInput = document.getElementById(namespace + 'currentFilter');
	const newTaskLink = document.getElementById(namespace + 'newTaskLink');

	if (!tabsContainer) {
		return;
	}

	const tabs = tabsContainer.querySelectorAll('.task-tab');
	const items = taskList ? taskList.querySelectorAll('.task-item') : [];
	const editLinks = document.querySelectorAll('.task-edit-link');
	const filterInputs = document.querySelectorAll('.task-filter-input');

	function updateLinksAndForms(filter) {
		currentFilterInput.value = filter;

		if (newTaskLink) {
			const url = new URL(newTaskLink.href, window.location.origin);
			url.searchParams.set(namespace + 'filter', filter);
			newTaskLink.href = url.pathname + url.search;
		}

		editLinks.forEach(function(link) {
			const url = new URL(link.href, window.location.origin);
			url.searchParams.set(namespace + 'filter', filter);
			link.href = url.pathname + url.search;
		});

		filterInputs.forEach(function(input) {
			input.value = filter;
		});
	}

	function applyFilter(filter) {
		items.forEach(function(item) {
			const status = item.getAttribute('data-status');

			if (filter === 'all' || status === filter) {
				item.style.display = '';
			}
			else {
				item.style.display = 'none';
			}
		});

		tabs.forEach(function(tab) {
			tab.classList.remove('task-tab-active');

			if (tab.getAttribute('data-filter') === filter) {
				tab.classList.add('task-tab-active');
			}
		});

		updateLinksAndForms(filter);
	}

	tabs.forEach(function(tab) {
		tab.addEventListener('click', function() {
			applyFilter(tab.getAttribute('data-filter'));
		});
	});

	applyFilter(currentFilterInput.value || 'all');
})();
</script>