public class Task {
    String nameTask;
    String description;
    int taskId;
    Status status;

    public Task(String nameTask, String description, int taskId) {
        this.nameTask = nameTask;
        this.description = description;
        this.taskId = taskId;
        this.status = Status.NEW;
    }

    @Override
    public String toString() {
        return "ID " + taskId +
                " Задача ==> '" + nameTask + '\'' +
                ", Описание='" + description + '\'' +
                ", Статус=" + status +
                '}';
    }

    public Task() {
    }

    public String getNameTask() {
        return nameTask;
    }

    public void setNameTask(String nameTask) {
        this.nameTask = nameTask;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
