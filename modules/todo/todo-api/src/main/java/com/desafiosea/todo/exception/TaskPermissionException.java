package com.desafiosea.todo.exception;

import com.liferay.portal.kernel.exception.PortalException;

public class TaskPermissionException extends PortalException {

    public TaskPermissionException() {
        super("task-permission-denied");
    }
}