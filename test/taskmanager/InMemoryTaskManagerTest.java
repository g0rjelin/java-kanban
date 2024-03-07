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
    Task task;
    Epic epic;

    @BeforeEach
    public void setUp() {
        taskManager = Managers.getDefault();
        task = new Task("Test InMemoryTaskManager task",
                "Test InMemoryTaskManager task description",
                TaskStatus.NEW);
        epic = new Epic("Test InMemoryTaskManager epic",
                "Test InMemoryTaskManager epic description");

    }

    /**проверка работы InMemoryTaskManager: добавление задачи*/
    @Test
    public void addNewTask() {
        //Task task = new Task("Test addNewTask", "Test addNewTask description", TaskStatus.NEW);
        final int taskId = taskManager.addTask(task);

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasksList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач: задача не добавилась.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    /**проверка работы InMemoryTaskManager: обновление задачи*/
    @Test
    public void updateTask() {
        final int taskId = taskManager.addTask(task);
        Task updTask = new Task(taskId, "Test InMemoryTaskManager Updated task",
                "Test Updated task description", TaskStatus.NEW);

        taskManager.updateTask(updTask);

        final List<Task> tasks = taskManager.getTasksList();
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(updTask, tasks.get(0), "Задачи не совпадают.");
    }


    /**проверка работы InMemoryTaskManager: удаление задачи*/
    @Test
    public void deleteTask() {
        final int taskId = taskManager.addTask(task);

        taskManager.deleteTaskById(taskId);
        final List<Task> tasks = taskManager.getTasksList();
        assertEquals(0, tasks.size(), "Неверное количество задач: задача не удалилась.");
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
        int taskGenId = taskManager.addTask(task);
        String nameBeforeAdd = task.getName();
        String descriptionBeforeAdd = task.getDescription();
        TaskStatus statusBeforeAdd = task.getStatus();
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
        final int epicId = taskManager.addEpic(epic);

        final Task savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Эпик без подзадач не в статусе NEW.");

        assertEquals(0, epic.getSubtaskIdList().size(), "В созданном эпике без указания подзадач не должно быть подзадач");

        final List<Epic> epics = taskManager.getEpicsList();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков: эпик не добавился");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }


    /**проверка работы InMemoryTaskManager: обновление эпика*/
    @Test
    public void updateEpic() {
        final int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test InMemory subtask", "Test InMemory subtask description",
                TaskStatus.NEW, epicId);
        final int subtaskId = taskManager.addSubtask(subtask);
        Epic updEpic = new Epic(epicId, "Test InMemoryTaskManager Updated epic",
                "Test Updated epic description", epic.getSubtaskIdList());

        taskManager.updateEpic(updEpic);

        final List<Epic> epics = taskManager.getEpicsList();
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(updEpic, epics.get(0), "Эпики не совпадают.");
        assertArrayEquals(epic.getSubtaskIdList().toArray(), epics.get(0).getSubtaskIdList().toArray(),"Подзадачи не совпадают");
    }


    /**проверка работы InMemoryTaskManager: удаление эпика*/
    @Test
    public void deleteEpic() {
        final int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test InMemory subtask", "Test InMemory subtask description",
                TaskStatus.NEW, epicId);
        final int subtaskId = taskManager.addSubtask(subtask);
        Epic updEpic = new Epic(epicId, "Test InMemoryTaskManager Updated epic",
                "Test Updated epic description", epic.getSubtaskIdList());

        taskManager.deleteEpicById(epicId);

        final List<Epic> epics = taskManager.getEpicsList();
        assertEquals(0, epics.size(), "Неверное количество эпиков: эпик не удалился");

        final List<Subtask> subtasks = taskManager.getSubtasksList();
        assertEquals(0, subtasks.size(), "При удалении эпика не удалилась подзадача эпика.");
    }

    /**проверка отсутствия конфликта между эпиком с заданным id и сгенерированным id внутри менеджера*/
    @Test
    public void shouldNotConflictBetweenEpicWithSetIdAndGeneratedId() {
        int epicGenId = taskManager.addEpic(epic);
        String nameBeforeAdd = epic.getName();
        String descriptionBeforeAdd = epic.getDescription();
        TaskStatus statusBeforeAdd = epic.getStatus();
        int sizeBeforeAdd = taskManager.getEpicsList().size();

        Epic epicSet = new Epic(epicGenId, "Test add epic to manager with id", "Test add epic to manager with id description", new ArrayList<>());
        int epicSetId = taskManager.addEpic(epicSet);

        Assertions.assertNotEquals(epicGenId, epicSetId, "Добавление эпика с заданным id не должно влиять на эпик с этим же сгенеренным id");
        Assertions.assertEquals(nameBeforeAdd, taskManager.getEpicById(epicGenId).getName(), "Добавление эпика с заданным id не должно менять Name эпика");
        Assertions.assertEquals(descriptionBeforeAdd, taskManager.getEpicById(epicGenId).getDescription(), "Добавление эпика с заданным id не должно менять Description эпика");
        Assertions.assertEquals(statusBeforeAdd, taskManager.getEpicById(epicGenId).getStatus(), "Добавление эпика с заданным id не должно менять статус эпика");
        Assertions.assertNotEquals(sizeBeforeAdd, taskManager.getEpicsList().size(), "Добавляемый с существующим id эпик не попал в менеджер");
    }


    /**проверка работы InMemoryTaskManager: добавление подзадачи*/
    @Test
    public void addNewSubtask() {
        final int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description",
                TaskStatus.NEW, epicId);
        final int subtaskId = taskManager.addSubtask(subtask);

        final Task savedSubtask = taskManager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getSubtasksList();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

    /**проверка работы InMemoryTaskManager: обновление подзадачи*/
    @Test
    public void updateSubtask() {
        final int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test InMemoryTaskManager subtask",
                "Test InMemoryTaskManager subtask description",
                TaskStatus.NEW, epicId);
        final int subtaskId = taskManager.addSubtask(subtask);

        Subtask updSubtask = new Subtask(subtaskId, "Test InMemoryTaskManager Updated subtask",
                "Test Updated subtask description", TaskStatus.NEW, epicId);

        taskManager.updateSubtask(updSubtask);

        final List<Subtask> subtasks = taskManager.getSubtasksList();
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(updSubtask, subtasks.get(0), "Подзадачи не совпадают.");
    }


    /**проверка работы InMemoryTaskManager: удаление подзадачи*/
    @Test
    public void deleteSubtask() {
        final int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test InMemoryTaskManager subtask",
                "Test InMemoryTaskManager subtask description",
                TaskStatus.NEW, epicId);
        final int subtaskId = taskManager.addSubtask(subtask);

        taskManager.deleteSubtaskById(subtaskId);
        final List<Subtask> subtasks = taskManager.getSubtasksList();
        assertEquals(0, subtasks.size(), "Неверное количество подзадач: подзадача не удалилась.");
        assertEquals(0, epic.getSubtaskIdList().size(), "В эпике осталась ссылка (id) на подзадачу");
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

    /**проверка работы InMemoryTaskManager: проверка удаления всех задач*/
    @Test
    public void removeAllTasksTest() {
        final int taskId = taskManager.addTask(task);

        taskManager.removeAllTasks();

        final List<Task> tasks = taskManager.getTasksList();
        assertEquals(0, tasks.size(), "Неверное количество задач: задач не очистился.");
    }

    /**проверка работы InMemoryTaskManager: проверка удаления всех подзадач*/
    @Test
    public void removeAllSubtasksTest() {
        final int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test InMemoryTaskManager subtask",
                "Test InMemoryTaskManager subtask description",
                TaskStatus.NEW, epicId);
        final int subtaskId = taskManager.addSubtask(subtask);

        taskManager.removeAllSubtasks();

        final List<Subtask> subtasks = taskManager.getSubtasksList();
        assertEquals(0, subtasks.size(), "Неверное количество подзадач: список подзадач не очистился.");
    }

    /**проверка работы InMemoryTaskManager: проверка удаления всех эпиков*/
    @Test
    public void removeAllEpicsTest() {
        final int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test InMemoryTaskManager subtask",
                "Test InMemoryTaskManager subtask description",
                TaskStatus.NEW, epicId);
        final int subtaskId = taskManager.addSubtask(subtask);

        taskManager.removeAllEpics();

        final List<Epic> epics = taskManager.getEpicsList();
        assertEquals(0, epics.size(), "Неверное количество эпиков: список эпиков не очистился");

        final List<Subtask> subtasks = taskManager.getSubtasksList();
        assertEquals(0, subtasks.size(), "Все подзадачи при удалении эпиков должны удалиться");
    }

    /**задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных*/
    @Test
    public void shouldPreserveTaskPrevVersionInHistory() {
        String initName = "Test task Version 1";
        String initDescription = "Test task Version 1 Description";
        TaskStatus initTaskStatus = TaskStatus.NEW;
        Task task = new Task(initName, initDescription, initTaskStatus);
        final int taskId = taskManager.addTask(task);

        //обращение к задаче для внесения е в историю
        taskManager.getTaskById(taskId);
        //внесение изменений в задачу
        Task changedTask = new Task( task.getId(), "Test task Version 2", "Test task Version 2 Description", TaskStatus.IN_PROGRESS);
        taskManager.updateTask(changedTask);
        //обращение к задаче после внесения ее в историю
        taskManager.getTaskById(taskId);

        Assertions.assertEquals( taskId, taskManager.getHistory().get(0).getId(), "Id задачи при сохранении в истории должно сохраниться");
        Assertions.assertEquals( initName, taskManager.getHistory().get(0).getName(), "В истории должно остаться первоначальное наименование задачи!");
        Assertions.assertEquals( initDescription, taskManager.getHistory().get(0).getDescription(), "В истории должно остаться первоначальное описание задачи!");
        Assertions.assertEquals( initTaskStatus, taskManager.getHistory().get(0).getStatus(), "В истории должен остаться первоначальный статус задачи!");
    }

    /**эпики, добавляемые в HistoryManager, сохраняют предыдущую версию эпика и её данных*/
    @Test
    public void shouldPreserveEpicPrevVersionInHistory() {
        String initName = "Test epic Version 1";
        String initDescription = "Test epic Version 1 Description";
        Epic epic = new Epic(initName, initDescription);
        int epicId = taskManager.addEpic(epic);
        //обращение к задачам для внесения их в историю
        taskManager.getEpicById(epicId);
        Subtask subtask = new Subtask("Test subtask1", "Test subtask1 Description", TaskStatus.NEW, epic.getId());
        int subtaskId = taskManager.addSubtask(subtask);
        TaskStatus initEpicStatus = epic.getStatus();


        //внесение изменений в задачи
        Subtask subtaskAdditional = new Subtask("Test subtask2", "Test subtask2 Description", TaskStatus.DONE, epic.getId());
        Epic changedEpic = new Epic(epic.getId(), "Test epic Version 2", "Test epic Version 2 Description", epic.getSubtaskIdList());
        taskManager.updateEpic(changedEpic);
        taskManager.addSubtask(subtaskAdditional);
        Subtask changedSubtask = new Subtask(subtask.getId(), "Test subtask Version 2", "Test subtask Version 2 Description", TaskStatus.DONE, subtask.getIdEpic());
        taskManager.updateSubtask(changedSubtask); //изменение приведет к смене статуса эпика
        //обращение к эпику после внесения изменений в историю
        taskManager.getEpicById(epicId);

        Assertions.assertEquals( epicId, taskManager.getHistory().get(0).getId(), "Id эпика при сохранении в истории должно сохраниться");
        Assertions.assertEquals( initName, taskManager.getHistory().get(0).getName(), "В истории должно остаться первоначальное наименование эпика!");
        Assertions.assertEquals( initDescription, taskManager.getHistory().get(0).getDescription(), "В истории должно остаться первоначальное описание эпика!");
        Assertions.assertEquals( initEpicStatus, taskManager.getHistory().get(0).getStatus(), "В истории должен остаться первоначальный статус эпика!");
    }

    /**подзадачи, добавляемые в HistoryManager, сохраняют предыдущую версию подзадачи и её данных*/
    @Test
    public void shouldPreserveSubtaskPrevVersionInHistory() {
        Epic epic = new Epic("Epic Name for Subtask Test", "Epic Description for Subtask Test");
        int epicId = taskManager.addEpic(epic);
        String initName = "Test subtask Version 1";
        String initDescription = "Test subtask Version 1 Description";
        TaskStatus initSubtaskStatus = TaskStatus.NEW;
        Subtask subtask = new Subtask(initName, initDescription, initSubtaskStatus, epicId);
        final int subtaskId = taskManager.addSubtask(subtask);

        //обращение к задаче для внесения в историю
        taskManager.getSubtaskById(subtaskId);
        //внесение изменений в подзадачу
        Epic newEpic = new Epic("NewEpic Name for Subtask Test", "NewEpic Description for Subtask Test");
        int newEpicId = taskManager.addEpic(newEpic);
        Subtask changedSubtask = new Subtask( subtask.getId(), "Test subtask Version 2", "Test subtask Version 2 Description", TaskStatus.IN_PROGRESS, newEpicId);
        taskManager.updateSubtask(changedSubtask);
        //обращение к задаче после внесения ее в историю
        taskManager.getSubtaskById(subtaskId);

        Assertions.assertEquals( subtaskId, taskManager.getHistory().get(1).getId(), "Id подзадачи при сохранении в истории должно сохраниться");
        Assertions.assertEquals( initName, taskManager.getHistory().get(1).getName(), "В истории должно остаться первоначальное наименование подзадачи!");
        Assertions.assertEquals( initDescription, taskManager.getHistory().get(1).getDescription(), "В истории должно остаться первоначальное описание подзадачи!");
        Assertions.assertEquals( initSubtaskStatus, taskManager.getHistory().get(1).getStatus(), "В истории должен остаться первоначальный статус подзадачи!");
        Subtask subtaskFromHistory = (Subtask) taskManager.getHistory().get(1);
        Assertions.assertEquals( epicId, subtaskFromHistory.getIdEpic(), "В истории должен остаться первоначальный эпик подзадачи!");
    }

}