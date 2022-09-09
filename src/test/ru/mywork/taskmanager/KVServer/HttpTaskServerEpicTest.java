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

public class HttpTaskServerEpicTest {

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
        epic = new Epic("epic", "epic decr");
        taskManager.addNewEpic(epic);
    }

    @AfterEach
    public void tearDown() throws Exception {
        server.stop();
        kvServer.stop();
    }

    @Test
    public void epicWithOutSubTasks() {
        assertEquals(NEW, taskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void epicWithSubTasksStatusNew() {
        Subtask sub = new Subtask("111", "111", epic.getId(), NEW);
        Subtask sub2 = new Subtask("222", "222", epic.getId(), NEW);
        taskManager.addNewSubTask(sub);
        taskManager.addNewSubTask(sub2);
        assertEquals(NEW, taskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void epicWithSubTasksStatusDone() {
        Subtask sub = new Subtask("111", "111", epic.getId(), DONE);
        Subtask sub2 = new Subtask("222", "222", epic.getId(), DONE);
        taskManager.addNewSubTask(sub);
        taskManager.addNewSubTask(sub2);
        assertEquals(DONE, taskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void epicWithSubTasksStatusNewAndDone() {
        Subtask sub = new Subtask("111", "111", epic.getId(), NEW);
        Subtask sub2 = new Subtask("222", "222", epic.getId(), DONE);
        taskManager.addNewSubTask(sub);
        taskManager.addNewSubTask(sub2);
        assertEquals(IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void epicWithSubTasksStatusInProgress() {
        Subtask sub = new Subtask("111", "111", epic.getId(), IN_PROGRESS);
        Subtask sub2 = new Subtask("222", "222", epic.getId(), IN_PROGRESS);
        taskManager.addNewSubTask(sub);
        taskManager.addNewSubTask(sub2);
        assertEquals(IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());
    }


}
