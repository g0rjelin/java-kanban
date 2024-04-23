package taskmanager;

import taskmodel.Epic;
import taskmodel.Subtask;
import taskmodel.Task;
import taskmodel.TaskStatus;
import taskmodel.TaskType;

import java.util.ArrayList;
import java.util.List;

public final class FileBackedUtils {
    public static Task fromString(String value) {
        String[] values = value.split(",");
        switch (TaskType.valueOf(values[1])) {
            case TASK:
                return new Task(Integer.valueOf(values[0]), values[2], values[4], TaskStatus.valueOf(values[3]));
            case EPIC:
                return new Epic(Integer.valueOf(values[0]), values[2], values[4], new ArrayList<>());
            case SUBTASK:
                return new Subtask(Integer.valueOf(values[0]), values[2], values[4], TaskStatus.valueOf(values[3]),
                        Integer.valueOf(values[5]));
            default:
                throw new ManagerSaveException("Ошибка парсинга записи");
        }
    }

    public static String taskToString(Task task) {
        return String.format("%d,%s,%s,%s,%s,",task.getId(), task.getClass().getSimpleName().toUpperCase(), task.getName(), task.getStatus().toString(), task.getDescription());
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
