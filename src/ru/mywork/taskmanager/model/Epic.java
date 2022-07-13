package ru.mywork.taskmanager.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    protected TypeTask typeTask;
    protected LocalDateTime startTime;
    protected int duration;
    protected LocalDateTime endTime;

    private ArrayList<Integer> subtaskId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
        this.startTime=null;
        this.duration=0;
        this.typeTask = TypeTask.EPIC;
    }
 public Epic(String name, String description,LocalDateTime startTime,int duration) {
        super(name, description);
        this.startTime=startTime;
        this.duration=duration;
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
        if (getStartTime()!=null) {
            sb.append(", timeStart=").append(getStartTime().format(formatter));
            sb.append(", timeEnd=").append(getEndTime().format(formatter));
        }
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
        sb.append(",").append(getStartTime());
        sb.append(",").append(getDuration());
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

    @Override
    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
        }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}