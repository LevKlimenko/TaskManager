import java.util.ArrayList;

public class Main {


    public static void main(String[] args) {
        System.out.println("Поехали!");
        Manager manager = new Manager();

       /* Task task1 = new Task("task1","desc1");
        Task task2 = new Task("task2","desc2");
        manager.addNewTask(task1);
        manager.addNewTask(task2);
        System.out.println(task1);
        manager.changeStatusTask(task1);
        System.out.println(task1);
        System.out.println(task2);
        System.out.println("проверка по номеру");
        System.out.println(manager.tasks.get(1));
        manager.changeStatusTask(task1);
        System.out.println(manager.tasks.get(1));*/
                System.out.println("________________________________________");
        System.out.println("Работа с эпиком");
        Epic epic1= new Epic("Epic1", "DescEpic1",new ArrayList<>());
        manager.addNewEpic(epic1);
        Subtask subtask1=new Subtask("subtask1","subtaskDescr1", epic1.taskId);
        manager.addNewSubTask(subtask1, epic1.taskId);
        Subtask subtask2=new Subtask("subtask2","subtaskDescr2", epic1.taskId);
        manager.addNewSubTask(subtask2, epic1.taskId);

        Epic epic2= new Epic("Epic2", "DescEpic1",new ArrayList<>());
        manager.addNewEpic(epic2);
        Subtask subtask3=new Subtask("subtask3","subtaskDescr3", epic2.taskId);
        manager.addNewSubTask(subtask3, epic2.taskId);
       //Subtask subtask4=new Subtask("subtask4","subtaskDescr4", epic1.taskId);
        //manager.addNewSubTask(subtask4, epic1.taskId);
        manager.printEpic(epic1);
        manager.changeStatusSubTask(subtask1);
        manager.printEpic(epic1);
        manager.changeStatusSubTask(subtask2);
        manager.changeStatusSubTask(subtask1);
        manager.printEpic(epic1);
        manager.changeStatusSubTask(subtask2);
        manager.printEpic(epic1);
        Subtask subtask4=new Subtask("subtask4","subtaskDescr4", epic1.taskId);
        manager.addNewSubTask(subtask4, epic1.taskId);
        manager.printEpic(epic1);
        manager.deleteSubTask(subtask1);
        manager.deleteSubTask(subtask2);
        manager.deleteSubTask(subtask4);
        manager.printEpic(epic1);
        System.out.println(epic1.subtaskId);
        Subtask subtask5=new Subtask("subtask5","subtaskDescr5", epic1.taskId);
        manager.addNewSubTask(subtask5, epic1.taskId);

        manager.printEpic(epic1);
        manager.changeStatusSubTask(subtask5);
        manager.printEpic(epic1);
        manager.deleteSubTask(subtask5);
        manager.printEpic(epic1);
        /*System.out.println(manager.tasks.get(1));
        System.out.println(epic1);
        System.out.println(subtask1);
        System.out.println(subtask2);
        System.out.println(subtask4);
        System.out.println(epic1.subtaskId);
        System.out.println(epic2);
        System.out.println(subtask3);
        System.out.println(epic2.subtaskId);
        manager.changeStatusSubTask(subtask1);
        System.out.println(epic1);
        manager.printEpic(epic1);
        manager.deleteSubTask(subtask3);
        manager.deleteTask(task2);
        manager.printAll();*/
           }
}
/**
 *Создайте в классе Main метод static void main(String[] args) и внутри него:
 *-Создайте 2 задачи, один эпик с 2 подзадачами, а другой эпик с 1 подзадачей.
 *-Распечатайте списки эпиков, задач и подзадач, через System.out.println(..)
 *-Измените статусы созданных объектов, распечатайте. Проверьте, что статус задачи и подзадачи сохранился,
 * а статус эпика рассчитался по статусам подзадач.
 *-И, наконец, попробуйте удалить одну из задач и один из эпиков.
 *Воспользуйтесь дебаггером, поставляемым вместе со средой разработки, что бы понять логику работы программы и отладить.
 */