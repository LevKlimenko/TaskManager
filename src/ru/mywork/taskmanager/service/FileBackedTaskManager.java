package ru.mywork.taskmanager.service;

import ru.mywork.taskmanager.model.*;
import ru.mywork.taskmanager.errors.*;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static String TABLE_HEADER = "id,type,name,description,status,epic/[subtasksId]\n";
    private final File file;


    public FileBackedTaskManager(File file) {
        this.file = file;
    }


    static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager load = new FileBackedTaskManager(file);
        load.loadDataFromFile(String.valueOf(file));
        return load;
    }

    public static void main(String[] args) {

        FileBackedTaskManager manager = new FileBackedTaskManager(new File("tasks.csv"));
        Task task1 = new Task("Купить корм кошке", "Магазин Лапки", Status.DONE, LocalDateTime.now(), 20);
        manager.addNewTask(task1);
        System.out.println("________________________________________");
        System.out.println("Работа с эпиком");
        Epic epic1 = new Epic("Уборка", "Убраться в квартире");
        manager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Пропылесосить", "Кухня и комната", epic1.getId(), Status.DONE, LocalDateTime.now(), 35);
        manager.addNewSubTask(subtask1);
        Subtask subtask2 = new Subtask("Мусор", "Выкинуть мусор", epic1.getId(), Status.DONE);
        manager.addNewSubTask(subtask2);
        Subtask subtask3 = new Subtask("Посуда", "Помыть посуду", epic1.getId(), Status.DONE);
        manager.addNewSubTask(subtask3);
        manager.printById(epic1.getId());
        manager.printById(subtask1.getId());
        manager.printById(task1.getId());
        manager.printById(epic1.getId());
        manager.printHistory();

        FileBackedTaskManager managers = FileBackedTaskManager.loadFromFile(new File("tasks.csv"));

        System.out.println(managers.getSubtaskByEpicId(2));
      Subtask subtask4 = new Subtask("Посуда", "Помыть посуду", epic1.getId(), Status.DONE, LocalDateTime.now().plusMinutes(10), 35);
       managers.addNewSubTask(subtask4);
       Subtask subtask5 = new Subtask("Посуда", "Помыть посуду", epic1.getId(), Status.IN_PROGRESS,LocalDateTime.now().plusMinutes(15), 37);
       managers.addNewSubTask(subtask5);
       managers.printById(6);
     managers.printById(7);
       managers.printById(2);
    }

    private String historyToString(HistoryManager manager) {
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

    private void historyFromString(String value) {
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
        System.out.println("----История----");
        for (Integer task : historyInt) {
            if (tasks.containsKey(task)) {
                System.out.println(tasks.get(task));
            } else if (epics.containsKey(task)) {
                System.out.println(epics.get(task));
            } else if (subtasks.containsKey(task)) {
                System.out.println(subtasks.get(task));
            }
        }
        System.out.println("----Конец----");
    }

    private void save() {
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

    private Task fromString(String value) {
        String[] task = value.split(",");
        Task newTask = null;
        switch (TypeTask.valueOf(task[1])) {
            case TASK:
                newTask = new Task(task[2], task[3], Status.valueOf(task[4]), LocalDateTime.parse(task[5]), Integer.parseInt(task[6]));
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
                    newTask = new Subtask(task[2], task[3], Integer.parseInt(task[5]), Status.valueOf(task[4]), LocalDateTime.parse(task[6]), Integer.parseInt(task[7]));
                }
                newTask.setId(Integer.parseInt(task[0]));
                break;
            default:
                break;
        }
        return newTask;
    }

    private void loadTask(Task task) {
        switch (task.getTypeTask()) {
            case TASK:
                tasks.put(task.getId(), task);
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
  /*  @Override
    public void setEpicStartAndEndTime() {
        if (!epics.isEmpty()) {
            for (Integer id : epics.keySet()) {
                if (!getSubtaskByEpicId(id).isEmpty()) {
                    ArrayList<Integer> subtaskWithDate = new ArrayList<>();
                    for (Integer idSubtask:epics.get(id).getSubtaskId()) {
                        if (subtasks.get(idSubtask).getStartTime()!=null){
                            subtaskWithDate.add(idSubtask);
                        }
                    }
                    if (subtaskWithDate.size() == 1) {
                        epics.get(id).setStartTime(getSubtaskById(subtaskWithDate.get(0)).getStartTime());
                        epics.get(id).setDuration(getSubtaskById(subtaskWithDate.get(0)).getDuration());
                        epics.get(id).setEndTime(getSubtaskById(subtaskWithDate.get(0)).getEndTime());
                    } else {
                        LocalDateTime timeStart=null;
                        LocalDateTime timeEnd=null;

                        for (int i = 0; i < subtaskWithDate.size(); i++) {
                            if ((i + 1) != subtaskWithDate.size()) {

                                if (getSubtaskById(subtaskWithDate.get(i)).getStartTime().isAfter(getSubtaskById(subtaskWithDate.get(i + 1)).getStartTime())) {
                                    timeStart = getSubtaskById(subtaskWithDate.get(i + 1)).getStartTime();
                                } else {
                                    timeStart = getSubtaskById(subtaskWithDate.get(i)).getStartTime();
                                }
                                if (getSubtaskById(subtaskWithDate.get(i)).getEndTime().isAfter(getSubtaskById(subtaskWithDate.get(i + 1)).getEndTime())) {
                                    timeEnd = getSubtaskById(subtaskWithDate.get(i)).getEndTime();
                                } else {
                                    timeEnd = getSubtaskById(subtaskWithDate.get(i + 1)).getEndTime();
                                }
                            }
                        }
                        epics.get(id).setStartTime(timeStart);
                        epics.get(id).setEndTime(timeEnd);
                        Duration between = Duration.between(epics.get(id).getStartTime(),epics.get(id).getEndTime());
                        int duration=(int)between.toMinutes();
                        epics.get(id).setDuration(duration);
                    }

                }
            }
        }
    }*/

}



