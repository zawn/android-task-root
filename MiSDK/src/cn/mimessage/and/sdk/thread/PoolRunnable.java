package cn.mimessage.and.sdk.thread;

public interface PoolRunnable
{
    /**
     * Starts executing the active part of the class' code in thread pool. This
     * method is called when a thread is started that has been created with a
     * class which implements PoolRunnable.
     * 
     * @throws InterruptedException
     */
    public void run() throws InterruptedException;
}
