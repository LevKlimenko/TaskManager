import java.util.ArrayList;
import java.util.HashMap;

/**
 * Здесь будет работа со всеми заданиями.сначала надо добавить объекты из заданий.
 * Еще надо добавить
 * Создаем классы с конструкторами для task,epic,subtask, с параметрами, которые у нас указаны в задании.
 * Так же создаем еще 2 класса. Manager и Enum(для всех вычислений и для списка статусов соответственно).
 * дальше в манагер мы добавлем объекты классов Task,Subtask,Epic и делаем их хэшмапами у которых ключ - id,
 * который после создания задачи имеет id++    ->>>>>> DONE
 */

/**
 *в менеджере будут храниться эти объекты, только не делать их Мапами а вносить в уже готовые
 *  (пример для SubTask: HashMap<Integer, Subtask>  ...)
 *  SubTask дополнительно знает epicId, а Epic дополнительно знает (у меня использован ArrayList) список id сабов.
 *  у нас получается 3 Хэшмапа по таску, субтаску и эпику
 *  HashMap<Integer, Subtask> - Integer это Id задачи
 *  в задании должны переопределять методы toString equals hashcode
 */
public class Manager {
    Task task;
    Epic epic;
    Subtask subtask;
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();
    HashMap<Integer, Task> tasks = new HashMap<>();
    private int generatorId = 0;

    public int getGeneratorId() {
        return generatorId;
    }

    @Override
    public String toString() {
        return
                "-" + tasks+
                "\n"
                ;
    }

    //здесь мы добавляем значение ID к задачам,эпикам и субтаскам
    public void addNewTask(Task task) {
        int id = ++generatorId; //устанавливаем значение ID для новой задачи
        task.setTaskId(id);
        tasks.put(id,task);
        }

    public void addNewEpic(Epic epic) {
        int id = ++generatorId;
        epic.setTaskId(id);
        epics.put(id, epic);
    }

    public void addNewSubTask(Subtask subtask, int epicId) {
        int id = ++generatorId;
        subtask.setTaskId(id);
        subtasks.put(id, subtask);
        epics.get(epicId).subtaskId.add(id);

    }



    //закончили блок

    //Смена статуса задачи
    public void changeStatusTask(int id,Task task) {
        if (task.status==Status.NEW) {
            task.setStatus(Status.IN_PROGRESS);
        }
        else if (task.status==Status.IN_PROGRESS){
            task.setStatus(Status.DONE);
        }
        else {
            task.status=task.getStatus();
        }
        tasks.put(task.getTaskId(),task);
    }


    public void changeStatusSubtask(Subtask subTask) {
        if (subTask.status==Status.NEW) {
            subTask.setStatus(Status.IN_PROGRESS);
        }
        else if (subTask.status==Status.IN_PROGRESS){
            subTask.setStatus(Status.DONE);
        }
        else {
            subTask.status=subTask.getStatus();
        }
    }
   //конец смены статуса задачи.Добавить смену статуса Эпика при смене всех Субтасков.



    //Блок удаления данных
    public void clearAllTask() {
        epics.clear();
        subtasks.clear();
        tasks.clear();
        generatorId = 0;
    }


    public void deleteTask(int generatorId) {
        if (tasks.get(generatorId) == null) {
            System.out.println("Данная задача отсутствует");
        } else {
            tasks.remove(generatorId);
        }
    }

    //закончился блок.надо добавить удаление определенных элементов - тасков, субтасков и эпиков.
    /**
     * Методы для каждого из типа задач(Задача/Эпик/Подзадача):
     * -Получение списка всех задач.
     * -Удаление всех задач.
     * -Получение по идентификатору.
     * -Создание. Сам объект должен передаваться в качестве параметра.
     * -Обновление. Новая версия объекта с верным идентификатором передаются в виде параметра.
     * -Удаление по идентификатору.
     */

    /**
     * Дополнительные методы:
     * -Получение списка всех подзадач определённого эпика.
     */

    /**
     * Управление статусами осуществляется по следующему правилу:
     * Менеджер сам не выбирает статус для задачи. Информация о нём приходит менеджеру вместе с информацией
     * о самой задаче. По этим данным в одних случаях он будет сохранять статус, в других будет рассчитывать.
     * Для эпиков:
     * -если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW.
     * -если все подзадачи имеют статус DONE, то и эпик считается завершённым — со статусом DONE.
     * -во всех остальных случаях статус должен быть IN_PROGRESS.
     */
}

