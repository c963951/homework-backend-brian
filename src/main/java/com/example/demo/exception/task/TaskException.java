package com.example.demo.exception.task;

public class TaskException {

    public static class TaskNotFoundException extends RuntimeException {
        public TaskNotFoundException(String id) {
            super("Task " + id + " not found");
        }
    }

    public static class TaskAlreadyExistsException extends RuntimeException {
        public TaskAlreadyExistsException(String id) {
            super("Task " + id + " already exists");
        }
    }

    public static class InvalidTaskException extends RuntimeException {
        public InvalidTaskException(String message) {
            super(message);
        }
    }
}
