package ru.mywork.taskmanager.service;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import ru.mywork.taskmanager.model.Task;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static ru.mywork.taskmanager.model.Status.NEW;

abstract class TaskManagerTest<T extends TaskManager> {
    private T taskManager;

    abstract T createTaskManager();

    @BeforeEach
    private void updateTaskManager() {
        taskManager = createTaskManager();
    }

    // тут тесты методов

    @Test
    public void getGeneratorId() {
        Task task = new Task("testTask","testTaskDescription");
        taskManager.addNewTask(task);
        int id = taskManager.getGeneratorId();
        Assert.assertEquals(task.getId(), id);
    }

    @Test
    void whenAddNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        final int taskId = taskManager.getGeneratorId();

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask);
        assertEquals("Задачи не совпадают.", task, savedTask);

        final HashMap<Integer,Task> tasks = taskManager.getTasks();

        assertNotNull(tasks);
        assertEquals("Размер массива не совпадает",1, tasks.size());
        assertEquals("Задачи не совпадают.", task, tasks.get(0));
    }

    @Test
    public void addNewEpic() {
    }

    @Test
    public void addNewSubTask() {
    }

    @Test
    public void updateTask() {
    }

    @Test
    public void updateSubtask() {
    }

    @Test
    public void updateEpic() {
    }

    @Test
    public void updateStatusEpic() {
    }

    @Test
    public void printEpic() {
    }

    @Test
    public void getSubtaskByEpicId() {
    }

    @Test
    public void printAll() {
    }

    @Test
    public void printById() {
    }

    @Test
    public void getAllTask() {
    }

    @Test
    public void getEpics() {
    }

    @Test
    public void getSubtasks() {
    }

    @Test
    public void getTasks() {
    }

    @Test
    public void getTaskById() {
    }

    @Test
    public void getEpicById() {
    }

    @Test
    public void getSubtaskById() {
    }

    @Test
    public void clearTask() {
    }

    @Test
    public void clearSubtask() {
    }

    @Test
    public void clearEpic() {
    }

    @Test
    public void checkTaskAvailability() {
    }

    @Test
    public void deleteTaskById() {
    }

    @Test
    public void deleteEpicById() {
    }

    @Test
    public void deleteSubtaskById() {
    }

    @Test
    public void getHistory() {
    }

    @Test
    public void printHistory() {
    }

    @Test
    public void setEpicStartAndEndTime() {
    }
}