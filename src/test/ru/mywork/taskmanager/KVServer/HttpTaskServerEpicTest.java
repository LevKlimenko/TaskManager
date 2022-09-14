package ru.mywork.taskmanager.KVServer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mywork.taskmanager.model.Epic;
import ru.mywork.taskmanager.model.Subtask;
import ru.mywork.taskmanager.service.HttpTaskManager;
import ru.mywork.taskmanager.service.Managers;
import ru.mywork.taskmanager.service.TaskManager;

import java.net.http.HttpClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.mywork.taskmanager.model.Status.*;

public class HttpTaskServerEpicTest {

    private HttpTaskServer server;
    private TaskManager taskManager;
    private Epic epic;
    private KVServer kvServer;
    private HttpClient client;

    @BeforeEach
    public void setUp() throws Exception {
        kvServer = Managers.getDefaultKVServer();
        taskManager = new HttpTaskManager(8078);
        server = new HttpTaskServer(taskManager);
        client = HttpClient.newHttpClient();
        server.start();
        epic = new Epic("epic", "epic descr");
        taskManager.addNewEpic(epic);
    }

    @AfterEach
    public void tearDown() {
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
