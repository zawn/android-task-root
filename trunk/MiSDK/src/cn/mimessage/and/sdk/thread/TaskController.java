package cn.mimessage.and.sdk.thread;

public interface TaskController
{
    /**
     * States of task.
     */
    public static final int TASK_STATE_INITIALIZE = 0;

    public static final int TASK_STATE_WAITING = 1;

    public static final int TASK_STATE_RUNNING = 2;

    public static final int TASK_STATE_FINISHED = 3;

    public static final int TASK_STATE_CANCEL = 4;

    public static final int TASK_STATE_CANCELRUNNING = 5;

    /**
     * to cancel task
     */
    public boolean cancel();

    /**
     * get a task object
     */
    public PoolRunnable getTaskObject();

    /**
     * get the state of a task
     */
    public int getState();
}
