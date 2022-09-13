package ru.mywork.taskmanager.service;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.mywork.taskmanager.KVServer.KVServer;
import ru.mywork.taskmanager.adapter.LocalDateTimeAdapter;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public class Managers {


    public static TaskManager getDefault() throws IOException {
        return new HttpTaskManager(KVServer.PORT);}

    public static KVServer getDefaultKVServer() throws IOException{
        final KVServer kvServer = new KVServer();
        kvServer.start();
        return kvServer;
    }

    public static Gson getGson(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        return gsonBuilder.create();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getFileBackedTaskManager() {
        return new FileBackedTaskManager(new File("tasks.csv"));
    }
}


