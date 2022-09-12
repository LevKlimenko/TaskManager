package ru.mywork.taskmanager.service;

import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mywork.taskmanager.model.Epic;
import ru.mywork.taskmanager.model.Subtask;
import ru.mywork.taskmanager.model.Task;

import java.io.File;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File file;

    @BeforeEach
    FileBackedTaskManager createTaskManager() {
        file = new File("test.csv");
        return new FileBackedTaskManager(file);
    }

    @AfterEach
    void deleteFile() {
        assertTrue(file.delete());
    }

    @Test
    public void shouldBeFileBackupManager() {
        File file = new File("test.csv");
        Task task = new Task("task1", "descr1",
                LocalDateTime.of(2022, 6, 1, 10, 0), 30);
        taskManager.addNewTask(task);
        Epic epic = new Epic("epic1", "descr1");
        taskManager.addNewEpic(epic);
        Subtask subTask = new Subtask("subtask2", "descr2",
                epic.getId(), LocalDateTime.of(2022, 6, 1, 11, 30), 30);
        taskManager.addNewSubTask(subTask);
        Subtask subTask2 = new Subtask("subtask2", "descr2", epic.getId());
        taskManager.addNewSubTask(subTask2);
        Epic epic2 = new Epic("epic1", "descr1");
        taskManager.addNewEpic(epic2);
        Task task2 = new Task("task2", "descr2");
        taskManager.addNewTask(task2);
        taskManager.getTaskById(1);
        taskManager.getEpicById(2);
        taskManager.getSubtaskById(3);

        FileBackedTaskManager fbk2 = FileBackedTaskManager.loadFromFile(file);
        assertEquals(taskManager.getTasks(), fbk2.getTasks(),
                "Список задач после выгрузки не совпадает");
        assertEquals(taskManager.getSubtasks(), fbk2.getSubtasks(),
                "Список подзадач после выгрузки не совпадает");
        assertEquals(taskManager.getEpics(), fbk2.getEpics(),
                "Список эпиков после выгрузки не совпадает");
        assertEquals(taskManager.getHistory(), fbk2.getHistory(), "История задач не совпадает");
        assertEquals(taskManager.getSortedTasks(), fbk2.getSortedTasks(),
                "Сортированные задачи не совпадают");
        assertNotNull(fbk2, "Не загружен");
    }
}
