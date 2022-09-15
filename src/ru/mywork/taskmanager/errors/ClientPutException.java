package ru.mywork.taskmanager.errors;

public class ClientPutException extends RuntimeException {

    public ClientPutException(final String message) {
        super(message);
    }
}
