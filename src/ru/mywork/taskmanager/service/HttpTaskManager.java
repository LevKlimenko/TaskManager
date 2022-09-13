package ru.mywork.taskmanager.service;

import com.google.gson.Gson;
import ru.mywork.taskmanager.KVServer.KVClient;
import ru.mywork.taskmanager.model.Epic;
import ru.mywork.taskmanager.model.Subtask;
import ru.mywork.taskmanager.model.Task;
import ru.mywork.taskmanager.model.TypeTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class HttpTaskManager extends FileBackedTaskManager {
    // private final Gson gson;
    private final KVClient client;
    private String key;

    public HttpTaskManager(int port) {
        this(port, "defaultKey");
    }

    public HttpTaskManager(int port, String key) {
        super(null);
        this.key = key;
        //  gson = Managers.getGson();
        client = new KVClient(port);
        // if (load) {
        //   load();
    }

    public static HttpTaskManager loadFromServer(int port, String key) {
        HttpTaskManager htm = new HttpTaskManager(port, key);
        String content = htm.client.load(key);
        String[] tasksAndHistory = content.split("\n\n");
        String[] tasks = tasksAndHistory[0].split("\n");
        for (int i = 1; i < tasks.length; i++) {
            Task task = htm.fromString(tasks[i]);
            htm.loadTask(task);
        }
        if (tasksAndHistory.length > 1) {
            String history = tasksAndHistory[1];
            htm.historyFromString(history);
        }
        htm.save();
        return htm;
    }

    public void save() {
        StringBuilder sb = new StringBuilder();
        sb.append(TABLE_HEADER);
        for (Task task : getTasks().values()) {
            sb.append(task.toStringInFile()).append("\n");
        }
        for (Epic epic : getEpics().values()) {
            sb.append(epic.toStringInFile()).append("\n");
        }
        for (Subtask subtask : getSubtasks().values()) {
            sb.append(subtask.toStringInFile()).append("\n");
        }
        sb.append("\n");
        if (getHistory().size() != 0) {
            sb.append(historyToString(historyManager));
        }
        try {
            client.put(key, sb.toString());
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
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
