package ru.mywork.taskmanager;

import ru.mywork.taskmanager.model.Epic;
import ru.mywork.taskmanager.model.Status;
import ru.mywork.taskmanager.model.Subtask;
import ru.mywork.taskmanager.model.Task;
import ru.mywork.taskmanager.service.Manager;

public class Main {


    public static void main(String[] args) {
        Manager manager = new Manager();
        System.out.println("Проверка работы :");
        Task task1 = new Task("Купить корм кошке", "Магазин Лапки");
        Task task2 = new Task("Пойти бегать", "5км");
        manager.addNewTask(task1);
        manager.addNewTask(task2);
        manager.printAll();
        Task task3 = new Task("Купить корм кошке", "Магазин Лапки", Status.DONE);
        task3.setId(task1.getId());
        manager.updateTask(task3);
        manager.printAll();

        System.out.println("________________________________________");
        System.out.println("Работа с эпиком");
        Epic epic1 = new Epic("Уборка", "Убраться в квартире");
        manager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Пропылесосить", "Кухня и комната", epic1.getId(), Status.NEW);
        manager.addNewSubTask(subtask1);
        Subtask subtask2 = new Subtask("Мусор", "Выкинуть мусор", epic1.getId(), Status.NEW);
        manager.addNewSubTask(subtask2);
        manager.printById(epic1.getId());
        Epic epic2 = new Epic("Сделать скамейку", "Длина 150см");
        manager.addNewEpic(epic2);
        Subtask subtask3 = new Subtask("Порезать доски", "Длина +-1мм", epic1.getId(), Status.NEW);
        manager.addNewSubTask(subtask3);
        manager.printById(epic1.getId());
        Subtask subtask4 = new Subtask("Пропылесосить", "Кухня и комната", epic1.getId(), Status.NEW);
        subtask4.setId(subtask1.getId());
        manager.updateSubtask(subtask4);
        Subtask subtask5 = new Subtask("Пропылесосить", "Кухня и комната", epic1.getId(), Status.DONE);
        subtask5.setId(subtask1.getId());
        manager.updateSubtask(subtask5);
        manager.deleteSubtaskById(5);
        manager.deleteSubtaskById(7);
        manager.printById(epic1.getId());
        manager.clearSubtask();
        manager.getAllTask();
        manager.printById(epic1.getId());
        System.out.println("Проверка смены эпика");
        Epic epic3 = new Epic("Уборка1", "Убраться в квартире", Status.DONE);
        epic3.setId(epic1.getId());
        epic3.setSubtaskId(epic1.getSubtaskId());
        manager.updateEpic(epic3);
        manager.printById(3);
        Subtask subtask6 = new Subtask("Пропылесосить1", "Кухня и комната", epic3.getId(), Status.DONE);
        manager.addNewSubTask(subtask6);
        manager.updateEpic(epic3);
        manager.printById(3);
        manager.clearTask();
        manager.clearEpic();
        manager.clearSubtask();
        System.out.println("\nСчетчик задач обнулен. Текущее значение: " + manager.getGeneratorId());//проверка обнуления счетчика

    }
}
