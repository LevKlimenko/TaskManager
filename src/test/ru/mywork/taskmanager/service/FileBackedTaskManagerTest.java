package ru.mywork.taskmanager.service;


import java.io.File;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @Override
    FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(new File("test.csv"));
    }
}
