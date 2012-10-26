package cn.mimessage.and.sdk.thread;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final public class ThreadPool implements IThreadPool
{
    /**
     * serial task queue
     */
    final private TaskQueue<TaskWarpper> mTaskQueue;

    /**
     * Thread pool
     */
    final private List<TaskThread> mThreadHolder;

    /**
     * idle thread holder
     */
    final private List<TaskThread> mIdleThreadHolder;

    /**
     * maximum thread count
     */
    private int mMaxCount = 5;

    /**
     * additional force started thread count
     */
    private int forceThreadCount = 0;

    /**
     * maximum life time of alive but waiting thread.
     */
    private int maxIdleDuration = 100000;

    public ThreadPool(int maxCount)
    {
        mTaskQueue = new TaskQueue<TaskWarpper>();
        mThreadHolder = new ArrayList<TaskThread>();
        mIdleThreadHolder = new ArrayList<TaskThread>();
        mMaxCount = maxCount;
    }

    /**
     * add a task to thread pool
     */
    @Override
    public TaskController addTask(PoolRunnable task)
    {
        return this.addTask(task, null);
    }

    /**
     * add a task with task observer to thread pool
     */
    @Override
    public TaskController addTask(PoolRunnable task, TaskObserver ob)
    {

        TaskController taskController = mTaskQueue.postTask(this, task, ob);

        synchronized (mThreadHolder)
        {
            // check if need to start a new thread
            if (mIdleThreadHolder.size() < 1 && mThreadHolder.size() < mMaxCount)
            {
                TaskThread taskThread = new TaskThread(this, ob);
                mThreadHolder.add(taskThread);
                taskThread.start();
            }
            else
            {
                mTaskQueue.wakeup();
            }
        }
        return taskController;
    }

    /**
     * get maximum thread count
     */
    @Override
    public int getMaxThreadCount()
    {
        return mMaxCount;
    }

    /**
     * stop all thread
     */
    @Override
    public void terminateAllThread()
    {
        TaskThread thread = null;
        synchronized (mThreadHolder)
        {
            Iterator<TaskThread> itr = mThreadHolder.iterator();
            while (itr.hasNext())
            {
                thread = itr.next();
                thread.setTerminate(true);
                thread.interrupt();
            }
        }

        mTaskQueue.cancelAllTasks();
    }

    /**
     * check if all tasks have been run over
     */
    @Override
    public boolean isAllTasksDone()
    {
        return mTaskQueue.isAllTasksDone();
    }

    /**
     * set the maximum idle duration of each thread
     */
    @Override
    public void setMaxIdleTime(int maxIdleTime)
    {
        this.maxIdleDuration = maxIdleTime;
    }

    /**
     * increase idle thread count by one
     */
    protected void addIdleThread(TaskThread th)
    {
        if (th instanceof TunneledThread)
        {
            return;
        }
        synchronized (mThreadHolder)
        {
            mIdleThreadHolder.add(th);
        }
    }

    /**
     * decrease idle thread count by one
     */
    protected void removeIdleThread(TaskThread th)
    {
        if (th instanceof TunneledThread)
        {
            return;
        }
        synchronized (mThreadHolder)
        {
            mIdleThreadHolder.remove(th);
        }
    }

    /**
     * get next task in the queue
     */
    TaskWarpper obtainTask(TaskThread thread) throws InterruptedException
    {
        return mTaskQueue.next(thread, maxIdleDuration);
    }

    /**
     * remove a task in mTaskQueue
     * 
     * @param taskHandle
     */
    boolean removeTask(TaskWarpper task)
    {
        return mTaskQueue.removeTask(task);
    }

    /**
     * terminate a task
     */
    boolean terminateTaskRunning(TaskThread taskThread, TaskWarpper taskHandle)
    {
        boolean ret = false;
        if (taskThread == null)
        {
            return ret;
        }
        if (taskHandle != null)
        {
            taskHandle.setState(TaskController.TASK_STATE_CANCELRUNNING);
        }
        taskThread.setTerminate(false);
        taskThread.interrupt();
        ret = true;

        return ret;
    }

    /**
     * remove a thread from thread pool
     */
    protected void deleteThread(TaskThread taskThread)
    {
        synchronized (mThreadHolder)
        {
            mThreadHolder.remove(taskThread);
            mIdleThreadHolder.remove(taskThread);
        }
    }

    /**
     * get the sum of threads
     */
    @Override
    public int getThreadCount()
    {
        synchronized (mThreadHolder)
        {
            return mThreadHolder.size();
        }
    }

    /**
     * get the count of idle thread
     */
    @Override
    public int getIdleCount()
    {
        synchronized (mThreadHolder)
        {
            return mIdleThreadHolder.size();
        }
    }

    /**
     * get a tunnel thread , all task in the tunnel will be run one after
     * another
     * 
     * @return
     */
    @Override
    public ITunnelThread getTunnelThread(TunneledThread.TunneledThreadObserver ob)
    {
        ITunnelThread tunneledThread = null;
        synchronized (mThreadHolder)
        {
            tunneledThread = createStartedThread(ob);
            if (tunneledThread == null && mIdleThreadHolder.size() > 0)
            {
                TaskThread taskThread = mIdleThreadHolder.get(0);
                taskThread.interrupt();
                deleteThread(taskThread);

                tunneledThread = createStartedThread(ob);
            }
        }
        return tunneledThread;
    }

    /**
     * create started thread with an observer
     * 
     * @param ob
     * @return
     */
    private ITunnelThread createStartedThread(TunneledThread.TunneledThreadObserver ob)
    {
        TunneledThread tunneledThread = null;
        synchronized (mThreadHolder)
        {
            // check if it can start a new thread
            if (mThreadHolder.size() < mMaxCount)
            {
                tunneledThread = new TunneledThread(this, ob, false);
                mThreadHolder.add(tunneledThread);
                tunneledThread.start();
            }
        }
        return tunneledThread;
    }

    @Override
    public ITunnelThread forceGetTunnelThread(TunneledThread.TunneledThreadObserver ob)
    {
        ITunnelThread tunneledThread = null;
        synchronized (mThreadHolder)
        {
            tunneledThread = getTunnelThread(ob);
            // God says, tunneledThread is good, then there is one.
            if (tunneledThread == null)
            {
                TunneledThread tt = new TunneledThread(this, ob, true);
                tt.start();
                tunneledThread = tt;
                encreaseForceThreadCount();
            }
        }
        return tunneledThread;
    }

    /**
     * decrease force thread count
     */
    void encreaseForceThreadCount()
    {
        synchronized (mThreadHolder)
        {
            forceThreadCount++;
        }
    }

    /**
     * decrease force thread count
     */
    void decreaseForceThreadCount()
    {
        synchronized (mThreadHolder)
        {
            forceThreadCount--;
        }
    }

    /**
     * obtain force thread count
     */
    @Override
    public int getForceThreadCount()
    {
        synchronized (mThreadHolder)
        {
            return forceThreadCount;
        }
    }
}
