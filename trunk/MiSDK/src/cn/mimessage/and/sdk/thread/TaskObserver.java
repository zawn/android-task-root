package cn.mimessage.and.sdk.thread;

import java.util.TimerTask;

public interface TaskObserver
{
    /**
     * The states of a task
     */
    public static final int RESPONSE_SUCCESS = 0;

    public static final int RESPONSE_TIMEOUT_PRE_RUN = 1;

    public static final int RESPONSE_TIMEOUT_RUNNING = 2;

    /**
     * response to the current task
     * 
     * @param state
     */
    public void onTaskResponse(int state);

    /**
     * task cancel call back
     */
    public void onTaskCancel();

    /**
     * setup a timeout task to execute.
     * 
     * @param timeoutTask
     */
    public void setTimeoutTask(TimerTask timeoutTask);

    /**
     * start timeout task
     */
    public void startTimeoutTimer();

    /**
     * cancel timeout task
     */
    public void stopTimeoutTimer();
}
