package ru.mywork.taskmanager.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    public void shouldBeGetHistory() {
        assertNotNull(createTaskManager().getHistory(),
                "История не возвращается");
    }
}
