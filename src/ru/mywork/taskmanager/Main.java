package ru.mywork.taskmanager;

import ru.mywork.taskmanager.model.Epic;
import ru.mywork.taskmanager.model.Status;
import ru.mywork.taskmanager.model.Subtask;
import ru.mywork.taskmanager.model.Task;
import ru.mywork.taskmanager.service.*;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Проверка работы :");
        FileBackedTaskManager manager = new FileBackedTaskManager("tasks.csv");
        Task task1 = new Task("Купить корм кошке", "Магазин Лапки");
        manager.addNewTask(task1);
        System.out.println("________________________________________");
        System.out.println("Работа с эпиком");
        Epic epic1 = new Epic("Уборка", "Убраться в квартире");
        manager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Пропылесосить", "Кухня и комната", epic1.getId(), Status.IN_PROGRESS);
        manager.addNewSubTask(subtask1);
        Subtask subtask2 = new Subtask("Мусор", "Выкинуть мусор", epic1.getId(), Status.NEW);
        manager.addNewSubTask(subtask2);
        manager.printById(epic1.getId());
        manager.printById(subtask1.getId());
        manager.printById(task1.getId());
        manager.printHistory();
        System.out.println("\nСчетчик ID задач. Текущее значение: " + manager.getGeneratorId());//проверка обнуления счетчика
    }
}
