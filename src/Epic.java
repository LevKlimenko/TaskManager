import java.util.ArrayList;

public class Epic extends Task {
    //String nameEpic;
    //String description;
    //Status status;
    int epicId;
    //int subtaskId;
    ArrayList<Integer>subtaskId;

    public Epic(String nameEpic, String description, ArrayList<Integer>subtaskId) {
        this.name = nameEpic;
        this.description = description;
        //this.epicId = epicId;
        //this.subtaskId = subtaskId;
        this.status = Status.NEW;
        this.subtaskId=subtaskId;
    }

    @Override
    public String toString() {
        return "Эпик "+ taskId +":      '" + name + '\'' +
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
