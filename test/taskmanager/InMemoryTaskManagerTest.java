package taskmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmodel.Epic;
import taskmodel.Task;
import taskmodel.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>{

    @BeforeEach
    public void setUp() {
        taskManager = (InMemoryTaskManager) Managers.getDefault();
        task = new Task("Test InMemoryTaskManager task",
                "Test InMemoryTaskManager task description",
                TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2024, 02, 24, 17, 05, 30));
        epic = new Epic("Test InMemoryTaskManager epic",
                "Test InMemoryTaskManager epic description");
    }

    /**
     * проверка работы InMemoryTaskManager: добавление задачи
     */
    @Test
    public void addNewTask() {
        super.addNewTask();
    }

    /**
     * проверка работы InMemoryTaskManager: обновление задачи
     */
    @Test
    public void updateTask() {
        super.updateTask();
    }


    /**
     * проверка работы InMemoryTaskManager: удаление задачи
     */
    @Test
    public void deleteTask() {
        super.deleteTask();
    }


    /**
     * проверка неизменности задачи (по всем полям) при добавлении задачи в менеджер
     */
    @Test
    public void shouldTaskFieldsNotBeChangedWhenAddedToTaskManager() {
        super.shouldTaskFieldsNotBeChangedWhenAddedToTaskManager();
    }


    /**
     * проверка отсутствия конфликта между задачи с заданным id и сгенерированным id внутри менеджера
     */
    @Test
    public void shouldNotConflictBetweenTaskWithSetIdAndGeneratedId() {
        super.shouldNotConflictBetweenTaskWithSetIdAndGeneratedId();
    }


    /**
     * проверка работы InMemoryTaskManager: добавление эпика
     */
    @Test
    public void addNewEpic() {
        super.addNewEpic();
    }


    /**
     * проверка работы InMemoryTaskManager: обновление эпика
     */
    @Test
    public void updateEpic() {
        super.updateEpic();
    }


    /**
     * проверка работы InMemoryTaskManager: удаление эпика
     */
    @Test
    public void deleteEpic() {
        super.deleteEpic();
    }

    /**
     * проверка отсутствия конфликта между эпиком с заданным id и сгенерированным id внутри менеджера
     */
    @Test
    public void shouldNotConflictBetweenEpicWithSetIdAndGeneratedId() {
        super.shouldNotConflictBetweenEpicWithSetIdAndGeneratedId();
    }


    /**
     * проверка работы InMemoryTaskManager: добавление подзадачи
     */
    @Test
    public void addNewSubtask() {
        super.addNewSubtask();
    }

    /**
     * проверка работы InMemoryTaskManager: обновление подзадачи
     */
    @Test
    public void updateSubtask() {
        super.updateSubtask();
    }


    /**
     * проверка работы InMemoryTaskManager: удаление подзадачи
     */
    @Test
    public void deleteSubtask() {
        super.deleteSubtask();
    }

    /**
     * проверка отсутствия конфликта между подзадачей с заданным id и сгенерированным id внутри менеджера
     */
    @Test
    public void shouldNotConflictBetweenSubtaskWithSetIdAndGeneratedId() {
        super.shouldNotConflictBetweenSubtaskWithSetIdAndGeneratedId();
    }

    /**
     * проверка, что объект Epic нельзя добавить в самого себя в виде подзадачи
     */
    @Test
    public void shouldNotAddEpicIntoEpicAsSubtask() {
        super.shouldNotAddEpicIntoEpicAsSubtask();
    }

    /**
     * проверка, что объект Subtask нельзя сделать своим же эпиком (через добавление)
     */
    @Test
    public void shouldNotAddSubtaskAsItsEpic() {
        super.shouldNotAddSubtaskAsItsEpic();
    }

    /**
     * проверка, что объект Subtask нельзя сделать своим же эпиком (через обновление)
     */
    @Test
    public void shouldNotUpdateSubtaskAsItsEpic() {
        super.shouldNotUpdateSubtaskAsItsEpic();
    }

    /**
     * проверка работы InMemoryTaskManager: проверка удаления всех задач
     */
    @Test
    public void removeAllTasksTest() {
        super.removeAllTasksTest();
    }

    /**
     * проверка работы InMemoryTaskManager: проверка удаления всех подзадач
     */
    @Test
    public void removeAllSubtasksTest() {
        super.removeAllSubtasksTest();
    }

    /**
     * проверка работы InMemoryTaskManager: проверка удаления всех эпиков
     */
    @Test
    public void removeAllEpicsTest() {
        super.removeAllEpicsTest();
    }

    /**
     * задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных
     */
    @Test
    public void shouldPreserveTaskPrevVersionInHistory() {
        super.shouldPreserveTaskPrevVersionInHistory();
    }

    /**
     * эпики, добавляемые в HistoryManager, сохраняют предыдущую версию эпика и её данных
     */
    @Test
    public void shouldPreserveEpicPrevVersionInHistory() {
        super.shouldPreserveEpicPrevVersionInHistory();
    }

    /**
     * подзадачи, добавляемые в HistoryManager, сохраняют предыдущую версию подзадачи и её данных
     */
    @Test
    public void shouldPreserveSubtaskPrevVersionInHistory() {
        super.shouldPreserveSubtaskPrevVersionInHistory();
    }

    /**
     * Проверка правильной работы связного списка по анализу результата возврата истории
     * с разной последовательностью обращения к задачам
     */
    @Test
    public void shouldLinkLastWorkCorrectly() {
        super.shouldLinkLastWorkCorrectly();
    }

    /**
     * InMemoryTaskManager: Проверка расчета статус эпика
     */
    @Test
    void shouldCorrectlyUpdateEpicStatus() {
        super.shouldCorrectlyUpdateEpicStatus();
    }

    /**
     * InMemoryTaskManager: Тест на корректность расчёта пересечения интервалов
     */
    @Test
    void shouldCorrectlyValidateIntersectionOfIntervals() {
        super.shouldCorrectlyValidateIntersectionOfIntervals();
    }
}