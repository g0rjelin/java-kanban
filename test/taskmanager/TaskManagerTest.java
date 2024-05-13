package taskmanager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmodel.Epic;
import taskmodel.Subtask;
import taskmodel.Task;
import taskmodel.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Task task;
    protected Epic epic;

    protected abstract T createTaskManager();

    @BeforeEach
    public void setUp() {
        taskManager = createTaskManager();
        task = new Task("Test TaskManager task",
                "Test TaskManager task description",
                TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.of(2024, 2, 24, 17, 5, 30));
        epic = new Epic("Test TaskManager epic",
                "Test TaskManager epic description");
    }

    /**
     * проверка работы TaskManager: добавление задачи
     */
    @Test
    public void addNewTask() {
        final int taskId = taskManager.addTask(task);

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasksList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач: задача не добавилась.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    /**
     * проверка работы TaskManager: обновление задачи
     */
    @Test
    public void updateTask() {
        final int taskId = taskManager.addTask(task);
        Task updTask = new Task(taskId, "Test InMemoryTaskManager Updated task",
                "Test Updated task description", TaskStatus.NEW, Duration.ofMinutes(20), LocalDateTime.now());

        taskManager.updateTask(updTask);

        final List<Task> tasks = taskManager.getTasksList();
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(updTask, tasks.get(0), "Задачи не совпадают.");
    }


    /**
     * проверка работы: удаление задачи
     */
    @Test
    public void deleteTask() {
        final int taskId = taskManager.addTask(task);

        taskManager.deleteTaskById(taskId);
        final List<Task> tasks = taskManager.getTasksList();
        assertEquals(0, tasks.size(), "Неверное количество задач: задача не удалилась.");
    }


    /**
     * проверка неизменности задачи (по всем полям) при добавлении задачи в менеджер
     */
    @Test
    public void shouldTaskFieldsNotBeChangedWhenAddedToTaskManager() {
        Task task = new Task("Test task to be added to manager", "Test task to be added to manager description",
                TaskStatus.NEW, Duration.ofMinutes(15));
        String nameBeforeAdd = task.getName();
        String descriptionBeforeAdd = task.getDescription();
        TaskStatus statusBeforeAdd = task.getStatus();
        int taskId = taskManager.addTask(task);

        Assertions.assertEquals(nameBeforeAdd, taskManager.getTaskById(taskId).getName(),
                "Добавление задачи в менеджер не должно менять Name задачи");
        Assertions.assertEquals(descriptionBeforeAdd, taskManager.getTaskById(taskId).getDescription(),
                "Добавление задачи в менеджер не должно менять Description задачи");
        Assertions.assertEquals(statusBeforeAdd, taskManager.getTaskById(taskId).getStatus(),
                "Добавление задачи в менеджер не должно менять статус задачи");
    }


    /**
     * проверка отсутствия конфликта между задачи с заданным id и сгенерированным id внутри менеджера
     */
    @Test
    public void shouldNotConflictBetweenTaskWithSetIdAndGeneratedId(){
        int taskGenId = taskManager.addTask(task);
        String nameBeforeAdd = task.getName();
        String descriptionBeforeAdd = task.getDescription();
        TaskStatus statusBeforeAdd = task.getStatus();
        int sizeBeforeAdd = taskManager.getTasksList().size();

        Task taskSet = new Task(taskGenId, "Test add to manager with id", "Test add to manager with id description",
                TaskStatus.NEW, Duration.ofMinutes(20));
        int taskSetId = taskManager.addTask(taskSet);

        Assertions.assertNotEquals(taskGenId, taskSetId,
                "Добавление задачи с заданным id не должно влиять на задачу с этим же сгенеренным id");
        Assertions.assertEquals(nameBeforeAdd, taskManager.getTaskById(taskGenId).getName(),
                "Добавление задачи с заданным id не должно менять Name задачи");
        Assertions.assertEquals(descriptionBeforeAdd, taskManager.getTaskById(taskGenId).getDescription(),
                "Добавление задачи с заданным id не должно менять Description задачи");
        Assertions.assertEquals(statusBeforeAdd, taskManager.getTaskById(taskGenId).getStatus(),
                "Добавление задачи с заданным id не должно менять статус задачи");
        Assertions.assertNotEquals(sizeBeforeAdd, taskManager.getTasksList().size(),
                "Добавляемая с существующим id задача не попала в менеджер");
    }


    /**
     * проверка работы TaskManager: добавление эпика
     */
    @Test
    public void addNewEpic() {
        final int epicId = taskManager.addEpic(epic);

        final Task savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Эпик без подзадач не в статусе NEW.");

        assertEquals(0, epic.getSubtaskIdList().size(),
                "В созданном эпике без указания подзадач не должно быть подзадач");

        final List<Epic> epics = taskManager.getEpicsList();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков: эпик не добавился");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }


    /**
     * проверка работы TaskManager: обновление эпика
     */
    @Test
    public void updateEpic() {
        final int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test InMemory subtask", "Test InMemory subtask description",
                TaskStatus.NEW, Duration.ofMinutes(180), epicId);
        final int subtaskId = taskManager.addSubtask(subtask);
        Epic updEpic = new Epic(epicId, "Test InMemoryTaskManager Updated epic",
                "Test Updated epic description", epic.getDuration(), epic.getSubtaskIdList());

        taskManager.updateEpic(updEpic);

        final List<Epic> epics = taskManager.getEpicsList();
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(updEpic, epics.get(0), "Эпики не совпадают.");
        assertArrayEquals(epic.getSubtaskIdList().toArray(), epics.get(0).getSubtaskIdList().toArray(),
                "Подзадачи не совпадают");
    }


    /**
     * проверка работы TaskManager: удаление эпика
     */
    @Test
    public void deleteEpic() {
        final int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test InMemory subtask", "Test InMemory subtask description",
                TaskStatus.NEW, Duration.ofDays(7), epicId);
        final int subtaskId = taskManager.addSubtask(subtask);
        Epic updEpic = new Epic(epicId, "Test InMemoryTaskManager Updated epic",
                "Test Updated epic description", epic.getDuration(), epic.getSubtaskIdList());

        taskManager.deleteEpicById(epicId);

        final List<Epic> epics = taskManager.getEpicsList();
        assertEquals(0, epics.size(), "Неверное количество эпиков: эпик не удалился");

        final List<Subtask> subtasks = taskManager.getSubtasksList();
        assertEquals(0, subtasks.size(), "При удалении эпика не удалилась подзадача эпика.");
    }

    /**
     * проверка отсутствия конфликта между эпиком с заданным id и сгенерированным id внутри менеджера
     */
    @Test
    public void shouldNotConflictBetweenEpicWithSetIdAndGeneratedId() {
        int epicGenId = taskManager.addEpic(epic);
        String nameBeforeAdd = epic.getName();
        String descriptionBeforeAdd = epic.getDescription();
        TaskStatus statusBeforeAdd = epic.getStatus();
        int sizeBeforeAdd = taskManager.getEpicsList().size();
        Duration epicDurationBeforeAdd = epic.getDuration();

        Epic epicSet =
                new Epic(epicGenId, "Test add epic to manager with id", "Test add epic to manager with id description",
                        Duration.ofMinutes(100),
                        new ArrayList<>());
        int epicSetId = taskManager.addEpic(epicSet);

        Assertions.assertNotEquals(epicGenId, epicSetId,
                "Добавление эпика с заданным id не должно влиять на эпик с этим же сгенеренным id");
        Assertions.assertEquals(nameBeforeAdd, taskManager.getEpicById(epicGenId).getName(),
                "Добавление эпика с заданным id не должно менять Name эпика");
        Assertions.assertEquals(descriptionBeforeAdd, taskManager.getEpicById(epicGenId).getDescription(),
                "Добавление эпика с заданным id не должно менять Description эпика");
        Assertions.assertEquals(statusBeforeAdd, taskManager.getEpicById(epicGenId).getStatus(),
                "Добавление эпика с заданным id не должно менять статус эпика");
        Assertions.assertNotEquals(sizeBeforeAdd, taskManager.getEpicsList().size(),
                "Добавляемый с существующим id эпик не попал в менеджер");
    }


    /**
     * проверка работы TaskManager: добавление подзадачи
     */
    @Test
    public void addNewSubtask() {
        final int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description",
                TaskStatus.NEW, Duration.ofMinutes(360), epicId);
        final int subtaskId = taskManager.addSubtask(subtask);

        final Task savedSubtask = taskManager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getSubtasksList();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

    /**
     * проверка работы TaskManager: обновление подзадачи
     */
    @Test
    public void updateSubtask() {
        final int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test InMemoryTaskManager subtask",
                "Test InMemoryTaskManager subtask description",
                TaskStatus.NEW, Duration.ofMinutes(30), epicId);
        final int subtaskId = taskManager.addSubtask(subtask);

        Subtask updSubtask = new Subtask(subtaskId, "Test InMemoryTaskManager Updated subtask",
                "Test Updated subtask description", TaskStatus.NEW, Duration.ofMinutes(30), epicId);

        taskManager.updateSubtask(updSubtask);

        final List<Subtask> subtasks = taskManager.getSubtasksList();
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(updSubtask, subtasks.get(0), "Подзадачи не совпадают.");
    }


    /**
     * проверка работы TaskManager: удаление подзадачи
     */
    @Test
    public void deleteSubtask() {
        final int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test InMemoryTaskManager subtask",
                "Test InMemoryTaskManager subtask description",
                TaskStatus.NEW, Duration.ofMinutes(40), epicId);
        final int subtaskId = taskManager.addSubtask(subtask);
        subtask = taskManager.getSubtaskById(subtaskId);

        taskManager.deleteSubtaskById(subtaskId);
        final List<Subtask> subtasks = taskManager.getSubtasksList();
        assertEquals(0, subtasks.size(), "Неверное количество подзадач: подзадача не удалилась.");
        assertEquals(0, epic.getSubtaskIdList().size(), "В эпике осталась ссылка (id) на подзадачу");
        assertEquals(0, subtask.getId(), "Удаляемые подзадачи не должны хранить внутри себя старые id");
    }

    /**
     * проверка отсутствия конфликта между подзадачей с заданным id и сгенерированным id внутри менеджера
     */
    @Test
    public void shouldNotConflictBetweenSubtaskWithSetIdAndGeneratedId() {
        Epic epic = new Epic("Test add epic to manager with id", "Test add epic to manager with id description");
        taskManager.addEpic(epic);
        int epicId = epic.getId();
        Subtask subtaskGen = new Subtask("Test add subtask to manager without id",
                "Test add subtask to manager without id description", TaskStatus.NEW, Duration.ofMinutes(60), epicId);
        int subtaskGenId = taskManager.addSubtask(subtaskGen);
        String nameBeforeAdd = subtaskGen.getName();
        String descriptionBeforeAdd = subtaskGen.getDescription();
        TaskStatus statusBeforeAdd = subtaskGen.getStatus();
        int idEpicBeforeAdd = subtaskGen.getIdEpic();
        int sizeBeforeAdd = taskManager.getSubtasksList().size();

        Subtask subtaskSet = new Subtask(subtaskGenId, "Test add subtask to manager with id",
                "Test add subtask to manager with id description", TaskStatus.IN_PROGRESS, Duration.ofMinutes(10),
                epicId);
        int subtaskSetId = taskManager.addSubtask(subtaskSet);

        Assertions.assertNotEquals(subtaskGenId, subtaskSetId,
                "Добавление подзадачи с заданным id не должно влиять на подзадачу с этим же сгенеренным id");
        Assertions.assertEquals(nameBeforeAdd, subtaskGen.getName(),
                "Добавление подзадачи с заданным id не должно менять Name подзадачи");
        Assertions.assertEquals(descriptionBeforeAdd, subtaskGen.getDescription(),
                "Добавление подзадачи с заданным id не должно менять Description подзадачи");
        Assertions.assertEquals(statusBeforeAdd, subtaskGen.getStatus(),
                "Добавление подзадачи с заданным id не должно менять статус подзадачи");
        Assertions.assertEquals(idEpicBeforeAdd, subtaskGen.getIdEpic(),
                "Добавление подзадачи с заданным id не должно менять привязку к эпику");
        Assertions.assertNotEquals(sizeBeforeAdd, taskManager.getSubtasksList().size(),
                "Добавляемая с существующим id подзадача не попала в менеджер");
    }

    /**
     * проверка, что объект Epic нельзя добавить в самого себя в виде подзадачи
     */
    @Test
    public void shouldNotAddEpicIntoEpicAsSubtask() {
        Epic epic = new Epic("Эпик: тест добавления в себя в виде подзадачи",
                "Описание: тест добавления в себя в виде подзадачи");
        taskManager.addEpic(epic);
        Assertions.assertThrows(NotFoundException.class,
                () -> taskManager.addSubtask(taskManager.getSubtaskById(epic.getId())));
    }

    /**
     * проверка, что объект Subtask нельзя сделать своим же эпиком (через добавление)
     */
    @Test
    public void shouldNotAddSubtaskAsItsEpic() {
        Epic epic1 = new Epic("Эпик: начальный эпик подзадачи", "Описание: начальный эпик подзадачи");
        final int epicId = taskManager.addEpic(epic1);

        Subtask subtask = new Subtask("Подзадача для проверки указания ее своим эпиком при добавлении",
                "Описание: Подзадача для проверки указания ее своим эпиком при добавлении", TaskStatus.NEW,
                Duration.ofMinutes(15), epicId);
        final int subtaskId = taskManager.addSubtask(subtask);

        Subtask newSubtask = new Subtask("Подзадача для проверки указания ее своим эпиком при добавлении",
                "Описание: Подзадача для проверки указания ее своим эпиком при добавлении", TaskStatus.NEW,
                Duration.ofMinutes(30), subtaskId);

        Assertions.assertThrows(NotFoundException.class, () -> taskManager.addSubtask(newSubtask),
                "При добавлении подзадача не должна быть указана в качестве эпика другой подзадачи");

    }

    /**
     * проверка, что объект Subtask нельзя сделать своим же эпиком (через обновление)
     */
    @Test
    public void shouldNotUpdateSubtaskAsItsEpic() {
        Epic epic1 = new Epic("Эпик: начальный эпик подзадачи", "Описание: начальный эпик подзадачи");
        final int epicId = taskManager.addEpic(epic1);

        Subtask subtask = new Subtask("Подзадача для проверки указания ее своим эпиком при обновлении",
                "Описание: Подзадача для проверки указания ее своим эпиком при обновлении", TaskStatus.NEW,
                Duration.ofMinutes(15), epicId);
        final int subtaskId = taskManager.addSubtask(subtask);

        Subtask newSubtask = new Subtask(subtaskId, "Подзадача для проверки указания ее своим эпиком при обновлении",
                "Описание: Подзадача для проверки указания ее своим эпиком при обновлении", TaskStatus.NEW,
                Duration.ofMinutes(30), subtaskId);

        Assertions.assertThrows(NullPointerException.class, () -> taskManager.updateSubtask(newSubtask),
                "При обновлении подзадача не должна быть указана в качестве эпика другой подзадачи");

    }

    /**
     * проверка работы TaskManager: проверка удаления всех задач
     */
    @Test
    public void removeAllTasksTest() {
        final int taskId = taskManager.addTask(task);

        taskManager.removeAllTasks();

        final List<Task> tasks = taskManager.getTasksList();
        assertEquals(0, tasks.size(), "Неверное количество задач: задач не очистился.");
    }

    /**
     * проверка работы TaskManager: проверка удаления всех подзадач
     */
    @Test
    public void removeAllSubtasksTest() {
        final int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test InMemoryTaskManager subtask",
                "Test InMemoryTaskManager subtask description",
                TaskStatus.NEW, Duration.ofMinutes(40), epicId);
        final int subtaskId = taskManager.addSubtask(subtask);

        taskManager.removeAllSubtasks();

        final List<Subtask> subtasks = taskManager.getSubtasksList();
        assertEquals(0, subtasks.size(), "Неверное количество подзадач: список подзадач не очистился.");
    }

    /**
     * проверка работы InMemoryTaskManager: проверка удаления всех эпиков
     */
    @Test
    public void removeAllEpicsTest() {
        final int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test InMemoryTaskManager subtask",
                "Test InMemoryTaskManager subtask description",
                TaskStatus.NEW, Duration.ofMinutes(50), epicId);
        final int subtaskId = taskManager.addSubtask(subtask);

        taskManager.removeAllEpics();

        final List<Epic> epics = taskManager.getEpicsList();
        assertEquals(0, epics.size(), "Неверное количество эпиков: список эпиков не очистился");

        final List<Subtask> subtasks = taskManager.getSubtasksList();
        assertEquals(0, subtasks.size(), "Все подзадачи при удалении эпиков должны удалиться");
    }

    /**
     * задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных
     */
    @Test
    public void shouldPreserveTaskPrevVersionInHistory() {
        String initName = "Test task Version 1";
        String initDescription = "Test task Version 1 Description";
        TaskStatus initTaskStatus = TaskStatus.NEW;
        Duration initDuration = Duration.ofMinutes(30);
        Task task = new Task(initName, initDescription, initTaskStatus, initDuration);
        final int taskId = taskManager.addTask(task);

        //обращение к задаче для внесения е в историю
        taskManager.getTaskById(taskId);
        //внесение изменений в задачу
        String changedTaskName = "Test task Version 2";
        String changedTaskDescription = "Test task Version 2 Description";
        TaskStatus changedTaskStatus = TaskStatus.IN_PROGRESS;
        Duration changedDuration = Duration.ofMinutes(45);
        Task changedTask =
                new Task(task.getId(), changedTaskName, changedTaskDescription, changedTaskStatus, changedDuration);
        taskManager.updateTask(changedTask);

        Assertions.assertEquals(initName, taskManager.getHistory().get(0).getName(),
                "В истории должно остаться первоначальное наименование задачи!");
        Assertions.assertEquals(initDescription, taskManager.getHistory().get(0).getDescription(),
                "В истории должно остаться первоначальное описание задачи!");
        Assertions.assertEquals(initTaskStatus, taskManager.getHistory().get(0).getStatus(),
                "В истории должен остаться первоначальный статус задачи!");
        Assertions.assertEquals(initDuration, taskManager.getHistory().get(0).getDuration(),
                "В истории должен остаться первоначальная длительность задачи!");

        for (Task curTask : taskManager.getHistory()) {
            if (curTask.getId() == taskId) {
                Assertions.assertEquals(initName, curTask.getName(),
                        "В истории должно храниться наименование из версии задачи на момент обращения к ней по Id!");
                Assertions.assertEquals(initDescription, curTask.getDescription(),
                        "В истории должно храниться описание из версии задачи на момент обращения к ней по Id!");
                Assertions.assertEquals(initTaskStatus, curTask.getStatus(),
                        "В истории должен храниться статус из версии задачи на момент обращения к ней по Id!");
                Assertions.assertEquals(initDuration, curTask.getDuration(),
                        "В истории должна храниться длительность задачи из версии задачи на момент обращения к ней по Id!");
                break;
            }
        }
        //обращение к задаче после внесения ее в историю
        taskManager.getTaskById(taskId);

        for (Task curTask : taskManager.getHistory()) {
            if (task.getId() == taskId) {
                Assertions.assertEquals(changedTaskName, curTask.getName(),
                        "В истории не должно остаться первоначальное наименование задачи после обращения к обновленной задачи!");
                Assertions.assertEquals(changedTaskDescription, curTask.getDescription(),
                        "В истории не должно остаться первоначальное описание задачи после обращения к обновленной задачи!");
                Assertions.assertEquals(changedTaskStatus, curTask.getStatus(),
                        "В истории не должен остаться первоначальный статус задачи после обращения к обновленной задачи!");
                break;
            }
        }

    }

    /**
     * эпики, добавляемые в HistoryManager, сохраняют предыдущую версию эпика и её данных
     */
    @Test
    public void shouldPreserveEpicPrevVersionInHistory() {
        String initName = "Test epic Version 1";
        String initDescription = "Test epic Version 1 Description";
        Epic epic = new Epic(initName, initDescription);
        int epicId = taskManager.addEpic(epic);
        //обращение к задачам для внесения их в историю
        taskManager.getEpicById(epicId);
        Subtask subtask =
                new Subtask("Test subtask1", "Test subtask1 Description", TaskStatus.NEW, Duration.ofMinutes(15),
                        epic.getId());
        int subtaskId = taskManager.addSubtask(subtask);
        TaskStatus initEpicStatus = epic.getStatus();
        Duration initEpicDuration = epic.getDuration();


        //внесение изменений в задачи
        String changedEpicName = "Test epic Version 2";
        String changedEpicDescription = "Test epic Version 2 Description";
        Subtask subtaskAdditional =
                new Subtask("Test subtask2", "Test subtask2 Description", TaskStatus.DONE, Duration.ofMinutes(20),
                        epic.getId());
        Epic changedEpic = new Epic(epic.getId(), changedEpicName, changedEpicDescription, epic.getDuration(),
                epic.getSubtaskIdList());
        taskManager.updateEpic(changedEpic);
        taskManager.addSubtask(subtaskAdditional);
        Subtask changedSubtask =
                new Subtask(subtask.getId(), "Test subtask Version 2", "Test subtask Version 2 Description",
                        TaskStatus.DONE, Duration.ofMinutes(30), subtask.getIdEpic());
        taskManager.updateSubtask(changedSubtask); //изменение приведет к смене статуса и длительности эпика

        for (Task task : taskManager.getHistory()) {
            if (task.getId() == epicId) {
                Assertions.assertEquals(initName, task.getName(),
                        "В истории должно храниться наименование из версии эпика на момент обращения к нему по Id!");
                Assertions.assertEquals(initDescription, task.getDescription(),
                        "В истории должно храниться описание из версии эпика на момент обращения к нему по Id!");
                Assertions.assertEquals(initEpicStatus, task.getStatus(),
                        "В истории должен храниться статус из версии эпика на момент обращения к нему по Id!!");
                Assertions.assertEquals(initEpicDuration, task.getDuration(),
                        "В истории должна храниться длительность эпика из версии эпика на момент обращения к нему по Id!");
                break;
            }
        }

        //обращение к эпику после внесения изменений в историю
        taskManager.getEpicById(epicId);

        for (Task task : taskManager.getHistory()) {
            if (task.getId() == epicId) {
                Assertions.assertEquals(changedEpicName, task.getName(),
                        "В истории не должно остаться первоначальное наименование эпика после обращения к обновленному эпику!");
                Assertions.assertEquals(changedEpicDescription, task.getDescription(),
                        "В истории не должно остаться первоначальное описание эпика после обращения к обновленному эпику!");
                Assertions.assertEquals(TaskStatus.DONE, task.getStatus(),
                        "В истории не должен остаться первоначальный статус эпика после обращения к обновленному эпику!");
                Assertions.assertEquals(changedSubtask.getDuration().plus(subtaskAdditional.getDuration()),
                        task.getDuration(),
                        "В истории не должен остаться первоначальная длительность эпика после обращения к обновленному эпику!");

                break;
            }
        }

    }

    /**
     * подзадачи, добавляемые в HistoryManager, сохраняют предыдущую версию подзадачи и её данных
     */
    @Test
    public void shouldPreserveSubtaskPrevVersionInHistory() {
        Epic epic = new Epic("Epic Name for Subtask Test", "Epic Description for Subtask Test");
        int epicId = taskManager.addEpic(epic);
        String initName = "Test subtask Version 1";
        String initDescription = "Test subtask Version 1 Description";
        TaskStatus initSubtaskStatus = TaskStatus.NEW;
        Duration initDuration = Duration.ofMinutes(15);
        Subtask subtask = new Subtask(initName, initDescription, initSubtaskStatus, initDuration, epicId);
        final int subtaskId = taskManager.addSubtask(subtask);

        //обращение к задаче для внесения в историю
        taskManager.getSubtaskById(subtaskId);
        //внесение изменений в подзадачу
        Epic newEpic = new Epic("NewEpic Name for Subtask Test", "NewEpic Description for Subtask Test");
        int newEpicId = taskManager.addEpic(newEpic);
        String changedSubtaskName = "Test subtask Version 2";
        String changedSubtaskDescription = "Test subtask Version 2 Description";
        TaskStatus changedSubtaskStatus = TaskStatus.IN_PROGRESS;
        Duration changedDuration = Duration.ofMinutes(30);
        Subtask changedSubtask =
                new Subtask(subtask.getId(), changedSubtaskName, changedSubtaskDescription, changedSubtaskStatus,
                        changedDuration,
                        newEpicId);
        taskManager.updateSubtask(changedSubtask);

        for (Task task : taskManager.getHistory()) {
            if (task.getId() == subtaskId) {
                Assertions.assertEquals(initName, task.getName(),
                        "В истории должно храниться наименование из версии подзадачи на момент обращения к ней по Id!");
                Assertions.assertEquals(initDescription, task.getDescription(),
                        "В истории должно храниться описание из версии подзадачи на момент обращения к ней по Id!");
                Assertions.assertEquals(initSubtaskStatus, task.getStatus(),
                        "В истории должен храниться статус из версии подзадачи на момент обращения к ней по Id!");
                Subtask subtaskFromHistory = (Subtask) task; //(Subtask) taskManager.getHistory().get(subtaskId);
                Assertions.assertEquals(epicId, subtaskFromHistory.getIdEpic(),
                        "В истории должен храниться эпик подзадачи из версии подзадачи на момент обращения к ней по Id!");
                Assertions.assertEquals(initDuration, task.getDuration(),
                        "В истории должна храниться длительность подзадачи из версии подзадачи на момент обращения к ней по Id!");
                break;
            }
        }

        //обращение к задаче для внесения ее в историю после изменений
        taskManager.getSubtaskById(subtaskId);
        for (Task task : taskManager.getHistory()) {
            if (task.getId() == subtaskId) {
                Assertions.assertEquals(changedSubtaskName, task.getName(),
                        "В истории не должно остаться первоначальное наименование подзадачи после обращения к обновленной подзадаче!");
                Assertions.assertEquals(changedSubtaskDescription, task.getDescription(),
                        "В истории не должно остаться первоначальное описание подзадачи после обращения к обновленной подзадаче!");
                Assertions.assertEquals(changedSubtaskStatus, task.getStatus(),
                        "В истории не должен остаться первоначальный статус подзадачи после обращения к обновленной подзадаче!");
                Subtask subtaskFromHistory = (Subtask) task;
                Assertions.assertEquals(newEpicId, subtaskFromHistory.getIdEpic(),
                        "В истории не должен остаться первоначальный эпик подзадачи после обращения к обновленной подзадаче!");
                Assertions.assertEquals(changedDuration, task.getDuration(),
                        "В истории не должна остаться первоначальная длительность подзадачи после обращения к обновленной подзадаче!");
                break;
            }
        }
    }

    /**
     * Проверка правильной работы связного списка по анализу результата возврата истории
     * с разной последовательностью обращения к задачам
     */
    @Test
    public void shouldLinkLastWorkCorrectly() {
        //подготовка данных
        Task task1 = new Task(1, "TestHistory check linkLast №" + 1,
                "TestHistory check linkLast №" + 1 + " description", TaskStatus.NEW, Duration.ofMinutes(10));
        Task task2 = new Task(2, "TestHistory check linkLast №" + 2,
                "TestHistory check linkLast №" + 2 + " description", TaskStatus.NEW, Duration.ofMinutes(20));
        Task task3 = new Task(3, "TestHistory check linkLast №" + 3,
                "TestHistory check linkLast №" + 3 + " description", TaskStatus.NEW, Duration.ofMinutes(30));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        //один элемент (head=tail)
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        Assertions.assertEquals(1, taskManager.getHistory().size(),
                "В истории должен отображаться только последний просмотр");

        taskManager.getHistoryManager().remove(1);
        Assertions.assertEquals(0, taskManager.getHistory().size(),
                "В истории после удаления единственного просмотра не должно быть элементов в списке");

        //несколько элементов
        taskManager.getTaskById(3);
        taskManager.getTaskById(2);
        taskManager.getTaskById(1);
        Assertions.assertTrue(taskManager.getHistory().get(0).getId() == 3 &&
                        taskManager.getHistory().get(1).getId() == 2 &&
                        taskManager.getHistory().get(2).getId() == 1
                , "Последовательность задач в истории должна соответствовать последовательности обращения к задачам");

        taskManager.getHistoryManager().remove(2);
        Assertions.assertTrue(taskManager.getHistory().get(0).getId() == 3 &&
                        taskManager.getHistory().get(1).getId() == 1
                , "Удаление из списка просмотра должно работать корректно");

        taskManager.deleteTaskById(3);
        Assertions.assertTrue(taskManager.getHistory().get(0).getId() == 1 &&
                        taskManager.getHistory().size() == 1,
                "Удаление задачи должно приводить к удалению ее из истории просмотров");

    }

    /**
     * Проверка расчета статуса эпика
     */
    @Test
    void shouldCorrectlyUpdateEpicStatus() {
        Integer epicId = taskManager.addEpic(epic);

        //Все подзадачи со статусом NEW
        Subtask subtask1 =
                new Subtask("Test subtask1", "Test subtask1 Description", TaskStatus.NEW, Duration.ofMinutes(15),
                        epicId);
        Subtask subtask2 =
                new Subtask("Test subtask2", "Test subtask2 Description", TaskStatus.NEW, Duration.ofMinutes(15),
                        epicId);
        Subtask subtask3 =
                new Subtask("Test subtask3", "Test subtask3 Description", TaskStatus.NEW, Duration.ofMinutes(15),
                        epicId);
        taskManager.removeAllSubtasks();
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        Assertions.assertEquals(TaskStatus.NEW, epic.getStatus(),
                "У эпика со всеми подзадачами в статусе NEW должен быть статус NEW");

        //Все подзадачи со статусом DONE
        subtask1 =
                new Subtask("Test subtask1", "Test subtask1 Description", TaskStatus.DONE, Duration.ofMinutes(15),
                        epicId);
        subtask2 =
                new Subtask("Test subtask2", "Test subtask2 Description", TaskStatus.DONE, Duration.ofMinutes(15),
                        epicId);
        subtask3 =
                new Subtask("Test subtask3", "Test subtask3 Description", TaskStatus.DONE, Duration.ofMinutes(15),
                        epicId);
        taskManager.removeAllSubtasks();
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        Assertions.assertEquals(TaskStatus.DONE, epic.getStatus(),
                "У эпика со всеми подзадачами в статусе DONE должен быть статус DONE");

        //Все подзадачи со статусами NEW и DONE
        subtask1 =
                new Subtask("Test subtask1", "Test subtask1 Description", TaskStatus.DONE, Duration.ofMinutes(15),
                        epicId);
        subtask2 =
                new Subtask("Test subtask2", "Test subtask2 Description", TaskStatus.NEW, Duration.ofMinutes(15),
                        epicId);
        subtask3 =
                new Subtask("Test subtask3", "Test subtask3 Description", TaskStatus.DONE, Duration.ofMinutes(15),
                        epicId);
        taskManager.removeAllSubtasks();
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(),
                "У эпика с подзадачами в статусах NEW и DONE должен быть статус IN_PROGRESS");

        //Все подзадачи со статусами NEW и DONE
        subtask1 =
                new Subtask("Test subtask1", "Test subtask1 Description", TaskStatus.DONE, Duration.ofMinutes(15),
                        epicId);
        subtask2 =
                new Subtask("Test subtask2", "Test subtask2 Description", TaskStatus.NEW, Duration.ofMinutes(15),
                        epicId);
        subtask3 =
                new Subtask("Test subtask3", "Test subtask3 Description", TaskStatus.IN_PROGRESS,
                        Duration.ofMinutes(15),
                        epicId);
        taskManager.removeAllSubtasks();
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(),
                "У эпика с подзадачами в статусе IN_PROGRESS должен быть статус IN_PROGRESS");
    }

    /**
     * Тест на корректность расчёта пересечения интервалов
     */
    @Test
    void shouldCorrectlyValidateIntersectionOfIntervals() {
        //кейс 1: не пересекаются
        Task task1 = new Task(1, "Test intersection 1",
                "Test intersection 1 description", TaskStatus.NEW, Duration.ofDays(3),
                LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        Task task2 = new Task(2, "Test intersection 2",
                "Test intersection 2 description", TaskStatus.NEW, Duration.ofDays(1),
                LocalDateTime.of(2024, 1, 6, 0, 0, 0));
        taskManager.removeAllTasks();
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        Assertions.assertEquals(2, taskManager.getTasksList().size(),
                "Если интервалы задач не пересекаются, то задача должна добавиться");

        //кейс 2: пересекаются
        task1 = new Task(1, "Test intersection 1",
                "Test intersection 1 description", TaskStatus.NEW, Duration.ofDays(3),
                LocalDateTime.of(2024, 1, 1, 0, 0, 0));
        task2 = new Task(2, "Test intersection 2",
                "Test intersection 2 description", TaskStatus.NEW, Duration.ofDays(1),
                LocalDateTime.of(2024, 1, 2, 0, 0, 0));
        taskManager.removeAllTasks();
        taskManager.addTask(task1);
        Integer idAddedTask = taskManager.addTask(task2);
        Assertions.assertTrue(1 == taskManager.getTasksList().size() && idAddedTask == 0,
                "Если интервалы задач пересекаются, то задача не должна добавиться");
    }
}
