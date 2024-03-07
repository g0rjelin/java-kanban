package taskmanager;

import taskmodel.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private ArrayList<Task> history; //список просмотренных задач

    InMemoryHistoryManager() {
        history = new ArrayList<>(NUM_TASKS_IN_HISTORY);
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (history.size() >= NUM_TASKS_IN_HISTORY) {
                history.remove(0);
            }
            history.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
