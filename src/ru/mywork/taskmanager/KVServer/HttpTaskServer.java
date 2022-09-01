package ru.mywork.taskmanager.KVServer;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpTaskServer {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static String hostName;
    private static int port;
    private static FileBackedTaskManager fbtm = Managers.getFileBackedTaskManager();
    private static Gson gson = new Gson();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/tasks/", new TaskHandler());
        server.createContext("/tasks/task", new TaskHandler());
        server.createContext("/tasks/subtask", new TaskHandler());
        server.createContext("/tasks/epic", new TaskHandler());
    }

    static class TaskHandler implements HttpHandler {
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

    }
}

