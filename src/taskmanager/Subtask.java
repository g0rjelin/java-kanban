package taskmanager;

public class Subtask extends Task{
    Epic epic;

    /**конструктор для подзадачи, уже заведенной в менеджер (обновление, удаление)*/
    public Subtask(String name, String description, Integer id, TaskStatus status, Epic epic) {
        super(name, description, id, status);
        this.epic = epic;
    }

    /**конструктор для новой задачи при создании с указанием статуса*/
    public Subtask(String name, String description, TaskStatus status, Epic epic) {
        super(name, description, status);
        this.epic = epic;
    }

    /**конструктор для новой подзадачи при создании без указания статуса*/
    public Subtask(String name, String description, Epic epic) {
        super(name, description);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", epic='" + epic.getName() + '\'' +
                '}';
    }
}
