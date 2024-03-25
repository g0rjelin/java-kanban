import taskmanager.*;
import taskmodel.Epic;
import taskmodel.Subtask;
import taskmodel.Task;
import taskmodel.TaskStatus;

public class Main {

    public static void main(String[] args) {
        //Реализация пользовательского сценария
        TaskManager taskManager = Managers.getDefault();

        userScenarioSample(taskManager);

    }

    private static void printHistory(TaskManager manager) {
        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
        System.out.println();
    }

    private static void userScenarioSample(TaskManager taskManager) {
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

        System.out.println("1й вариант последовательности запроса задач");
        taskManager.getTaskById(idTask1);
        taskManager.getTaskById(idTask2);
        taskManager.getEpicById(idEpic1);
        taskManager.getSubtaskById(idSubtask1);
        taskManager.getSubtaskById(idSubtask2);
        taskManager.getSubtaskById(idSubtask3);
        taskManager.getEpicById(idEpic2);

        printHistory(taskManager);

        System.out.println("2й вариант последовательности запроса задач");
        taskManager.getEpicById(idEpic2);
        taskManager.getSubtaskById(idSubtask3);
        taskManager.getSubtaskById(idSubtask2);
        taskManager.getSubtaskById(idSubtask1);
        taskManager.getEpicById(idEpic1);
        taskManager.getTaskById(idTask2);
        taskManager.getTaskById(idTask1);

        printHistory(taskManager);

        System.out.println("3й вариант последовательности запроса задач");
        taskManager.getSubtaskById(idSubtask3);
        taskManager.getTaskById(idTask2);
        taskManager.getEpicById(idEpic1);
        taskManager.getSubtaskById(idSubtask1);
        taskManager.getTaskById(idTask1);
        taskManager.getEpicById(idEpic2);
        taskManager.getSubtaskById(idSubtask2);

        printHistory(taskManager);

        //удаление задачи
        System.out.println("Удаление задачи с id = " + idTask2);
        taskManager.deleteTaskById(idTask2);
        printHistory(taskManager);

        //удаление эпика с тремя подзадачами
        System.out.println("Удаление эпика с id = " + idEpic1);
        taskManager.deleteEpicById(idEpic1);
        printHistory(taskManager);
    }
}
