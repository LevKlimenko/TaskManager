package ru.mywork.taskmanager.KVServer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions.*;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTest {

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
        taskManager=Managers.getDefault();
        server = new HttpTaskServer();
        task = new Task("TaskTest","Task1descr",
                LocalDateTime.of(2022,9,6,13,0),10);
        taskManager.addNewTask(task);
        epic = new Epic("EpicTest", "EpicTestDescr");
        taskManager.addNewEpic(epic);
        subtask = new Subtask("Subtask1Test", "Subtask1TestDescr",epic.getId(),
                LocalDateTime.of(2022,9,6,12,0),1);
        taskManager.addNewSubTask(subtask);
        subtask2 = new Subtask("Subtask2Test", "Subtask2TestDescr",epic.getId(),
                LocalDateTime.of(2022,9,6,12,30),3);
        taskManager.addNewSubTask(subtask2);

        server.start();
    }

    @AfterEach
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void GetTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());
       assertEquals(200 ,response.statusCode());


    }
}