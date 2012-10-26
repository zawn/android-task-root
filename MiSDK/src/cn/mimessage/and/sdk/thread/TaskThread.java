package cn.mimessage.and.sdk.thread;

import cn.mimessage.and.sdk.util.log.LogX;

public class TaskThread extends Thread
{
    protected ThreadPool mThreadPool = null;
    protected TaskObserver mObserver;
    protected boolean isTerminate = false;

    protected TaskThread(ThreadPool threadPool, TaskObserver observer)
    {
        mThreadPool = threadPool;
        mObserver = observer;
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
                // check if Currently running thread already has more than the
                // maximum number of threads
                if (mThreadPool.getThreadCount() > mThreadPool
                        .getMaxThreadCount())
                {
                    throw new InterruptedException("Thread count is overload!");
                }

                // get a task from task queue, thread may wait here
                task = mThreadPool.obtainTask(this);

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

        mThreadPool.deleteThread(this);

        setTerminate(true);
    }

    public ThreadPool getThreadPool()
    {
        return mThreadPool;
    }

    /**
     * set thread terminated or not
     */
    public synchronized void setTerminate(boolean isTerminate)
    {
        this.isTerminate = isTerminate;
    }

    /**
     * check if terminated
     */
    public synchronized boolean isTerminate()
    {
        return isTerminate;
    }

    protected void startTimeoutTimerCallback()
    {
        if (mObserver != null)
        {
            mObserver.startTimeoutTimer();
        }
    }

    protected void taskResponseCallback(int state)
    {
        if (mObserver != null)
        {
            mObserver.onTaskResponse(state);
        }
    }
}
