package ru.mywork.taskmanager.service;

import ru.mywork.taskmanager.model.Task;
import java.util.ArrayList;
import java.util.List;

class InMemoryHistoryManager<T extends Task> implements HistoryManager<T> {
    private final List<T> taskBrowsingHistory = new ArrayList<>();

    @Override
    public void add(T task) {
        if (taskBrowsingHistory.size() == 10) {
            taskBrowsingHistory.remove(0);
        }
        taskBrowsingHistory.add(task);
    }



    @Override
    public List<T> getHistory() {
        return taskBrowsingHistory;
    }
}
