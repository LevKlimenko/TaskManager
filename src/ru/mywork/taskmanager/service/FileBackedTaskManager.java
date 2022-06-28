package ru.mywork.taskmanager.service;

import ru.mywork.taskmanager.model.Epic;
import ru.mywork.taskmanager.model.Subtask;
import ru.mywork.taskmanager.model.Task;
import ru.mywork.taskmanager.model.TypeTask;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    @Override
    public int getGeneratorId() {
        return generatorId;
    }

    @Override
    public void addNewTask(Task task) {
        super.addNewTask(task);
        save();
    }

    @Override
    public void addNewEpic(Epic epic) {

    }

    @Override
    public void addNewSubTask(Subtask subtask) {

    }

    @Override
    public void updateTask(Task task) {

    }

    @Override
    public void updateSubtask(Subtask subtask) {

    }

    @Override
    public void updateEpic(Epic epic) {

    }

    @Override
    public void updateStatusEpic(Epic epic) {

    }

    @Override
    public void printEpic(Epic epic) {

    }

    @Override
    public List<Subtask> getSubtaskByEpicId(int id) {
        return null;
    }

    @Override
    public void printAll() {

    }

    @Override
    public void printById(int id) {

    }

    @Override
    public void getAllTask() {

    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        return null;
    }

    @Override
    public HashMap<Integer, Subtask> getSubtasks() {
        return null;
    }

    @Override
    public HashMap<Integer, Task> getTasks() {
        return null;
    }

    @Override
    public Task getTaskById(int id) {
        return null;
    }

    @Override
    public Epic getEpicById(int id) {
        return null;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        return null;
    }

    @Override
    public void clearTask() {

    }

    @Override
    public void clearSubtask() {

    }

    @Override
    public void clearEpic() {

    }

    @Override
    public void checkTaskAvailability() {

    }

    @Override
    public void deleteTaskById(int id) {

    }

    @Override
    public void deleteEpicById(int id) {

    }

    @Override
    public void deleteSubtaskById(int id) {

    }

    @Override
    public void printHistory() {

    }

    public void save() throws IOException {
        try(FileWriter fw = new FileWriter("tasks.csv")){
            fw.write("id,type,name,status,description,epic\n");
            for (Task task: getHistory()) {
                fw.write(toString(tasks));
            }
    }
    }

    public String toString(InMemoryHistoryManager historyManager){
        StringBuilder sb = new StringBuilder();
        for (Task task: historyManager.getHistory()) {
            sb.append(task.getId());
            sb.append(",");
        }
    return String.valueOf(sb);
    }



}
