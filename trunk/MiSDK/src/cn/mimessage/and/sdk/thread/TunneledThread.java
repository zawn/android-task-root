package cn.mimessage.and.sdk.thread;

import cn.mimessage.and.sdk.util.log.LogX;

public class TunneledThread extends TaskThread implements ITunnelThread
{
    private static final int MAX_TASK_COUNT = 100;

    private final long createTime;

    private final boolean isForceStarted;

    private final TaskQueue<TaskWarpper> mIsolationTaskQueue;

    private final TunneledThreadObserver mTunneledThreadObserver;

    /**
     * maximum life time of alive but waiting thread.
     */
    private int maxIdleDuration = 100000;

    public TunneledThread(ThreadPool threadPool, TunneledThreadObserver ob,
            boolean isForce)
    {
        super(threadPool, null);
        isForceStarted = isForce;
        createTime = System.currentTimeMillis();
        mTunneledThreadObserver = ob;
        mIsolationTaskQueue = new TaskQueue<TaskWarpper>();
    }

    public boolean getIsforceThread()
    {
        return isForceStarted;
    }

    /**
     * add a task to thread pool
     */
    @Override
    public TaskController addTask(PoolRunnable task)
    {
        return addTask(task, null);
    }

    /**
     * add a task with task observer to thread pool
     */
    @Override
    public TaskController addTask(PoolRunnable task, TaskObserver ob)
    {
        TaskController taskHandle = null;
        final TaskQueue<TaskWarpper> queue = mIsolationTaskQueue;
        synchronized (queue)
        {
            if (queue.size() <= MAX_TASK_COUNT)
            {
                mObserver = ob;
                taskHandle = queue.postTask(mThreadPool, task, ob);
                queue.notify();
            }
            else
            {
                LogX.jw(this, new Throwable(
                        "Too many tasks,please wait for a while."));
            }
        }

        return taskHandle;
    }

    @Override
    public void run()
    {
        try
        {
            TaskWarpper task;
            PoolRunnable runnable;
            while (!isTerminate)
            {
                // increase one idle count
                // get a task from task queue, thread may wait here
                task = mIsolationTaskQueue.next(this, maxIdleDuration);

                if (task != null)
                {

                    mObserver = task.getObserver();
                    runnable = task.getTaskObject();

                    if (runnable != null)
                    {
                        startTimeoutTimerCallback();

                        runnable.run();

                        task.setState(TaskWarpper.TASK_STATE_FINISHED);

                        taskResponseCallback(TaskObserver.RESPONSE_SUCCESS);

                        task.cancel();

                        task = null;
                        mObserver = null;
                    }
                }
                else
                {
                    throw new InterruptedException("Task object is null!");
                }
            }
        }
        catch (InterruptedException e)
        {
            LogX.d(this, "Task object is null!");
        }

        if (!isForceStarted)
        {
            mThreadPool.deleteThread(this);
        }

        if (mTunneledThreadObserver != null)
        {
            mTunneledThreadObserver.onThreadDead(System.currentTimeMillis()
                    - createTime);
        }

        setTerminate(true);
    }

    @Override
    public void clearAllTasks()
    {
        mIsolationTaskQueue.cancelAllTasks();
    }

    @Override
    public void kill()
    {
        clearAllTasks();
        if (isForceStarted)
        {
            mThreadPool.decreaseForceThreadCount();
        }
        else
        {
            mThreadPool.deleteThread(this);
        }

        interrupt();

        setTerminate(true);
    }

    @Override
    public boolean isRunning()
    {
        return !super.isTerminate();
    }

    public static interface TunneledThreadObserver
    {
        public void onThreadDead(long lifeTime);
    }
}
