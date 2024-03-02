package taskmanager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import taskmodel.Task;

public class ManagersTest {

    /**проверка, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров*/
    @Test
    public void shouldAlwaysReturnInitiatedInstancesOfManagers() {
        TaskManager manager = Managers.getDefault();

        Assertions.assertNotNull(manager, "Менеджер задач не проиницилизирован");
        Assertions.assertNotNull(manager.getHistoryManager(), "Менеджер истории задач не проиницилизирован");
        Assertions.assertNotNull(manager.getTasksList(), "Задачи не проинициализированы в менеджере");
        Assertions.assertNotNull(manager.getSubtasksList(), "Подзадачи не проинициализированы в менеджере");
        Assertions.assertNotNull(manager.getEpicsList(), "Эпики не проинициализированы в менеджере");
    }
}
