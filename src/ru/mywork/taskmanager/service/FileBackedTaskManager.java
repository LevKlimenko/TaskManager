package ru.mywork.taskmanager.service;

import ru.mywork.taskmanager.model.*;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    public List<Integer> historyInt;


    public FileBackedTaskManager() {
    }

    public FileBackedTaskManager(String fileName) {
        if (!Files.exists(Paths.get(fileName))) {
            try {
                Files.createFile(Paths.get(fileName));
            } catch (IOException exception) {
                System.out.println("Ошибка создания файла");
                exception.printStackTrace();
            }
        }
    }

    static FileBackedTaskManager loadFromFile(Path path) {
        FileBackedTaskManager load = new FileBackedTaskManager();
        try {
            load.loadDataFromFile(String.valueOf(path));
        } catch (ManagerLoadException e) {
            throw new RuntimeException(e);
        }
        return load;
    }

    static String historyToString(HistoryManager manager) {
        List<Task> history = new ArrayList<>(manager.getHistory());
        StringBuilder sb = new StringBuilder();
        if (history.isEmpty()) {
            System.out.println("История просмотров пуста");
            return sb.toString();
        }
        for (Task task : manager.getHistory()) {
            sb.append(task.getId());
            if (manager.getHistory().indexOf(task) < manager.getHistory().size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    private void historyFromStringToArray(String value) {
        this.historyInt = new ArrayList<>();
        if (value.isEmpty()) {
            System.out.println("Передана пустая строка");
            return;
        }
        String[] line = value.split(",");
        for (String str : line) {
            historyInt.add(Integer.parseInt(str));
        }
    }

    public String getHistoryInt() {
        return historyToString(historyManager);
    }

    public void getHistoryFromFile() {
        for (Integer task : historyInt) {
            if (tasks.containsKey(task)) {
                System.out.println(tasks.get(task));
            } else if (epics.containsKey(task)) {
                System.out.println(epics.get(task));
            } else if (subtasks.containsKey(task)) {
                System.out.println(subtasks.get(task));
            }
        }
    }


    private void save() throws ManagerSaveException {
        try (FileWriter fw = new FileWriter("tasks.csv", false)) {
            fw.write("id,type,name,description,status,epic/[subtasksId]\n");
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
                fw.close();
            }
        } catch (IOException ex) {
            throw new ManagerSaveException("Ошибка записи");
        }
    }

    private void loadDataFromFile(String fileName) throws ManagerLoadException {
        //FileBackedTaskManager fbtm = new FileBackedTaskManager(fileName);
        if (Files.exists(Paths.get(fileName))) {
            BufferedReader br;
            try {
                br = new BufferedReader(new FileReader(fileName));
                while (br.ready()) {
                    String line = br.readLine();
                    if (!line.equals("")) {
                        fromString(line);
                        if (fromString(line) != null) {
                            loadTask(fromString(line));
                        }
                    } else {
                        line = br.readLine();
                        if ((line != null && !line.isEmpty())) {
                            historyFromStringToArray(line);
                        }
                    }
                }
            } catch (IOException e) {
                throw new ManagerLoadException("Ошибка чтения данных");
            }
        }
    }


    private Task fromString(String value) {
        String[] task = value.split(",");
        Task newTask = null;
        switch (task[1]) {
            case "TASK":
                newTask = new Task(task[2], task[3], Status.valueOf(task[4]));
                newTask.setId(Integer.parseInt(task[0]));
                break;
            case "EPIC":
                newTask = new Epic(task[2], task[3]);
                newTask.setId(Integer.parseInt(task[0]));
                newTask.setStatus(Status.valueOf(task[4]));
                break;
            case "SUBTASK":
                newTask = new Subtask(task[2], task[3], Integer.parseInt(task[5]), Status.valueOf(task[4]));
                newTask.setId(Integer.parseInt(task[0]));
                break;
        }
        return newTask;
    }

    private void loadTask(Task task) {
        if (task.getTypeTask() == TypeTask.TASK) {
            tasks.put(task.getId(), task);
        }
        if (task.getTypeTask() == TypeTask.EPIC) {
            epics.put(task.getId(), (Epic) task);
        }
        if (task.getTypeTask() == TypeTask.SUBTASK) {
            if (task instanceof Subtask) {
                subtasks.put(task.getId(), (Subtask) task);
            }
            assert task instanceof Subtask;
            int epicID = ((Subtask) task).getEpicId();
            if (epics.containsKey(epicID)) {
                epics.get(epicID).getSubtaskId().add(task.getId());
            }
        }
        if (task.getId() > getGeneratorId()) {
            setGeneratorId(task.getId());
        }
    }


    @Override
    public int getGeneratorId() {
        return super.getGeneratorId();
    }

    @Override
    public void addNewTask(Task task) throws ManagerSaveException {
        super.addNewTask(task);
        save();
    }

    @Override
    public void addNewEpic(Epic epic) throws ManagerSaveException {
        super.addNewEpic(epic);
        save();
    }

    @Override
    public void addNewSubTask(Subtask subtask) throws ManagerSaveException {
        super.addNewSubTask(subtask);
        updateEpic(epics.get(subtask.getEpicId()));
        save();
    }

    @Override
    public void updateTask(Task task) throws ManagerSaveException {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) throws ManagerSaveException {
        super.updateSubtask(subtask);
        updateEpic(epics.get(subtask.getEpicId()));
        save();
    }

    @Override
    public void updateEpic(Epic epic) throws ManagerSaveException {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateStatusEpic(Epic epic) throws ManagerSaveException {
        super.updateStatusEpic(epic);
        save();
    }

    @Override
    public void printEpic(Epic epic) throws ManagerSaveException {
        super.printEpic(epic);
        save();
    }

    @Override
    public List<Subtask> getSubtaskByEpicId(int id) throws ManagerSaveException {
        save();
        return super.getSubtaskByEpicId(id);
    }

    @Override
    public void printAll() throws ManagerSaveException {
        super.printAll();
        save();
    }

    @Override
    public void printById(int id) throws ManagerSaveException {
        if (subtasks.containsKey(id)) {
            updateEpic(epics.get(subtasks.get(id).getEpicId()));
        }
        super.printById(id);
        save();

    }

    @Override
    public void getAllTask() throws ManagerSaveException {
        super.getAllTask();
        save();
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        return super.getEpics();
    }

    @Override
    public HashMap<Integer, Subtask> getSubtasks() {
        return super.getSubtasks();
    }

    @Override
    public HashMap<Integer, Task> getTasks() {
        return super.getTasks();
    }

    @Override
    public Task getTaskById(int id) throws ManagerSaveException {
        save();
        return super.getTaskById(id);
    }

    @Override
    public Epic getEpicById(int id) throws ManagerSaveException {
        save();
        return super.getEpicById(id);
    }

    @Override
    public Subtask getSubtaskById(int id) throws ManagerSaveException {
        save();
        return super.getSubtaskById(id);
    }

    @Override
    public void clearTask() throws ManagerSaveException {
        super.clearTask();
        save();
    }

    @Override
    public void clearSubtask() throws ManagerSaveException {
        super.clearSubtask();
        save();
    }

    @Override
    public void clearEpic() throws ManagerSaveException {
        super.clearEpic();
        save();
    }

    @Override
    public void checkTaskAvailability() {
        super.checkTaskAvailability();
    }

    @Override
    public void deleteTaskById(int id) throws ManagerSaveException {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) throws ManagerSaveException {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) throws ManagerSaveException {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void printHistory() throws ManagerSaveException {
        super.printHistory();
        save();
    }

    private static class ManagerLoadException extends Throwable {
        public ManagerLoadException(String message) {
            super(message);
        }
    }

    protected static class ManagerSaveException extends IOException {

        public ManagerSaveException(final String message) {
            super(message);
        }
    }

}


