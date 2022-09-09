package ru.mywork.taskmanager.KVServer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.After;
//import org.junit.jupiter.api.After;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions.*;
import ru.mywork.taskmanager.errors.CollisionTaskException;
import ru.mywork.taskmanager.model.Epic;
import ru.mywork.taskmanager.model.Subtask;
import ru.mywork.taskmanager.model.Task;
import ru.mywork.taskmanager.service.FileBackedTaskManager;
import ru.mywork.taskmanager.service.Managers;
import ru.mywork.taskmanager.service.TaskManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.mywork.taskmanager.model.Status.*;
import static ru.mywork.taskmanager.model.Status.IN_PROGRESS;

public class HttpTaskServerEndpointTest {

    private HttpTaskServer server;
    private TaskManager fileManager;
    private HttpTaskManager httpTaskManager;
    private Task task;
    private Subtask subtask;
    private Subtask subtask2;
    private Epic epic;
    private Gson gson = Managers.getGson();
    private KVServer kvServer;
    private TaskManager taskManager;


    @BeforeEach
    public void setUp() throws Exception {
        kvServer = Managers.getDefaultKVServer();
        taskManager = new FileBackedTaskManager(new File("test.csv"));
        server=new HttpTaskServer(taskManager);
        HttpClient client = HttpClient.newHttpClient();
        server.start();
       /* kvServer = Managers.getDefaultKVServer();
        server = new HttpTaskServer();
        this.taskManager = new HttpTaskManager(8078);
        server.start();
        fileManager = new FileBackedTaskManager(new File("tasks.csv"));
        Task task = new Task("TestTask","TestTaskDesc");
       taskManager.addNewTask(task);*/
       // Epic epic = new Epic("TestEpic", "TestEpicDesc");
       // taskManager.addNewEpic(epic);
        //Subtask subtask = new Subtask("TestSubtask", "TestSubtaskDesc",epic.getId());
        //taskManager.addNewSubTask(subtask);
    }

    @AfterEach
    public void tearDown() throws Exception {
        server.stop();
        kvServer.stop();
    }

   @Test
    public void testGetEndpoint() throws IOException, InterruptedException {
        Task task = new Task("TestTask","TestTaskDesc");
        taskManager.addNewTask(task);
        //fileManager.addNewTask(task);
        URI uriGet = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .GET()
                .uri(uriGet)
                .build();
        HttpClient client = HttpClient.newHttpClient();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request,handler);
        assertEquals(200, response.statusCode());
        Type type = new TypeToken<HashMap<Integer,Task>>(){

        }.getType();
      HashMap<Integer,Task>actual = gson.fromJson(response.body(),type);
     assertNotNull(actual, "Задачи не возвращаются");
     assertEquals(1, actual.size(), "Неверное количество задач");
     assertEquals(task, actual.get(1), "Задачи не совпадают");


    }

}

