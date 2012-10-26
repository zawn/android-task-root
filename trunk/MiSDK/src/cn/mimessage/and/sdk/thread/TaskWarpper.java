package cn.mimessage.and.sdk.thread;

public class TaskWarpper implements TaskController
{
    /**
     * Task Queue holding serial runnable.
     */
    private ThreadPool mThreadPool = null;

    /**
     * runnable of a user task
     */
    private PoolRunnable mPoolRunnable = null;

    /**
     * current thread
     */
    private TaskThread taskThread = null;

    /**
     * task observer
     */
    private TaskObserver mObserver = null;

    /**
     * the state of this task
     */
    private int state = TASK_STATE_INITIALIZE;

    protected TaskWarpper(ThreadPool threadPool, PoolRunnable taskObject,
            TaskObserver observer)
    {
        this.mThreadPool = threadPool;
        this.mPoolRunnable = taskObject;
        this.mObserver = observer;
    }

    /**
     * cancel this task
     */
    @Override
    public boolean cancel()
    {
        // stop time out task
        stopTimeoutTimerCallback();

        boolean ret = false;
        if (mThreadPool == null)
        {
            return ret;
        }

        switch (state)
        {
            case TASK_STATE_WAITING:
                ret = mThreadPool.removeTask(this);
                taskCancelCallback();
                break;
            case TASK_STATE_RUNNING:
                ret = mThreadPool.terminateTaskRunning(taskThread, this);
                taskCancelCallback();
                ret = true;
                break;
            case TASK_STATE_CANCEL:
                taskCancelCallback();
                ret = true;
                break;
            case TASK_STATE_FINISHED:
                taskCancelCallback();
                break;
            default:
                break;
        }

        // release memory
        mPoolRunnable = null;

        return ret;
    }

    public TaskObserver getObserver()
    {
        return mObserver;
    }

    /**
     * get the state of the task
     */
    @Override
    public int getState()
    {
        return state;
    }

    /**
     * get the runnable
     */
    @Override
    public PoolRunnable getTaskObject()
    {
        return mPoolRunnable;
    }

    /**
     * get the runtime thread
     */
    TaskThread getTaskThread()
    {
        return taskThread;
    }

    /**
     * hold the runtime thread
     */
    void setTaskThread(TaskThread taskThread)
    {
        this.taskThread = taskThread;
    }

    /**
     * set the status to task
     */
    void setState(int state)
    {
        this.state = state;
    }

    private void taskCancelCallback()
    {
        if (mObserver != null)
        {
            mObserver.onTaskCancel();
        }
    }

    private void stopTimeoutTimerCallback()
    {
        if (mObserver != null)
        {
            mObserver.stopTimeoutTimer();
        }
    }
}
