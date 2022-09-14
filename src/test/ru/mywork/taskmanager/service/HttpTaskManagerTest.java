package ru.mywork.taskmanager.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mywork.taskmanager.KVServer.KVServer;
import ru.mywork.taskmanager.model.Task;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private final KVServer server = new KVServer();
    private String key;

    public HttpTaskManagerTest() throws IOException {
    }

    @BeforeEach
    void setUp() {
        server.start();
        taskManager = new HttpTaskManager(8078);
        key = taskManager.getKey();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void shouldBeTestSaveEmptyTasksToServer() {
        taskManager.save();
        HttpTaskManager restoredManager = HttpTaskManager.loadFromServer(8078, key);
        assertEquals(taskManager, restoredManager, "Менеджеры не совпадает");
    }

    @Test
    void shouldBeTestOnlyAddTaskToServer() {
        Task task = new Task("TestTask", "TestTaskDescr");
        taskManager.addNewTask(task);
        HttpTaskManager loadedTaskManager = HttpTaskManager.loadFromServer(8078, key);
        assertEquals(taskManager, loadedTaskManager, "Менеджеры не совпадают");
    }

    @Test
    void shouldBeTestAddAndGetTaskAndHistoryServer() {
        Task task = new Task("TestTask", "TestTaskDescr");
        taskManager.addNewTask(task);
        taskManager.getTaskById(task.getId());
        HttpTaskManager loadedTaskManager = HttpTaskManager.loadFromServer(8078, key);
        assertEquals(taskManager, loadedTaskManager, "Менеджеры не совпадают");
    }
}