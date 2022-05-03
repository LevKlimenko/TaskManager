public class Subtask extends Task{
    //String name;
    //String description;
    //Status status;
    int epicId;

    public Subtask(String nameSubtask, String description, int epicId) {
        this.name = nameSubtask;
        this.description = description;
        this.status = Status.NEW;
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Подзадача "+ taskId +": '" + name + '\'' +
                ", Описание='" + description + '\'' +
                ", Статус=" + status+ " Epic->" + epicId;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
