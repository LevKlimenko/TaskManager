package ru.mywork.taskmanager.service;

import ru.mywork.taskmanager.model.Task;

import java.util.ArrayList;
import java.util.List;

class InMemoryHistoryManager implements HistoryManager {
    private final int MAX_HISTORY_SIZE = 10;
    private final List<Task> taskBrowsingHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            if (taskBrowsingHistory.size() == MAX_HISTORY_SIZE) {
                taskBrowsingHistory.remove(0);
            }
            taskBrowsingHistory.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(taskBrowsingHistory);
    }
}
