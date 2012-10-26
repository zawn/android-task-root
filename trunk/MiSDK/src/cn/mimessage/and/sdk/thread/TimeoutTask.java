package cn.mimessage.and.sdk.thread;

import java.util.TimerTask;

public class TimeoutTask extends TimerTask
{
    private ThreadPool taskQueue = null;

    private TaskWarpper mTaskWrapper = null;

    private TaskObserver mTaskObserver = null;

    protected TimeoutTask(ThreadPool taskQueue, TaskWarpper taskController,
            TaskObserver ob)
    {
        this.taskQueue = taskQueue;
        this.mTaskWrapper = taskController;
        this.mTaskObserver = ob;
    }

    @Override
    public void run()
    {
        if (mTaskWrapper == null || taskQueue == null)
        {
            return;
        }

        switch (mTaskWrapper.getState())
        {
            case TaskWarpper.TASK_STATE_WAITING:
            {
                taskQueue.removeTask(mTaskWrapper);
                if (mTaskObserver != null)
                {
                    mTaskObserver
                            .onTaskResponse(TaskObserver.RESPONSE_TIMEOUT_PRE_RUN);
                }
                break;
            }
            case TaskWarpper.TASK_STATE_RUNNING:
            {
                taskQueue.terminateTaskRunning(mTaskWrapper.getTaskThread(),
                        mTaskWrapper);
                if (mTaskObserver != null)
                {
                    mTaskObserver
                            .onTaskResponse(TaskObserver.RESPONSE_TIMEOUT_RUNNING);
                }
                break;
            }
            default:
            {
                break;
            }
        }
    }

}
