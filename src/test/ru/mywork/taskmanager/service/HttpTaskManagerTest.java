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

class HttpTaskManagerTest{
    private final KVServer server = new KVServer();
    private final String serverURL = "http://localhost:8078";
    private String key;
    private HttpTaskManager taskManager;
    //private KVClient client = new KVClient(8078);



    public HttpTaskManagerTest() throws IOException {
    }

    @BeforeEach
    void setUp(){
        server.start();
        taskManager = new HttpTaskManager(8078);
        key=taskManager.getKey();

    }

    @AfterEach
    void tearDown(){
        server.stop();
    }

    @Test
    void shouldBeTestSaveEmptyTaskToServer(){
        taskManager.save();
        HttpTaskManager restoredManager = HttpTaskManager.loadFromServer(8078, key);
        assertEquals(taskManager,restoredManager,"Менеджеры не совпадает");
        }

  /*  @Test
    void shouldBeTestOnlyTaskAddToServer(){
        Task task = new Task("TestTask","TestTaskDescr");
        taskManager.addNewTask(task);
        HttpTaskManager loadedTaskManager = taskManager.load();
        assertEquals(taskManager.getTasks().get(task.getId()),loadedTaskManager.getTasks().get(1),"Задачи не совпадают");
        //assertEquals(1,taskManager.getGeneratorId(),"Номер генератора не совпадает");
       // assertEquals(1,taskManager.getTasks().size(),"Количество задач не совпадает");
        //assertEquals(1,task.getId(),"ID не совпадает");
    }*/
}
