package ru.mywork.taskmanager.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mywork.taskmanager.KVServer.KVServer;
import ru.mywork.taskmanager.model.Task;
import ru.mywork.taskmanager.service.HttpTaskManager;

import static org.junit.jupiter.api.Assertions.*;


import java.io.IOException;
import java.security.Key;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private final KVServer server = new KVServer();
    private final String serverURL = "http://localhost:8078";
    private String key;
    private HttpTaskManager taskManager;
    //private KVClient client = new KVClient(8078);


    public HttpTaskManagerTest() throws IOException {
    }

    @BeforeEach
    HttpTaskManager createTaskManager(){
        server.start();
        return new HttpTaskManager(8078);
    }

    @BeforeEach
    void setUp() {
       key = taskManager.getKey();

    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void shouldBeTestSaveEmptyTaskToServer() {
        taskManager.save();
        HttpTaskManager restoredManager = HttpTaskManager.loadFromServer(8078, key);
        assertEquals(taskManager, restoredManager, "Менеджеры не совпадает");
    }

    @Test
    void shouldBeTestOnlyTaskAddToServer() {
        Task task = new Task("TestTask", "TestTaskDescr");
        taskManager.addNewTask(task);
        taskManager.getTaskById(1);
        HttpTaskManager loadedTaskManager = HttpTaskManager.loadFromServer(8078, key);
        assertEquals(taskManager.getTaskById(1), loadedTaskManager.getTaskById(1), "Задачи не совпадают");
        assertEquals(1, loadedTaskManager.getGeneratorId(), "Номер генератора не совпадает");
        assertEquals(1, loadedTaskManager.getTasks().size(), "Количество задач не совпадает");
        assertEquals(taskManager.getTasks(), loadedTaskManager.getTasks(), "ID не совпадает");
        assertEquals(taskManager.getHistory(), loadedTaskManager.getHistory(), "История не совпадает");
        assertEquals(taskManager, loadedTaskManager, "Менеджеры не совпадают");
    }
}
