<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/init.jsp" %>

<%@ page import="com.desafiosea.todo.model.Task" %>
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

long parentTaskId = ParamUtil.getLong(request, "parentTaskId", 0L);

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

	if (parentTaskId == 0) {
		parentTaskId = task.getParentTaskId();
	}
}

boolean creatingSubtask = !editing && (parentTaskId > 0);
String formTitle = editing ? "Editar Tarefa" : (creatingSubtask ? "Nova Subtarefa" : "Nova Tarefa");
%>

<portlet:actionURL name="<%= editing ? "/task/update" : "/task/add" %>" var="taskActionURL" />

<portlet:renderURL var="viewTasksURL">
	<portlet:param name="mvcRenderCommandName" value="/task/view" />
	<portlet:param name="filter" value="<%= currentFilter %>" />
</portlet:renderURL>

<div class="task-form-container">
	<h2><%= formTitle %></h2>

	<aui:form action="<%= taskActionURL %>" method="post" enctype="multipart/form-data">
		<aui:input name="filter" type="hidden" value="<%= currentFilter %>" />
		<aui:input name="parentTaskId" type="hidden" value="<%= parentTaskId %>" />

		<% if (editing) { %>
			<aui:input name="taskId" type="hidden" value="<%= taskId %>" />
		<% } %>

		<aui:input
			name="title"
			label="Título"
			value="<%= title %>"
		/>

		<aui:input
			name="description"
			label="Descrição"
			type="textarea"
			value="<%= description %>"
		/>

		<aui:input
			name="done"
			label="Concluída"
			type="checkbox"
			checked="<%= done %>"
		/>

		<aui:input
			name="taskImage"
			label="Imagem da tarefa"
			type="file"
		/>

		<aui:button-row>
			<aui:button type="submit" value="Salvar" />
			<a class="btn btn-secondary" href="<%= viewTasksURL.toString() %>">
				Cancelar
			</a>
		</aui:button-row>
	</aui:form>
</div>