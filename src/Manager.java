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
        epics.get(epicId).getSubtaskId().add(id);
        checkEpic(epics.get(subtask.getEpicId()));


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
        checkEpic(epics.get(subtask.getEpicId()));
    }

    //печать Эпика ---> работает
    public void printEpic(Epic epic) {
        System.out.println("");
        System.out.println(epic);
        for (int i = 0; i < epic.getSubtaskId().size(); i++) {
            if (subtasks.get(epic.getSubtaskId().get(i)) != null) {
                System.out.println(subtasks.get(epic.getSubtaskId().get(i)));
            }
        }
        System.out.println("");
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

    public void printById(int id){
        if (tasks.containsKey(id)) {
            System.out.println(tasks.get(id));
        }
        if (epics.containsKey(id)) {
            printEpic(epics.get(id));
        }
        if (subtasks.containsKey(id)) {
            System.out.println(subtasks.get(id));
        }
    }


    //Блок удаления данных
    public void clearAllTask() {
        subtasks.clear();
        epics.clear();
        tasks.clear();
        generatorId = 0;
    }

    //проверка на статусы
    public void checkEpic(Epic epic) {
        int check = 0;
        for (int i = 0; i < epic.getSubtaskId().size(); i++) {
            if (((subtasks.get(epic.getSubtaskId().get(i)).status == Status.IN_PROGRESS))) {
                epic.setStatus(Status.IN_PROGRESS);
            }
            if ((subtasks.get(epic.getSubtaskId().get(i)).status == Status.DONE)) {
                check++;
            }
        }
        if (check != 0 && epic.status != Status.NEW) {
            epic.setStatus(Status.IN_PROGRESS);
        }
        if (check == epic.getSubtaskId().size()) {
            epic.setStatus(Status.DONE);
        }
        if (epic.getSubtaskId().isEmpty()) {
            epic.setStatus(Status.NEW);
        }
    }


    public void deleteSubTask(Subtask subtask) {
        epics.get(subtasks.get(subtask.taskId).getEpicId()).getSubtaskId().remove(new Integer(subtask.taskId));
        subtasks.remove(subtask.getTaskId());
        checkEpic(epics.get(subtask.getEpicId()));
    }

    public void deleteTask(Task task) {
        if (tasks.get(task.taskId) != null) {
            tasks.remove(task.taskId);
        }
    }

    public void deleteById(int id) {
       // for (int i = 1; i <= generatorId; i++) {
            if (tasks.containsKey(id)) {
                tasks.remove(id);
            }
            if (epics.containsKey(id)) {
                epics.remove(id);
            }
            if (subtasks.containsKey(id)) {
                epics.get(subtasks.get(id).getEpicId()).getSubtaskId().remove(new Integer(id));
                int delId=subtasks.get(id).getEpicId();
                subtasks.remove(id);
                checkEpic(epics.get(delId));
            }
       // }
    }
}







