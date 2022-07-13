package ru.mywork.taskmanager.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Formatter;
import java.util.Objects;

public class Task {


    protected TypeTask typeTask;
    protected String name;
    protected String description;
    protected int id;
    protected Status status;
    protected int duration; //продолжительность
    protected LocalDateTime startTime; //время начала
    protected LocalDateTime endTime;
    protected final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss.ms");


    public Task(String name, String description) {
        this.typeTask = TypeTask.TASK;
        this.name = name;
        this.description = description;
        status = Status.NEW;
        this.startTime=null;
        this.duration=0;
    }

    public Task(String name, String description, Status status) {
        this.typeTask = TypeTask.TASK;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime=null;
        this.duration=0;
    }

    public Task(String name, String description, Status status,LocalDateTime startTime,int duration) {
        this.startTime=startTime;
        this.duration=duration;
        this.typeTask = TypeTask.TASK;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description,LocalDateTime startTime, int duration) {
        this.startTime=startTime;
        this.duration=duration;
        this.typeTask = TypeTask.TASK;
        this.name = name;
        this.description = description;
        status = Status.NEW;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Task{");
        sb.append("id=").append(getId());
        sb.append(", type='").append(getTypeTask()).append('\'');
        sb.append(", name='").append(getName()).append('\'');
        sb.append(", description='").append(getDescription()).append('\'');
        sb.append(", status=").append(getStatus());
        sb.append(", timeStart=").append(getStartTime().format(formatter));
        sb.append(", timeEnd=").append(getEndTime().format(formatter));
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
        sb.append(",").append(getStartTime());
       sb.append(",").append(getDuration());
        return sb.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id &&
                Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TypeTask getTypeTask() {
        return typeTask;
    }

    public LocalDateTime getStartTime() {
      return this.startTime;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getEndTime() {
        this.endTime=getStartTime().plusMinutes(duration);
        endTime.format(formatter);
        return this.endTime;
    }
    public int getDuration() {
        return duration;
    }
}
