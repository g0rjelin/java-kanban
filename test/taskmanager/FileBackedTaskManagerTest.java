package taskmanager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmodel.Epic;
import taskmodel.Subtask;
import taskmodel.Task;
import taskmodel.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    Path testTaskManagerPath;

    @BeforeEach
    public void setUp() throws IOException {
        //создание менеджера задач
        testTaskManagerPath =
                File.createTempFile(String.format("FileBackedTaskManager_%s", UUID.randomUUID()), ".csv").toPath();
        super.taskManager = new FileBackedTaskManager(Managers.getDefaultHistory(), testTaskManagerPath);

        super.task = new Task("Test FileBackedTaskManager task",
                "Test FileBackedTaskManager task description",
                TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2024, 02, 24, 17, 05, 30));
        super.epic = new Epic("Test FileBackedTaskManager epic",
                "Test FileBackedTaskManager epic description");

    }

    @Test
    void shouldCorrectlySaveAndLoadTaskManager() throws IOException {
        //наполнение задачами
        Task task1 = new Task("Первая задача", "Пример запланированной задачи", Duration.ZERO);
        Integer idTask1 = taskManager.addTask(task1);
        Epic epic1 = new Epic("Первый эпик", "Первое эпичное описание");
        Integer idEpic1 = taskManager.addEpic(epic1);
        Subtask subtask1 =
                new Subtask("Первая подзадачка", "Запланированная подзадача", TaskStatus.DONE, Duration.ofMinutes(15),
                        LocalDateTime.now(), idEpic1);
        Integer idSubtask1 = taskManager.addSubtask(subtask1);

        //история просмотра задач
        taskManager.getEpicById(idEpic1);
        taskManager.getSubtaskById(idSubtask1);
        taskManager.getSubtaskById(idTask1);

        //загрузка из файла в новый менеджер
        FileBackedTaskManager loadedTaskManager = FileBackedTaskManager.loadFromFile(testTaskManagerPath);

        Assertions.assertEquals(taskManager.getTasksList(), loadedTaskManager.getTasksList(),
                "Задачи в первоначальном менеджере и после загрузки из файла должны совпадать");
        Assertions.assertEquals(taskManager.getSubtasksList(), loadedTaskManager.getSubtasksList(),
                "Подзадачи в первоначальном менеджере и после загрузки из файла должны совпадать");
        Assertions.assertEquals(taskManager.getEpicsList(), loadedTaskManager.getEpicsList(),
                "Эпики в первоначальном менеджере и после загрузки из файла должны совпадать");
        Assertions.assertEquals(taskManager.historyManager.getHistory(),
                loadedTaskManager.historyManager.getHistory(),
                "История просмотров в первоначальном менеджере и после загрузки из файла должна совпадать");
    }

    /**
     * проверка перехвата исключения ManagerSaveException
     */
    @Test
    void shouldCorrectlyInterceptManagerSaveException() {
        Assertions.assertThrows(ManagerSaveException.class, () -> {
            //загрузка из неправильного места должна вызвать IOException, перехващенное ManagerSaveException
            testTaskManagerPath = Paths.get("C:\\some\\error\\path__");
            FileBackedTaskManager loadedTaskManager = FileBackedTaskManager.loadFromFile(testTaskManagerPath);
        });
    }

    /**
     * проверка работы FileBackedTaskManager: добавление задачи
     */
    @Test
    public void addNewTask() {
        super.addNewTask();
    }

    /**
     * проверка работы FileBackedTaskManager: обновление задачи
     */
    @Test
    public void updateTask() {
        super.updateTask();
    }


    /**
     * проверка работы FileBackedTaskManager: удаление задачи
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
     * проверка работы FileBackedTaskManager: добавление эпика
     */
    @Test
    public void addNewEpic() {
        super.addNewEpic();
    }


    /**
     * проверка работы FileBackedTaskManager: обновление эпика
     */
    @Test
    public void updateEpic() {
        super.updateEpic();
    }


    /**
     * проверка работы FileBackedTaskManager: удаление эпика
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
     * проверка работы FileBackedTaskManager: добавление подзадачи
     */
    @Test
    public void addNewSubtask() {
        super.addNewSubtask();
    }

    /**
     * проверка работы FileBackedTaskManager: обновление подзадачи
     */
    @Test
    public void updateSubtask() {
        super.updateSubtask();
    }


    /**
     * проверка работы FileBackedTaskManager: удаление подзадачи
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
     * проверка работы FileBackedTaskManager: проверка удаления всех задач
     */
    @Test
    public void removeAllTasksTest() {
        super.removeAllTasksTest();
    }

    /**
     * проверка работы FileBackedTaskManager: проверка удаления всех подзадач
     */
    @Test
    public void removeAllSubtasksTest() {
        super.removeAllSubtasksTest();
    }

    /**
     * проверка работы FileBackedTaskManager: проверка удаления всех эпиков
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
     * FileBackedTaskManager: Проверка расчета статуса эпика
     */
    @Test
    void shouldCorrectlyUpdateEpicStatus() {
        super.shouldCorrectlyUpdateEpicStatus();
    }

    /**
     * FileBackedTaskManager: Тест на корректность расчёта пересечения интервалов
     */
    @Test
    void shouldCorrectlyValidateIntersectionOfIntervals() {
        super.shouldCorrectlyValidateIntersectionOfIntervals();
    }
}