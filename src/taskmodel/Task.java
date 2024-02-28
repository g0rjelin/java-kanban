package taskmodel;

import java.util.Objects;

public class Task {
    private Integer id; //Уникальный идентификационный номер задачи, по которому её можно будет найти.

    private String name; //Название, кратко описывающее суть задачи (например, «Переезд»).
    private String description; //Описание, в котором раскрываются детали.
    private TaskStatus status; //Статус


    //конструктор для использования в классе-наследнике (Epic)
    protected Task(Integer id, String name, String description) {
        this(name, description);
        this.id = id;
    }

    //конструктор для задачи, уже заведенной в менеджер (обновление, удаление)
    public Task(Integer id, String name, String description, TaskStatus status) {
        this(id, name, description);
        this.status = status;
    }

    //конструктор для новой задачи при создании с указанием статуса
    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    //конструктор для новой задачи при создании без указания статуса
    public Task(String name, String description) {
        this(name,description,TaskStatus.NEW);
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

    public int getId() {
        return id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
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
