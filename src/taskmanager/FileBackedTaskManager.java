package taskmanager;

import taskmodel.Epic;
import taskmodel.Subtask;
import taskmodel.Task;
import taskmodel.TaskStatus;
import taskmodel.TaskType;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    static final String DEFAULT_TASK_MANAGER_PATH = "resources/task_manager.csv";
    static final String TASK_CSV_HEADER = "id,type,name,status,description,epic";

    private Path taskManagerPath;

    public FileBackedTaskManager(HistoryManager historyManager) {
        super(historyManager);
        taskManagerPath = Paths.get(DEFAULT_TASK_MANAGER_PATH);
    }

    public FileBackedTaskManager(HistoryManager historyManager, Path taskManagerPath) {
        this(historyManager);
        this.taskManagerPath = taskManagerPath;
    }

    private FileBackedTaskManager(HistoryManager historyManager, HashMap<Integer, Task> tasks,
                                  HashMap<Integer, Epic> epics, HashMap<Integer, Subtask> subtasks) {
        super(historyManager, tasks, epics, subtasks);
    }

    private FileBackedTaskManager(HistoryManager historyManager, HashMap<Integer, Task> tasks,
                                  HashMap<Integer, Epic> epics, HashMap<Integer, Subtask> subtasks,
                                  Path taskManagerPath) {
        this(historyManager, tasks, epics, subtasks);
        this.taskManagerPath = taskManagerPath;
    }

    private static Task fromString(String value) throws ManagerSaveException {
        String[] values = value.split(",");
        switch (TaskType.valueOf(values[1])) {
            case TASK:
                return new Task(Integer.valueOf(values[0]), values[2], values[4], TaskStatus.valueOf(values[3]));
            case EPIC:
                return new Epic(Integer.valueOf(values[0]), values[2], values[4], new ArrayList<>());
            case SUBTASK:
                return new Subtask(Integer.valueOf(values[0]), values[2], values[4], TaskStatus.valueOf(values[3]),
                        Integer.valueOf(values[5]));
            default:
                throw new ManagerSaveException("Ошибка парсинга записи");
        }
    }

    static FileBackedTaskManager loadFromFile(Path taskManagerFile, Path historyManagerFile)
            throws ManagerSaveException {
        FileBackedHistoryManager historyManager = new FileBackedHistoryManager(historyManagerFile);
        HashMap<Integer, Task> tasks = new HashMap<>();
        HashMap<Integer, Subtask> subtasks = new HashMap<>();
        HashMap<Integer, Epic> epics = new HashMap<>();
        try {
            List<String> taskLines = Files.readAllLines(taskManagerFile);
            String historyString = Files.readString(historyManagerFile);
            for (int i = 1; i < taskLines.size(); i++) {
                Task task = fromString(taskLines.get(i));
                if (task instanceof Subtask) {
                    subtasks.put(task.getId(), (Subtask) task);
                } else if (task instanceof Epic) {
                    epics.put(task.getId(), (Epic) task);
                } else {
                    tasks.put(task.getId(), task);
                }
            }
            //восстановление информации о списке id подзадач в эпике
            for (Subtask subtask : subtasks.values()) {
                Epic epic = epics.get(subtask.getIdEpic());
                epic.addIdSubtask(subtask.getId());
            }
            FileBackedTaskManager taskManager =
                    new FileBackedTaskManager(historyManager, tasks, epics, subtasks, taskManagerFile);
            //определение статусов эпиков
            for (Epic epic : epics.values()) {
                taskManager.updateEpicStatus(epic);
            }
            //т.к. при определении статуса эпиков идет поиск подзадач по id, то меняется история, почистим ее перед заполнением
            for (int i = 0; i < historyManager.getHistory().size(); i++) {
                historyManager.remove(taskManager.getHistoryManager().getHistory().get(i).getId());
            }
            List<Integer> historyIdList = FileBackedHistoryManager.historyFromString(historyString);
            for (Integer historyId : historyIdList) {
                if (tasks.containsKey(historyId)) {
                    historyManager.add(tasks.get(historyId));
                } else if (epics.containsKey(historyId)) {
                    historyManager.add(epics.get(historyId));
                } else if (subtasks.containsKey(historyId)) {
                    historyManager.add(subtasks.get(historyId));
                } else {
                    throw new ManagerSaveException("В истории указан несуществующий идентификатор задачи менеджера");
                }
            }
            return taskManager;
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла");
        }
    }

    /**
     * Реализация пользовательского сценария
     */
    public static void main(String[] args) throws ManagerSaveException {
        //1. Заведите несколько разных задач, эпиков и подзадач.
        FileBackedTaskManager taskManager = new FileBackedTaskManager(new FileBackedHistoryManager());
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
        taskManager.getTaskById(idTask1);
        taskManager.getTaskById(idTask2);
        taskManager.getEpicById(idEpic1);
        taskManager.getSubtaskById(idSubtask1);
        taskManager.getSubtaskById(idSubtask2);
        taskManager.getSubtaskById(idSubtask3);
        taskManager.getEpicById(idEpic2);
        //2. Создайте новый FileBackedTaskManager-менеджер из этого же файла.
        FileBackedTaskManager newTaskManager = loadFromFile(Paths.get(DEFAULT_TASK_MANAGER_PATH),
                Paths.get(FileBackedHistoryManager.DEFAULT_HISTORY_MANAGER_PATH));
        //3. Проверьте, что все задачи, эпики, подзадачи, которые были в старом менеджере, есть в новом.
        System.out.format("Результат проверки на равенство задач, эпиков, подзадач и истории просмотров: %b",
                taskManager.getTasksList().equals(newTaskManager.getTasksList())
                        && taskManager.getSubtasksList().equals(newTaskManager.getSubtasksList())
                        && taskManager.getEpicsList().equals(newTaskManager.getEpicsList())
                        && taskManager.getHistory().equals(newTaskManager.getHistory())
        );
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public Integer addTask(Task newTask) {
        Integer idTask = super.addTask(newTask);
        save();
        return idTask;
    }

    @Override
    public Integer addEpic(Epic newEpic) {
        Integer idEpic = super.addEpic(newEpic);
        save();
        return idEpic;
    }

    @Override
    public Integer addSubtask(Subtask newSubtask) {
        Integer idSubtask = super.addSubtask(newSubtask);
        save();
        return idSubtask;
    }

    @Override
    public Integer updateTask(Task updTask) {
        Integer idTask = super.updateTask(updTask);
        save();
        return idTask;
    }

    @Override
    public Integer updateEpic(Epic updEpic) {
        Integer idEpic = super.updateEpic(updEpic);
        save();
        return idEpic;
    }

    @Override
    public Integer updateSubtask(Subtask updSubtask) {
        Integer idSubtask = super.updateSubtask(updSubtask);
        save();
        return idSubtask;
    }

    @Override
    public void deleteTaskById(Integer id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(Integer id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(Integer id) {
        super.deleteSubtaskById(id);
        save();
    }

    /**
     * Сохранение всех задач, подзадач, эпиков и истории просмотра любых задач
     */
    private void save() {
        try {
            if (!Files.exists(taskManagerPath)) {
                Files.createFile(taskManagerPath);
            }
            Writer taskManagerFileWriter = new FileWriter(String.valueOf(taskManagerPath));

            taskManagerFileWriter.write(TASK_CSV_HEADER + "\n");
            for (Integer taskId : tasks.keySet()) {
                taskManagerFileWriter.write(tasks.get(taskId).taskToString() + "\n");
            }
            for (Integer epicId : epics.keySet()) {
                taskManagerFileWriter.write(epics.get(epicId).taskToString() + "\n");
            }
            for (Integer subtaskId : subtasks.keySet()) {
                taskManagerFileWriter.write(subtasks.get(subtaskId).taskToString() + "\n");
            }
            taskManagerFileWriter.close();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка автосохранения менеджера");
        }
    }
}
