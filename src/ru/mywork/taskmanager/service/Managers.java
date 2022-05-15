package ru.mywork.taskmanager.service;

public abstract class Managers <T extends TaskManager> {


    private static ru.mywork.taskmanager.service.InMemoryHistoryManager InMemoryHistoryManager;

    public abstract TaskManager getDefault();

    static InMemoryHistoryManager getDefaultHistory() {
        return InMemoryHistoryManager;
    }
}
