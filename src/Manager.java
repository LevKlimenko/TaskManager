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
 * в менеджере будут храниться эти объекты, только не делать их Мапами а вносить в уже готовые
 * (пример для SubTask: HashMap<Integer, Subtask>  ...)
 * SubTask дополнительно знает epicId, а Epic дополнительно знает (у меня использован ArrayList) список id сабов.
 * у нас получается 3 Хэшмапа по таску, субтаску и эпику
 * HashMap<Integer, Subtask> - Integer это Id задачи
 * в задании должны переопределять методы toString equals hashcode
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
                "-" + tasks +
                        "\n"
                ;
    }

    //здесь мы добавляем значение ID к задачам,эпикам и субтаскам
    public void addNewTask(Task task) {
        int id = ++generatorId; //устанавливаем значение ID для новой задачи
        task.setTaskId(id);
        tasks.put(id, task);
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
        checkEpic(epics.get(subtask.epicId));


    }
    //закончили блок

    //Смена статуса задачи
    public void changeStatusTask(Task task) {
        if (task.status == Status.NEW) {
            task.setStatus(Status.IN_PROGRESS);
        } else if (task.status == Status.IN_PROGRESS) {
            task.setStatus(Status.DONE);
        } else {
            task.status = task.getStatus();
        }
        tasks.put(task.getTaskId(), task);
    }

    public void changeStatusSubTask(Subtask subtask) {
        if (subtask.status == Status.NEW) {
            subtask.setStatus(Status.IN_PROGRESS);
        } else if (subtask.status == Status.IN_PROGRESS) {
            subtask.setStatus(Status.DONE);
        } else {
            subtask.status = subtask.getStatus();
        }
        subtasks.put(subtask.getTaskId(), subtask);

            checkEpic(epics.get(subtask.epicId));


     }




    //конец смены статуса задачи.Добавить смену статуса Эпика при смене всех Субтасков.


    //печать Эпика ---> работает
    public void printEpic(Epic epic) {
        System.out.println("_________________");
        System.out.println(epic);
        for (int i = 0; i < epic.subtaskId.size(); i++) {
            if (subtasks.get(epic.subtaskId.get(i)) != null) {
                System.out.println(subtasks.get(epic.subtaskId.get(i)));
            }
        }
        System.out.println("_________________");

    }


    public void printAll() {
        for (int i = 0; i <= generatorId; i++) {
            if (tasks.containsKey(i)) {
                System.out.println(tasks.get(i));
            }
            if (epics.containsKey(i)) {
                printEpic(epics.get(i));
            }

        }
    }


    //Блок удаления данных
    public void clearAllTask() {
        epics.clear();
        subtasks.clear();
        tasks.clear();
        generatorId = 0;
    }
    /**
     * Управление статусами осуществляется по следующему правилу:
     * Менеджер сам не выбирает статус для задачи. Информация о нём приходит менеджеру вместе с информацией
     * о самой задаче. По этим данным в одних случаях он будет сохранять статус, в других будет рассчитывать.
     * Для эпиков:
     * -если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW.
     * -если все подзадачи имеют статус DONE, то и эпик считается завершённым — со статусом DONE.
     * -во всех остальных случаях статус должен быть IN_PROGRESS.
     */
    //проверка на статусы
    public void checkEpic(Epic epic) {
        int check = 0;

        for (int i = 0; i < epic.subtaskId.size(); i++) {

            if (((subtasks.get(epic.subtaskId.get(i)).status==Status.IN_PROGRESS))){
                epic.setStatus(Status.IN_PROGRESS);
            }
            if ((subtasks.get(epic.subtaskId.get(i)).status==Status.DONE)) {
                check++;
            }
        }
        if (check != 0 && epic.status != Status.NEW) {
            epic.setStatus(Status.IN_PROGRESS);
        }
        if (check == epic.subtaskId.size()) {
            epic.setStatus(Status.DONE);

        }
        }










    public void deleteSubTask(Subtask subtask) {
        int numberEpic = subtask.epicId;
        if (subtasks.get(subtask.taskId) != null) {
           epics.get(subtask.getEpicId()).getSubtaskId().remove(subtask.taskId);
        }
       /* if (epics.get(numberEpic) != null
                && epics.get(numberEpic).status != Status.DONE
                && !epics.get(numberEpic).subtaskId.isEmpty()) {
            epics.get(numberEpic).setStatus(Status.NEW);
            if (epics.get(numberEpic)==null){
                epics.get(numberEpic).setStatus(Status.NEW);
            }


        }
*/
    }


    public void deleteTask(Task task) {
        if (tasks.get(task.taskId) != null) {
            tasks.remove(task.taskId);
        }
    }

    //закончился блок.надо добавить удаление определенных элементов - тасков, субтасков и эпиков.
    /**
     * Методы для каждого из типа задач(Задача/Эпик/Подзадача):
     * -Получение списка всех задач. --->Done
     * -Удаление всех задач.  ---> Done
     * -Получение по идентификатору. --> Done
     * -Создание. Сам объект должен передаваться в качестве параметра. -->Done
     * -Обновление. Новая версия объекта с верным идентификатором передаются в виде параметра.
     * -Удаление по идентификатору.
     */

    /**
     * Дополнительные методы:
     * -Получение списка всех подзадач определённого эпика.   --->Done
     */


}

