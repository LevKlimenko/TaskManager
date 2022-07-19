package ru.mywork.taskmanager.service;

import ru.mywork.taskmanager.model.Task;


import java.util.List;
import java.util.Map;

public interface HistoryManager {

    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}
