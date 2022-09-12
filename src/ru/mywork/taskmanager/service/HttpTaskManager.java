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
import java.util.stream.Collectors;


public class HttpTaskManager extends FileBackedTaskManager {
    private final Gson gson;
    private final KVClient client;
    private String key;

    public HttpTaskManager(int port) {
        this(port, "defaultKey");
    }

    public HttpTaskManager(int port, String key) {
        super(null);
        this.key = key;
        gson = Managers.getGson();
        client = new KVClient(port);
       // if (load) {
         //   load();
        }
   // }

    public static HttpTaskManager loadFromServer(int port, String key){
    HttpTaskManager htm = new HttpTaskManager(port,key);
    String content = htm.client.load(key);
    String[] tasksAndHistory = content.split("\n\n");
    String[] tasks = tasksAndHistory[0].split("\n");
    for (int i=1;i<tasks.length;i++){
       Task task = htm.fromString(tasks[i]);
        htm.loadTask(task);
    }
    if (tasksAndHistory.length>1){
        String history = tasksAndHistory[1];
        htm.historyFromString(history);
    }
    htm.save();
    return htm;
     }

     public void save() {
        StringBuilder sb = new StringBuilder();
        sb.append(TABLE_HEADER);
        for (Task task : getTasks().values()){
            sb.append(task.toStringInFile()).append("\n");
        }
        for (Epic epic:getEpics().values()){
            sb.append(epic.toStringInFile()).append("\n");
        }
        for (Subtask subtask : getSubtasks().values()){
            sb.append(subtask.toStringInFile()).append("\n");
        }
        sb.append("\n");
        if (getHistory().size()!=0){
            sb.append(historyToString(historyManager));
        }
         client.put(key,sb.toString());
     }



  /*  protected Task addTasks(String[] task) {
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
    }*/

  /*  public HttpTaskManager load() {
        HttpTaskManager loadedTaskManager = new HttpTaskManager(8078);
        HashMap<Integer,Task> tasks = getTasks(); //gson.fromJson(client.load("tasks"), new TypeToken<HashMap<Integer,Task>>() {
        //}.getType());
        addTasks(tasks);

        HashMap<Integer,Epic> epics = getEpics();//gson.fromJson(client.load("epics"), new TypeToken< HashMap<Integer,Epic>>() {
        //}.getType());
        addTasks(epics);

        HashMap<Integer,Subtask> subtasks = getSubtasks();// = gson.fromJson(client.load("subtasks"), new TypeToken< HashMap<Integer,Subtask>>() {
      //  }.getType());
        addTasks(subtasks);

        List<Task> history =getHistory(); //gson.fromJson(client.load("history"), new TypeToken<ArrayList<Integer>>() {
       // }.getType());
        for (Task taskId : history) {
            addByTypeTaskForLoad(taskId.getId());
        }
        return loadedTaskManager;
    }*/


    private void addByTypeTaskForLoad(Integer taskId) {
        if (tasks.containsKey(taskId)) {
            historyManager.add(tasks.get(taskId));
        } else if (epics.containsKey(taskId)) {
            historyManager.add(epics.get(taskId));
        } else if (subtasks.containsKey(taskId)) {
            historyManager.add(subtasks.get(taskId));
        }
    }

  /*  @Override
    public void save() {
        String jsonTasks = gson.toJson(new ArrayList<>(tasks.values()));
        client.put("tasks", jsonTasks);
        String jsonSubtasks = gson.toJson(new ArrayList<>(subtasks.values()));
        client.put("subtasks", jsonSubtasks);
        String jsonEpics = gson.toJson(new ArrayList<>(epics.values()));
        client.put("epics", jsonEpics);

        String jsonHistory = gson.toJson(historyManager.getHistory().stream().map(Task::getId)
                .collect(Collectors.toList()));
        client.put("history", jsonHistory);
    }*/

    public int getGeneratorId() {
        return generatorId;
    }

    public String clientKey(){
        return client.getApiToken();
    }

    public KVClient getClient() {
        return client;
    }

    public String getKey() {
        return key;
    }
}

