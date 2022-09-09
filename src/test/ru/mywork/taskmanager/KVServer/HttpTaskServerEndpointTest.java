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
import ru.mywork.taskmanager.service.Managers;
import ru.mywork.taskmanager.service.TaskManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static ru.mywork.taskmanager.model.Status.*;
import static ru.mywork.taskmanager.model.Status.IN_PROGRESS;

public class HttpTaskServerEndpointTest {

    private HttpTaskServer server;
    private HttpTaskManager httpTaskManager;
    private TaskManager taskManager;
    private Task task;
    private Subtask subtask;
    private Subtask subtask2;
    private Epic epic;
    private Gson gson = Managers.getGson();
    private KVServer kvServer;

    @BeforeEach
    public void setUp() throws Exception {
        kvServer = Managers.getDefaultKVServer();
        server = new HttpTaskServer();
        taskManager = new HttpTaskManager(8078);
        server.start();
        Task task = new Task("TestTask","TestTaskDesc");
       taskManager.addNewTask(task);
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
      /* // String taskPost = gson.fromJson(task,Task.class);
        URI uriPut = URI.create("http://localhost:8080/tasks/task");
        HttpRequest.Builder requestBulderPut = HttpRequest.newBuilder();
        HttpRequest requestPut = requestBulderPut
                .uri(uriPut)
                .POST()
                .build();*/


        URI uriGet = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .GET()
                .uri(uriGet)
                .build();
        HttpClient client = HttpClient.newHttpClient();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request,handler);
        assertEquals(response.body(),gson.toJson(task),"задачи не совпадают");


    }
}

