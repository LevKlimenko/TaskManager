package ru.mywork.taskmanager.KVServer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.mywork.taskmanager.model.Epic;
import ru.mywork.taskmanager.model.Subtask;
import ru.mywork.taskmanager.model.Task;
import ru.mywork.taskmanager.model.TypeTask;
import ru.mywork.taskmanager.service.FileBackedTaskManager;
import ru.mywork.taskmanager.service.Managers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//import static sun.font.FontManagerNativeLibrary.load;

public class HttpTaskManager extends FileBackedTaskManager {
    private final Gson gson;
    private final KVClient client;

    public HttpTaskManager(int port) {
        this(port, false);
    }

    public HttpTaskManager(int port, boolean load) {
        super(null);
        gson = Managers.getGson();
        client = new KVClient(port);
        if (load) {
            load();
        }
    }

    protected void addTasks(List<? extends Task> tasks) {
        for (Task task : tasks) {
            final int id = task.getId();
            if (id > generatorId) {
                setGeneratorId(id);
            }
            TypeTask type = task.getType();
            if (type == TypeTask.TASK) {
                this.tasks.put(id, task);
                sortedTasks.add(task);
            } else if (type == TypeTask.SUBTASK) {
                subtasks.put(id, (Subtask) task);
                sortedTasks.add(task);
            } else if (type == TypeTask.EPIC) {
                epics.put(id, (Epic) task);
            }
        }
    }

    private void load() {
        ArrayList<Task> tasks = gson.fromJson(client.load("tasks"), new TypeToken<ArrayList<Task>>() {
        }.getType());
        addTasks(tasks);

        ArrayList<Epic> epics = gson.fromJson(client.load("epics"), new TypeToken<ArrayList<Epic>>() {
        }.getType());
        addTasks(epics);

        ArrayList<Subtask> subtasks = gson.fromJson(client.load("subtasks"), new TypeToken<ArrayList<Subtask>>() {
        }.getType());
        addTasks(subtasks);

        List<Integer> history = gson.fromJson(client.load("history"), new TypeToken<ArrayList<Integer>>() {
        }.getType());
        for (Integer taskId : history) {
            addByTypeTaskForLoad(taskId);
        }
    }

    private void addByTypeTaskForLoad(Integer taskId) {
        if (tasks.containsKey(taskId)) {
            historyManager.add(tasks.get(taskId));
        } else if (epics.containsKey(taskId)) {
            historyManager.add(epics.get(taskId));
        } else if (subtasks.containsKey(taskId)) {
            historyManager.add(subtasks.get(taskId));
        }
    }

    private void save(){
        String jsonTasks = gson.toJson(new ArrayList<>(tasks.values()));
        client.put("tasks", jsonTasks);
        String jsonSubtasks = gson.toJson(new ArrayList<>(subtasks.values()));
        client.put("subtasks",jsonSubtasks);
        String jsonEpics = gson.toJson(new ArrayList<>(epics.values()));
        client.put("epics",jsonEpics);

        String jsonHistory = gson.toJson(historyManager.getHistory().stream().map(Task::getId)
                .collect(Collectors.toList()));
        client.put("history", jsonHistory);
    }

    public int getGeneratorId(){
        return generatorId;
    }
}
