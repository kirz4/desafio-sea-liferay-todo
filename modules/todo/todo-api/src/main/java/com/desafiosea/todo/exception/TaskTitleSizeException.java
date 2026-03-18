package com.desafiosea.todo.exception;

public class TaskTitleSizeException extends TaskValidationException {

    public TaskTitleSizeException() {
        super("task-title-size-invalid");
    }
}