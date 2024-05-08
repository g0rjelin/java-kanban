package taskmanager;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager createTaskManager() {
        return (InMemoryTaskManager) Managers.getDefault();
    }

}