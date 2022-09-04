package ru.mywork.taskmanager.KVServer;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
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
        //server.createContext("/tasks/", new TaskHandler());
        // server.createContext("/tasks/task", new TaskHandler());
        // server.createContext("/tasks/subtask", new TaskHandler());
        // server.createContext("/tasks/epic", new TaskHandler());
        server.createContext("/tasks", this::handler);
    }


    public void handler(HttpExchange httpExchange) {
        try {
            System.out.println("\n/tasks: " + httpExchange.getRequestURI());
            final String path = httpExchange.getRequestURI().getPath().substring(7);
            switch (path) {
                case "" -> {
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
                    System.out.println("Добавлена новая задача id=" + id );
                    final String response = gson.toJson(task);
                    sendText(httpExchange,response);
                }
            }

        }
    }

    private void sendText(HttpExchange httpExchange, String response) throws IOException {
        OutputStream os = httpExchange.getResponseBody();
        httpExchange.getResponseHeaders().add("Content-Type","application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(200, 0);
        os.write(response.getBytes(DEFAULT_CHARSET));
        os.close();
    }

    private String readText(HttpExchange httpExchange) throws IOException {
        InputStream is = httpExchange.getRequestBody();
        httpExchange.sendResponseHeaders(201, 0);
        return new String(is.readAllBytes(), DEFAULT_CHARSET);
    }


   /* static class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            OutputStream os = exchange.getResponseBody();
            InputStream is = exchange.getRequestBody();
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] splitPathId = path.split("id=");
            int id = Integer.parseInt(splitPathId[1]);
            switch (method) {
                case "GET":
                    if (path.endsWith("/tasks/")) {
                        if (fbtm.getSortedTasks().isEmpty()) {
                            exchange.sendResponseHeaders(404, 0);
                        } else {
                            exchange.sendResponseHeaders(200, 0);
                            String allTasksGson = gson.toJson(fbtm.getSortedTasks());
                            os.write(allTasksGson.getBytes(DEFAULT_CHARSET));
                            os.close();
                        }
                    } else if (path.endsWith("/tasks/task")) {
                        if (fbtm.getTasks().isEmpty()) {
                            exchange.sendResponseHeaders(404, 0);
                        } else {
                            exchange.sendResponseHeaders(200, 0);
                            String tasksGson = gson.toJson(fbtm.getTasks());
                            os.write(tasksGson.getBytes(DEFAULT_CHARSET));
                            os.close();
                        }
                    } else if (path.endsWith("/tasks/task/?id=")) {
                        //int id = Integer.parseInt(splitPathId[1]);
                        if (fbtm.getTaskById(id) != null) {
                            exchange.sendResponseHeaders(200, 0);
                            String taskByIdGson = gson.toJson(fbtm.getTaskById(id));
                            os.write(taskByIdGson.getBytes());
                            os.close();
                        } else {
                            exchange.sendResponseHeaders(404, 0);
                        }

                    } else if (path.endsWith("/tasks/subtask")) {
                        if (fbtm.getSubtasks().isEmpty()) {
                            exchange.sendResponseHeaders(404, 0);
                        } else {
                            exchange.sendResponseHeaders(200, 0);
                            String subtaskGson = gson.toJson(fbtm.getSubtasks());
                            os.write(subtaskGson.getBytes(DEFAULT_CHARSET));
                            os.close();
                        }
                    } else if (path.endsWith("/tasks/subtask/?id=")) {
                        //int id = Integer.parseInt(splitPathId[1]);
                        if (fbtm.getSubtaskById(id) != null) {
                            exchange.sendResponseHeaders(200, 0);
                            String subtaskByIdGson = gson.toJson(fbtm.getSubtaskById(id));
                            os.write(subtaskByIdGson.getBytes());
                            os.close();
                        } else {
                            exchange.sendResponseHeaders(404, 0);
                        }

                    } else if (path.endsWith("/tasks/epic")) {
                        if (fbtm.getEpics().isEmpty()) {
                            exchange.sendResponseHeaders(404, 0);
                        } else {
                            exchange.sendResponseHeaders(200, 0);
                            String epicGson = gson.toJson(fbtm.getEpics());
                            os.write(epicGson.getBytes(DEFAULT_CHARSET));
                            os.close();
                        }
                    } else if (path.endsWith("/tasks/epic/?id=")) {
                        if (fbtm.getEpicById(id) != null) {
                            exchange.sendResponseHeaders(200, 0);
                            // int id = Integer.parseInt(splitPathId[1]);
                            String epicByIdGson = gson.toJson(fbtm.getEpicById(id));
                            os.write(epicByIdGson.getBytes());
                            os.close();
                        } else {
                            exchange.sendResponseHeaders(404, 0);
                        }
                    } else if (path.endsWith("/tasks/subtask/epic/?id=")) {
                        if (fbtm.getSubtaskById(id) != null) {
                            exchange.sendResponseHeaders(200, 0);
                            //int id = Integer.parseInt(splitPathId[1]);
                            String subtaskByEpicIdGson = gson.toJson(fbtm.getSubtaskByEpicId(id));
                            os.write(subtaskByEpicIdGson.getBytes());
                            os.close();
                        } else {
                            exchange.sendResponseHeaders(404, 0);
                        }
                    } else if (path.endsWith("/tasks/history")) {
                        if (fbtm.getHistory().isEmpty()) {
                            exchange.sendResponseHeaders(404, 0);
                        } else {
                            exchange.sendResponseHeaders(200, 0);
                            String historyGson = gson.toJson(fbtm.getHistory());
                            os.write(historyGson.getBytes(DEFAULT_CHARSET));
                            os.close();
                        }
                    }
                    break;
                case "POST":
                        if (path.endsWith("/tasks/task/")){
                            exchange.sendResponseHeaders(201,0);
                            String body = new String(is.readAllBytes(),DEFAULT_CHARSET);
                            Task task = gson.fromJson(body,Task.class);
                            fbtm.addNewTask(task);
                        }
                        else if (path.endsWith("/tasks/subtask/epic/")){
                            exchange.sendResponseHeaders(201,0);
                            String body = new String(is.readAllBytes(),DEFAULT_CHARSET);
                            Subtask subtask = gson.fromJson(body,Subtask.class);
                            fbtm.addNewSubTask(subtask);
                        }
                        else if (path.endsWith("/tasks/epic/")){
                            exchange.sendResponseHeaders(201,0);
                            String body = new String(is.readAllBytes(),DEFAULT_CHARSET);
                            Epic epic = gson.fromJson(body, Epic.class);
                            fbtm.addNewEpic(epic);
                        }
                        break;
                case "DELETE":
                    if(path.endsWith("/tasks/task/")){
                        exchange.sendResponseHeaders(200,0);
                        fbtm.clearTask();
                    }
                    else if(path.endsWith("/tasks/subtask/")){
                        exchange.sendResponseHeaders(200,0);
                        fbtm.clearSubtask();
                    }
                    else if(path.endsWith("/tasks/epic/")){
                        exchange.sendResponseHeaders(200,0);
                        fbtm.clearEpic();
                    }

            }

        }

    }*/
}

