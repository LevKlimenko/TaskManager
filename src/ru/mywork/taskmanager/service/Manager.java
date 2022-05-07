package ru.mywork.taskmanager.service;

import ru.mywork.taskmanager.model.Epic;
import ru.mywork.taskmanager.model.Subtask;
import ru.mywork.taskmanager.model.Task;
import ru.mywork.taskmanager.model.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Manager {

    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private int generatorId = 0;

    public int getGeneratorId() {
        return generatorId;
    }

    public void addNewTask(Task task) {
        int id = ++generatorId;
        task.setId(id);
        tasks.put(id, task);
    }

    public void addNewEpic(Epic epic) {
        int id = ++generatorId;
        epic.setId(id);
        epics.put(id, epic);
    }

    public void addNewSubTask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId())) {
            int id = ++generatorId;
            subtask.setId(id);
            subtasks.put(id, subtask);
            epics.get(subtask.getEpicId()).getSubtaskId().add(id);
            updateStatusEpic(epics.get(subtask.getEpicId()));
        }
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            updateStatusEpic((epics.get(subtasks.get(subtask.getId()).getEpicId())));
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            updateStatusEpic(epic);
            epics.put(epic.getId(), epic);
        }
    }

    private void updateStatusEpic(Epic epic) {
        int checkDone = 0;
        int checkNew = 0;
        for (Integer subId : epic.getSubtaskId()) {
            if (epic.getSubtaskId().isEmpty()) {
                epic.setStatus(Status.NEW);
                return;
            } else if (((subtasks.get(subId).getStatus() == Status.NEW))) {
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

    private void printEpic(Epic epic) {
        System.out.println("\n" + epic);
        for (int i = 0; i < epic.getSubtaskId().size(); i++) {
            if (subtasks.get(epic.getSubtaskId().get(i)) != null) {
                System.out.println(subtasks.get(epic.getSubtaskId().get(i)));
            }
        }
    }

    public List<Subtask> getSubtaskByEpicId(int id) {
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

    public void printAll() { //Метод для удобной проверки данных.Геттеры ниже.Этот метод на будущие проверки
        for (int i = 0; i <= generatorId; i++) {
            if (tasks.containsKey(i)) {
                System.out.println(tasks.get(i));
            }
            if (epics.containsKey(i)) {
                printEpic(epics.get(i));
            }
        }
    }

    public void printById(int id) {//для удобного чтения данных со строки.Геттеры ниже.Этот метод на будущие проверки
        if (tasks.containsKey(id)) {
            System.out.println(tasks.get(id));
            return;
        }
        if (epics.containsKey(id)) {
            printEpic(epics.get(id));
            return;
        }
        if (subtasks.containsKey(id)) {
            System.out.println(subtasks.get(id));
        }
    }

    public void getAllTask() {
        if (!tasks.isEmpty()) {
            System.out.println(getTasks());
        }
        if (!epics.isEmpty()) {
            System.out.println(getEpics());
        }
        if (!subtasks.isEmpty()) {
            System.out.println(getSubtasks());
        }
    }

    public HashMap<Integer, Epic> getEpics() {
        return new HashMap<>(epics);
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return new HashMap<>(subtasks);
    }

    public HashMap<Integer, Task> getTasks() {
        return new HashMap<>(tasks);
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void clearTask() {
        tasks.clear();
        checkTaskAvailability();
    }

    public void clearSubtask() {
        subtasks.clear();
        for (Epic values : epics.values()) {
            values.getSubtaskId().clear();
            updateStatusEpic(epics.get(values.getId()));
        }
        checkTaskAvailability();
    }

    public void clearEpic() {
        epics.clear();
        subtasks.clear();
        checkTaskAvailability();
    }

    private void checkTaskAvailability() {
        if (tasks.isEmpty() && subtasks.isEmpty() && epics.isEmpty()) {
            generatorId = 0;
        }
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteEpicBuId(int id) {
        if (epics.containsKey(id)) {
            for (Integer subDel : epics.get(id).getSubtaskId()) {
                subtasks.remove(subDel);
            }
            epics.remove(id);
        }
    }

    public void deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            epics.get(subtasks.get(id).getEpicId()).getSubtaskId().remove(Integer.valueOf(id));
            int delId = subtasks.get(id).getEpicId();
            subtasks.remove(id);
            updateStatusEpic(epics.get(delId));
        }
    }
}