package taskmodel;

import java.util.ArrayList;

public class Epic extends Task{
    private ArrayList<Integer> subtaskIdList;

    /**конструктор для эпика, уже заведенного в менеджер (обновление, удаление)*/
    public Epic(Integer id, String name, String description, ArrayList<Integer> subtaskIdList) {
        super(id, name, description);
        this.subtaskIdList = new ArrayList<>(subtaskIdList);
    }

    /**конструктор для нового эпика*/
    public Epic(String name, String description) {
        super(name, description);
        subtaskIdList = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskIdList() {
        return subtaskIdList;
    }

    public void clearSubtaskIdList() {
        subtaskIdList.clear();
    }

    public void addIdSubtask(Integer idSubtask) {
        subtaskIdList.add(idSubtask);
    }

    public void deleteIdSubtask(Integer idSubtask) {
        subtaskIdList.remove(idSubtask);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", subtaskIdList=" + subtaskIdList.toString() +
                '}';
    }
}
