package ru.mywork.taskmanager.model;

import java.util.Objects;

public class Subtask extends Task {

    private int epicId;
    protected TypeTask typeTask;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.typeTask=TypeTask.SUBTASK;
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int epicId, Status status) {
        super(name, description, status);
        this.typeTask=TypeTask.SUBTASK;
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Subtask{");
        sb.append("id=").append(getId());
        sb.append(", type='").append(getTypeTask()).append('\'');
        sb.append(", name='").append(getName()).append('\'');
        sb.append(", description='").append(getDescription()).append('\'');
        sb.append(", status=").append(getStatus());
        sb.append(", epicId=").append(getEpicId());
        sb.append('}');
        return sb.toString();
    }

    public String toStringInFile() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getId());
        sb.append(",").append(getTypeTask());
        sb.append(",").append(getName());
        sb.append(",").append(getDescription());
        sb.append(",").append(getStatus());
        sb.append(",").append(getEpicId());
        return sb.toString();
    }

    @Override
    public TypeTask getTypeTask() {
        return typeTask;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }


    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}