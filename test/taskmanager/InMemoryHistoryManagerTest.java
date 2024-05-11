package taskmanager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmodel.Epic;
import taskmodel.Subtask;
import taskmodel.Task;
import taskmodel.TaskStatus;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void shouldReturnEmptyListWhenNoHistory() {
        Assertions.assertEquals(0, historyManager.getHistory().size(),
                "getHistory должен возвращать пустой список, если история пустая");
    }

    @Test
    public void shouldAddWorkCorrectlyWithHistory() {
        int numTaskInHistoryForTest = 15;
        for (int i = 1; i <= numTaskInHistoryForTest; i++) {
            Task task = new Task(i, "TestHistory check_size_task №" + i,
                    "Test check_size_task №" + i + " description", TaskStatus.NEW, Duration.ofMinutes(15));
            historyManager.add(task);
        }
        Assertions.assertEquals(numTaskInHistoryForTest, historyManager.getHistory().size(),
                "В истории должны храниться все добавленные задачи (из " + numTaskInHistoryForTest + " задач)");

        Task lastTask = new Task(numTaskInHistoryForTest + 1, "TestHistory last task",
                "Test last task description", TaskStatus.NEW, Duration.ofMinutes(15));
        historyManager.add(lastTask);
        Assertions.assertEquals(lastTask, historyManager.getHistory().get(historyManager.getHistory().size() - 1),
                "В истории последняя добавленная задача возвращается последней в списке");

    }

    @Test
    public void shouldRemoveWorkCorrectlyWithHistory() {
        int idAdd1 = 1;
        Task task1 = new Task(idAdd1, "TestHistory check_remove №" + 1,
                "Test check_remove №" + 1 + " description", TaskStatus.NEW, Duration.ofMinutes(15));
        historyManager.add(task1);
        int idAdd2 = 2;
        Task task2 = new Task(idAdd2, "TestHistory check_remove №" + 2,
                "Test check_remove №" + 2 + " description", TaskStatus.NEW, Duration.ofMinutes(15));
        historyManager.add(task2);


        historyManager.remove(idAdd1);
        Assertions.assertEquals(1, historyManager.getHistory().size(),
                "После удаления размер истории должно уменьшаться на 1");
        Assertions.assertEquals(idAdd2, historyManager.getHistory().get(0).getId(),
                "Должна удаляться задача с указанным id =" + idAdd1 + ", а остаться в истории задача с id = " + idAdd2);

        int idNotAdded = 3333;
        historyManager.remove(idNotAdded);
        Assertions.assertTrue(
                1 == historyManager.getHistory().size() && historyManager.getHistory().get(0).getId() == idAdd2,
                "Удаление задачи с id, которого нет в менеджере, не влияет на историю");

        historyManager.remove(idAdd2);
        Assertions.assertTrue(historyManager.getHistory().isEmpty(),
                "После удаления последней задачи из истории она должна быть пустой");

        historyManager.remove(idAdd2);
        Assertions.assertTrue(historyManager.getHistory().isEmpty(),
                "Удаление из пустой истории ни к чему не приводит");


        int idAdd3 = 3;
        Task task3 = new Task(idAdd3, "TestHistory check_remove №" + 3,
                "Test check_remove №" + 3 + " description", TaskStatus.NEW, Duration.ofMinutes(30));
        //проверка удаления из начала истории
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(idAdd1);
        Assertions.assertTrue(idAdd2 == historyManager.getHistory().get(0).getId() &&
                        idAdd3 == historyManager.getHistory().get(1).getId() &&
                        historyManager.getHistory().size() == 2,
                "Некорректно работает удаление из начала истории: \n" +
                        String.format(
                                "В начале истории должна остаться задача задача с id = %d, заканчиваться задачей с id = %d",
                                idAdd2, idAdd3));
        historyManager.getHistory().clear();

        //проверка удаления из середины истории
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(idAdd2);
        Assertions.assertTrue(idAdd1 == historyManager.getHistory().get(0).getId() &&
                        idAdd3 == historyManager.getHistory().get(1).getId() &&
                        historyManager.getHistory().size() == 2,
                "Некорректно работает удаление из середины истории: \n" +
                        String.format(
                                "В начале истории должна остаться задача задача с id = %d, заканчиваться задачей с id = %d",
                                idAdd1, idAdd3));
        historyManager.getHistory().clear();

        //проверка удаления из конца истории
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(idAdd3);
        Assertions.assertTrue(idAdd1 == historyManager.getHistory().get(0).getId() &&
                        idAdd2 == historyManager.getHistory().get(1).getId() &&
                        historyManager.getHistory().size() == 2,
                "Некорректно работает удаление из конца истории: \n" +
                        String.format(
                                "В начале истории должна остаться задача задача с id = %d, заканчиваться задачей с id = %d",
                                idAdd1, idAdd2));

    }

    /**
     * Не должно быть дублирования задач в истории
     */
    @Test
    void shouldBeNoDuplicatesWhenTaskAddedToHistorySeveralTimes() {
        int idAdd1 = 1;
        Task task1 = new Task(idAdd1, "TestHistory check_remove №" + 1,
                "Test check_remove №" + 1 + " description", TaskStatus.NEW, Duration.ofMinutes(15));
        historyManager.add(task1);
        historyManager.add(task1);

        Assertions.assertEquals(1, historyManager.getHistory().size(), "В истории не должно быть дублей задач");

    }

}