package ru.mywork.taskmanager.KVServer;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.mywork.taskmanager.model.Epic;
import ru.mywork.taskmanager.model.Subtask;
import ru.mywork.taskmanager.model.Task;
import ru.mywork.taskmanager.service.FileBackedTaskManager;
import ru.mywork.taskmanager.service.Managers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

public class HttpTaskServer {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static String hostName;
    private static int port;
    private static FileBackedTaskManager fbtm = Managers.getFileBackedTaskManager();
    private static Gson gson = Managers.getGson();


    public void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/tasks", this::handler);
    }


    public void handler(HttpExchange httpExchange) {
        try {
            System.out.println("\n/tasks: " + httpExchange.getRequestURI());
            final String path = httpExchange.getRequestURI().getPath().substring(7);
            switch (path) {
                case "": {
                    if (!httpExchange.getRequestMethod().equals("GET")) {
                        System.out.println("/ Ждёт GET-запрос, а получил: " + httpExchange.getRequestMethod());
                        httpExchange.sendResponseHeaders(405, 0);
                    }
                    final String response = gson.toJson(fbtm.getSortedTasks());
                    sendText(httpExchange, response);
                }
                case "task":
                    handleTask(httpExchange);
                case "subtask":
                    handleSubtask(httpExchange);
                case "subtask/epic":
                    handleSubtasksByEpic(httpExchange);
                case "epic":
                    handleEpic(httpExchange);
                case "history": {
                    if (!httpExchange.getRequestMethod().equals("GET")) {
                        System.out.println("/history Ждёт GET-запрос, а получил: " + httpExchange.getRequestMethod());
                        httpExchange.sendResponseHeaders(405, 0);
                    }
                    final String response = gson.toJson(fbtm.getHistory());
                    sendText(httpExchange, response);
                }
                default: {
                    System.out.println("Неизвестный запрос: " + httpExchange.getRequestURI());
                    httpExchange.sendResponseHeaders(404, 0);
                }

            }
            httpExchange.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleTask(HttpExchange httpExchange) throws IOException {
        final String query = httpExchange.getRequestURI().getQuery();
        switch (httpExchange.getRequestMethod()) {
            case "GET": {
                if (query == null) {
                    final HashMap<Integer, Task> tasks = fbtm.getTasks();
                    final String response = gson.toJson(tasks);
                    System.out.println("Получили все задачи");
                    sendText(httpExchange, response);
                    return;
                }
                String idParam = query.substring(3); //?id=
                final int id = Integer.parseInt(idParam);
                final Task task = fbtm.getTaskById(id);
                final String response = gson.toJson(task);
                System.out.println("Получили задачу id=" + id);
                sendText(httpExchange, response);
            }
            break;
            case "DELETE": {
                if (query == null) {
                    fbtm.clearTask();
                    System.out.println("Удалили все задачи");
                    httpExchange.sendResponseHeaders(200, 0);
                    return;
                }
                String idParam = query.substring(3);
                final int id = Integer.parseInt(idParam);
                fbtm.deleteTaskById(id);
                System.out.println("Удалили задачу id=" + id);
                httpExchange.sendResponseHeaders(200, 0);
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
                if (id != null) {
                    fbtm.updateTask(task);
                    System.out.println("Обновили задачу id=" + id);
                    httpExchange.sendResponseHeaders(200, 0);
                } else {
                    fbtm.addNewTask(task);
                    System.out.println("Добавлена новая задача id=" + id);
                    final String response = gson.toJson(task);
                    sendText(httpExchange, response);
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
                    final HashMap<Integer, Epic> epics = fbtm.getEpics();
                    final String response = gson.toJson(epics);
                    System.out.println("Получили все Эпики");
                    sendText(httpExchange, response);
                    return;
                }
                String idParam = query.substring(3); //?id=
                final int id = Integer.parseInt(idParam);
                final Epic epic = fbtm.getEpicById(id);
                final String response = gson.toJson(epic);
                System.out.println("Получили Эпик id=" + id);
                sendText(httpExchange, response);
            }
            break;
            case "DELETE": {
                if (query == null) {
                    fbtm.clearEpic();
                    System.out.println("Удалили все Эпики");
                    httpExchange.sendResponseHeaders(200, 0);
                    return;
                }
                String idParam = query.substring(3);
                final int id = Integer.parseInt(idParam);
                fbtm.deleteEpicById(id);
                System.out.println("Удалили Эпик id=" + id);
                httpExchange.sendResponseHeaders(200, 0);
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
                if (id != null) {
                    fbtm.updateEpic(epic);
                    System.out.println("Обновили Эпик id=" + id);
                    httpExchange.sendResponseHeaders(200, 0);
                } else {
                    fbtm.addNewEpic(epic);
                    System.out.println("Добавлен новый Эпик id=" + id);
                    final String response = gson.toJson(epic);
                    sendText(httpExchange, response);
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
                    final HashMap<Integer, Subtask> subtasks = fbtm.getSubtasks();
                    final String response = gson.toJson(subtasks);
                    System.out.println("Получили все Сабтаски");
                    sendText(httpExchange, response);
                    return;
                }
                String idParam = query.substring(3); //?id=
                final int id = Integer.parseInt(idParam);
                final Subtask subtask = fbtm.getSubtaskById(id);
                final String response = gson.toJson(subtask);
                System.out.println("Получили Сабтаск id=" + id);
                sendText(httpExchange, response);
            }
            break;
            case "DELETE": {
                if (query == null) {
                    fbtm.clearSubtask();
                    System.out.println("Удалили все Сабтаски");
                    httpExchange.sendResponseHeaders(200, 0);
                    return;
                }
                String idParam = query.substring(3);
                final int id = Integer.parseInt(idParam);
                fbtm.deleteSubtaskById(id);
                System.out.println("Удалили Сабтаск id=" + id);
                httpExchange.sendResponseHeaders(200, 0);
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
                if (id != null) {
                    fbtm.updateSubtask(subtask);
                    System.out.println("Обновили Сабтаск id=" + id);
                    httpExchange.sendResponseHeaders(200, 0);
                } else {
                    fbtm.addNewSubTask(subtask);
                    System.out.println("Добавлен новый Сабтаск id=" + id);
                    final String response = gson.toJson(subtask);
                    sendText(httpExchange, response);
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
            final List<Subtask> subtaskByEpicId = fbtm.getSubtaskByEpicId(id);
            final String response = gson.toJson(subtaskByEpicId);
            System.out.println("Получили все Сабтаски у EpicId=" + id);
            sendText(httpExchange, response);
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
        httpExchange.sendResponseHeaders(201, 0);
        return new String(is.readAllBytes(), DEFAULT_CHARSET);
    }

}

