package taskmanager;

import java.util.ArrayList;

public class Epic extends Task{
    ArrayList<Subtask> subtasksList;

    /**конструктор для эпика, уже заведенного в менеджер (обновление, удаление)*/
    public Epic(String name, String description, Integer id, ArrayList<Subtask> subtasksList) {
        super(name, description, id);
        this.subtasksList = subtasksList;
        updateStatus();
    }

    /**конструктор для нового эпика*/
    public Epic(String name, String description) {
        super(name, description);
        subtasksList = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtasksList() {
        return subtasksList;
    }

    public void updateStatus() {
        if (subtasksList.isEmpty()) {
            setStatus(TaskStatus.NEW);
        } else {
            //проверяем все статусы подзадач (сначала 0, а потом с 1й в цикле)
            TaskStatus currentSubTaskStatus = subtasksList.get(0).getStatus();
            for (int i = 1; i < subtasksList.size(); i++) {
                if (subtasksList.get(i).getStatus() != currentSubTaskStatus) { //если статус хотя бы одной подзадачи отличается от других, то эпик в работе
                    currentSubTaskStatus = TaskStatus.IN_PROGRESS;
                    break;
                }
            }
            setStatus(currentSubTaskStatus);
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", subtasks=" + subtasksList.toString() +
                '}';
    }
}
