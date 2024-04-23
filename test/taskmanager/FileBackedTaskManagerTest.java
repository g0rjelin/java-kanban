package taskmanager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmodel.Epic;
import taskmodel.Subtask;
import taskmodel.Task;
import taskmodel.TaskStatus;
import taskmodel.TaskType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.nio.file.Files.readAllLines;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    @Test
    void shouldCorrectlySaveAndLoadTaskManager() throws IOException{
        //создание менеджера задач
        Path testTaskManagerPath = File.createTempFile(String.format("FileBackedTaskManager_%s", UUID.randomUUID()) ,".csv").toPath();
        FileBackedTaskManager initTaskManager = new FileBackedTaskManager(Managers.getDefaultHistory(), testTaskManagerPath);

        //наполнение задачами
        Task task1 = new Task("Первая задача", "Пример запланированной задачи");
        Integer idTask1 = initTaskManager.addTask(task1);
        Epic epic1 = new Epic("Первый эпик", "Первое эпичное описание");
        Integer idEpic1 = initTaskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Первая подзадачка", "Запланированная подзадача", TaskStatus.DONE, idEpic1);
        Integer idSubtask1 = initTaskManager.addSubtask(subtask1);

        //история просмотра задач
        initTaskManager.getEpicById(idEpic1);
        initTaskManager.getSubtaskById(idSubtask1);
        initTaskManager.getSubtaskById(idTask1);

        //загрузка из файла в новый менеджер
        FileBackedTaskManager loadedTaskManager = FileBackedTaskManager.loadFromFile(testTaskManagerPath);

        Assertions.assertEquals(initTaskManager.getTasksList(), loadedTaskManager.getTasksList(),
                "Задачи в первоначальном менеджере и после загрузки из файла должны совпадать");
        Assertions.assertEquals(initTaskManager.getSubtasksList(), loadedTaskManager.getSubtasksList(),
                "Подзадачи в первоначальном менеджере и после загрузки из файла должны совпадать");
        Assertions.assertEquals(initTaskManager.getEpicsList(), loadedTaskManager.getEpicsList(),
                "Эпики в первоначальном менеджере и после загрузки из файла должны совпадать");
        Assertions.assertEquals(initTaskManager.historyManager.getHistory(),
                loadedTaskManager.historyManager.getHistory(),
                "История просмотров в первоначальном менеджере и после загрузки из файла должна совпадать");
    }

}