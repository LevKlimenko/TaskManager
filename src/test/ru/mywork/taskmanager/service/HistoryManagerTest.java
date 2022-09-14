package ru.mywork.taskmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mywork.taskmanager.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }


    @Test
    public void testAdd() {
        Task task = new Task("task", "task descr");
        task.setId(1);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая");
        assertEquals(1, history.size(), "История не пустая.");
    }


    @Test
    public void testRemove() {
        Task task = new Task("task", "task descr");
        Task task1 = new Task("task1", "task descr1");
        task1.setId(1);
        historyManager.add(task);
        historyManager.add(task1);
        historyManager.remove(task.getId());
        historyManager.remove(task1.getId());
        final List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size(), "История не пустая");
    }


    @Test
    public void testGetHistory() {
        final List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size(), "История не возвращается.");
    }
}
