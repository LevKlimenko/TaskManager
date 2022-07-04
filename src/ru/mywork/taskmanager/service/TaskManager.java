package ru.mywork.taskmanager.service;

import ru.mywork.taskmanager.model.Epic;
import ru.mywork.taskmanager.model.Subtask;
import ru.mywork.taskmanager.model.Task;

import java.util.HashMap;
import java.util.List;

public interface TaskManager {


    int getGeneratorId();

    void addNewTask(Task task) throws FileBackedTaskManager.ManagerSaveException;

    void addNewEpic(Epic epic) throws FileBackedTaskManager.ManagerSaveException;

    void addNewSubTask(Subtask subtask) throws FileBackedTaskManager.ManagerSaveException;

    void updateTask(Task task) throws FileBackedTaskManager.ManagerSaveException;

    void updateSubtask(Subtask subtask) throws FileBackedTaskManager.ManagerSaveException;

    void updateEpic(Epic epic) throws FileBackedTaskManager.ManagerSaveException;

    void updateStatusEpic(Epic epic) throws FileBackedTaskManager.ManagerSaveException;

    void printEpic(Epic epic) throws FileBackedTaskManager.ManagerSaveException;

    List<Subtask> getSubtaskByEpicId(int id) throws FileBackedTaskManager.ManagerSaveException;

    void printAll() throws FileBackedTaskManager.ManagerSaveException;

    void printById(int id) throws FileBackedTaskManager.ManagerSaveException;

    void getAllTask() throws FileBackedTaskManager.ManagerSaveException;

    HashMap<Integer, Epic> getEpics();

    HashMap<Integer, Subtask> getSubtasks();

    HashMap<Integer, Task> getTasks();

    Task getTaskById(int id) throws FileBackedTaskManager.ManagerSaveException;

    Epic getEpicById(int id) throws FileBackedTaskManager.ManagerSaveException;

    Subtask getSubtaskById(int id) throws FileBackedTaskManager.ManagerSaveException;

    void clearTask() throws FileBackedTaskManager.ManagerSaveException;

    void clearSubtask() throws FileBackedTaskManager.ManagerSaveException;

    void clearEpic() throws FileBackedTaskManager.ManagerSaveException;

    void checkTaskAvailability();

    void deleteTaskById(int id) throws FileBackedTaskManager.ManagerSaveException;


    void deleteEpicById(int id) throws FileBackedTaskManager.ManagerSaveException;

    void deleteSubtaskById(int id) throws FileBackedTaskManager.ManagerSaveException;

    List<Task> getHistory();

    void printHistory() throws FileBackedTaskManager.ManagerSaveException;
}
