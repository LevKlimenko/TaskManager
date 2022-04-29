public class Subtask extends Task{
    String nameSubtask;
    String description;
    Status status;
    int epicId;

    public Subtask(String nameSubtask, String description, int epicId) {
        this.nameSubtask = nameSubtask;
        this.description = description;
        this.status = Status.NEW;
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "nameSubtask='" + nameSubtask + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", epicId=" + epicId +
                '}';
    }

    public String getNameSubtask() {
        return nameSubtask;
    }

    public void setNameSubtask(String nameSubtask) {
        this.nameSubtask = nameSubtask;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}
