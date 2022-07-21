package ru.mywork.taskmanager.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import ru.mywork.taskmanager.errors.CollisionTaskException;
import ru.mywork.taskmanager.model.Task;
import ru.mywork.taskmanager.model.Epic;
import ru.mywork.taskmanager.model.Subtask;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static ru.mywork.taskmanager.model.Status.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    abstract T createTaskManager();

    @BeforeEach
    private void updateTaskManager() {
        taskManager = createTaskManager();
    }

    @Test
    public void shouldBeTestsTask() {
        Task task = new Task("task", "task descr", DONE);
        taskManager.addNewTask(task);
        Task savedTask = taskManager.getTaskById(task.getId());
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        assertNotNull(taskManager.getTasks(), "Задачи не возвращаются.");
        assertEquals(1, taskManager.getTasks().size(), "Неверное количество задач.");
        assertEquals(task, taskManager.getTasks().get(task.getId()), "Задачи не совпадают.");
        assertEquals(DONE, taskManager.getTasks().get(task.getId()).getStatus(), "Статус не совпадает");
        taskManager.deleteTaskById(task.getId());
        assertEquals(0, taskManager.getTasks().size(), "Задача не удалена.");
        Task task2 = new Task("task2", "task descr");
        taskManager.addNewTask(task2);
        Task task3 = new Task("task3", "task descr");
        taskManager.addNewTask(task3);
        taskManager.clearTask();
        assertEquals(0, taskManager.getTasks().size(), "Задачи не удалены.");
        Task timeTask1 = new Task("timeTask1", "timeTask1 descr",
                LocalDateTime.of(2022, 1, 1, 1, 0), 10);
        taskManager.addNewTask(timeTask1);
        Task timeTask2 = new Task("timeTask2", "timeTask2 descr",
                LocalDateTime.of(2022, 1, 1, 1, 5), 10);
        assertThrows(CollisionTaskException.class, () -> taskManager.addNewTask(timeTask2),
                "Новая задача не входит внутрь существующей");
    }

    @Test
    public void shouldBeTestsEpic() {
        Epic epic = new Epic("epic", "epic descr");
        taskManager.addNewEpic(epic);
        Subtask subTask = new Subtask("subTask", "subTask descr", epic.getId());
        taskManager.addNewSubTask(subTask);
        Subtask subTask2 = new Subtask("subTask2", "subTask descr", epic.getId());
        taskManager.addNewSubTask(subTask2);
        assertEquals(NEW, taskManager.getEpics().get(epic.getId()).getStatus(), "Статус не совпадает");
        Epic savedEpic = taskManager.getEpicById(epic.getId());
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");
        assertNotNull(taskManager.getEpics(), "Задачи не возвращаются.");
        assertEquals(1, taskManager.getEpics().size(), "Неверное количество задач.");
        assertEquals(epic, taskManager.getEpics().get(epic.getId()), "Задачи не совпадают.");
        assertEquals(NEW, taskManager.getEpics().get(epic.getId()).getStatus(), "Статус не совпадает.");
        Subtask subtaskUpdate = new Subtask(subTask.getName(), subTask.getDescription(),
                subTask.getEpicId(), IN_PROGRESS);
        taskManager.addNewSubTask(subtaskUpdate);
        assertEquals(IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus(), "Статус не совпадает");
        taskManager.deleteEpicById(epic.getId());
        assertEquals(0, taskManager.getEpics().size(), "Эпик не удален");
        Epic epic2 = new Epic("epic2", "epic descr");
        taskManager.addNewEpic(epic2);
        Epic epic3 = new Epic("epic3", "epic descr");
        taskManager.addNewEpic(epic3);
        Subtask subTask3 = new Subtask("subtask3", "subtask descr", epic3.getId());
        taskManager.addNewSubTask(subTask3);
        taskManager.clearEpic();
        assertEquals(0, taskManager.getEpics().size(), "Эпики не удалены");
    }

    @Test
    public void shouldBeTestsSubtask() {
        Epic epic = new Epic("epic", "epic decr");
        taskManager.addNewEpic(epic);
        Subtask subTask = new Subtask("subTask", "subTask decr", epic.getId());
        taskManager.addNewSubTask(subTask);
        Subtask savedSubTask = taskManager.getSubtaskById(subTask.getId());
        assertNotNull(subTask, "Задача не найдена.");
        assertEquals(subTask, savedSubTask, "Задачи не совпадают.");
        assertNotNull(taskManager.getSubtasks(), "Задачи не возвращаются.");
        assertEquals(epic.getId(), taskManager.getSubtaskById(subTask.getId()).getEpicId(), "Нет эпика");
        assertEquals(1, taskManager.getSubtasks().size(), "Неверное количество задач.");
        assertEquals(subTask, taskManager.getSubtaskById(subTask.getId()), "Задачи не совпадают.");
        Subtask subtaskUpdate = new Subtask(subTask.getName(), subTask.getDescription(), subTask.getEpicId(), DONE);
        subtaskUpdate.setId(subTask.getId());
        taskManager.updateSubtask(subtaskUpdate);
        assertEquals(DONE, taskManager.getSubtaskById(subTask.getId()).getStatus());
        taskManager.deleteSubtaskById(subTask.getId());
        assertEquals(0, taskManager.getSubtasks().size(), "Подзадача не удалена");
        Subtask subTask2 = new Subtask("subTask2", "subTask decr", epic.getId());
        taskManager.addNewSubTask(subTask2);
        Subtask subTask3 = new Subtask("subTask3", "subTask decr", epic.getId());
        taskManager.addNewSubTask(subTask3);
        taskManager.clearSubtask();
        assertEquals(0, taskManager.getSubtasks().size(), "Подзадачи не удалены");
        Epic epic1 = new Epic("epic", "epic decr");
        taskManager.addNewEpic(epic1);
        Subtask timeSubtask1 = new Subtask("timeSubtask1", "timeSubtask1 descr", epic1.getId(),
                LocalDateTime.of(2022, 1, 1, 1, 0), 10);
        taskManager.addNewSubTask(timeSubtask1);
        Subtask timeSubtask2 = new Subtask("timeSubtask1", "timeSubtask1 descr", epic1.getId(),
                LocalDateTime.of(2022, 1, 1, 1, 11), 10);
        taskManager.addNewSubTask(timeSubtask2);
        assertEquals(epic1.getStartTime(), timeSubtask1.getStartTime(), "Время начала не совпадает");
        assertEquals(epic1.getEndTime(), timeSubtask2.getEndTime(), "Время конца не совпадает");
        Subtask timeSubtask3 = new Subtask("timeSubtask2", "timeSubtask2 descr", epic1.getId(),
                LocalDateTime.of(2022, 1, 1, 1, 5), 10);
        assertThrows(CollisionTaskException.class, () -> taskManager.addNewSubTask(timeSubtask3),
                "Новая задача не входит внутрь существующей");
    }

}