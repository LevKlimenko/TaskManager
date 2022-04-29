public class Epic extends Task {
    String nameEpic;
    String description;
    Status status;
    int epicId;
    int subtaskId;

    public Epic(String nameEpic, String description, int epicId, int subtaskId) {
        this.nameEpic = nameEpic;
        this.description = description;
        this.epicId = epicId;
        this.subtaskId = subtaskId;
        this.status = Status.NEW;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "nameEpic='" + nameEpic + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", epicId=" + epicId +
                '}';
    }

    public String getNameEpic() {
        return nameEpic;
    }

    public void setNameEpic(String nameEpic) {
        this.nameEpic = nameEpic;
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

    public int getSubtaskId() {
        return subtaskId;
    }

    public void setSubtaskId(int subtaskId) {
        this.subtaskId = subtaskId;
    }
}
