package com.desafiosea.todo.web.action;

import com.desafiosea.todo.exception.TaskDescriptionSizeException;
import com.desafiosea.todo.exception.TaskPermissionException;
import com.desafiosea.todo.exception.TaskTitleRequiredException;
import com.desafiosea.todo.exception.TaskTitleSizeException;
import com.desafiosea.todo.model.Task;
import com.desafiosea.todo.service.TaskLocalService;
import com.desafiosea.todo.web.constants.TodoPortletKeys;
import com.desafiosea.todo.web.service.TaskActionFeedbackService;
import com.desafiosea.todo.web.service.TaskImageUploadService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	property = {
		"javax.portlet.name=" + TodoPortletKeys.TODO,
		"mvc.command.name=/task/update"
	},
	service = MVCActionCommand.class
)
public class UpdateTaskMVCActionCommand implements MVCActionCommand {

	@Override
	public boolean processAction(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		long taskId = _getLong(actionRequest, "taskId", 0L);
		String title = _getString(actionRequest, "title");
		String description = _getString(actionRequest, "description");
		boolean done = _getBoolean(actionRequest, "done");

		if (_log.isInfoEnabled()) {
			_log.info("Iniciando atualização da tarefa");
			_log.info("taskId=" + taskId);
			_log.info("title=" + title);
			_log.info("descriptionLength=" + description.length());
			_log.info("done=" + done);
		}

		try {
			ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

			if (themeDisplay == null) {
				_log.error("ThemeDisplay veio nulo no update da tarefa");
				throw new IllegalStateException("ThemeDisplay não encontrado");
			}

			User user = themeDisplay.getUser();

			if (user == null) {
				_log.error("User veio nulo no update da tarefa");
				throw new IllegalStateException("Usuário não encontrado");
			}

			if (_log.isInfoEnabled()) {
				_log.info("userId=" + user.getUserId());
				_log.info("scopeGroupId=" + themeDisplay.getScopeGroupId());
				_log.info("Buscando tarefa atual no banco");
			}

			Task task = _taskLocalService.getTask(taskId);

			if (_log.isInfoEnabled()) {
				_log.info(
					"Tarefa encontrada. taskId=" + task.getTaskId() +
						", fileEntryId atual=" + task.getFileEntryId());
				_log.info("Tentando upload de nova imagem");
			}

			long uploadedFileEntryId = _taskImageUploadService.uploadTaskImage(
				actionRequest, themeDisplay);

			if (_log.isInfoEnabled()) {
				_log.info("Resultado do upload. uploadedFileEntryId=" + uploadedFileEntryId);
			}

			long fileEntryId = uploadedFileEntryId;

			if (fileEntryId == 0L) {
				fileEntryId = task.getFileEntryId();

				if (_log.isInfoEnabled()) {
					_log.info(
						"Nenhuma nova imagem enviada. Mantendo fileEntryId atual=" +
							fileEntryId);
				}
			}
			else if (_log.isInfoEnabled()) {
				_log.info("Nova imagem enviada. Novo fileEntryId=" + fileEntryId);
			}

			if (_log.isInfoEnabled()) {
				_log.info("Chamando taskLocalService.updateTask(...)");
			}

			_taskLocalService.updateTask(
				user.getUserId(), taskId, title, description, done, fileEntryId);

			if (_log.isInfoEnabled()) {
				_log.info("Tarefa atualizada com sucesso");
			}

			return true;
		}
		catch (TaskTitleRequiredException e) {
			_log.error("Erro de validação: título obrigatório", e);

			_taskActionFeedbackService.addError(
				actionRequest, "task-title-required");
		}
		catch (TaskTitleSizeException e) {
			_log.error("Erro de validação: tamanho do título inválido", e);

			_taskActionFeedbackService.addError(
				actionRequest, "task-title-size");
		}
		catch (TaskDescriptionSizeException e) {
			_log.error("Erro de validação: descrição muito longa", e);

			_taskActionFeedbackService.addError(
				actionRequest, "task-description-size");
		}
		catch (TaskPermissionException e) {
			_log.error("Erro de permissão ao atualizar tarefa", e);

			_taskActionFeedbackService.addError(
				actionRequest, "task-permission-denied");
		}
		catch (Exception e) {
			_log.error("Erro inesperado ao atualizar tarefa", e);

			_taskActionFeedbackService.addError(
				actionRequest, "task-update-error");
		}

		_taskActionFeedbackService.hideDefaultErrorMessage(actionRequest);
		_taskActionFeedbackService.prepareFormRender(
			actionRequest, actionResponse);

		actionResponse.setRenderParameter(
			"mvcRenderCommandName", "/task/edit-form");
		actionResponse.setRenderParameter(
			"taskId", String.valueOf(taskId));

		return true;
	}

	private boolean _getBoolean(ActionRequest actionRequest, String name) {
		String value = actionRequest.getParameter(name);

		return "true".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value);
	}

	private long _getLong(
		ActionRequest actionRequest, String name, long defaultValue) {

		String value = actionRequest.getParameter(name);

		if ((value == null) || value.trim().isEmpty()) {
			return defaultValue;
		}

		try {
			return Long.parseLong(value);
		}
		catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	private String _getString(ActionRequest actionRequest, String name) {
		String value = actionRequest.getParameter(name);

		return value == null ? "" : value;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpdateTaskMVCActionCommand.class);

	@Reference
	private TaskActionFeedbackService _taskActionFeedbackService;

	@Reference
	private TaskImageUploadService _taskImageUploadService;

	@Reference
	private TaskLocalService _taskLocalService;
}