package com.desafiosea.todo.exception;

import com.liferay.portal.kernel.exception.PortalException;

public class TaskValidationException extends PortalException {

    public TaskValidationException(String msg) {
        super(msg);
    }
}