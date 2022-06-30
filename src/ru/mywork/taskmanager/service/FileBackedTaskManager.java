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
    ArrayList<Integer> history = new ArrayList<>();
    private String fileName;

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

    static List<Integer> historyFromString(String value) {
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

    public void save() {
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

    public void fromString(String value) {
        String[] task = value.split(",");
        switch (task[1]) {
            case "TASK":
                addNewTask(new Task(task[2], task[3], Status.valueOf(task[4])));
                break;
            case "EPIC":
                addNewEpic(new Epic(task[2], task[3]));
                break;
            case "SUBTASK":
                //if (getEpics().containsKey(Integer.parseInt(task[5]))) {
                addNewSubTask(new Subtask(task[2], task[3], Integer.parseInt(task[5]), Status.valueOf(task[4])));
                break;
            }

       }

    public List<Integer> loadFromFile(Path path) throws NullPointerException {
        String line = " ";
        String cvsSplitBy = ",";
        try (BufferedReader br = new BufferedReader(new FileReader(String.valueOf(path)))) {
            br.readLine();
            while (!(line = br.readLine()).equals("")) {
                String[] task = line.split(cvsSplitBy);
                history.add(Integer.parseInt(task[0]));
                fromString(line);  //<----------вот тут ошибка, он добавляет объекты, а потом в след.раз считывает
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения данных");
        }
        return history;
    }

    public void loadDataFromFile(String fileName) throws IOException {
        FileReader fr = new FileReader(fileName);
        BufferedReader br = new BufferedReader(fr);
        while (br.ready()) {
            String line = br.readLine();
            System.out.println(line);
        }
        br.close();
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

    @Override
    public void addNewEpic(Epic epic) {
        super.addNewEpic(epic);
        save();
    }

    @Override
    public void addNewSubTask(Subtask subtask) {
        super.addNewSubTask(subtask);
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
