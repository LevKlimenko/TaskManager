package ru.mywork.taskmanager.service;

import ru.mywork.taskmanager.model.Task;

import java.util.ArrayList;
import java.util.List;

class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> taskBrowsingHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (taskBrowsingHistory.size() == 10) {
            taskBrowsingHistory.remove(0);
        }
        taskBrowsingHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return taskBrowsingHistory;
    }
}
