package ru.mywork.taskmanager.service;

import ru.mywork.taskmanager.model.Epic;
import ru.mywork.taskmanager.model.Subtask;
import ru.mywork.taskmanager.model.Task;
import ru.mywork.taskmanager.errors.*;

import java.util.HashMap;
import java.util.List;

public interface TaskManager {


    int getGeneratorId();

    void addNewTask(Task task) throws ManagerSaveException;

    void addNewEpic(Epic epic) throws ManagerSaveException;

    void addNewSubTask(Subtask subtask) throws ManagerSaveException;

    void updateTask(Task task) throws ManagerSaveException;

    void updateSubtask(Subtask subtask) throws ManagerSaveException;

    void updateEpic(Epic epic) throws ManagerSaveException;

    void updateStatusEpic(Epic epic) throws ManagerSaveException;

    void printEpic(Epic epic) throws ManagerSaveException;

    List<Subtask> getSubtaskByEpicId(int id) throws ManagerSaveException;

    void printAll() throws ManagerSaveException;

    void printById(int id) throws ManagerSaveException;

    void getAllTask() throws ManagerSaveException;

    HashMap<Integer, Epic> getEpics();

    HashMap<Integer, Subtask> getSubtasks();

    HashMap<Integer, Task> getTasks();

    Task getTaskById(int id) throws ManagerSaveException;

    Epic getEpicById(int id) throws ManagerSaveException;

    Subtask getSubtaskById(int id) throws ManagerSaveException;

    void clearTask() throws ManagerSaveException;

    void clearSubtask() throws ManagerSaveException;

    void clearEpic() throws ManagerSaveException;

    void checkTaskAvailability();

    void deleteTaskById(int id) throws ManagerSaveException;


    void deleteEpicById(int id) throws ManagerSaveException;

    void deleteSubtaskById(int id) throws ManagerSaveException;

    List<Task> getHistory();

    void printHistory();

    void setEpicStartAndEndTime();

    }
