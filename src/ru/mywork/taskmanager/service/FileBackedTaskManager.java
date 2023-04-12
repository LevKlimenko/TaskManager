package ru.mywork.taskmanager.service;

import ru.mywork.taskmanager.model.*;
import ru.mywork.taskmanager.errors.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileBackedTaskManager extends InMemoryTaskManager {

    protected final static String TABLE_HEADER = "id,type,name,description,status,epic,startTime,duration\n";
    private final File file;


    public FileBackedTaskManager(File file) {
        this.file = file;
    }


    static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager load = new FileBackedTaskManager(file);
        load.loadDataFromFile(String.valueOf(file));
        return load;
    }

    protected String historyToString(HistoryManager manager) {
        StringBuilder sb = new StringBuilder();
        List<Task> history;
        history = getHistory();
        for (Task task : history) {
            sb.append(task.getId());
            if (manager.getHistory().indexOf(task) < manager.getHistory().size() - 1) {
                sb.append(",");
            }
        }
        return String.valueOf(sb);
    }

    protected void historyFromString(String value) {
        List<Integer> historyInt = new ArrayList<>();
        if (value.isEmpty()) {
            System.out.println("Передана пустая строка");
            return;
        }
        String[] line = value.split(",");
        for (String str : line) {
            historyInt.add(Integer.parseInt(str));
        }
        getHistoryFromFile(historyInt);
    }

    private void getHistoryFromFile(List<Integer> historyInt) {
        //System.out.println("----История----");
        for (Integer task : historyInt) {
            if (tasks.containsKey(task)) {
                //  System.out.println(tasks.get(task));
                historyManager.add(tasks.get(task));
            } else if (epics.containsKey(task)) {
                // System.out.println(epics.get(task));
                historyManager.add(epics.get(task));
            } else if (subtasks.containsKey(task)) {
                //System.out.println(subtasks.get(task));
                historyManager.add(subtasks.get(task));
            }
        }
        //System.out.println("----Конец----");
    }

    protected void save() {
        try (FileWriter fw = new FileWriter(file, false)) {
            fw.write(TABLE_HEADER);
            for (Task task : getTasks().values()) {
                fw.write(task.toStringInFile() + "\n");
            }
            for (Epic epic : getEpics().values()) {
                fw.write(epic.toStringInFile() + "\n");
            }
            for (Subtask subtask : getSubtasks().values()) {
                fw.write(subtask.toStringInFile() + "\n");
            }
            fw.write("\n");
            if (getHistory().size() != 0) {
                fw.write(historyToString(historyManager));
            }
        } catch (IOException ex) {
            throw new ManagerSaveException("Ошибка записи");
        }
    }

    private void loadDataFromFile(String fileName) {
        if (Files.exists(Paths.get(fileName))) {
            try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                br.readLine();
                while (br.ready()) {
                    String line = br.readLine();
                    if (line.isBlank()) {
                        line = br.readLine();
                        if ((line != null && !line.isEmpty())) {
                            historyFromString(line);
                        }
                    } else {
                        Task taskFromHistory = fromString(line);
                        if (taskFromHistory != null) {
                            loadTask(taskFromHistory);
                        }
                    }
                }
            } catch (IOException e) {
                throw new ManagerLoadException("Ошибка чтения данных");
            }
        }
    }

    protected Task fromString(String value) {
        String[] task = value.split(",");
        Task newTask = null;
        switch (TypeTask.valueOf(task[1])) {
            case TASK:
                if (!task[5].equals("null")) {
                    newTask = new Task(task[2], task[3], Status.valueOf(task[4]),
                            LocalDateTime.parse(task[5]), Integer.parseInt(task[6]));
                } else {
                    newTask = new Task(task[2], task[3], Status.valueOf(task[4]));
                }
                newTask.setId(Integer.parseInt(task[0]));
                break;
            case EPIC:
                newTask = new Epic(task[2], task[3]);
                newTask.setId(Integer.parseInt(task[0]));
                newTask.setStatus(Status.valueOf(task[4]));
                break;
            case SUBTASK:
                if (task[6].equals("null")) {
                    newTask = new Subtask(task[2], task[3], Integer.parseInt(task[5]), Status.valueOf(task[4]));
                } else {
                    newTask = new Subtask(task[2], task[3], Integer.parseInt(task[5]), Status.valueOf(task[4]),
                            LocalDateTime.parse(task[6]), Integer.parseInt(task[7]));
                    setEpicStartAndEndTime(epics.get(Integer.parseInt(task[5])));
                }
                newTask.setId(Integer.parseInt(task[0]));
                break;
            default:
                break;
        }
        return newTask;
    }

    protected void loadTask(Task task) {
        switch (task.getType()) {
            case TASK:
                tasks.put(task.getId(), task);
                sortedTasks.add(task);
                break;
            case EPIC:
                epics.put(task.getId(), (Epic) task);
                break;
            case SUBTASK:
                subtasks.put(task.getId(), (Subtask) task);
                int epicID = ((Subtask) task).getEpicId();
                if (epics.containsKey(epicID)) {
                    epics.get(epicID).getSubtaskId().add(task.getId());
                }
                sortedTasks.add(task);
                setEpicStartAndEndTime(epics.get(epicID));
                break;
        }
        if (task.getId() > getGeneratorId()) {
            setGeneratorId(task.getId());
        }
    }

    @Override
    public void addNewTask(Task task) {
        super.addNewTask(task);
        save();
    }

    @Override
    public void addNewEpic(Epic epic) {
        super.addNewEpic(epic);
        save();
    }

    @Override
    public void addNewSubTask(Subtask subtask) {
        super.addNewSubTask(subtask);
        updateEpic(epics.get(subtask.getEpicId()));
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        updateEpic(epics.get(subtask.getEpicId()));
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void printEpic(Epic epic) {
        super.printEpic(epic);
        save();
    }

    @Override
    public void printAll() {
        super.printAll();
        save();
    }

    @Override
    public void printById(int id) {
        if (subtasks.containsKey(id)) {
            updateEpic(epics.get(subtasks.get(id).getEpicId()));
        }
        super.printById(id);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public void clearTask() {
        super.clearTask();
        save();
    }

    @Override
    public void clearSubtask() {
        super.clearSubtask();
        save();
    }

    @Override
    public void clearEpic() {
        super.clearEpic();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileBackedTaskManager that = (FileBackedTaskManager) o;
        return Objects.equals(file, that.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file);
    }
}


