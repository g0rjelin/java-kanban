package taskmodel;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private Integer id; //Уникальный идентификационный номер задачи, по которому её можно будет найти.

    private String name; //Название, кратко описывающее суть задачи (например, «Переезд»).
    private String description; //Описание, в котором раскрываются детали.
    private TaskStatus status; //Статус
    private Duration duration; //Продолжительность задачи
    private LocalDateTime startTime; //дата и время планируемого начала задачи

    //конструктор для использования в классе-наследнике (Epic) #1
    protected Task(Integer id, String name, String description, Duration duration) {
        this(name, description, duration);
        this.id = id;
    }

    //конструктор для задачи, уже заведенной в менеджер (обновление, удаление) #2
    public Task(Integer id, String name, String description, TaskStatus status, Duration duration) {
        this(id, name, description, duration);
        this.status = status;
    }

    //конструктор для новой задачи при создании с указанием статуса #3
    public Task(String name, String description, TaskStatus status, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
    }

    //конструктор для новой задачи при создании без указания статуса #4
    public Task(String name, String description, Duration duration) {
        this(name,description,TaskStatus.NEW, duration);
    }

    //набор конструкторов в случае указания даты начала задачи
    //#1
    protected Task(Integer id, String name, String description, Duration duration, LocalDateTime startTime) {
        this(id, name, description, duration);
        this.startTime = startTime;
    }

    //#2
    public Task(Integer id, String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime) {
        this(id, name, description, status, duration);
        this.startTime = startTime;
    }

    //#3
    public Task(String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime) {
        this(name, description, status, duration);
        this.startTime = startTime;
    }

    //#4
    public Task(String name, String description, Duration duration, LocalDateTime startTime) {
        this(name, description, duration);
        this.startTime = startTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime == null ? null : startTime.plusMinutes(duration.toMinutes());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        //т.к. id уникально для каждой задачи, то две задачи равны тогда и только тогда, когда у них равны id
        return id.intValue() == task.id.intValue();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); //т.к. id уникально для всех задач, то хэш можно определять только по id
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }

}
