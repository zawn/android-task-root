package cn.mimessage.and.sdk.thread;

public interface ITunnelThread
{
    /**
     * add a task to thread pool
     *
     * @param task
     * @return
     */
    public TaskController addTask(PoolRunnable task);

    /**
     * add a task with task observer to thread pool
     *
     * @param task
     * @param ob
     * @return
     */
    public TaskController addTask(PoolRunnable task, TaskObserver ob);

    /**
     * remove all tasks in queue
     */
    public void clearAllTasks();

    /**
     * terminate current tunnel thread
     */
    public void kill();

    /**
     * check the tunnel thread alive
     */
    public boolean isRunning();
}
