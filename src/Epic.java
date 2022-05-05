import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private int epicId;
    private ArrayList<Integer> subtaskId;

    public Epic(String nameEpic, String description, ArrayList<Integer> subtaskId) {
        this.name = nameEpic;
        this.description = description;
        this.status = Status.NEW;
        this.subtaskId = subtaskId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return epicId == epic.epicId &&
                Objects.equals(subtaskId, epic.subtaskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId, subtaskId);
    }

    @Override
    public String toString() {
        return "ID " + taskId + ": Эпик       '" + name + '\'' +
                ", Описание='" + description + '\'' +
                ", Статус=" + status
                ;
    }

    public String getNameEpic() {
        return name;
    }

    public void setNameEpic(String nameEpic) {
        this.name = nameEpic;
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

    public ArrayList<Integer> getSubtaskId() {
        return subtaskId;
    }

    public void setSubtaskId(ArrayList<Integer> subtaskId) {
        this.subtaskId = subtaskId;
    }
}
