package cn.mimessage.and.sdk.thread;

public interface IThreadPool
{

    /**
     * 添加一个常规任务
     *
     * @param task
     * @return
     */
    public TaskController addTask(PoolRunnable task);

    /**
     * 添加一个带观察者的常规任务
     *
     * @param task
     * @param ob
     * @return
     */
    public TaskController addTask(PoolRunnable task, TaskObserver ob);

    /**
     * 获取最大线程个数
     *
     * @return
     */
    public int getMaxThreadCount();

    /**
     * 终止所有线程
     */
    public void terminateAllThread();

    /**
     * 判断是否所有线程执行完毕
     *
     * @return
     */
    public boolean isAllTasksDone();

    /**
     * 设置空闲线程等待最大时间
     *
     * @param maxIdleTime
     */
    public void setMaxIdleTime(int maxIdleTime);

    /**
     * 获取当前线程个数
     *
     * @return
     */
    public int getThreadCount();

    /**
     * 获取空闲线程个数
     *
     * @return
     */
    public int getIdleCount();

    /**
     * 获取管道线程
     *
     * @param ob
     * @return
     */
    public ITunnelThread getTunnelThread(
            TunneledThread.TunneledThreadObserver ob);

    /**
     * 强制获取管道线程
     *
     * @param ob
     * @return
     */
    public ITunnelThread forceGetTunnelThread(
            TunneledThread.TunneledThreadObserver ob);

    /**
     * 获取强制管道线程的个数
     *
     * @return
     */
    public int getForceThreadCount();
}
