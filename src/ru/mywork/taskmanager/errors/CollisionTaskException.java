package ru.mywork.taskmanager.errors;


public class CollisionTaskException extends RuntimeException {
    public CollisionTaskException(String message) {
       super(message);
    }
}
