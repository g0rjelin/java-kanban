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
        switch (TaskType.valueOf(values[1])) {
            case TASK:
                return values[6].equals("null") ?
                        new Task(Integer.valueOf(values[0]), values[2], values[4], TaskStatus.valueOf(values[3]),
                                Duration.ofMinutes(Integer.parseInt(values[5]))) :
                        new Task(Integer.valueOf(values[0]), values[2], values[4], TaskStatus.valueOf(values[3]),
                                Duration.ofMinutes(Integer.parseInt(values[5])), LocalDateTime.parse(values[6]));
            case EPIC:
                return values[6].equals("null") ?
                        new Epic(Integer.valueOf(values[0]), values[2], values[4],
                                Duration.ofMinutes(Integer.parseInt(values[5])), new ArrayList<>()) :
                        new Epic(Integer.valueOf(values[0]), values[2], values[4],
                                Duration.ofMinutes(Integer.parseInt(values[5])), LocalDateTime.parse(values[6]),
                                new ArrayList<>());
            case SUBTASK:
                return values[6].equals("null") ?
                        new Subtask(Integer.valueOf(values[0]), values[2], values[4], TaskStatus.valueOf(values[3]),
                                Duration.ofMinutes(Integer.parseInt(values[5])), Integer.valueOf(values[7])) :
                        new Subtask(Integer.valueOf(values[0]), values[2], values[4], TaskStatus.valueOf(values[3]),
                                Duration.ofMinutes(Integer.parseInt(values[5])), LocalDateTime.parse(values[6]),
                                Integer.valueOf(values[7]));
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
