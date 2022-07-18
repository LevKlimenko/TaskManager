package ru.mywork.taskmanager.service;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import ru.mywork.taskmanager.model.Task;
import ru.mywork.taskmanager.model.Epic;
import ru.mywork.taskmanager.model.Subtask;
import ru.mywork.taskmanager.service.*;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.mywork.taskmanager.model.Status.*;

abstract class TaskManagerTest<T extends TaskManager> {
    private T taskManager;

    abstract T createTaskManager();

    @BeforeEach
    private void updateTaskManager() {
        taskManager = createTaskManager();
    }

    // тут тесты методов
    @Test
    public void shouldBeGetHistory() {
        assertNotNull(taskManager.getHistory(), "История не возвращается");
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
        Subtask subtaskUpdate = new Subtask(subTask.getName(), subTask.getDescription(), subTask.getEpicId(), IN_PROGRESS);
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
    }

    @Test
    public void shouldBeFileBackupManager() {
        File file = new File("test.csv");
        FileBackedTaskManager fbk = new FileBackedTaskManager(file);
        Task task = new Task("task1", "descr1", LocalDateTime.of(2022, 6, 1, 10, 0), 30);
        fbk.addNewTask(task);
        Epic epic = new Epic("epic1", "descr1");
        fbk.addNewEpic(epic);
        Subtask subTask = new Subtask("subtask2", "descr2", epic.getId(), LocalDateTime.of(2022, 6, 1, 11, 30), 30);
        fbk.addNewSubTask(subTask);
        Subtask subTask2 = new Subtask("subtask2", "descr2", epic.getId());
        fbk.addNewSubTask(subTask2);
        Epic epic2 = new Epic("epic1", "descr1");
        fbk.addNewEpic(epic2);
        Task task2 = new Task("task1", "descr1");
        fbk.addNewTask(task2);
        fbk.getTaskById(1);
        fbk.getEpicById(2);
        fbk.getSubtaskById(3);
        FileBackedTaskManager fbk2 = new FileBackedTaskManager(file);
        assertNotNull(fbk2, "Не загружен");
    }
}