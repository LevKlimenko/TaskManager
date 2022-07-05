package ru.mywork.taskmanager.service;

import ru.mywork.taskmanager.model.*;
import ru.mywork.taskmanager.errors.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryTaskManager implements TaskManager {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();
    private int generatorId = 0;

    @Override
    public int getGeneratorId() {
        return generatorId;
    }

    public void setGeneratorId(int generatorId) {
        this.generatorId = generatorId;
    }

    @Override
    public void addNewTask(Task task) throws ManagerSaveException {
        int id = ++generatorId;
        task.setId(id);
        tasks.put(id, task);
    }

    @Override
    public void addNewEpic(Epic epic) throws ManagerSaveException {
        int id = ++generatorId;
        epic.setId(id);
        epics.put(id, epic);
    }

    @Override
    public void addNewSubTask(Subtask subtask) throws ManagerSaveException {
        if (epics.containsKey(subtask.getEpicId())) {
            int id = ++generatorId;
            subtask.setId(id);
            subtasks.put(id, subtask);
            epics.get(subtask.getEpicId()).getSubtaskId().add(id);
            updateStatusEpic(epics.get(subtask.getEpicId()));
        }
    }

    @Override
    public void updateTask(Task task) throws ManagerSaveException {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) throws ManagerSaveException {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            updateStatusEpic((epics.get(subtasks.get(subtask.getId()).getEpicId())));
        }
    }

    @Override
    public void updateEpic(Epic epic) throws ManagerSaveException {
        if (epics.containsKey(epic.getId())) {
            updateStatusEpic(epic);
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void updateStatusEpic(Epic epic) throws ManagerSaveException {
        int checkDone = 0;
        int checkNew = 0;
        for (Integer subId : epic.getSubtaskId()) {
            if (((subtasks.get(subId).getStatus() == Status.NEW))) {
                checkNew++;
            } else if ((subtasks.get(subId)).getStatus() == Status.DONE) {
                checkDone++;
            } else {
                epic.setStatus(Status.IN_PROGRESS);
                return;
            }
        }
        if (checkNew == epic.getSubtaskId().size()) {
            epic.setStatus(Status.NEW);
        } else if (checkDone == epic.getSubtaskId().size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public void printEpic(Epic epic) throws ManagerSaveException {
        System.out.println("\n" + epic);
        for (int i = 0; i < epic.getSubtaskId().size(); i++) {
            if (subtasks.get(epic.getSubtaskId().get(i)) != null) {
                System.out.println(subtasks.get(epic.getSubtaskId().get(i)));
            }
            historyManager.add(subtasks.get(epic.getSubtaskId().get(i)));
        }
    }

    @Override
    public List<Subtask> getSubtaskByEpicId(int id)  {
        List<Subtask> subtask = new ArrayList<>();
        if (epics.containsKey(id)) {
            for (int i = 0; i < epics.get(id).getSubtaskId().size(); i++) {
                if (subtasks.get(epics.get(id).getSubtaskId().get(i)) != null) {
                    subtask.add(subtasks.get(epics.get(id).getSubtaskId().get(i)));
                }
            }
        }
        return subtask;
    }

    @Override
    public void printAll() throws ManagerSaveException { //Метод для удобной проверки данных.Геттеры ниже.Этот метод на будущие проверки
        for (int i = 0; i <= generatorId; i++) {
            if (tasks.containsKey(i)) {
                historyManager.add(tasks.get(i));
                System.out.println(tasks.get(i));
            }
            if (epics.containsKey(i)) {
                historyManager.add(epics.get(i));
                printEpic(epics.get(i));
            }
        }
    }

    @Override
    public void printById(int id) throws ManagerSaveException {//для удобного чтения данных со строки.Геттеры ниже.Этот метод на будущие проверки
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
            System.out.println(tasks.get(id));
            return;
        }
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
            System.out.println(epics.get(id));
            return;
        }
        if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
            System.out.println(subtasks.get(id));
        }
    }

    @Override
    public void getAllTask() throws ManagerSaveException {
        if (!tasks.isEmpty()) {
            for (Task task : tasks.values()) {
                historyManager.add(tasks.get(task.getId()));
            }
            System.out.println(getTasks());
        }
        if (!epics.isEmpty()) {
            for (Epic epic : epics.values()) {
                historyManager.add(epics.get(epic.getId()));
            }
            System.out.println(getEpics());
        }
        if (!subtasks.isEmpty()) {
            for (Subtask subtask : subtasks.values()) {
                historyManager.add(subtasks.get(subtask.getId()));
            }
            System.out.println(getSubtasks());
        }
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        return new HashMap<>(epics);
    }

    @Override
    public HashMap<Integer, Subtask> getSubtasks() {
        return new HashMap<>(subtasks);
    }

    @Override
    public HashMap<Integer, Task> getTasks() {
        return new HashMap<>(tasks);
    }

    @Override
    public Task getTaskById(int id) throws ManagerSaveException {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
        }
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) throws ManagerSaveException {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
        }
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) throws ManagerSaveException {
        if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
        }
        return subtasks.get(id);
    }

    @Override
    public void clearTask() throws ManagerSaveException {
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
        checkTaskAvailability();
    }

    @Override
    public void clearSubtask() throws ManagerSaveException {
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        subtasks.clear();
        for (Epic values : epics.values()) {
            values.getSubtaskId().clear();
            updateStatusEpic(epics.get(values.getId()));
        }
        checkTaskAvailability();
    }

    @Override
    public void clearEpic() throws ManagerSaveException {
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
        subtasks.clear();
        checkTaskAvailability();
    }

    @Override
    public void checkTaskAvailability() {
        if (tasks.isEmpty() && subtasks.isEmpty() && epics.isEmpty()) {
            generatorId = 0;
        }
    }

    @Override
    public void deleteTaskById(int id) throws ManagerSaveException {
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(int id) throws ManagerSaveException {
        if (epics.containsKey(id)) {
            for (Integer subDel : epics.get(id).getSubtaskId()) {
                historyManager.remove(subDel);
                subtasks.remove(subDel);
            }
            historyManager.remove(id);
            epics.remove(id);
        }
    }

    @Override
    public void deleteSubtaskById(int id) throws ManagerSaveException {
        if (subtasks.containsKey(id)) {
            epics.get(subtasks.get(id).getEpicId()).getSubtaskId().remove(Integer.valueOf(id));
            int delId = subtasks.get(id).getEpicId();
            historyManager.remove(id);
            subtasks.remove(id);
            updateStatusEpic(epics.get(delId));
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void printHistory() throws ManagerSaveException {//Сделал для удобства просмотра построчно
        System.out.println(ANSI_RED + "История просмотров:" + ANSI_RESET);
        for (int i = 0; i < getHistory().size(); i++) {
            System.out.println(getHistory().get(i));
        }
        System.out.println(ANSI_RED + "Конец истории просмотров" + ANSI_RESET);
    }
}

