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
        manager.updateStatusTask(task1);
        manager.printAll();
        manager.updateStatusTask(task1);
        manager.updateStatusTask(task2);
        manager.printAll();

        System.out.println("________________________________________");
        System.out.println("Работа с эпиком");
        Epic epic1 = new Epic("Уборка", "Убраться в квартире");
        manager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Пропылесосить", "Кухня и комната", epic1.getTaskId(), Status.NEW);
        manager.addNewSubTask(subtask1);
        Subtask subtask2 = new Subtask("Мусор", "Выкинуть мусор", epic1.getTaskId(), Status.NEW);
        manager.addNewSubTask(subtask2);

        Epic epic2 = new Epic("Сделать скамейку", "Длина 150см");
        manager.addNewEpic(epic2);
        Subtask subtask3 = new Subtask("Порезать доски", "Длина +-1мм", epic2.getTaskId(),Status.NEW);
        manager.addNewSubTask(subtask3);
        manager.printById(epic1.getTaskId());
        manager.printById(epic2.getTaskId());
        manager.updateStatusSubTask(subtask1);
        manager.printById(epic1.getTaskId());
        manager.updateStatusSubTask(subtask2);
        manager.updateStatusSubTask(subtask1);
        manager.printById(epic1.getTaskId());
        manager.updateStatusSubTask(subtask1);
        manager.updateStatusSubTask(subtask2);
        manager.printById(epic1.getTaskId());
        Subtask subtask4 = new Subtask("Помыть пол", "Коридор", epic1.getTaskId(),Status.NEW);
        manager.addNewSubTask(subtask4);
        manager.printById(epic1.getTaskId());
        manager.updateStatusSubTask(subtask4);
        manager.printById(epic1.getTaskId());
        System.out.println("____________СПИСОК ЗАДАЧ____________________");
        manager.printAll();
        System.out.println("_____________________________________________");
        System.out.println("Список сабтасков по эпику: " + epic1.getName());
        System.out.println(manager.getSubtaskByEpicId(epic1.getTaskId()));
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());
        System.out.println(manager.getTasks());
        System.out.println("_____________________________________________");
        manager.updateStatusTask(task2);
        manager.getAllTask();
        manager.printById(3);
        manager.clearTask();
        manager.clearEpic();
        manager.clearSubtask();
        System.out.println("\nСчетчик задач обнулен. Текущее значение: " + manager.getGeneratorId());//проверка обнуления счетчика

    }
}
