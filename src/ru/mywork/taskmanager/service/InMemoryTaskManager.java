package ru.mywork.taskmanager.service;

import ru.mywork.taskmanager.model.*;
import ru.mywork.taskmanager.errors.*;

import java.time.LocalDateTime;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();
    protected TreeSet<Task> sortedTasks = new TreeSet<>((o1, o2) -> {
        if (o1.getStartTime() == null) {
            if (o2.getStartTime() == null) {
                return o1.getId() - o2.getId();
            } else {
                return 1;
            }
        } else if (o2.getStartTime() == null) {
            return -1;
        } else if (o1.getStartTime().isBefore(o2.getStartTime())) {
            return -1;
        } else if (o1.getStartTime().isAfter(o2.getStartTime())) {
            return 1;
        }
        return 0;
    });
    protected int generatorId = 0;

    @Override
    public int getGeneratorId() {
        return generatorId;
    }

    protected void setGeneratorId(int generatorId) {
        this.generatorId = generatorId;
    }

    @Override
    public void addNewTask(Task task) {
        checkTimeTask(task);
        int id = ++generatorId;
        task.setId(id);
        sortedTasks.add(task);
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
            checkTimeTask(subtask);
            int id = ++generatorId;
            subtask.setId(id);
            subtasks.put(id, subtask);
            sortedTasks.add(subtask);
            epics.get(subtask.getEpicId()).getSubtaskId().add(id);
            updateStatusEpic(epics.get(subtask.getEpicId()));
            setEpicStartAndEndTime(epics.get(subtask.getEpicId()));
        }
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            checkTimeTask(task);
            sortedTasks.remove(tasks.get(task.getId()));
            tasks.put(task.getId(), task);
            sortedTasks.add(task);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            checkTimeTask(subtask);
            sortedTasks.remove(subtasks.get(subtask.getId()));
            subtasks.put(subtask.getId(), subtask);
            updateStatusEpic((epics.get(subtasks.get(subtask.getId()).getEpicId())));
            sortedTasks.add(subtask);
            setEpicStartAndEndTime(epics.get(subtask.getEpicId()));
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            updateStatusEpic(epic);
            setEpicStartAndEndTime(epic);
            epics.put(epic.getId(), epic);
        }
    }

    private void updateStatusEpic(Epic epic) {
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
            historyManager.add(subtasks.get(epic.getSubtaskId().get(i)));
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
    public void printById(int id) {//для удобного чтения данных со строки.Геттеры ниже.Этот метод на будущие проверки
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
    public void getAllTask() {
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
    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
        }
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
        }
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
        }
        return subtasks.get(id);
    }

    @Override
    public void clearTask() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            sortedTasks.remove(task);
        }
        tasks.clear();
        checkTaskAvailability();
    }

    @Override
    public void clearSubtask() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            sortedTasks.remove(subtask);
        }
        subtasks.clear();
        for (Epic values : epics.values()) {
            values.getSubtaskId().clear();
            updateStatusEpic(epics.get(values.getId()));
            setEpicStartAndEndTime(epics.get(values.getId()));
        }
        checkTaskAvailability();
    }

    @Override
    public void clearEpic() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            sortedTasks.remove(subtask);
        }
        for (Epic epics : epics.values()) {
            historyManager.remove(epics.getId());
        }
        epics.clear();
        subtasks.clear();
        checkTaskAvailability();
    }

    private void checkTaskAvailability() {
        if (tasks.isEmpty() && subtasks.isEmpty() && epics.isEmpty()) {
            generatorId = 0;
        }
    }

    @Override
    public void deleteTaskById(int id) {
        historyManager.remove(id);
        sortedTasks.remove(tasks.get(id));
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            for (Integer subDel : epics.get(id).getSubtaskId()) {
                historyManager.remove(subDel);
                sortedTasks.remove(subtasks.get(subDel));
                subtasks.remove(subDel);
            }
            historyManager.remove(id);
            epics.remove(id);
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            epics.get(subtasks.get(id).getEpicId()).getSubtaskId().remove(Integer.valueOf(id));
            int delId = subtasks.get(id).getEpicId();
            historyManager.remove(id);
            sortedTasks.remove(subtasks.get(id));
            subtasks.remove(id);
            updateStatusEpic(epics.get(delId));
            setEpicStartAndEndTime(epics.get(delId));
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public TreeSet<Task> getSortedTasks() {
        return sortedTasks;
    }

    @Override
    public void printHistory() {//Сделал для удобства просмотра построчно
        System.out.println(ANSI_RED + "История просмотров:" + ANSI_RESET);
        for (int i = 0; i < getHistory().size(); i++) {
            System.out.println(getHistory().get(i));
        }
        System.out.println(ANSI_RED + "Конец истории просмотров" + ANSI_RESET);
    }

    protected void setEpicStartAndEndTime(Epic epic) {
        LocalDateTime timeStart = null;
        LocalDateTime timeEnd = null;
        int duration = 0;
        for (Integer subtaskId : epic.getSubtaskId()) {
            Subtask subtask = subtasks.get(subtaskId);
            LocalDateTime startTimeSubtask = subtask.getStartTime();
            LocalDateTime endTimeSubtask = subtask.getEndTime();
            if (startTimeSubtask != null) {
                if (timeStart == null || timeStart.isAfter(startTimeSubtask)) {
                    timeStart = startTimeSubtask;
                }
                if (timeEnd == null || timeEnd.isBefore(endTimeSubtask)) {
                    timeEnd = endTimeSubtask;
                }
                duration += subtask.getDuration();
            }
        }
        epic.setEndTime(timeEnd);
        epic.setStartTime(timeStart);
        epic.setDuration(duration);
    }

    private void checkTimeTask(Task task) {
        ArrayList<Task> sortListTask = new ArrayList<>(sortedTasks);
        for (Task sortedTask : sortListTask) {
            if (task.getId() == sortedTask.getId()) {
                continue;
            }
            if (sortListTask.get(0).getStartTime() == null || task.getStartTime() == null) {
                return;
            }
            if (sortedTask.getStartTime() != null) {
                if (!task.getStartTime().isBefore(sortedTask.getEndTime())) {
                    continue;
                }
                if (!task.getEndTime().isAfter(sortedTask.getStartTime())) {
                    continue;
                }
                System.out.println("Новая задача " + task.getName() +
                        " совпадает по времени с " + sortedTask.getName());//для консольного вывода с сервером
                throw new CollisionTaskException("Новая задача " + task.getName() +
                        " совпадает по времени с " + sortedTask.getName());
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InMemoryTaskManager that = (InMemoryTaskManager) o;
        return generatorId == that.generatorId &&
                Objects.equals(epics, that.epics) &&
                Objects.equals(subtasks, that.subtasks) &&
                Objects.equals(tasks, that.tasks) &&
                Objects.equals(historyManager, that.historyManager) &&
                Objects.equals(sortedTasks, that.sortedTasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(epics, subtasks, tasks, historyManager, sortedTasks, generatorId);
    }
}



