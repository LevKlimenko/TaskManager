import java.util.ArrayList;

public class Main {


    public static void main(String[] args) {
        Manager manager = new Manager();
        System.out.println("Проверка работы Task:");
        Task task1 = new Task("Купить корм кошке", "Магазин Лапки");
        Task task2 = new Task("Пойти бегать", "5км");
        manager.addNewTask(task1);
        manager.addNewTask(task2);
        manager.printAll();
        manager.changeStatusTask(1);
        manager.printAll();
        manager.changeStatusTask(1);
        manager.changeStatusTask(2);
        manager.printAll();

        System.out.println("________________________________________");
        System.out.println("Работа с эпиком");
        Epic epic1 = new Epic("Уборка", "Убраться в квартире", new ArrayList<>());
        manager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Пропылесосить", "Кухня и комната", epic1.taskId);
        manager.addNewSubTask(subtask1, epic1.taskId);
        Subtask subtask2 = new Subtask("Мусор", "Выкинуть мусор", epic1.taskId);
        manager.addNewSubTask(subtask2, epic1.taskId);

        Epic epic2 = new Epic("Сделать скамейку", "Длина 150см", new ArrayList<>());
        manager.addNewEpic(epic2);
        Subtask subtask3 = new Subtask("Порезать доски", "Длина +-1мм", epic2.taskId);
        manager.addNewSubTask(subtask3, epic2.taskId);
        manager.printById(epic1.getTaskId());
        manager.printById(epic2.getTaskId());
        manager.changeStatusSubTask(subtask1.getTaskId());
        manager.printById(epic1.getTaskId());
        manager.changeStatusSubTask(subtask2.getTaskId());
        manager.changeStatusSubTask(subtask1.getTaskId());
        manager.printById(epic1.getTaskId());
        manager.changeStatusSubTask(subtask1.getTaskId());
        manager.changeStatusSubTask(subtask2.getTaskId());
        manager.printById(epic1.getTaskId());
        Subtask subtask4 = new Subtask("Помыть пол", "Коридор", epic1.taskId);
        manager.addNewSubTask(subtask4, epic1.taskId);
        manager.printById(epic1.getTaskId());
        manager.changeStatusSubTask(subtask4.getTaskId());
        manager.printById(epic1.getTaskId());
        manager.deleteById(subtask4.getTaskId());
        manager.deleteById(task2.getTaskId());
        System.out.println("____________СПИСОК ЗАДАЧ____________________");
        manager.printAll();
        System.out.println("_____________________________________________");
        System.out.println("Список сабтасков по эпику: " + epic1.getName());
        manager.printSubtaskByEpicId(epic1.getTaskId());
        manager.clearAllTask();
        manager.printAll();
        manager.printById(3);
        System.out.println("\nСчетчик задач обнулен. Текущее значение: " + manager.getGeneratorId());//проверка обнуления счетчика

    }
}
/**
 * Создайте в классе Main метод static void main(String[] args) и внутри него:
 * -Создайте 2 задачи, один эпик с 2 подзадачами, а другой эпик с 1 подзадачей.
 * -Распечатайте списки эпиков, задач и подзадач, через System.out.println(..)
 * -Измените статусы созданных объектов, распечатайте. Проверьте, что статус задачи и подзадачи сохранился,
 * а статус эпика рассчитался по статусам подзадач.
 * -И, наконец, попробуйте удалить одну из задач и один из эпиков.
 * Воспользуйтесь дебаггером, поставляемым вместе со средой разработки, что бы понять логику работы программы и отладить.
 */