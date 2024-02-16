package taskmanager;

import java.util.Objects;

public class Task {
    private String name; //Название, кратко описывающее суть задачи (например, «Переезд»).
    private String description; //Описание, в котором раскрываются детали.
    private Integer id; //Уникальный идентификационный номер задачи, по которому её можно будет найти.
    private TaskStatus status; //Статус

    //конструктор для задачи, уже заведенной в менеджер (обновление, удаление)
    public Task(String name, String description, Integer id, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    //конструктор для использования в классе-наследнике (Epic)
    protected Task(String name, String description, Integer id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    //конструктор для новой задачи при создании без указания статуса
    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    //конструктор для новой задачи при создании с указанием статуса
    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    protected void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
