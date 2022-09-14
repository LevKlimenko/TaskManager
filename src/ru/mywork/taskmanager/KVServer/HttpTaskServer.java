package ru.mywork.taskmanager.KVServer;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.mywork.taskmanager.model.Epic;
import ru.mywork.taskmanager.model.Subtask;
import ru.mywork.taskmanager.model.Task;
import ru.mywork.taskmanager.service.Managers;
import ru.mywork.taskmanager.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

public class HttpTaskServer {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final String HOST_NAME = "localhost";
    public static int PORT = 8080;
    private static Gson gson;
    private final HttpServer httpServer;
    private final TaskManager taskManager;

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = Managers.getGson();
        httpServer = HttpServer.create(new InetSocketAddress(HOST_NAME, PORT), 0);
        httpServer.createContext("/tasks", this::handler);
    }

    public static void main(String[] args) throws IOException {
        Managers.getDefaultKVServer();
        final HttpTaskServer server = new HttpTaskServer(Managers.getDefault());
        server.start();
    }

    private void handler(HttpExchange httpExchange) {
        try {
            System.out.println("\n/tasks: " + httpExchange.getRequestURI());
            final String path = httpExchange.getRequestURI().getPath().substring(7);
            switch (path) {
                case "": {
                    if (!httpExchange.getRequestMethod().equals("GET")) {
                        System.out.println("/ Ждёт GET-запрос, а получил: " + httpExchange.getRequestMethod());
                        httpExchange.sendResponseHeaders(405, 0);
                        return;
                    }
                    System.out.println("Получены все задачи");
                    final String response = gson.toJson(taskManager.getSortedTasks());
                    sendText(httpExchange, response);
                }
                break;
                case "task/":
                    handleTask(httpExchange);
                    break;
                case "subtask/":
                    handleSubtask(httpExchange);
                    break;
                case "subtask/epic/":
                    handleSubtasksByEpic(httpExchange);
                    break;
                case "epic/":
                    handleEpic(httpExchange);
                    break;
                case "history": {
                    if (httpExchange.getRequestMethod().equals("GET")) {
                        System.out.println("Получена история");
                        final String response = gson.toJson(taskManager.getHistory());
                        sendText(httpExchange, response);
                    } else {
                        System.out.println("/history Ждёт GET-запрос, а получил: " + httpExchange.getRequestMethod());
                        httpExchange.sendResponseHeaders(405, 0);
                    }
                }
                break;
                default: {
                    System.out.println("Неизвестный запрос: " + httpExchange.getRequestURI());
                    httpExchange.sendResponseHeaders(404, 0);
                }
            }
            httpExchange.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleTask(HttpExchange httpExchange) throws Exception {
        final String query = httpExchange.getRequestURI().getQuery();
        switch (httpExchange.getRequestMethod()) {
            case "GET": {
                if (query == null) {
                    final HashMap<Integer, Task> tasks = taskManager.getTasks();
                    final String response = gson.toJson(tasks);
                    System.out.println("Получили все Таски");
                    sendText(httpExchange, response);
                    return;
                } else {
                    String idParam = query.substring(3); //?id=
                    final int id = Integer.parseInt(idParam);
                    final Task task = taskManager.getTaskById(id);
                    if (task != null) {
                        final String response = gson.toJson(task);
                        System.out.println("Получили Таску id=" + id);
                        sendText(httpExchange, response);
                    } else {
                        System.out.println("Нет Таски с id=" + id);
                        httpExchange.sendResponseHeaders(404, 0);
                    }
                }
            }
            break;
            case "DELETE": {
                if (query == null) {
                    taskManager.clearTask();
                    System.out.println("Удалили все Таски");
                    httpExchange.sendResponseHeaders(200, 0);
                    return;
                }
                String idParam = query.substring(3);
                final int id = Integer.parseInt(idParam);
                if (taskManager.getTaskById(id) != null) {
                    taskManager.deleteTaskById(id);
                    System.out.println("Удалили Таску id=" + id);
                    httpExchange.sendResponseHeaders(200, 0);
                } else {
                    System.out.println("Нет Таска с id=" + id);
                    httpExchange.sendResponseHeaders(404, 0);
                }
            }
            break;
            case "POST": {
                String json = readText(httpExchange);
                if (json.isEmpty()) {
                    System.out.println("Body с задачей пустой. Указывается в теле запроса");
                    httpExchange.sendResponseHeaders(400, 0);
                    return;
                }
                final Task task = gson.fromJson(json, Task.class);
                final Integer id = task.getId();
                if (taskManager.getTasks().containsKey(id)) {
                    taskManager.updateTask(task);
                    System.out.println("Обновили задачу id=" + id);
                    httpExchange.sendResponseHeaders(200, 0);
                } else {
                    taskManager.addNewTask(task);
                    System.out.println("Добавлена новая задача id=" + task.getId());
                    httpExchange.sendResponseHeaders(201, 0);
                }
            }
            break;
            default:
                System.out.println("Нет действий для метода " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(405, 0);
        }
    }

    private void handleEpic(HttpExchange httpExchange) throws IOException {
        final String query = httpExchange.getRequestURI().getQuery();
        switch (httpExchange.getRequestMethod()) {
            case "GET": {
                if (query == null) {
                    final HashMap<Integer, Epic> epics = taskManager.getEpics();
                    final String response = gson.toJson(epics);
                    System.out.println("Получили все Эпики");
                    sendText(httpExchange, response);
                    return;
                } else {
                    String idParam = query.substring(3); //?id=
                    final int id = Integer.parseInt(idParam);
                    final Epic epic = taskManager.getEpicById(id);
                    if (epic != null) {
                        final String response = gson.toJson(epic);
                        System.out.println("Получили Эпик id=" + id);
                        sendText(httpExchange, response);
                    } else {
                        System.out.println("Нет Эпика с id=" + id);
                        httpExchange.sendResponseHeaders(404, 0);
                    }
                }
            }
            break;
            case "DELETE": {
                if (query == null) {
                    taskManager.clearEpic();
                    System.out.println("Удалили все Эпики");
                    httpExchange.sendResponseHeaders(200, 0);
                    return;
                }
                String idParam = query.substring(3);
                final int id = Integer.parseInt(idParam);
                if (taskManager.getEpicById(id) != null) {
                    taskManager.deleteEpicById(id);
                    System.out.println("Удалили Эпик id=" + id);
                    httpExchange.sendResponseHeaders(200, 0);
                } else {
                    System.out.println("Нет Эпика с id=" + id);
                    httpExchange.sendResponseHeaders(404, 0);
                }
            }
            break;
            case "POST": {
                String json = readText(httpExchange);
                if (json.isEmpty()) {
                    System.out.println("Body с задачей пустой. Указывается в теле запроса");
                    httpExchange.sendResponseHeaders(400, 0);
                    return;
                }
                final Epic epic = gson.fromJson(json, Epic.class);
                final Integer id = epic.getId();
                if (taskManager.getEpics().containsKey(id)) {
                    taskManager.updateEpic(epic);
                    System.out.println("Обновили Эпик id=" + id);
                    httpExchange.sendResponseHeaders(200, 0);
                } else {
                    taskManager.addNewEpic(epic);
                    System.out.println("Добавлен новый Эпик id=" + epic.getId());
                    httpExchange.sendResponseHeaders(201, 0);
                }
            }
            break;
            default:
                System.out.println("Нет действий для метода " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(405, 0);
        }
    }

    private void handleSubtask(HttpExchange httpExchange) throws IOException {
        final String query = httpExchange.getRequestURI().getQuery();
        switch (httpExchange.getRequestMethod()) {
            case "GET": {
                if (query == null) {
                    final HashMap<Integer, Subtask> subtasks = taskManager.getSubtasks();
                    final String response = gson.toJson(subtasks);
                    System.out.println("Получили все Сабтаски");
                    sendText(httpExchange, response);
                    return;
                } else {
                    String idParam = query.substring(3); //?id=
                    final int id = Integer.parseInt(idParam);
                    final Subtask subtask = taskManager.getSubtaskById(id);
                    if (subtask != null) {
                        final String response = gson.toJson(subtask);
                        System.out.println("Получили Сабтаск id=" + id);
                        sendText(httpExchange, response);
                    } else {
                        System.out.println("Нет Сабтаска с id=" + id);
                        httpExchange.sendResponseHeaders(404, 0);
                    }
                }
            }
            break;
            case "DELETE": {
                if (query == null) {
                    taskManager.clearSubtask();
                    System.out.println("Удалили все Сабтаски");
                    httpExchange.sendResponseHeaders(200, 0);
                    return;
                }
                String idParam = query.substring(3);
                final int id = Integer.parseInt(idParam);
                if (taskManager.getSubtaskById(id) != null) {
                    taskManager.deleteSubtaskById(id);
                    System.out.println("Удалили Сабтаск id=" + id);
                    httpExchange.sendResponseHeaders(200, 0);
                } else {
                    System.out.println("Нет Сабтаска с id=" + id);
                    httpExchange.sendResponseHeaders(404, 0);
                }
            }
            break;
            case "POST": {
                String json = readText(httpExchange);
                if (json.isEmpty()) {
                    System.out.println("Body с задачей пустой. Указывается в теле запроса");
                    httpExchange.sendResponseHeaders(400, 0);
                    return;
                }
                final Subtask subtask = gson.fromJson(json, Subtask.class);
                final Integer id = subtask.getId();
                if (taskManager.getSubtasks().containsKey(id)) {
                    taskManager.updateSubtask(subtask);
                    System.out.println("Обновили Сабтаск id=" + id);
                    httpExchange.sendResponseHeaders(200, 0);
                } else {
                    taskManager.addNewSubTask(subtask);
                    System.out.println("Добавлен новый Сабтаск id=" + subtask.getId());
                    httpExchange.sendResponseHeaders(201, 0);
                }
            }
            break;
            default:
                System.out.println("Нет действий для метода " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(405, 0);
        }
    }

    private void handleSubtasksByEpic(HttpExchange httpExchange) throws IOException {
        final String query = httpExchange.getRequestURI().getQuery();
        if (httpExchange.getRequestMethod().equals("GET")) {
            String idParam = query.substring(3); //?id=
            final int id = Integer.parseInt(idParam);
            if (taskManager.getEpicById(id) != null) {
                final List<Subtask> subtaskByEpicId = taskManager.getSubtaskByEpicId(id);
                final String response = gson.toJson(subtaskByEpicId);
                System.out.println("Получили все Сабтаски у EpicId=" + id);
                sendText(httpExchange, response);
            } else {
                System.out.println("Отсутствует Эпик с id=" + id);
                httpExchange.sendResponseHeaders(404, 0);
            }
        } else {
            System.out.println("/subtask/epic/ ждет GET-запрос, а получил " + httpExchange.getRequestMethod());
            httpExchange.sendResponseHeaders(405, 0);
        }
    }

    private void sendText(HttpExchange httpExchange, String response) throws IOException {
        OutputStream os = httpExchange.getResponseBody();
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(200, 0);
        os.write(response.getBytes(DEFAULT_CHARSET));
        os.close();
    }

    private String readText(HttpExchange httpExchange) throws IOException {
        InputStream is = httpExchange.getRequestBody();
        return new String(is.readAllBytes(), DEFAULT_CHARSET);
    }

    public void start() {
        System.out.println("Запускаем HttpTaskServer на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Сервер остановлен на порту " + PORT);
    }
}

