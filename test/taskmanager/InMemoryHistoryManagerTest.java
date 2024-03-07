package taskmanager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmodel.Epic;
import taskmodel.Subtask;
import taskmodel.Task;
import taskmodel.TaskStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void shouldReturnEmptyListWhenNoHistory() {
        Assertions.assertEquals(0, historyManager.getHistory().size(),
                "getHistory должен возвращать пустой список, если история пустая");
    }

    @Test
    public void shouldKeepNoMoreThan_NUM_TASKS_IN_HISTORY_ElementsInHistory() {
        for (int i = 1; i <= historyManager.NUM_TASKS_IN_HISTORY; i++) {
            Task task = new Task(i, "TestHistory check_size_task №" + i,
                    "Test check_size_task №" + i + " description", TaskStatus.NEW);
            historyManager.add(task);
        }
        Assertions.assertEquals(historyManager.NUM_TASKS_IN_HISTORY, historyManager.getHistory().size(),
                "История должна хранить не более " + historyManager.NUM_TASKS_IN_HISTORY + " задач");
        Task lastTask = new Task((historyManager.NUM_TASKS_IN_HISTORY +1),
                "TestHistory check_size_task №" + (historyManager.NUM_TASKS_IN_HISTORY +1),
                "TestHistory check_size_task №" + (historyManager.NUM_TASKS_IN_HISTORY +1) + " description",
                TaskStatus.NEW);

        Task firstTask  = historyManager.getHistory().get(0);
        historyManager.add(lastTask);
        Assertions.assertEquals(historyManager.NUM_TASKS_IN_HISTORY, historyManager.getHistory().size(),
                "История должна хранить не более " + historyManager.NUM_TASKS_IN_HISTORY + " задач");
        Assertions.assertNotEquals(firstTask, historyManager.getHistory().get(0),
                "При добавлении задачи сверх лимита первая в списке задача должна исключаться из истории");
        Assertions.assertEquals(lastTask, historyManager.getHistory().get(historyManager.NUM_TASKS_IN_HISTORY-1),
                "Добавленная последней задача должны быть в конце списка");
    }

}