package com.desafiosea.todo.exception;

public class TaskDescriptionSizeException extends TaskValidationException {

    public TaskDescriptionSizeException() {
        super("task-description-size-invalid");
    }
}