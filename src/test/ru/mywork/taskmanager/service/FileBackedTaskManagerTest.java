package ru.mywork.taskmanager.service;


import org.junit.jupiter.api.Test;
import ru.mywork.taskmanager.model.Epic;
import ru.mywork.taskmanager.model.Subtask;
import ru.mywork.taskmanager.model.Task;

import java.io.File;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @Override
    FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(new File("test.csv"));
    }

    @Test
    public void shouldBeFileBackupManager() {
        File file = new File("test.csv");
        FileBackedTaskManager fbk = new FileBackedTaskManager(file);
        Task task = new Task("task1", "descr1",
                LocalDateTime.of(2022, 6, 1, 10, 0), 30);
        fbk.addNewTask(task);
        Epic epic = new Epic("epic1", "descr1");
        fbk.addNewEpic(epic);
        Subtask subTask = new Subtask("subtask2", "descr2",
                epic.getId(), LocalDateTime.of(2022, 6, 1, 11, 30), 30);
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
        assertEquals(createTaskManager().getTasks(), fbk2.getTasks(),
                "Список задач после выгрузки не совпадает");
        assertEquals(createTaskManager().getSubtasks(), fbk2.getSubtasks(),
                "Список подзадач после выгрузки не совпадает");
        assertEquals(createTaskManager().getEpics(), fbk2.getEpics(),
                "Список эпиков после выгрузки не совпадает");
        assertEquals(createTaskManager().getSortedTasks(), fbk2.getSortedTasks(),
                "Сортированные задачи не совпадают");
        assertEquals(createTaskManager().getHistory(), fbk2.getHistory(), "История задач не совпадает");
        assertNotNull(fbk2, "Не загружен");
    }
}
