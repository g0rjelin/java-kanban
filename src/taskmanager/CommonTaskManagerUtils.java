package taskmanager;

import taskmodel.Task;

public final class CommonTaskManagerUtils {
    public static boolean isIntersecting(Task t1, Task t2) {
        if (t1.getStartTime().isBefore(t2.getEndTime()) && t1.getEndTime().isAfter(t2.getStartTime())) {
            return true;
        } else {
            return false;
        }
    }
}
