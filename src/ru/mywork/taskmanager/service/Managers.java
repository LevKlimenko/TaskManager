package ru.mywork.taskmanager.service;

import ru.mywork.taskmanager.model.Epic;
import ru.mywork.taskmanager.model.Status;
import ru.mywork.taskmanager.model.Subtask;
import ru.mywork.taskmanager.model.Task;

import java.io.*;
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

    public static void main(String[] args) throws ManagerSaveException, IOException {

        FileBackedTaskManager manager = new FileBackedTaskManager("tasks.csv");
        // manager.loadDataFromFile("tasks.csv");
        //System.out.println(manager.loadFromFile(Paths.get("tasks.csv")));
        Task task1 = new Task("Купить корм кошке", "Магазин Лапки",Status.DONE);
        manager.addNewTask(task1);
        System.out.println("________________________________________");
        System.out.println("Работа с эпиком");
        Epic epic1 = new Epic("Уборка", "Убраться в квартире");
        manager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Пропылесосить", "Кухня и комната", epic1.getId(), Status.DONE);
        manager.addNewSubTask(subtask1);
        Subtask subtask2 = new Subtask("Мусор", "Выкинуть мусор", epic1.getId(), Status.DONE);
        manager.addNewSubTask(subtask2);
        Subtask subtask3 = new Subtask("Посуда", "Помыть посуду", epic1.getId(), Status.NEW);
        manager.addNewSubTask(subtask3);
        manager.printById(epic1.getId());
        manager.printById(subtask1.getId());
        manager.printById(task1.getId());
        manager.printHistory();
        System.out.println("Это история просмотров-->" +manager.getHistory1());

        FileBackedTaskManager managers = FileBackedTaskManager.loadFromFile(new File("tasks.csv"));
        System.out.println("----История----");
        System.out.println(managers.getHistory());
        System.out.println("----Конец----");
       // manager.printAll();
       // managers.printAll();
        managers.printById(1);
        managers.printById(2);
               Subtask subtask4 = new Subtask("Посуда", "Помыть посуду", epic1.getId(), Status.IN_PROGRESS);
        managers.addNewSubTask(subtask4);
        Subtask subtask5 = new Subtask("Посуда", "Помыть посуду", epic1.getId(), Status.DONE);
        managers.addNewSubTask(subtask5);
        managers.printById(7);
       // FileBackedTaskManager.loadDataFromFile("tasks.csv");
      // System.out.println(managers.loadFromFile(Paths.get("tasks.csv")));






             }


}


