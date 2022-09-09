package ru.mywork.taskmanager.KVServer;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mywork.taskmanager.model.Epic;
import ru.mywork.taskmanager.model.Subtask;
import ru.mywork.taskmanager.model.Task;
import ru.mywork.taskmanager.service.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerHistoryTest {

    private HttpTaskServer server;
    private HttpTaskManager httpTaskManager;
    private TaskManager taskManager;
    private Task task;
    private Subtask subtask;
    private Subtask subtask2;
    private Epic epic;
    private Gson gson = Managers.getGson();
    private KVServer kvServer;
    HistoryManager historyManager;


    @BeforeEach
    public void setUp() throws Exception {
        kvServer = Managers.getDefaultKVServer();
        server = new HttpTaskServer();
        taskManager = new HttpTaskManager(8078);
        server.start();


    }

    @AfterEach
    public void tearDown() throws Exception {
        server.stop();
        kvServer.stop();
    }

    @Test
    public void testAdd() {
        Task task = new Task("task", "task descr");
        taskManager.addNewTask(task);
        final List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История не пустая");
        //taskManager.getTaskById(task.getId());
        assertEquals(1, history.size(), "История не пустая.");
    }

/*
    @Test
    public void testRemove() {
        Task task = new Task("task", "task descr");
        historyManager.add(task);
        historyManager.remove(task.getId());
        final List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size(), "История не пустая");
    }


    @Test
    public void testGetHistory() {
        final List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size(), "История не возвращается.");
    }*/
}


