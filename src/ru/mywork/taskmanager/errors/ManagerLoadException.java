package ru.mywork.taskmanager.errors;

public class ManagerLoadException extends RuntimeException {
    public ManagerLoadException(String message) {
        super(message);
    }
}