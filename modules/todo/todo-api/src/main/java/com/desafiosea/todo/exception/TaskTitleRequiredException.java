package com.desafiosea.todo.exception;

public class TaskTitleRequiredException extends TaskValidationException {

    public TaskTitleRequiredException() {
        super("task-title-required");
    }
}