package ru.mywork.taskmanager.service;

import ru.mywork.taskmanager.model.Epic;
import ru.mywork.taskmanager.model.Subtask;
import ru.mywork.taskmanager.model.Task;
import ru.mywork.taskmanager.model.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final List<Task> taskBrowsingHistory = new ArrayList<>();


    private int generatorId = 0;

    @Override
    public int getGeneratorId() {
        return generatorId;
    }

    @Override
    public void addNewTask(Task task) {
        int id = ++generatorId;
        task.setId(id);
        tasks.put(id, task);
    }

    @Override
    public void addNewEpic(Epic epic) {
        int id = ++generatorId;
        epic.setId(id);
        epics.put(id, epic);
    }

    @Override
    public void addNewSubTask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId())) {
            int id = ++generatorId;
            subtask.setId(id);
            subtasks.put(id, subtask);
            epics.get(subtask.getEpicId()).getSubtaskId().add(id);
            updateStatusEpic(epics.get(subtask.getEpicId()));
        }
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            updateStatusEpic((epics.get(subtasks.get(subtask.getId()).getEpicId())));
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            updateStatusEpic(epic);
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void updateStatusEpic(Epic epic) {
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
    public void printEpic(Epic epic) {
        System.out.println("\n" + epic);
        for (int i = 0; i < epic.getSubtaskId().size(); i++) {
            if (subtasks.get(epic.getSubtaskId().get(i)) != null) {
                System.out.println(subtasks.get(epic.getSubtaskId().get(i)));
            }
            addBrowsingHistory(subtasks.get(epic.getSubtaskId().get(i)));
        }
    }

    @Override
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

    @Override
    public void printAll() { //Метод для удобной проверки данных.Геттеры ниже.Этот метод на будущие проверки
        for (int i = 0; i <= generatorId; i++) {
            if (tasks.containsKey(i)) {
                addBrowsingHistory(tasks.get(i));
                System.out.println(tasks.get(i));
            }
            if (epics.containsKey(i)) {
                addBrowsingHistory(epics.get(i));
                printEpic(epics.get(i));
            }
        }
    }

    @Override
    public void printById(int id) {//для удобного чтения данных со строки.Геттеры ниже.Этот метод на будущие проверки
        if (tasks.containsKey(id)) {
            addBrowsingHistory(tasks.get(id));
            System.out.println(tasks.get(id));
            return;
        }
        if (epics.containsKey(id)) {
            addBrowsingHistory(epics.get(id));
            printEpic(epics.get(id));
            return;
        }
        if (subtasks.containsKey(id)) {
            addBrowsingHistory(subtasks.get(id));
            System.out.println(subtasks.get(id));
        }
    }

    @Override
    public void getAllTask() {
        if (!tasks.isEmpty()) {
            for (Task task : tasks.values()) {
                addBrowsingHistory(tasks.get(task.getId()));
            }
            System.out.println(getTasks());
        }
        if (!epics.isEmpty()) {
            for (Epic epic : epics.values()) {
                addBrowsingHistory(epics.get(epic.getId()));
            }
            System.out.println(getEpics());
        }
        if (!subtasks.isEmpty()) {
            for (Subtask subtask : subtasks.values()) {
                addBrowsingHistory(subtasks.get(subtask.getId()));
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

    private void addBrowsingHistory(Task task) {
        if (taskBrowsingHistory.size() == 10) {
            taskBrowsingHistory.remove(0);
        }
        taskBrowsingHistory.add(task);
    }

    @Override
    public Task getTaskById(int id) {
        addBrowsingHistory(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        addBrowsingHistory(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        addBrowsingHistory(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void clearTask() {
        tasks.clear();
        checkTaskAvailability();
    }

    @Override
    public void clearSubtask() {
        subtasks.clear();
        for (Epic values : epics.values()) {
            values.getSubtaskId().clear();
            updateStatusEpic(epics.get(values.getId()));
        }
        checkTaskAvailability();
    }

    @Override
    public void clearEpic() {
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
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            for (Integer subDel : epics.get(id).getSubtaskId()) {
                subtasks.remove(subDel);
            }
            epics.remove(id);
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            epics.get(subtasks.get(id).getEpicId()).getSubtaskId().remove(Integer.valueOf(id));
            int delId = subtasks.get(id).getEpicId();
            subtasks.remove(id);

            updateStatusEpic(epics.get(delId));
        }
    }

    @Override
    public List<Task> getHistory() {
        return taskBrowsingHistory;
    }

    public void printHistory() {//Сделал для удобства просмотра построчно
        for (Task task : getHistory()) {
            System.out.println(task);
        }
    }

}