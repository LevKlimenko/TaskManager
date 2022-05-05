import java.util.ArrayList;
import java.util.HashMap;


public class Manager {

    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Task> tasks = new HashMap<>();
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

    public void addNewTask(Task task) {
        int id = ++generatorId;
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

    public void changeStatusTask(int id) {
        if (tasks.get(id).status == Status.NEW) {
            tasks.get(id).setStatus(Status.IN_PROGRESS);
        } else if (tasks.get(id).status == Status.IN_PROGRESS) {
            tasks.get(id).setStatus(Status.DONE);
        } else {
            tasks.get(id).status = tasks.get(id).getStatus();
        }
        tasks.put(tasks.get(id).getTaskId(), tasks.get(id));
    }

    public void changeStatusSubTask(int id) {
        if (subtasks.get(id).status == Status.NEW) {
            subtasks.get(id).setStatus(Status.IN_PROGRESS);
        } else if (subtasks.get(id).status == Status.IN_PROGRESS) {
            subtasks.get(id).setStatus(Status.DONE);
        } else {
            subtasks.get(id).status = subtasks.get(id).getStatus();
        }
        subtasks.put(subtasks.get(id).getTaskId(), subtasks.get(id));
        checkEpic(epics.get(subtasks.get(id).getEpicId()));
    }

    private void printEpic(Epic epic) {
        System.out.println("\n" + epic);
        for (int i = 0; i < epic.getSubtaskId().size(); i++) {
            if (subtasks.get(epic.getSubtaskId().get(i)) != null) {
                System.out.println(subtasks.get(epic.getSubtaskId().get(i)));
            }
        }
    }

    public void printSubtaskByEpicId(int id) {
        if (epics.containsKey(id)) {
            for (int i = 0; i < epics.get(id).getSubtaskId().size(); i++) {
                if (subtasks.get(epics.get(id).getSubtaskId().get(i)) != null) {
                    System.out.println(subtasks.get(epics.get(id).getSubtaskId().get(i)));
                }
            }
        }
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

    public void printById(int id) {
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

    private void checkEpic(Epic epic) {
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

    public void clearAllTask() {
        for (int i = 1; i <= generatorId; i++) {
            tasks.remove(i);
            epics.remove(i);
            subtasks.remove(i);
        }
        System.gc();
        generatorId = 0;
    }

    public void deleteById(int id) {
        tasks.remove(id);
        if (epics.containsKey(id)) {
            for (Integer subDel : epics.get(id).getSubtaskId()) {
                subtasks.remove(subDel);
            }
            epics.remove(id);
        }
        if (subtasks.containsKey(id)) {
            epics.get(subtasks.get(id).getEpicId()).getSubtaskId().remove(new Integer(id));
            int delId = subtasks.get(id).getEpicId();
            subtasks.remove(id);
            checkEpic(epics.get(delId));
        }
    }
}







