package ru.mywork.taskmanager.model;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    protected TypeTask typeTask;

    private ArrayList<Integer> subtaskId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
        this.typeTask = TypeTask.EPIC;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskId, epic.subtaskId);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Epic{");
        sb.append("id=").append(getId());
        sb.append(", type='").append(getTypeTask()).append('\'');
        sb.append(", name='").append(getName()).append('\'');
        sb.append(", description='").append(getDescription()).append('\'');
        sb.append(", status=").append(getStatus());
        sb.append(", subtaskId=").append(getSubtaskId());
        sb.append('}');
        return sb.toString();
    }

    public String toStringInFile() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getId());
        sb.append(",").append(getTypeTask());
        sb.append(",").append(getName());
        sb.append(",").append(getDescription());
        sb.append(",").append(getStatus());
        sb.append(",").append(getSubtaskId());
        return sb.toString();
    }

    @Override
    public TypeTask getTypeTask() {
        return typeTask;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskId);
    }

    public ArrayList<Integer> getSubtaskId() {
        return subtaskId;
    }

    public void setSubtaskId(ArrayList<Integer> subtaskId) {
        this.subtaskId = subtaskId;
    }
}