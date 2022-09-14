package ru.mywork.taskmanager.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.mywork.taskmanager.KVServer.KVClient;
import ru.mywork.taskmanager.model.Epic;
import ru.mywork.taskmanager.model.Subtask;
import ru.mywork.taskmanager.model.Task;
import ru.mywork.taskmanager.model.TypeTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


public class HttpTaskManager extends FileBackedTaskManager {
    private final KVClient client;
    private final Gson gson = Managers.getGson();
    private final String key = "defaultKey";

    public HttpTaskManager(int port) {
        this(port, false);
    }

    public HttpTaskManager(int port, boolean load) {
        super(null);
        client = new KVClient(port);
        if (load) {
            load();
        }
    }

    protected void load() {
        HashMap<Integer, Task> tasks = gson.fromJson(client.load("tasks"), new TypeToken<HashMap<Integer, Task>>() {
        }.getType());
        addTasks(tasks);
        HashMap<Integer, Epic> epics = gson.fromJson(client.load("epics"), new TypeToken<HashMap<Integer, Epic>>() {
        }.getType());
        addTasks(epics);
        HashMap<Integer, Subtask> subtasks = gson.fromJson(client.load("subtasks"), new TypeToken<HashMap<Integer, Subtask>>() {
        }.getType());
        addTasks(subtasks);
        List<Integer> history = gson.fromJson(client.load("history"), new TypeToken<ArrayList<Integer>>() {
        }.getType());
        for (Integer task : history) {
            addByTypeTaskForLoad(task);
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

    @Override
    public void save() {
        String jsonTask = gson.toJson(new HashMap<>(tasks));
        client.put("tasks", jsonTask);
        String jsonEpic = gson.toJson(new HashMap<>(epics));
        client.put("epics", jsonEpic);
        String jsonSubtask = gson.toJson(new HashMap<>(subtasks));
        client.put("subtasks", jsonSubtask);
        String jsonHistory = gson.toJson(getHistory().stream().map(Task::getId).collect(Collectors.toList()));
        client.put("history", jsonHistory);
    }

    private void addTasks(HashMap<Integer, ? extends Task> tasks) {
        for (Task task : tasks.values()) {
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


    public int getGeneratorId() {
        return generatorId;
    }

    public String clientKey() {
        return client.getApiToken();
    }

    public KVClient getClient() {
        return client;
    }

    public String getKey() {
        return key;
    }
}
