package com.desafiosea.todo.web.service.impl;

import com.desafiosea.todo.web.service.TaskImageUploadService;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.PortalUtil;

import java.io.File;
import java.util.UUID;

import javax.portlet.ActionRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = TaskImageUploadService.class)
public class TaskImageUploadServiceImpl implements TaskImageUploadService {

	@Override
	public long uploadTaskImage(
			ActionRequest actionRequest, ThemeDisplay themeDisplay)
		throws Exception {

		UploadPortletRequest uploadPortletRequest =
			PortalUtil.getUploadPortletRequest(actionRequest);

		File file = uploadPortletRequest.getFile("taskImage");
		String fileName = uploadPortletRequest.getFileName("taskImage");
		String contentType = uploadPortletRequest.getContentType("taskImage");

		if (_log.isInfoEnabled()) {
			_log.info("Upload de imagem iniciado");
			_log.info("fileName=" + fileName);
			_log.info("contentType=" + contentType);
			_log.info("file null? " + (file == null));
			_log.info("file length=" + ((file != null) ? file.length() : -1));
		}

		if ((file == null) || (file.length() == 0) ||
			(fileName == null) || fileName.isEmpty()) {

			if (_log.isInfoEnabled()) {
				_log.info("Nenhum novo arquivo enviado");
			}

			return 0L;
		}

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			ActionRequest.class.getName(), actionRequest);

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		String uniqueFileName = _buildUniqueFileName(fileName);

		if (_log.isInfoEnabled()) {
			_log.info("Nome original=" + fileName);
			_log.info("Nome único gerado=" + uniqueFileName);
		}

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null,
			themeDisplay.getUserId(),
			themeDisplay.getScopeGroupId(),
			0,
			uniqueFileName,
			(contentType != null) ? contentType :
				ContentTypes.APPLICATION_OCTET_STREAM,
			uniqueFileName,
			null,
			"Tarefa - imagem anexada",
			"",
			file,
			null,
			null,
			null,
			serviceContext);

		if (_log.isInfoEnabled()) {
			_log.info("Upload concluído com sucesso. fileEntryId=" + fileEntry.getFileEntryId());
		}

		return fileEntry.getFileEntryId();
	}

	private String _buildUniqueFileName(String originalFileName) {
		int extensionIndex = originalFileName.lastIndexOf('.');

		if ((extensionIndex <= 0) || (extensionIndex == originalFileName.length() - 1)) {
			return UUID.randomUUID() + "_" + originalFileName;
		}

		String baseName = originalFileName.substring(0, extensionIndex);
		String extension = originalFileName.substring(extensionIndex);

		return baseName + "_" + UUID.randomUUID() + extension;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TaskImageUploadServiceImpl.class);

	@Reference
	private DLAppLocalService _dlAppLocalService;

}