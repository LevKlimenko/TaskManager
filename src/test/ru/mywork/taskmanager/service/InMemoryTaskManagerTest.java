package ru.mywork.taskmanager.service;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }
}
