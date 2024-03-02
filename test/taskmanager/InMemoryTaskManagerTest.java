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

class InMemoryTaskManagerTest {

    TaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = Managers.getDefault();
    }

    /**проверка работы InMemoryTaskManager: добавление задачи*/
    @Test
    public void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", TaskStatus.NEW);
        final int taskId = taskManager.addTask(task);

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasksList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    /**проверка неизменности задачи (по всем полям) при добавлении задачи в менеджер*/
    @Test
    public void shouldTaskFieldsNotBeChangedWhenAddedToTaskManager() {
        Task task = new Task("Test task to be added to manager", "Test task to be added to manager description", TaskStatus.NEW);
        String nameBeforeAdd = task.getName();
        String descriptionBeforeAdd = task.getDescription();
        TaskStatus statusBeforeAdd = task.getStatus();
        int taskId = taskManager.addTask(task);

        Assertions.assertEquals(nameBeforeAdd, taskManager.getTaskById(taskId).getName(), "Добавление задачи в менеджер не должно менять Name задачи");
        Assertions.assertEquals(descriptionBeforeAdd, taskManager.getTaskById(taskId).getDescription(), "Добавление задачи в менеджер не должно менять Description задачи");
        Assertions.assertEquals(statusBeforeAdd, taskManager.getTaskById(taskId).getStatus(), "Добавление задачи в менеджер не должно менять статус задачи");
    }

    /**проверка отсутствия конфликта между задачи с заданным id и сгенерированным id внутри менеджера*/
    @Test
    public void shouldNotConflictBetweenTaskWithSetIdAndGeneratedId() {
        Task taskGen = new Task("Test add to manager without id", "Test add to manager without id description", TaskStatus.NEW);
        int taskGenId = taskManager.addTask(taskGen);
        String nameBeforeAdd = taskGen.getName();
        String descriptionBeforeAdd = taskGen.getDescription();
        TaskStatus statusBeforeAdd = taskGen.getStatus();
        int sizeBeforeAdd = taskManager.getTasksList().size();

        Task taskSet = new Task(taskGenId, "Test add to manager with id", "Test add to manager with id description", TaskStatus.NEW);
        int taskSetId = taskManager.addTask(taskSet);

        Assertions.assertNotEquals(taskGenId, taskSetId, "Добавление задачи с заданным id не должно влиять на задачу с этим же сгенеренным id");
        Assertions.assertEquals(nameBeforeAdd, taskManager.getTaskById(taskGenId).getName(), "Добавление задачи с заданным id не должно менять Name задачи");
        Assertions.assertEquals(descriptionBeforeAdd, taskManager.getTaskById(taskGenId).getDescription(), "Добавление задачи с заданным id не должно менять Description задачи");
        Assertions.assertEquals(statusBeforeAdd, taskManager.getTaskById(taskGenId).getStatus(), "Добавление задачи с заданным id не должно менять статус задачи");
        Assertions.assertNotEquals(sizeBeforeAdd, taskManager.getTasksList().size(), "Добавляемая с существующим id задача не попала в менеджер");
    }

    /**проверка работы InMemoryTaskManager: добавление эпика*/
    @Test
    public void addNewEpic() {
        Epic epic = new Epic("Test addNewEpic", "Epic addNewEpic description");
        final int epicId = taskManager.addEpic(epic);

        final Task savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Эпик без подзадач не в статусе NEW.");

        assertEquals(0, epic.getSubtaskIdList().size(), "В созданном эпике без указания подзадач не должно быть подзадач");

        final List<Epic> epics = taskManager.getEpicsList();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    /**проверка отсутствия конфликта между эпиком с заданным id и сгенерированным id внутри менеджера*/
    @Test
    public void shouldNotConflictBetweenEpicWithSetIdAndGeneratedId() {
        Epic epicGen = new Epic("Test add epic to manager without id", "Test add epic to manager without id description");
        int epicGenId = taskManager.addEpic(epicGen);
        String nameBeforeAdd = epicGen.getName();
        String descriptionBeforeAdd = epicGen.getDescription();
        TaskStatus statusBeforeAdd = epicGen.getStatus();
        int sizeBeforeAdd = taskManager.getEpicsList().size();

        Epic epicSet = new Epic(epicGenId, "Test add epic to manager with id", "Test add epic to manager with id description", new ArrayList<>());
        int epicSetId = taskManager.addEpic(epicSet);

        Assertions.assertNotEquals(epicGenId, epicSetId, "Добавление эпика с заданным id не должно влиять на эпик с этим же сгенеренным id");
        Assertions.assertEquals(nameBeforeAdd, taskManager.getEpicById(epicGenId).getName(), "Добавление эпика с заданным id не должно менять Name эпика");
        Assertions.assertEquals(descriptionBeforeAdd, taskManager.getEpicById(epicGenId).getDescription(), "Добавление эпика с заданным id не должно менять Description эпика");
        Assertions.assertEquals(statusBeforeAdd, taskManager.getEpicById(epicGenId).getStatus(), "Добавление эпика с заданным id не должно менять статус эпика");
        Assertions.assertNotEquals(sizeBeforeAdd, taskManager.getEpicsList().size(), "Добавляемый с существующим id эпик не попал в менеджер");
    }

    /**проверка неизменности эпика (по всем полям) при добавлении задачи в менеджер*/
    @Test
    public void shouldEpicFieldsNotBeChangedWhenAddedToTaskManager() {
        Epic epic = new Epic("Test epic to be added to manager", "Test epic to be added to manager description");
        String nameBeforeAdd = epic.getName();
        String descriptionBeforeAdd = epic.getDescription();
        TaskStatus statusBeforeAdd = epic.getStatus();
        int epicId = taskManager.addEpic(epic);

        Assertions.assertEquals(nameBeforeAdd, taskManager.getEpicById(epicId).getName(), "Добавление эпика в менеджер не должно менять Name эпика");
        Assertions.assertEquals(descriptionBeforeAdd, taskManager.getEpicById(epicId).getDescription(), "Добавление эпика в менеджер не должно менять Description эпика");
        Assertions.assertEquals(statusBeforeAdd, taskManager.getEpicById(epicId).getStatus(), "Добавление эпика в менеджер не должно менять статус эпика");
    }


    /**проверка работы InMemoryTaskManager: добавление подзадачи*/
    @Test
    public void addNewSubtask() {
        Epic epic = new Epic("Test New Epic for Subtask test", "Test New Epic for Subtask test description");
        final int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description",  TaskStatus.NEW, epicId);
        final int subtaskId = taskManager.addSubtask(subtask);

        final Task savedSubtask = taskManager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getSubtasksList();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

    /**проверка отсутствия конфликта между подзадачей с заданным id и сгенерированным id внутри менеджера*/
    @Test
    public void shouldNotConflictBetweenSubtaskWithSetIdAndGeneratedId() {
        Epic epic = new Epic("Test add epic to manager with id", "Test add epic to manager with id description");
        taskManager.addEpic(epic);
        int epicId = epic.getId();
        Subtask subtaskGen = new Subtask("Test add subtask to manager without id", "Test add subtask to manager without id description", TaskStatus.NEW, epicId);
        int subtaskGenId = taskManager.addSubtask(subtaskGen);
        String nameBeforeAdd = subtaskGen.getName();
        String descriptionBeforeAdd = subtaskGen.getDescription();
        TaskStatus statusBeforeAdd = subtaskGen.getStatus();
        int idEpicBeforeAdd = subtaskGen.getIdEpic();
        int sizeBeforeAdd = taskManager.getSubtasksList().size();

        Subtask subtaskSet = new Subtask(subtaskGenId, "Test add subtask to manager with id", "Test add subtask to manager with id description", TaskStatus.IN_PROGRESS, epicId);
        int subtaskSetId = taskManager.addSubtask(subtaskSet);

        Assertions.assertNotEquals(subtaskGenId, subtaskSetId, "Добавление подзадачи с заданным id не должно влиять на подзадачу с этим же сгенеренным id");
        Assertions.assertEquals(nameBeforeAdd, subtaskGen.getName(), "Добавление подзадачи с заданным id не должно менять Name подзадачи");
        Assertions.assertEquals(descriptionBeforeAdd, subtaskGen.getDescription(), "Добавление подзадачи с заданным id не должно менять Description подзадачи");
        Assertions.assertEquals(statusBeforeAdd, subtaskGen.getStatus(), "Добавление подзадачи с заданным id не должно менять статус подзадачи");
        Assertions.assertEquals(idEpicBeforeAdd, subtaskGen.getIdEpic(), "Добавление подзадачи с заданным id не должно менять привязку к эпику");
        Assertions.assertNotEquals(sizeBeforeAdd, taskManager.getSubtasksList().size(), "Добавляемая с существующим id подзадача не попала в менеджер");
    }

    /**проверка неизменности подзадачи (по всем полям) при добавлении подзадачи в менеджер*/
    @Test
    public void shouldSubtaskFieldsNotBeChangedWhenAddedToTaskManager() {
        Epic epic = new Epic("Test epic for subtask to be added to manager with id", "Test epic for subtask to be added to manager with id description");
        taskManager.addEpic(epic);
        int epicId = epic.getId();
        Subtask subtask = new Subtask("Test subtask to be added to manager", "Test subtask to be added to manager description", epicId);
        String nameBeforeAdd = subtask.getName();
        String descriptionBeforeAdd = subtask.getDescription();
        TaskStatus statusBeforeAdd = subtask.getStatus();
        int subtaskId = taskManager.addSubtask(subtask);

        Assertions.assertEquals(nameBeforeAdd, taskManager.getSubtaskById(subtaskId).getName(), "Добавление подзадачи в менеджер не должно менять Name подзадачи");
        Assertions.assertEquals(descriptionBeforeAdd, taskManager.getSubtaskById(subtaskId).getDescription(), "Добавление подзадачи в менеджер не должно менять Description подзадачи");
        Assertions.assertEquals(statusBeforeAdd, taskManager.getSubtaskById(subtaskId).getStatus(), "Добавление подзадачи в менеджер не должно менять статус подзадачи");
        Assertions.assertEquals(epicId, taskManager.getSubtaskById(subtaskId).getIdEpic(), "Добавление подзадачи в менеджер не должно менять эпик подзадачи");
    }


    /**проверка, что объект Epic нельзя добавить в самого себя в виде подзадачи*/
    @Test
    public void shouldNotAddEpicIntoEpicAsSubtask() {
        Epic epic = new Epic("Эпик: тест добавления в себя в виде подзадачи", "Описание: тест добавления в себя в виде подзадачи");
        taskManager.addEpic(epic);
        Assertions.assertThrows(NullPointerException.class, () -> {
            taskManager.addSubtask(taskManager.getSubtaskById(epic.getId()));
        });

    }

    /**проверка, что объект Subtask нельзя сделать своим же эпиком (через добавление)*/
    @Test
    public void shouldNotAddSubtaskAsItsEpic() {
        Epic epic1 = new Epic("Эпик: начальный эпик подзадачи", "Описание: начальный эпик подзадачи");
        final int epicId = taskManager.addEpic(epic1);

        Subtask subtask = new Subtask("Подзадача для проверки указания ее своим эпиком при добавлении",
                "Описание: Подзадача для проверки указания ее своим эпиком при добавлении",  TaskStatus.NEW, epicId);
        final int subtaskId = taskManager.addSubtask(subtask);

        Subtask newSubtask = new Subtask("Подзадача для проверки указания ее своим эпиком при добавлении",
                "Описание: Подзадача для проверки указания ее своим эпиком при добавлении",  TaskStatus.NEW, subtaskId);

        Assertions.assertThrows(NullPointerException.class, () -> {
            taskManager.addSubtask(newSubtask);
        }, "При добавлении подзадача не должна быть указана в качестве эпика другой подзадачи");

    }

    /**проверка, что объект Subtask нельзя сделать своим же эпиком (через обновление)*/
    @Test
    public void shouldNotUpdateSubtaskAsItsEpic() {
        Epic epic1 = new Epic("Эпик: начальный эпик подзадачи", "Описание: начальный эпик подзадачи");
        final int epicId = taskManager.addEpic(epic1);

        Subtask subtask = new Subtask("Подзадача для проверки указания ее своим эпиком при обновлении",
                "Описание: Подзадача для проверки указания ее своим эпиком при обновлении",  TaskStatus.NEW, epicId);
        final int subtaskId = taskManager.addSubtask(subtask);

        Subtask newSubtask = new Subtask(subtaskId, "Подзадача для проверки указания ее своим эпиком при обновлении",
                "Описание: Подзадача для проверки указания ее своим эпиком при обновлении",  TaskStatus.NEW, subtaskId);

        Assertions.assertThrows(NullPointerException.class, () -> {
            taskManager.updateSubtask(newSubtask);
        }, "При обновлении подзадача не должна быть указана в качестве эпика другой подзадачи");

    }
}