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
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    static final String DEFAULT_TASK_MANAGER_PATH = "resources/task_manager.csv";
    static final String TASK_CSV_HEADER = "id,type,name,status,description,epic";

    private Path taskManagerPath;

    public FileBackedTaskManager(HistoryManager historyManager) {
        super(historyManager);
        taskManagerPath = Paths.get(DEFAULT_TASK_MANAGER_PATH);
        try {
            if (!Files.exists(taskManagerPath)) {
                Files.createFile(taskManagerPath);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при создании файла менеджера задач");
        }
    }

    public FileBackedTaskManager(HistoryManager historyManager, Path taskManagerPath) {
        this(historyManager);
        this.taskManagerPath = taskManagerPath;
    }

    static FileBackedTaskManager loadFromFile(Path taskManagerFile) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(Managers.getDefaultHistory());

        try {
            List<String> taskLines = Files.readAllLines(taskManagerFile);
            String historyString = taskLines.get(taskLines.size() - 1); //Files.readString(taskManagerFile);
            for (int i = 1; i < taskLines.size() - 2; i++) {
                Task task = Managers.fromString(taskLines.get(i));
                String[] taskLineValues = taskLines.get(i).split(",");
                switch (TaskType.valueOf(taskLineValues[1])) {
                    case TASK:
                        fileBackedTaskManager.tasks.put(task.getId(), task);
                        break;
                    case EPIC:
                        fileBackedTaskManager.epics.put(task.getId(), (Epic) task);
                        break;
                    case SUBTASK:
                        fileBackedTaskManager.subtasks.put(task.getId(), (Subtask) task);
                        break;
                    default:
                        throw new ManagerSaveException("Ошибка загрузки менеджера задач");
                }
            }
            //восстановление информации о списке id подзадач в эпике
            for (Subtask subtask : fileBackedTaskManager.subtasks.values()) {
                Epic epic = fileBackedTaskManager.epics.get(subtask.getIdEpic());
                epic.addIdSubtask(subtask.getId());
            }
            List<Integer> historyIdList = Managers.historyFromString(historyString);
            for (Integer historyId : historyIdList) {
                if (fileBackedTaskManager.tasks.containsKey(historyId)) {
                    fileBackedTaskManager.historyManager.add(fileBackedTaskManager.tasks.get(historyId));
                } else if (fileBackedTaskManager.epics.containsKey(historyId)) {
                    fileBackedTaskManager.historyManager.add(fileBackedTaskManager.epics.get(historyId));
                } else if (fileBackedTaskManager.subtasks.containsKey(historyId)) {
                    fileBackedTaskManager.historyManager.add(fileBackedTaskManager.subtasks.get(historyId));
                } else {
                    throw new ManagerSaveException("В истории указан несуществующий идентификатор задачи менеджера");
                }
            }
            return fileBackedTaskManager;
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла");
        }
    }

    /**
     * Реализация пользовательского сценария
     */
    public static void main(String[] args) {
        //1. Заведите несколько разных задач, эпиков и подзадач.
        FileBackedTaskManager taskManager = new FileBackedTaskManager(Managers.getDefaultHistory());
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
        FileBackedTaskManager newTaskManager = loadFromFile(Paths.get(DEFAULT_TASK_MANAGER_PATH));
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
    public Task getTaskById(Integer id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(Integer id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        save();
        return subtask;
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
    void save() {
        try {
            Writer taskManagerFileWriter = new FileWriter(String.valueOf(taskManagerPath));

            taskManagerFileWriter.write(TASK_CSV_HEADER + "\n");
            for (Integer taskId : tasks.keySet()) {
                taskManagerFileWriter.write(Managers.taskToString(tasks.get(taskId)) + "\n");
            }
            for (Integer epicId : epics.keySet()) {
                taskManagerFileWriter.write(Managers.taskToString(epics.get(epicId)) + "\n");
            }
            for (Integer subtaskId : subtasks.keySet()) {
                taskManagerFileWriter.write(Managers.subtaskToString(subtasks.get(subtaskId)) + "\n");
            }
            taskManagerFileWriter.write("\n");
            String historyString = Managers.historyToString(historyManager);
            taskManagerFileWriter.write(historyString);
            taskManagerFileWriter.close();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка автосохранения менеджера");
        }
    }
}
