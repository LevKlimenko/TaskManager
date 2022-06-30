package ru.mywork.taskmanager.service;

import ru.mywork.taskmanager.model.Epic;
import ru.mywork.taskmanager.model.Subtask;
import ru.mywork.taskmanager.model.Task;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Managers {


    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getSavedHistory(){
        return new FileBackedTaskManager("tasks.csv");
    }

    public static void main(String[] args) throws IOException {
        FileBackedTaskManager managers = new FileBackedTaskManager("tasks.csv");
        FileBackedTaskManager.loadDataFromFile("tasks.csv");
       System.out.println(managers.loadFromFile(Paths.get("tasks.csv")));
       managers.printById(1);
       System.out.println(managers.getEpicById(2));
       managers.printHistory();




             }


}


