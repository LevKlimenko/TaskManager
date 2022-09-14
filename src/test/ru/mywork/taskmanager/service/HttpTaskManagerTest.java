package ru.mywork.taskmanager.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mywork.taskmanager.KVServer.KVServer;
import ru.mywork.taskmanager.model.Epic;
import ru.mywork.taskmanager.model.Subtask;
import ru.mywork.taskmanager.model.Task;

import java.io.IOException;
import java.time.LocalDateTime;

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
        HttpTaskManager restoredManager = new HttpTaskManager(8078, true);
        assertEquals(taskManager, restoredManager, "Менеджеры не совпадает");
    }

    @Test
    void shouldBeTestOnlyAddTaskToServer() {
        Task task = new Task("TestTask", "TestTaskDescr");
        taskManager.addNewTask(task);
        HttpTaskManager loadedTaskManager = new HttpTaskManager(8078, true);
        assertEquals(taskManager, loadedTaskManager, "Менеджеры не совпадают");
    }

    @Test
    void shouldBeTestAddAndGetTaskAndHistoryServer() {
        Task task = new Task("TestTask", "TestTaskDescr");
        taskManager.addNewTask(task);
        taskManager.getTaskById(task.getId());
        HttpTaskManager loadedTaskManager = new HttpTaskManager(8078, true);
        assertEquals(taskManager, loadedTaskManager, "Менеджеры не совпадают");
    }

    @Test
    void shouldBeTestTaskMap() {
        Task task = new Task("TestTask", "TestTaskDescr");
        taskManager.addNewTask(task);
        HttpTaskManager loadedTaskManager = new HttpTaskManager(8078, true);
        assertEquals(taskManager.getTasks(), loadedTaskManager.getTasks(), "Task HashMap не совпадает");
    }

    @Test
    void shouldBeTestEpicMap() {
        Epic epic = new Epic("TestEpic", "TestEpicDiscr");
        taskManager.addNewEpic(epic);
        HttpTaskManager loadedTaskManager = new HttpTaskManager(8078, true);
        assertEquals(taskManager.getEpics(), loadedTaskManager.getEpics(), "Epic HashMap не совпадает");
    }

    @Test
    void shouldBeTestEpicAndSubtaskMap() {
        Epic epic = new Epic("TestEpic", "TestEpicDiscr");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("TestSubtask", "TestSubtaskDiscr", epic.getId());
        taskManager.addNewSubTask(subtask);
        HttpTaskManager loadedTaskManager = new HttpTaskManager(8078, true);
        assertEquals(taskManager.getEpics(), loadedTaskManager.getEpics(), "Epic HashMap не совпадает");
        assertEquals(taskManager.getSubtasks(), loadedTaskManager.getSubtasks(), "Subtask HashMap не совпадает");
    }

    @Test
    void shouldBeTestAllTaskAndSortedList() {
        Task task = new Task("TestTask", "TestTaskDescr", LocalDateTime.of(2022, 9, 14, 10, 0, 0), 10);
        taskManager.addNewTask(task);
        Epic epic = new Epic("TestEpic", "TestEpicDiscr");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("TestSubtask", "TestSubtaskDiscr", epic.getId());
        taskManager.addNewSubTask(subtask);
        HttpTaskManager loadedTaskManager = new HttpTaskManager(8078, true);
        assertEquals(taskManager.getTasks(), loadedTaskManager.getTasks(), "Список всех задач не совпадает");
        assertEquals(taskManager.getSortedTasks(), loadedTaskManager.getSortedTasks(), "Сортированный список не совпадает");
    }

    @Test
    void shouldBeTestgeneratorId() {
        Task task = new Task("TestTask", "TestTaskDescr", LocalDateTime.of(2022, 9, 14, 10, 0, 0), 10);
        taskManager.addNewTask(task);
        Epic epic = new Epic("TestEpic", "TestEpicDiscr");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("TestSubtask", "TestSubtaskDiscr", epic.getId());
        taskManager.addNewSubTask(subtask);
        HttpTaskManager loadedTaskManager = new HttpTaskManager(8078, true);
        assertEquals(taskManager.getGeneratorId(), loadedTaskManager.getGeneratorId(), "Значение generatorId не совпадают");
    }
}