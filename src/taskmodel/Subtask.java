package taskmodel;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private Integer idEpic;

    /**
     * конструктор для подзадачи, уже заведенной в менеджер (обновление, удаление) #1
     */
    public Subtask(Integer id, String name, String description, TaskStatus status, Duration duration, Integer idEpic) {
        super(id, name, description, status, duration);
        this.idEpic = idEpic;
    }

    /**
     * конструктор для новой задачи при создании с указанием статуса #2
     */
    public Subtask(String name, String description, TaskStatus status, Duration duration, Integer idEpic) {
        super(name, description, status, duration);
        this.idEpic = idEpic;
    }

    /**
     * конструктор для новой подзадачи при создании без указания статуса #3
     */
    public Subtask(String name, String description, Duration duration, Integer idEpic) {
        super(name, description, duration);
        this.idEpic = idEpic;
    }

    //конструкторы подзадач в случае указания даты начала
    //#1
    public Subtask(Integer id, String name, String description, TaskStatus status, Duration duration,
                   LocalDateTime startTime, Integer idEpic) {
        super(id, name, description, status, duration, startTime);
        this.idEpic = idEpic;
    }

    //#2
    public Subtask(String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime,
                   Integer idEpic) {
        super(name, description, status, duration, startTime);
        this.idEpic = idEpic;
    }

    //#3
    public Subtask(String name, String description, Duration duration, LocalDateTime startTime, Integer idEpic) {
        super(name, description, duration, startTime);
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
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                ", idEpic=" + idEpic +
                '}';
    }
}
