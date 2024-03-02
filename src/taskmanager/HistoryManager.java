package taskmanager;

import taskmodel.Task;

import java.util.List;

public interface HistoryManager {
    int NUM_TASKS_IN_HISTORY = 10;

    void add(Task task);

    List<Task> getHistory();
}
