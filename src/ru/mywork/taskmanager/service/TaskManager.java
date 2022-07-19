package ru.mywork.taskmanager.service;

import ru.mywork.taskmanager.model.Epic;
import ru.mywork.taskmanager.model.Subtask;
import ru.mywork.taskmanager.model.Task;

import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {


    int getGeneratorId();

    void addNewTask(Task task);

    void addNewEpic(Epic epic);

    void addNewSubTask(Subtask subtask);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void updateStatusEpic(Epic epic);

    void printEpic(Epic epic);

    List<Subtask> getSubtaskByEpicId(int id);

    void printAll();

    void printById(int id);

    void getAllTask();

    HashMap<Integer, Epic> getEpics();

    HashMap<Integer, Subtask> getSubtasks();

    HashMap<Integer, Task> getTasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    void clearTask();

    void clearSubtask();

    void clearEpic();

    void checkTaskAvailability();

    void deleteTaskById(int id);


    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    List<Task> getHistory();

    void printHistory();

    TreeSet<Task> getSortedTasks();
}
