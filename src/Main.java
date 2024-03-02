import taskmanager.*;
import taskmodel.Epic;
import taskmodel.Subtask;
import taskmodel.Task;
import taskmodel.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Первая задача", "Пример запланированной задачи");
        Task task2 = new Task("Вторая задача", "Пример задачи в работе", TaskStatus.IN_PROGRESS);
        Epic epic1 = new Epic("Первый эпик", "Первое эпичное описание");
        System.out.println("Добавлена задача с id = " + taskManager.addTask(task1));
        System.out.println("Добавлена задача с id = " + taskManager.addTask(task2));
        System.out.println("Добавлен эпик с id = " + taskManager.addEpic(epic1));
        Subtask subtask1 = new Subtask("Первая подзадачка", "Запланированная подзадача", TaskStatus.DONE, epic1.getId());
        Subtask subtask2 = new Subtask("Вторая подзадачка", "Подзадача в работе", epic1.getId());
        System.out.println("Добавлена подзадача с id = " + taskManager.addSubtask(subtask1));
        System.out.println("Добавлена подзадача с id = " + taskManager.addSubtask(subtask2));
        System.out.println(taskManager.getTasksList());
        System.out.println(taskManager.getEpicsList());
        System.out.println(taskManager.getSubtasksList());

        System.out.println(taskManager.getTaskById(task1.getId()));
        System.out.println(taskManager.getTaskById(task2.getId()));

        printAllTasks(taskManager);
    }

    /**сценарий для проверки не через тесты*/
    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasksList()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : manager.getEpicsList()) {
            System.out.println(epic);

            for (Task task : manager.getSubtasksListByEpic(epic)) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtasksList()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
