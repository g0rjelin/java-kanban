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
    Path testTaskManagerPath;
    FileBackedTaskManager taskManager;

    @BeforeEach
    void setUp() throws IOException {
        testTaskManagerPath = File.createTempFile(String.format("FileBackedTaskManager_%s", UUID.randomUUID()) ,".csv").toPath();
        Path testHistoryManagerPath = File.createTempFile(String.format("FileBackedHistoryManager_%s",
                UUID.randomUUID()) ,".csv").toPath();
        taskManager = new FileBackedTaskManager(new FileBackedHistoryManager(testHistoryManagerPath), testTaskManagerPath);
        Task task1 = new Task("Первая задача", "Пример запланированной задачи");
        Integer idTask1 = taskManager.addTask(task1);
        Task task2 = new Task("Вторая задача", "Пример задачи в работе", TaskStatus.IN_PROGRESS);
        Integer idTask2 = taskManager.addTask(task2);
        Epic epic1 = new Epic("Первый эпик", "Первое эпичное описание");
        Integer idEpic1 = taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Первая подзадачка", "Запланированная подзадача", TaskStatus.DONE, idEpic1);
        Integer idSubtask1 = taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Вторая подзадачка", "Подзадача в работе", idEpic1);
        Integer idSubtask2 = taskManager.addSubtask(subtask2);
        Subtask subtask3 = new Subtask("Третья подзадачка", "Подзадача в работе", idEpic1);
        Integer idSubtask3 = taskManager.addSubtask(subtask3);

        Epic epic2 = new Epic("Второй эпик", "Второе эпичное описание");
        Integer idEpic2 = taskManager.addEpic(epic2);
    }

    @Test
    void shouldCorrectlyLoadTaskManagerWithoutData() throws IOException{
        testTaskManagerPath = File.createTempFile(String.format("EmptyFileBackedTaskManager_%s", UUID.randomUUID()) ,".csv").toPath();
        Path historyTestHistoryManagerPath =  File.createTempFile(String.format("EmptyFileBackedHistoryManager_%s",
                UUID.randomUUID()) ,"csv").toPath();
        taskManager = FileBackedTaskManager.loadFromFile(testTaskManagerPath, historyTestHistoryManagerPath);

        Assertions.assertTrue(taskManager.getTasksList().size() == 0
                && taskManager.getEpicsList().size() == 0
                && taskManager.getSubtasksList().size() == 0
                && taskManager.getHistory().size() == 0,
                "После прочтения пустого файла в менеджере не должно быть задач, подзадач, эпиков и истории просмотра"
        );
    }

    @Test
    void shouldCorrectlyLoadTaskManagerWithData() throws IOException{
        Task task = new Task(1,"Task1", "Description task1", TaskStatus.NEW);
        Epic epic = new Epic(2,"Epic2", "Description epic2",new ArrayList<>(List.of(3)));
        Subtask subtask = new Subtask(3, "Sub Task2", "Description sub task3",TaskStatus.DONE, 2);
        testTaskManagerPath = File.createTempFile(String.format("FileBackedTaskManager_%s", UUID.randomUUID()) ,".csv").toPath();
        String[] taskManagerStringArray =
                {"id,type,name,status,description,epic",
                "1,TASK,Task1,NEW,Description task1,",
                "2,EPIC,Epic2,DONE,Description epic2,",
                "3,SUBTASK,Sub Task2,DONE,Description sub task3,2"};
        Files.write(testTaskManagerPath, Arrays.asList(taskManagerStringArray));
        Path historyTestHistoryManagerPath =  File.createTempFile(String.format("HistoryBackedHistoryManager_%s",
                UUID.randomUUID()) ,".csv").toPath();
        String historyString = "1,2,3";
        Files.writeString(historyTestHistoryManagerPath, historyString);

        taskManager = FileBackedTaskManager.loadFromFile(testTaskManagerPath, historyTestHistoryManagerPath);
        Integer[] getHistoryIdList = new Integer[taskManager.getHistory().size()];
        for (int i = 0; i < taskManager.getHistory().size(); i++) {
            getHistoryIdList[i] = taskManager.getHistory().get(i).getId();
        }
        Assertions.assertArrayEquals( new Integer[] {task.getId(), epic.getId(), subtask.getId()}, getHistoryIdList,
                "История просмотров читается в таск менеджер некорректно");
        Assertions.assertTrue(task.equals(taskManager.getTaskById(1)) &&
                        task.getName().equals(taskManager.getTaskById(1).getName())
                && task.getDescription().equals(taskManager.getTaskById(1).getDescription()) && task.getStatus() == taskManager.getTaskById(1).getStatus(),
                "Задача (TASK) читается некорректно из файла");
        Assertions.assertTrue(epic.equals(taskManager.getEpicById(2)) &&
                        epic.getName().equals(taskManager.getEpicById(2).getName())
                        && epic.getDescription().equals(taskManager.getEpicById(2).getDescription()),
                "Эпик (EPIC) читается некорректно из файла");
        Assertions.assertTrue(subtask.equals(taskManager.getSubtaskById(3)) &&
                        subtask.getName().equals(taskManager.getSubtaskById(3).getName())
                        && subtask.getDescription().equals(taskManager.getSubtaskById(3).getDescription())
                        && subtask.getStatus() == taskManager.getSubtaskById(3).getStatus() && subtask.getIdEpic() == taskManager.getSubtaskById(3).getIdEpic(),
                "Подзадача (TASK) читается некорректно из файла");

    }

    @Test
    void shouldCorrectlySaveTaskManagerAfterAdd() throws IOException{
        List<String> strings =  Files.readAllLines(testTaskManagerPath);

        Assertions.assertEquals(FileBackedTaskManager.TASK_CSV_HEADER, strings.get(0), "Первой строкой должна идти \"шапка\"");
        Assertions.assertEquals(taskManager.getTaskById(1).taskToString(), strings.get(1), "Неправильно сохранилась задача после добавления");
        Assertions.assertEquals(taskManager.getEpicById(3).taskToString(), strings.get(3), "Неправильно сохранился эпик после добавления");
        Assertions.assertEquals(taskManager.getSubtaskById(4).taskToString(), strings.get(5), "Неправильно сохранилась подзадача после добавления");
    }

    @Test
    void shouldCorrectlySaveTaskManagerAfterDelete() throws IOException{
        taskManager.deleteTaskById(1);
        taskManager.deleteEpicById(7);
        taskManager.deleteSubtaskById(4);
        List<String> strings =  Files.readAllLines(testTaskManagerPath);

        Assertions.assertEquals(FileBackedTaskManager.TASK_CSV_HEADER, strings.get(0), "Первой строкой должна идти \"шапка\"");
        Assertions.assertEquals(taskManager.getTaskById(2).taskToString(), strings.get(1), "Неправильно сохранилась задача после удаления");
        Assertions.assertEquals(taskManager.getEpicById(3).taskToString(), strings.get(2), "Неправильно сохранился эпик после удаления");
        Assertions.assertEquals(taskManager.getSubtaskById(5).taskToString(), strings.get(3), "Неправильно сохранилась подзадача после удаления");
    }

    @Test
    void shouldCorrectlySaveTaskManagerAfterUpdate() throws IOException{
        Task updTask = new Task(1, "Обновленная первая задача", "Обновление Пример запланированной задачи", TaskStatus.IN_PROGRESS);
        Epic updEpic = new Epic(7, "Обновленный Второй эпик", "Обновление Второе эпичное описание", new ArrayList<>());
        Integer updTaskId = taskManager.updateTask(updTask);
        Integer updEpicId = taskManager.updateEpic(updEpic);
        Subtask updSubtaskId = new Subtask(6,"Обновленная Третья подзадачка", "Обновление, Подзадача в работе", TaskStatus.IN_PROGRESS, updEpicId);
        Integer updSubtask = taskManager.updateSubtask(updSubtaskId);
        List<String> strings =  Files.readAllLines(testTaskManagerPath);

        Assertions.assertEquals(FileBackedTaskManager.TASK_CSV_HEADER, strings.get(0), "Первой строкой должна идти \"шапка\"");
        Assertions.assertEquals(taskManager.getTaskById(updTaskId).taskToString(), strings.get(1), "Неправильно сохранилась задача после обновления");
        Assertions.assertEquals(taskManager.getEpicById(updEpicId).taskToString(), strings.get(4), "Неправильно сохранился эпик после обновления");
        Assertions.assertEquals(taskManager.getSubtaskById(updSubtask).taskToString(), strings.get(7), "Неправильно сохранилась подзадача после обновления");
    }

    @Test
    void shouldCorrectlySaveTaskManagerAfterRemoveAllTasks() throws IOException{
        taskManager.removeAllTasks();

        List<String> strings =  Files.readAllLines(testTaskManagerPath);
        boolean hasTasks = false;
        for (String line : strings) {
            if (line.indexOf(TaskType.TASK.toString()) > 0 && line.indexOf(TaskType.SUBTASK.toString()) == 0) {
                hasTasks = true;
                break;
            }
        }
        Assertions.assertFalse(hasTasks, "После удаления всех задач их не должно быть в файле");
    }

    @Test
    void shouldCorrectlySaveTaskManagerAfterRemoveAllEpics() throws IOException{
        taskManager.removeAllEpics();

        List<String> strings =  Files.readAllLines(testTaskManagerPath);
        boolean hasEpicsAndSubtasks = false;
        for (String line : strings) {
            if (line.indexOf(TaskType.EPIC.toString()) > 0 || line.indexOf(TaskType.SUBTASK.toString()) > 0) {
                hasEpicsAndSubtasks = true;
                break;
            }
        }
        Assertions.assertFalse(hasEpicsAndSubtasks, "После удаления всех эпиков не должно быть в файле эпиков и подзадач");
    }

    @Test
    void shouldCorrectlySaveTaskManagerAfterRemoveAllSubtasks() throws IOException{
        taskManager.removeAllSubtasks();

        List<String> strings =  Files.readAllLines(testTaskManagerPath);
        boolean hasSubtasks = false;
        for (String line : strings) {
            if (line.indexOf(TaskType.SUBTASK.toString()) > 0) {
                hasSubtasks = true;
                break;
            }
        }
        Assertions.assertFalse(hasSubtasks, "После удаления всех подзадач их не должно быть в файле");
    }

}