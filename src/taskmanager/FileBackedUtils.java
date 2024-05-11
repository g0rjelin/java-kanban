package taskmanager;

import taskmodel.Epic;
import taskmodel.Subtask;
import taskmodel.Task;
import taskmodel.TaskStatus;
import taskmodel.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class FileBackedUtils {
    public static Task fromString(String value) {
        String[] values = value.split(",");
        Integer id = Integer.valueOf(values[0]);
        String name = values[2];
        String description = values[4];
        TaskStatus status = TaskStatus.valueOf(values[3]);
        Duration duration = Duration.ofMinutes(Integer.parseInt(values[5]));
        LocalDateTime startTime = null;
        if (!values[6].equals("null")) {
            startTime = LocalDateTime.parse(values[6]);
        }
        switch (TaskType.valueOf(values[1])) {
            case TASK:
                return startTime == null ?
                        new Task(id, name, description, status,
                                duration) :
                        new Task(id, name, description, status,
                                duration, startTime);
            case EPIC:
                return startTime == null ?
                        new Epic(id, name, description,
                                duration, new ArrayList<>()) :
                        new Epic(id, name, description,
                                duration, startTime,
                                new ArrayList<>());
            case SUBTASK:
                Integer epicId = Integer.valueOf(values[7]);
                return startTime == null ?
                        new Subtask(id, name, description, status,
                                duration, epicId) :
                        new Subtask(id, name, description, status,
                                duration, startTime, epicId);
            default:
                throw new ManagerSaveException("Ошибка парсинга записи");
        }
    }

    public static String taskToString(Task task) {
        return String.format("%d,%s,%s,%s,%s,%d,%s,", task.getId(), task.getClass().getSimpleName().toUpperCase(),
                task.getName(),
                task.getStatus().toString(), task.getDescription(), task.getDuration().getSeconds() / 60,
                task.getStartTime());
    }

    public static String subtaskToString(Subtask subtask) {
        return taskToString(subtask) + subtask.getIdEpic();
    }

    static String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        String[] historyIdArray = new String[history.size()];
        int count = 0;
        for (Task task : history) {
            historyIdArray[count] = String.valueOf(task.getId());
            count++;
        }
        return String.join(",", historyIdArray);
    }

    static List<Integer> historyFromString(String value) {
        List<Integer> listId = new ArrayList<>();
        for (String strId : value.split(",")) {
            if (strId.length() > 0) {
                listId.add(Integer.valueOf(strId));
            }
        }
        return listId;
    }
}
