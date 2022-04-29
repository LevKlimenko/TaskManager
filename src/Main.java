public class Main {


    public static void main(String[] args) {
        System.out.println("Поехали!");
        Manager manager = new Manager();

        Task task1 = new Task("task1","desc1", manager.getGeneratorId());
        Task task2 = new Task("task2","desc2",manager.getGeneratorId());
        manager.addNewTask(task1);
        manager.addNewTask(task2);
        System.out.println(task1);
        manager.changeStatusTask(task1,1);
        System.out.println(task1);
        System.out.println(task2);
        System.out.println("проверка по номеру");
        System.out.println(manager.tasks.get(1));
        manager.changeStatusTask(task1,2);
        System.out.println("проверка по номеру2");
        System.out.println(manager.tasks.get(1));
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