package cn.mimessage.and.sdk.thread;

import java.util.Iterator;
import java.util.LinkedList;

public class TaskQueue<E extends TaskWarpper> extends LinkedList<TaskWarpper>
{
    private static final long serialVersionUID = 1L;

    /**
     * add a new task at last of the queue
     */
    TaskController postTask(ThreadPool pool, PoolRunnable task, TaskObserver ob)
    {
        TaskWarpper taskHandle = null;
        if (pool == null || task == null)
        {
            return null;
        }

        taskHandle = new TaskWarpper(pool, task, ob);

        // create a timed task
        TimeoutTask timeoutTask = new TimeoutTask(pool, taskHandle, ob);
        if (ob != null)
        {
            ob.setTimeoutTask(timeoutTask);
        }

        synchronized (this)
        {
            addLast(taskHandle);
            taskHandle.setState(TaskWarpper.TASK_STATE_WAITING);
        }

        return taskHandle;
    }

    /**
     * call cancel method of each task
     */
    synchronized void cancelAllTasks()
    {
        Iterator<TaskWarpper> taskItr = iterator();
        TaskWarpper tw = null;
        while (taskItr.hasNext())
        {
            tw = taskItr.next();
            tw.setState(TaskWarpper.TASK_STATE_CANCEL);
            tw.cancel();
        }
        clear();
    }

    /**
     * check if all tasks have been run over
     */
    synchronized boolean isAllTasksDone()
    {
        boolean isDone = false;
        TaskWarpper taskHandle = null;
        Iterator<TaskWarpper> taskItr = iterator();
        while (taskItr.hasNext())
        {
            taskHandle = taskItr.next();
            if (taskHandle.getState() != TaskController.TASK_STATE_FINISHED)
            {
                isDone = false;
                break;
            }
        }
        return isDone;
    }

    /**
     * get next task from the queue
     *
     * @param thread
     * @param duration
     * @return
     * @throws InterruptedException
     */
    synchronized TaskWarpper next(TaskThread thread, int duration)
            throws InterruptedException
    {
        TaskWarpper task = null;
        if (isEmpty())
        {
            final ThreadPool pool = thread.getThreadPool();
            // increase one idle count
            pool.addIdleThread(thread);
            wait(duration);
            // decrease one idle count
            pool.removeIdleThread(thread);
        }

        if (!isEmpty())
        {
            task = poll();
        }

        if (task != null)
        {
            // save runtime thread to task
            task.setTaskThread(thread);

            // set the status to task
            task.setState(TaskWarpper.TASK_STATE_RUNNING);
        }

        return task;
    }

    synchronized void wakeup()
    {
        notify();
    }

    /**
     * remove a task form the queue
     *
     * @param task
     */
    synchronized boolean removeTask(TaskWarpper task)
    {
        boolean ret = false;
        if (task == null)
        {
            return ret;
        }
        if (!isEmpty())
        {
            ret = remove(task);
        }
        task.setState(TaskWarpper.TASK_STATE_CANCEL);

        return ret;
    }
}
