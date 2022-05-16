package ru.mywork.taskmanager.service;

import ru.mywork.taskmanager.model.Task;


import java.util.List;

public interface HistoryManager {

    void add(Task task);

    List<Task> getHistory();
}
