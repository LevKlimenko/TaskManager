package ru.mywork.taskmanager.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private final ArrayList<Integer> subtaskId = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        this.startTime = null;
        this.duration = 0;
    }

    public Epic(String name, String description, LocalDateTime startTime, int duration) {
        super(name, description);
        this.startTime = startTime;
        this.duration = duration;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return subtaskId.containsAll(epic.subtaskId) && epic.subtaskId.containsAll(subtaskId);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Epic{");
        sb.append("id=").append(getId());
        sb.append(", type='").append(getType()).append('\'');
        sb.append(", name='").append(getName()).append('\'');
        sb.append(", description='").append(getDescription()).append('\'');
        sb.append(", status=").append(getStatus());
        sb.append(", subtaskID=").append(getSubtaskId());
        if (getStartTime() != null) {
            sb.append(", timeStart=").append(getStartTime().format(formatter));
            sb.append(", duration=").append(getEndTime().format(formatter));
        }
        sb.append('}');
        return sb.toString();
    }

    public String toStringInFile() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getId());
        sb.append(",").append(getType());
        sb.append(",").append(getName());
        sb.append(",").append(getDescription());
        sb.append(",").append(getStatus());
        sb.append(",").append(getStartTime());
        sb.append(",").append(getDuration());
        return sb.toString();
    }

    @Override
    public TypeTask getType() {
        return TypeTask.EPIC;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskId);
    }

    public ArrayList<Integer> getSubtaskId() {
        return subtaskId;
    }


    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}