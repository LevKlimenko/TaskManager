package ru.mywork.taskmanager.KVServer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mywork.taskmanager.errors.CollisionTaskException;
import ru.mywork.taskmanager.model.Epic;
import ru.mywork.taskmanager.model.Status;
import ru.mywork.taskmanager.model.Subtask;
import ru.mywork.taskmanager.model.Task;
import ru.mywork.taskmanager.service.FileBackedTaskManager;
import ru.mywork.taskmanager.service.HttpTaskManager;
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

public class HttpTaskServerEndpointTest {

    private HttpTaskServer server;
    private TaskManager fileManager;
    private HttpTaskManager httpTaskManager;
    private HttpClient client;
    private Task task;
    private Subtask subtask;
    private Subtask subtask2;
    private Epic epic;
    private Gson gson = Managers.getGson();
    private KVServer kvServer;
    private TaskManager taskManager;


    @BeforeEach
    void setUp() throws Exception {
        kvServer = Managers.getDefaultKVServer();
        taskManager = new FileBackedTaskManager(new File("test.csv"));
        server = new HttpTaskServer(taskManager);
        client = HttpClient.newHttpClient();
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
    void tearDown() {
        server.stop();
        kvServer.stop();
    }

    @Test
    public void shouldBeTestGetTask() throws IOException, InterruptedException {
        Task task = new Task("TestTask", "TestTaskDesc", Status.DONE,
                LocalDateTime.of(2022, 9, 10, 10, 0, 0), 10);
        taskManager.addNewTask(task);
        URI uriGet = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .GET()
                .uri(uriGet)
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        Type type = new TypeToken<HashMap<Integer, Task>>() {
        }.getType();
        HashMap<Integer, Task> actual = gson.fromJson(response.body(), type);
        assertNotNull(actual, "Задачи не возвращаются");
        assertEquals(1, actual.size(), "Неверное количество задач");
        assertEquals(task, actual.get(1), "Задачи не совпадают");
        assertEquals(task.getStatus(), actual.get(1).getStatus(), "Статус не совпадает");
        assertEquals(task.getStartTime(), actual.get(1).getStartTime(), "Время старта не совпадает");
    }

    @Test
    public void shouldBeTestGetTaskByID() throws IOException, InterruptedException {
        Task task = new Task("TestTask", "TestTaskDesc", Status.DONE,
                LocalDateTime.of(2022, 9, 10, 10, 0, 0), 10);
        taskManager.addNewTask(task);
        int id = task.getId();
        URI uriGet = URI.create("http://localhost:8080/tasks/task/?id=" + id);
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .GET()
                .uri(uriGet)
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        Type type = new TypeToken<Task>() {
        }.getType();
        Task actual = gson.fromJson(response.body(), type);
        assertNotNull(actual, "Задача не возвращаются");
        assertEquals(task, actual, "Задачи не совпадают");
    }

    @Test
    public void shouldBeTestPostTask() throws IOException, InterruptedException {
        Task task = new Task("TestTask", "TestTaskDesc", Status.DONE,
                LocalDateTime.of(2022, 9, 10, 10, 0, 0), 10);
        String json = gson.toJson(task);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        URI uri = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder.uri(uri).POST(body).build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(201, response.statusCode());
        HashMap<Integer, Task> tasks = taskManager.getTasks();
        assertEquals(1, tasks.size(), "Неверное число задач");
        Task taskCrossTime = new Task("TestTaskCross", "TestTaskCrossDisc",
                LocalDateTime.of(2022, 9, 10, 10, 5, 0), 10);
        assertThrows(CollisionTaskException.class, () -> taskManager.addNewTask(taskCrossTime), "Новая задача не входит внутрь существующей");
    }

    @Test
    public void shouldBeUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("TestTask", "TestTaskDesc", Status.DONE,
                LocalDateTime.of(2022, 9, 10, 10, 0, 0), 10);
        taskManager.addNewTask(task);
        Task taskUpdate = new Task("TestTaskUpdate", "TestTaskUpdateDisc", Status.NEW);
        taskUpdate.setId(task.getId());
        String json = gson.toJson(taskUpdate);
        URI uri = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = requestBuilder.uri(uri).POST(body).build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(201, response.statusCode());
        assertEquals("TestTaskUpdate", taskManager.getTaskById(1).getName(), "Неверное название задачи");
        assertEquals(Status.NEW, taskManager.getTaskById(1).getStatus(), "Неверный статус задачи");
    }

    @Test
    public void shouldBeTestDeleteAllTask() throws IOException, InterruptedException {
        Task task1 = new Task("TestTask1", "TestTask1Desc");
        taskManager.addNewTask(task1);
        Task task2 = new Task("TestTask2", "TestTask2Desc");
        taskManager.addNewTask(task2);
        URI uri = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder.DELETE().uri(uri).build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        Type type = new TypeToken<HashMap<Integer, Task>>() {
        }.getType();
        HashMap<Integer, Task> actual = gson.fromJson(response.body(), type);
        assertNotNull(actual, "Задачи не возвращаются");
        assertEquals(0, actual.size(), "Количество задач неверное");
    }

    @Test
    public void shouldBeTestDeleteTaskByID() throws IOException, InterruptedException {
        Task task = new Task("TestTask", "TestTaskDesc");
        taskManager.addNewTask(task);
        URI uri = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder.DELETE().uri(uri).build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, handler);
        assertEquals(404, response.statusCode());
    }


    @Test
    public void shouldBeTestGetEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("TestEpic", "TestEpicDesc");
        taskManager.addNewEpic(epic);
        URI uriGet = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .GET()
                .uri(uriGet)
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        Type type = new TypeToken<HashMap<Integer, Epic>>() {
        }.getType();
        HashMap<Integer, Epic> actual = gson.fromJson(response.body(), type);
        assertNotNull(actual, "Эпики не возвращаются");
        assertEquals(1, actual.size(), "Неверное количество эпиков");
        assertEquals(epic, actual.get(1), "Эпики не совпадают");
        assertEquals(epic.getStatus(), actual.get(1).getStatus(), "Статус не совпадает");
    }

    @Test
    public void shouldBeTestGetEpicByID() throws IOException, InterruptedException {
        Epic epic = new Epic("TestEpic", "TestEpicDesc");
        taskManager.addNewEpic(epic);
        int id = epic.getId();
        URI uriGet = URI.create("http://localhost:8080/tasks/epic/?id=" + id);
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .GET()
                .uri(uriGet)
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        Type type = new TypeToken<Epic>() {
        }.getType();
        Epic actual = gson.fromJson(response.body(), type);
        assertNotNull(actual, "Эпик не возвращаются");
        assertEquals(epic, actual, "Эпики не совпадают");
    }

    @Test
    public void shouldBeTestPostEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("TestEpic", "TestEpicDesc");
        String json = gson.toJson(epic);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        URI uri = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder.uri(uri).POST(body).build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(201, response.statusCode());
        HashMap<Integer, Epic> epics = taskManager.getEpics();
        assertEquals(1, epics.size(), "Неверное число Эпиков");
    }

    @Test
    public void shouldBeUpdateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("TestEpic", "TestEpicDesc");
        taskManager.addNewEpic(epic);
        Epic epicUpdate = new Epic("TestEpicUpdate", "TestEpicUpdateDisc");
        epicUpdate.setId(epic.getId());
        String json = gson.toJson(epicUpdate);
        URI uri = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = requestBuilder.uri(uri).POST(body).build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(201, response.statusCode());
        assertEquals("TestEpicUpdate", taskManager.getEpicById(1).getName(), "Неверное название Эпика");
    }

    @Test
    public void shouldBeTestDeleteAllEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("TestEpic1", "TestEpic1Desc");
        taskManager.addNewEpic(epic1);
        Epic epic2 = new Epic("TestEpic2", "TestEpic2Desc");
        taskManager.addNewEpic(epic2);
        URI uri = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder.DELETE().uri(uri).build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        Type type = new TypeToken<HashMap<Integer, Epic>>() {
        }.getType();
        HashMap<Integer, Epic> actual = gson.fromJson(response.body(), type);
        assertNotNull(actual, "Эпики не возвращаются");
        assertEquals(0, actual.size(), "Количество Эпиков неверное");
    }


    @Test
    public void shouldBeTestDeleteEpicByID() throws IOException, InterruptedException {
        Epic epic = new Epic("TestEpic", "TestEpicDisc");
        taskManager.addNewEpic(epic);
        URI uri = URI.create("http://localhost:8080/tasks/epic/?id=1");
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder.DELETE().uri(uri).build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, handler);
        assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldBeTestGetSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("TestEpic", "TestEpicDesc");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("TestSubtask", "TestSubtaskDisc", epic.getId(), Status.DONE);
        taskManager.addNewSubTask(subtask);
        int id = subtask.getId();
        URI uriGet = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .GET()
                .uri(uriGet)
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        Type type = new TypeToken<HashMap<Integer, Subtask>>() {
        }.getType();
        HashMap<Integer, Subtask> actual = gson.fromJson(response.body(), type);
        assertNotNull(actual, "Субтаски не возвращаются");
        assertEquals(1, actual.size(), "Неверное количество Субтасков");
        assertEquals(subtask, actual.get(id), "Субтаски не совпадают");
        assertEquals(subtask.getStatus(), actual.get(id).getStatus(), "Статус субтасков не совпадает");
        assertEquals(actual.get(id).getStatus(), epic.getStatus(), "Статус эпиков не поменялся");
    }

    @Test
    public void shouldBeTestGetSubtaskByID() throws IOException, InterruptedException {
        Epic epic = new Epic("TestEpic", "TestEpicDesc");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("TestSubtask", "TestSubtaskDisc", epic.getId(), Status.DONE);
        taskManager.addNewSubTask(subtask);
        int id = subtask.getId();
        URI uriGet = URI.create("http://localhost:8080/tasks/subtask/?id=" + id);
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .GET()
                .uri(uriGet)
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        Type type = new TypeToken<Subtask>() {
        }.getType();
        Subtask actual = gson.fromJson(response.body(), type);
        assertNotNull(actual, "Субтаски не возвращаются");
        assertEquals(subtask, actual, "Субтаски не совпадают");
    }


    @Test
    public void shouldBeTestPostSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("TestEpic", "TestEpicDesc");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("TestSubtask", "TestSubtaskDesc", epic.getId(),
                LocalDateTime.of(2022, 9, 10, 10, 0, 0), 10);
        String json = gson.toJson(subtask);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        URI uri = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder.uri(uri).POST(body).build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(201, response.statusCode());
        HashMap<Integer, Subtask> subtasks = taskManager.getSubtasks();
        assertEquals(1, subtasks.size(), "Неверное число Сабтасков");
        Subtask subtaskCrossTime = new Subtask("TestSubtaskCross", "TestSubtaskCrossDisc", epic.getId(),
                LocalDateTime.of(2022, 9, 10, 10, 5, 0), 10);
        assertThrows(CollisionTaskException.class, () -> taskManager.addNewSubTask(subtaskCrossTime),
                "Новый Субтаск не входит внутрь существующей");
    }

    @Test
    public void shouldBeUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("TestEpic", "TestEpicDesc");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("TestSubtask", "TestSubtaskDesc", epic.getId());
        taskManager.addNewSubTask(subtask);
        int id = subtask.getId();
        Subtask subtaskUpdate = new Subtask("TestSubtaskUpdate", "TestSubtaskUpdateDesc", epic.getId());
        subtaskUpdate.setId(id);
        String json = gson.toJson(subtaskUpdate);
        URI uri = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = requestBuilder.uri(uri).POST(body).build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(201, response.statusCode());
        assertEquals("TestSubtaskUpdate", taskManager.getSubtaskById(id).getName(), "Неверное название Субтаска");
    }

    @Test
    public void shouldBeTestDeleteAllSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("TestEpic1", "TestEpic1Desc");
        taskManager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("TestSubtask1", "TestSubtask1Desc", epic.getId());
        taskManager.addNewSubTask(subtask1);
        Subtask subtask2 = new Subtask("TestSubtask2", "TestSubtask2Desc", epic.getId());
        taskManager.addNewSubTask(subtask2);
        URI uri = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder.DELETE().uri(uri).build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        Type type = new TypeToken<HashMap<Integer, Subtask>>() {
        }.getType();
        HashMap<Integer, Subtask> actual = gson.fromJson(response.body(), type);
        assertNotNull(actual, "Субтаски не возвращаются");
        assertEquals(0, actual.size(), "Количество Субтасков неверное");
    }


    @Test
    public void shouldBeTestDeleteSubtaskByID() throws IOException, InterruptedException {
        Epic epic = new Epic("TestEpic", "TestEpicDisc");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("TestSubtask", "TestSubtaskDesc", epic.getId());
        taskManager.addNewSubTask(subtask);
        int id = subtask.getId();
        URI uri = URI.create("http://localhost:8080/tasks/subtask/?id=" + id);
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder.DELETE().uri(uri).build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, handler);
        assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldBeTestDeleteSubtasksWithEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("TestEpic1", "TestEpic1Desc");
        taskManager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("TestSubtask1", "TestSubtask1Desc", epic.getId());
        taskManager.addNewSubTask(subtask1);
        Subtask subtask2 = new Subtask("TestSubtask2", "TestSubtask2Desc", epic.getId());
        taskManager.addNewSubTask(subtask2);
        URI uriDel = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder.DELETE().uri(uriDel).build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        URI uriSub = URI.create("http://localhost:8080/tasks/subtask/");
        request = HttpRequest.newBuilder().uri(uriSub).GET().build();
        response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        Type type = new TypeToken<HashMap<Integer, Subtask>>() {
        }.getType();
        HashMap<Integer, Subtask> actual = gson.fromJson(response.body(), type);
        assertNotNull(actual, "Субтаски не возвращаются");
        assertEquals(0, actual.size(), "Количество Субтасков неверное");
    }

    @Test
    public void shouldBeTestGetSubtaskByEpicID() throws IOException, InterruptedException {
        Epic epic = new Epic("TestEpic", "TestEpicDesc");
        taskManager.addNewEpic(epic);
        int epicId = epic.getId();
        Subtask subtask1 = new Subtask("TestSubtask1", "TestSubtask1Disc", epic.getId(), Status.DONE);
        taskManager.addNewSubTask(subtask1);
        Subtask subtask2 = new Subtask("TestSubtask2", "TestSubtask2Disc", epic.getId(), Status.DONE);
        taskManager.addNewSubTask(subtask2);
        URI uriGet = URI.create("http://localhost:8080/tasks/subtask/epic/?id=" + epicId);
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .GET()
                .uri(uriGet)
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        Type type = new TypeToken<ArrayList<Subtask>>() {
        }.getType();
        List<Subtask> actual = gson.fromJson(response.body(), type);
        assertNotNull(actual, "Субтаски не возвращаются");
        assertEquals(subtask1, actual.get(0), "Субтаски не совпадают");
        assertEquals(actual.size(), 2, "Количество Субтасков не совпадает");
    }

    @Test
    public void shouldBeTestGetHistory() throws IOException, InterruptedException {
        Task task = new Task("TestTask", "TestTaskDisc");
        taskManager.addNewTask(task);
        Epic epic = new Epic("TestEpic", "TestEpicDisc");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("TestSubtask", "TestSubtaskDisc", epic.getId());
        taskManager.addNewSubTask(subtask);
        taskManager.getSubtaskById(subtask.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getTaskById(task.getId());
        URI uri = URI.create("http://localhost:8080/tasks/history");
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder.GET().uri(uri).build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        Type type = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> actual = gson.fromJson(response.body(), type);
        assertNotNull(actual, "История не возвращается");
        assertEquals(3, actual.size());
    }

    @Test
    public void shouldBeTestGetAllTask() throws IOException, InterruptedException {
        Task task = new Task("TestTask", "TestTaskDisc",
                LocalDateTime.of(2022, 9, 10, 10, 1, 1), 10);
        taskManager.addNewTask(task);
        Epic epic = new Epic("TestEpic", "TestEpicDisc");
        taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("TestSubtask", "TestSubtaskDisc", epic.getId(),
                LocalDateTime.of(2022, 9, 10, 9, 1, 1), 10);
        taskManager.addNewSubTask(subtask);
        taskManager.getEpicById(epic.getId());
        taskManager.getTaskById(task.getId());
        taskManager.getSubtaskById(subtask.getId());
        URI uri = URI.create("http://localhost:8080/tasks/");
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder.GET().uri(uri).build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200, response.statusCode());
        Type type = new TypeToken<List<Subtask>>() {
        }.getType();
        List<Subtask> actual = gson.fromJson(response.body(), type);
        assertNotNull(actual, "Список всех задач не возвращается");
        assertEquals(2, actual.size());
        assertEquals(subtask, actual.get(0), "Запись по времени неверная");
    }


}

