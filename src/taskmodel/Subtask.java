package taskmodel;

public class Subtask extends Task {
    private Integer idEpic;

    /**конструктор для подзадачи, уже заведенной в менеджер (обновление, удаление)*/
    public Subtask(Integer id, String name, String description, TaskStatus status, Integer idEpic) {
        super(id, name, description, status);
        this.idEpic = idEpic;
    }

    /**конструктор для новой задачи при создании с указанием статуса*/
    public Subtask(String name, String description, TaskStatus status, Integer idEpic) {
        super(name, description, status);
        this.idEpic = idEpic;
    }

    /**конструктор для новой подзадачи при создании без указания статуса*/
    public Subtask(String name, String description, Integer idEpic) {
        super(name, description);
        this.idEpic = idEpic;
    }

    public Integer getIdEpic() {
        return idEpic;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", idEpic=" + idEpic +
                '}';
    }

    @Override
    public String taskToString() {
        return super.taskToString() + getIdEpic();
    }
}
