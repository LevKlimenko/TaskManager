package ru.mywork.taskmanager.KVServer;

import ru.mywork.taskmanager.errors.ManagerSaveException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String url;
    private final String apiToken;

    public KVClient(int port) {
        url = "http://localhost:" + port + "/";
        apiToken = register(url);
    }

    private String register(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "register"))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ManagerSaveException("Невозможно выполнить запрос, код статуса: " + response.statusCode());
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Невозможно выполнить запрос " + e.getMessage());
        }
    }

    public String load(String key) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "load/" + key + "?API_TOKEN=" + apiToken))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw (new ManagerSaveException("Невозможно выполнить запрос, код статуса: " + response.statusCode()));
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Невозможно выполнить запрос " + e.getMessage());
        }
    }

    public void put(String key, String value){
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(value);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "save/" + key + "?API_TOKEN=" + apiToken))
                .POST(body)
                .build();
        try {
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getUrl() {
        return url;
    }

    public String getApiToken() {
        return apiToken;
    }
}
