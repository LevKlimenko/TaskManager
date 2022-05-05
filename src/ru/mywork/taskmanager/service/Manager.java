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
        task.setTaskId(id);
        tasks.put(id, task);
    }

    public void addNewEpic(Epic epic) {
        int id = ++generatorId;
        epic.setTaskId(id);
        epics.put(id, epic);
    }

    public void addNewSubTask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId())) {
            int id = ++generatorId;
            subtask.setTaskId(id);
            subtasks.put(id, subtask);
            epics.get(subtask.getEpicId()).getSubtaskId().add(id);
            updateEpic(epics.get(subtask.getEpicId()));
        }
    }


    public void updateStatusTask(Task task){
        if (tasks.containsValue(task)) {
            if (task.getStatus().equals(Status.NEW)) {
                task.setStatus(Status.IN_PROGRESS);
            }
            else if (task.getStatus().equals(Status.IN_PROGRESS)){
                task.setStatus(Status.DONE);
            }

            tasks.put(task.getTaskId(),task);
        }
    }

    public void updateStatusSubTask1(int id) {
        if (subtasks.get(id).getStatus() == Status.NEW) {
            subtasks.get(id).setStatus(Status.IN_PROGRESS);
        } else if (subtasks.get(id).getStatus() == Status.IN_PROGRESS) {
            subtasks.get(id).setStatus(Status.DONE);
        } else {
            subtasks.get(id).getStatus();
        }
        subtasks.put(subtasks.get(id).getTaskId(), subtasks.get(id));
        updateEpic(epics.get(subtasks.get(id).getEpicId()));
    }

    public void updateStatusSubTask(Subtask subtask){
        if (subtasks.containsValue(subtask)) {
            if (subtask.getStatus().equals(Status.NEW)) {
                subtask.setStatus(Status.IN_PROGRESS);
            }
            else if (subtask.getStatus().equals(Status.IN_PROGRESS)){
                subtask.setStatus(Status.DONE);
            }
            subtasks.put(subtask.getTaskId(),subtask);
            updateEpic(epics.get(subtasks.get(subtask.getTaskId()).getEpicId()));
        }
    }

    private void updateEpic(Epic epic) {
        int check = 0;
        for (int i = 0; i < epic.getSubtaskId().size(); i++) {
            if (((subtasks.get(epic.getSubtaskId().get(i)).getStatus() == Status.IN_PROGRESS))) {
                epic.setStatus(Status.IN_PROGRESS);
                return;
            }
            if ((subtasks.get(epic.getSubtaskId().get(i)).getStatus() == Status.DONE)) {
                check++;
            }
        }
        if (check != 0 && epic.getStatus() != Status.NEW) {
            epic.setStatus(Status.IN_PROGRESS);
        }
        if (check == epic.getSubtaskId().size()) {
            epic.setStatus(Status.DONE);
        }
        if (epic.getSubtaskId().isEmpty()) {
            epic.setStatus(Status.NEW);
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
        }
        if (epics.containsKey(id)) {
            printEpic(epics.get(id));
        }
        if (subtasks.containsKey(id)) {
            System.out.println(subtasks.get(id));
        }
    }

    public void getAllTask() {
        System.out.println(getTasks());
        System.out.println(getEpics());
        System.out.println(getSubtasks());
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }



    public Object getTaskById(int id) {
        return tasks.get(id);
    }

    public Object getEpicById(int id) {
        return epics.get(id);
    }

    public Object getSubtaskById(int id) {
        return subtasks.get(id);
    }


    public void clearTask() {
        tasks.clear();
        if (tasks.isEmpty() && subtasks.isEmpty() && epics.isEmpty()) {
            generatorId = 0;
        }
    }

    public void clearSubtask() {
        subtasks.clear();
        if (tasks.isEmpty() && subtasks.isEmpty() && epics.isEmpty()) {
            generatorId = 0;
        }
    }

    public void clearEpic() {
        epics.clear();
        if (tasks.isEmpty() && subtasks.isEmpty() && epics.isEmpty()) {
            generatorId = 0;
        }
    }


    public void deleteTaskById(int id) {
        if (tasks.containsKey(id))
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
            epics.get(subtasks.get(id).getEpicId()).getSubtaskId().remove(new Integer(id));
            int delId = subtasks.get(id).getEpicId();
            subtasks.remove(id);
            updateEpic(epics.get(delId));
        }
    }

}







