package ru.mywork.taskmanager.service;

import ru.mywork.taskmanager.model.*;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    ArrayList<Integer> history;
    private String fileName;


    public FileBackedTaskManager() {

    }

    public FileBackedTaskManager(String fileName) {
        this.fileName = fileName;
        if (!Files.exists(Paths.get(fileName))) {
            try {
                Files.createFile(Paths.get(fileName));
            } catch (IOException exception) {
                System.out.println("Ошибка создания файла");
                exception.printStackTrace();
            }
        }
    }

    static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager load = new FileBackedTaskManager();
        try {
            load.loadDataFromFile(String.valueOf(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return load;
    }

    static String historyToString(HistoryManager manager) {  //это работает.
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

    static List<Integer> historyFromString(String value) { //непонятно для чего
        List<Integer> history = new ArrayList<>();
        if (value.isEmpty()) {
            System.out.println("Передана пустая строка");
            return Collections.emptyList();
        }
        String[] line = value.split(",");
        for (String str : line) {
            history.add(Integer.parseInt(str));
        }
        return history;
    }

    public String getHistory1() {
        return historyToString(historyManager); ///верное решение?
    }

    //static void printHistory()

    public void save() {//это тоже работает
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
            ex.printStackTrace();
        }
    }

    public FileBackedTaskManager loadDataFromFile(String fileName) throws IOException {
        FileBackedTaskManager fbtm = new FileBackedTaskManager(fileName);
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
                    }
                }
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка чтения данных");
            }
        }
        return fbtm;
    }

    Task fromString(String value) {//тут что то не так
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
        long maxID = 0;
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
            int epicID = ((Subtask) task).getEpicId();
            if (epics.containsKey(epicID)) {
                epics.get(epicID).getSubtaskId().add(task.getId()); // получаем эпик и добавляем в него ссылку на подзадачу
            }
        }
        if(task.getId()>getGeneratorId()){
            setGeneratorId(task.getId());
        }
    }


    public List<Integer> loadFromFile(Path path) throws ManagerSaveException {
        String line = " ";
        String cvsSplitBy = ",";
        try (BufferedReader br = new BufferedReader(new FileReader(String.valueOf(path)))) {
            br.readLine();
            while (!(line = br.readLine()).equals("")) {
                String[] task = line.split(cvsSplitBy);
                history.add(Integer.parseInt(task[0]));
                fromString(line);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения данных" + e.getMessage());
        }
        return history;
    }


    @Override
    public int getGeneratorId() {
        return super.getGeneratorId();
    }

    @Override
    public void addNewTask(Task task) {
        super.addNewTask(task);
        save();
    }

    public void addOldTask(Task task) {
        super.addNewTask(task);
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
    public void updateStatusEpic(Epic epic) {
        super.updateStatusEpic(epic);
        save();
    }

    @Override
    public void printEpic(Epic epic) {
        super.printEpic(epic);
        save();
    }

    @Override
    public List<Subtask> getSubtaskByEpicId(int id) {
        save();
        return super.getSubtaskByEpicId(id);


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
    public void getAllTask() {
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
    public Task getTaskById(int id) {
        save();
        return super.getTaskById(id);
    }

    @Override
    public Epic getEpicById(int id) {
        save();
        return super.getEpicById(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        save();
        return super.getSubtaskById(id);
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
    public void checkTaskAvailability() {
        super.checkTaskAvailability();
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
    public void printHistory() {
        super.printHistory();
        save();
    }
}


class ManagerSaveException extends IOException {
    public ManagerSaveException() {
    }

    public ManagerSaveException(final String message) {
        super(message);
    }
}
