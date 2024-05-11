package taskmodel;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIdList;
    private LocalDateTime endTime;

    /**конструктор для эпика, уже заведенного в менеджер (обновление, удаление) #1*/
    public Epic(Integer id, String name, String description, Duration duration, ArrayList<Integer> subtaskIdList) {
        super(id, name, description, duration);
        this.subtaskIdList = new ArrayList<>(subtaskIdList);
    }

    /**конструктор для эпика при восстановлении #2*/
    public Epic(String name, String description, Duration duration) {
        super(name, description, duration);
        subtaskIdList = new ArrayList<>();
    }

    /**конструктор для нового эпика #3*/
    public Epic(String name, String description) {
        this(name, description, Duration.ZERO);
    }

    //конструкторы для эпика в случае указания даты начала
    //#1
    public Epic(Integer id, String name, String description, Duration duration, LocalDateTime startTime, ArrayList<Integer> subtaskIdList) {
        super(id, name, description, duration, startTime);
        this.subtaskIdList = new ArrayList<>(subtaskIdList);
    }

    //#2
    public Epic(String name, String description, Duration duration, LocalDateTime startTime) {
        super(name, description, duration, startTime);
        subtaskIdList = new ArrayList<>();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                ", subtaskIdList=" + subtaskIdList.toString() +
                '}';
    }
}
