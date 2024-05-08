package taskmanager;

import org.junit.jupiter.api.Assertions;
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
    private Path testTaskManagerPath;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            testTaskManagerPath =
                    File.createTempFile(String.format("FileBackedTaskManager_%s", UUID.randomUUID()), ".csv").toPath();
            return new FileBackedTaskManager(Managers.getDefaultHistory(), testTaskManagerPath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка создания временного файла");
        }
    }

    @Test
    void shouldCorrectlySaveAndLoadTaskManager() {
        //наполнение задачами
        Task task1 = new Task("Первая задача", "Пример запланированной задачи", Duration.ofMinutes(50),
                LocalDateTime.of(2024, 1, 15, 15, 0, 0));
        Integer idTask1 = taskManager.addTask(task1);
        Epic epic1 = new Epic("Первый эпик", "Первое эпичное описание");
        Integer idEpic1 = taskManager.addEpic(epic1);
        Subtask subtask1 =
                new Subtask("Первая подзадачка", "Запланированная подзадача", TaskStatus.DONE, Duration.ofMinutes(15),
                        LocalDateTime.of(2024, 1, 3, 12, 0, 0), idEpic1);
        Integer idSubtask1 = taskManager.addSubtask(subtask1);
        Task task2 = new Task("Вторая задача", "Пример запланированной задачи", Duration.ofMinutes(10),
                LocalDateTime.of(2023, 12, 1, 9, 0, 0));
        Integer idTask2 = taskManager.addTask(task2);


        //история просмотра задач
        taskManager.getEpicById(idEpic1);
        taskManager.getSubtaskById(idSubtask1);
        taskManager.getTaskById(idTask2);
        taskManager.getTaskById(idTask1);

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
        Assertions.assertEquals(taskManager.getPrioritizedTasks(), loadedTaskManager.getPrioritizedTasks(),
                "Отсортированный список задач должен корректно восстанавливаться из файла");
    }

    /**
     * проверка перехвата исключения ManagerSaveException
     */
    @Test
    void shouldCorrectlyInterceptManagerSaveException() {
        Assertions.assertThrows(ManagerSaveException.class, () -> {
            //загрузка из неправильного места должна вызвать IOException, перехваченное ManagerSaveException
            testTaskManagerPath = Paths.get("C:\\some\\error\\path__");
            FileBackedTaskManager loadedTaskManager = FileBackedTaskManager.loadFromFile(testTaskManagerPath);
        });
    }

}