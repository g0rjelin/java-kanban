package taskmanager;

public final class Managers {

    static public TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    static public HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
