<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/init.jsp" %>

<%@ page import="com.desafiosea.todo.model.Task" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<liferay-ui:error key="task-permission-denied" message="Você não tem permissão para modificar esta tarefa." />
<liferay-ui:error key="task-delete-error" message="Ocorreu um erro ao deletar a tarefa." />
<liferay-ui:error key="task-toggle-error" message="Ocorreu um erro ao alterar o status da tarefa." />
<liferay-ui:error key="task-update-error" message="Ocorreu um erro ao atualizar a tarefa." />

<%
List<Task> rootTasks = (List<Task>)request.getAttribute("rootTasks");
Map<Long, List<Task>> subtasksByParentTaskId =
	(Map<Long, List<Task>>)request.getAttribute("subtasksByParentTaskId");
Map<Long, String> taskImageUrls =
	(Map<Long, String>)request.getAttribute("taskImageUrls");

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

<div class="task-page-container">
	<div class="task-top-section">
		<div class="task-header">
			<div>
				<h2 class="task-page-title">Minhas Tarefas</h2>
				<p class="task-page-subtitle">Organize suas tarefas e acompanhe o progresso.</p>
			</div>

			<a
				class="btn btn-primary task-new-button"
				id="<%= namespace %>newTaskLink"
				href="<%= newTaskURL.toString() %>&<%= namespace %>filter=<%= currentFilter %>"
			>
				Nova Tarefa
			</a>
		</div>

		<input type="hidden" id="<%= namespace %>currentFilter" value="<%= currentFilter %>" />

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
	</div>

	<% if (rootTasks == null || rootTasks.isEmpty()) { %>
		<div class="task-empty-card">
			<p class="task-empty-title">Nenhuma tarefa cadastrada ainda.</p>
			<p class="task-empty-subtitle">Crie sua primeira tarefa para começar a organizar seu dia.</p>
		</div>
	<% } else { %>
		<ul class="task-list" id="<%= namespace %>taskList">
			<% for (Task task : rootTasks) { %>
				<%
				String imageURL = null;

				if (taskImageUrls != null) {
					imageURL = taskImageUrls.get(task.getTaskId());
				}

				List<Task> subtasks = null;

				if (subtasksByParentTaskId != null) {
					subtasks = subtasksByParentTaskId.get(task.getTaskId());
				}
				%>

				<li class="task-item" data-status="<%= task.isDone() ? "done" : "pending" %>">
					<div class="task-item-header">
						<div class="task-item-main">
							<h3 class="task-title"><%= task.getTitle() %></h3>

							<% if (task.getDescription() != null && !task.getDescription().trim().isEmpty()) { %>
								<p class="task-description"><%= task.getDescription() %></p>
							<% } %>
						</div>

						<span class="task-status-badge <%= task.isDone() ? "task-status-done" : "task-status-pending" %>">
							<%= task.isDone() ? "Concluída" : "Pendente" %>
						</span>
					</div>

					<% if (imageURL != null && !imageURL.isEmpty()) { %>
						<div class="task-image-wrapper">
							<img
								src="<%= imageURL %>"
								alt="Imagem da tarefa"
								class="task-image"
							/>
						</div>
					<% } %>

					<portlet:renderURL var="editTaskURL">
						<portlet:param name="mvcRenderCommandName" value="/task/edit-form" />
						<portlet:param name="taskId" value="<%= String.valueOf(task.getTaskId()) %>" />
					</portlet:renderURL>

					<portlet:renderURL var="newSubtaskURL">
						<portlet:param name="mvcRenderCommandName" value="/task/form" />
						<portlet:param name="parentTaskId" value="<%= String.valueOf(task.getTaskId()) %>" />
						<portlet:param name="filter" value="<%= currentFilter %>" />
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
							Editar
						</a>

						<a
							class="btn btn-light task-subtask-link"
							href="<%= newSubtaskURL.toString() %>"
						>
							Adicionar Subtarefa
						</a>

						<aui:form action="<%= toggleTaskStatusURL %>" method="post" cssClass="d-inline task-filter-form">
							<aui:input name="filter" type="hidden" value="<%= currentFilter %>" cssClass="task-filter-input" />
							<aui:button
								type="submit"
								value="<%= task.isDone() ? "Reabrir" : "Concluir" %>"
							/>
						</aui:form>

						<aui:form action="<%= deleteTaskURL %>" method="post" cssClass="d-inline task-filter-form">
							<aui:input name="filter" type="hidden" value="<%= currentFilter %>" cssClass="task-filter-input" />
							<aui:button
								type="submit"
								value="Excluir"
							/>
						</aui:form>
					</div>

					<% if (subtasks != null && !subtasks.isEmpty()) { %>
						<div class="subtask-section-title">Subtarefas</div>

						<ul class="subtask-list">
							<% for (Task subtask : subtasks) { %>
								<%
								String subtaskImageURL = null;

								if (taskImageUrls != null) {
									subtaskImageURL = taskImageUrls.get(subtask.getTaskId());
								}
								%>

								<li class="subtask-item" data-status="<%= subtask.isDone() ? "done" : "pending" %>">
									<div class="task-item-header">
										<div class="task-item-main">
											<h4 class="subtask-title"><%= subtask.getTitle() %></h4>

											<% if (subtask.getDescription() != null && !subtask.getDescription().trim().isEmpty()) { %>
												<p class="task-description"><%= subtask.getDescription() %></p>
											<% } %>
										</div>

										<span class="task-status-badge <%= subtask.isDone() ? "task-status-done" : "task-status-pending" %>">
											<%= subtask.isDone() ? "Concluída" : "Pendente" %>
										</span>
									</div>

									<% if (subtaskImageURL != null && !subtaskImageURL.isEmpty()) { %>
										<div class="task-image-wrapper">
											<img
												src="<%= subtaskImageURL %>"
												alt="Imagem da subtarefa"
												class="task-image"
											/>
										</div>
									<% } %>

									<portlet:renderURL var="editSubtaskURL">
										<portlet:param name="mvcRenderCommandName" value="/task/edit-form" />
										<portlet:param name="taskId" value="<%= String.valueOf(subtask.getTaskId()) %>" />
									</portlet:renderURL>

									<portlet:actionURL name="/task/toggle-status" var="toggleSubtaskStatusURL">
										<portlet:param name="taskId" value="<%= String.valueOf(subtask.getTaskId()) %>" />
									</portlet:actionURL>

									<portlet:actionURL name="/task/delete" var="deleteSubtaskURL">
										<portlet:param name="taskId" value="<%= String.valueOf(subtask.getTaskId()) %>" />
									</portlet:actionURL>

									<div class="task-actions">
										<a
											class="btn btn-secondary task-edit-link"
											href="<%= editSubtaskURL.toString() %>&<%= namespace %>filter=<%= currentFilter %>"
										>
											Editar
										</a>

										<aui:form action="<%= toggleSubtaskStatusURL %>" method="post" cssClass="d-inline task-filter-form">
											<aui:input name="filter" type="hidden" value="<%= currentFilter %>" cssClass="task-filter-input" />
											<aui:button
												type="submit"
												value="<%= subtask.isDone() ? "Reabrir" : "Concluir" %>"
											/>
										</aui:form>

										<aui:form action="<%= deleteSubtaskURL %>" method="post" cssClass="d-inline task-filter-form">
											<aui:input name="filter" type="hidden" value="<%= currentFilter %>" cssClass="task-filter-input" />
											<aui:button
												type="submit"
												value="Excluir"
											/>
										</aui:form>
									</div>
								</li>
							<% } %>
						</ul>
					<% } %>
				</li>
			<% } %>
		</ul>
	<% } %>
</div>

<script>
(function() {
	const namespace = '<%= namespace %>';
	const tabsContainer = document.getElementById(namespace + 'taskTabs');
	const currentFilterInput = document.getElementById(namespace + 'currentFilter');
	const newTaskLink = document.getElementById(namespace + 'newTaskLink');

	if (!tabsContainer) {
		return;
	}

	const tabs = tabsContainer.querySelectorAll('.task-tab');
	const taskItems = document.querySelectorAll('.task-item');
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

	function matchesFilter(status, filter) {
		return filter === 'all' || status === filter;
	}

	function applyFilter(filter) {
		taskItems.forEach(function(taskItem) {
			const taskStatus = taskItem.getAttribute('data-status');
			const subtaskItems = taskItem.querySelectorAll('.subtask-item');
			const subtaskList = taskItem.querySelector('.subtask-list');

			let taskMatches = matchesFilter(taskStatus, filter);
			let hasVisibleSubtask = false;

			subtaskItems.forEach(function(subtaskItem) {
				const subtaskStatus = subtaskItem.getAttribute('data-status');
				const subtaskMatches = matchesFilter(subtaskStatus, filter);

				if (subtaskMatches) {
					subtaskItem.style.display = '';
					hasVisibleSubtask = true;
				}
				else {
					subtaskItem.style.display = 'none';
				}
			});

			if (subtaskList) {
				subtaskList.style.display = hasVisibleSubtask ? '' : 'none';
			}

			if (taskMatches || hasVisibleSubtask) {
				taskItem.style.display = '';
			}
			else {
				taskItem.style.display = 'none';
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