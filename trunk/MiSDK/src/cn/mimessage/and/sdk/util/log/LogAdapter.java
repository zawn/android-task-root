package cn.mimessage.and.sdk.util.log;

public interface LogAdapter
{
    public void logHttpFailure(ConnectionLoggingInfo connectionLoggingInfo);

    public void logHttpSuccess(ConnectionLoggingInfo connectionLoggingInfo);

    public void logHttpTimeout(ConnectionLoggingInfo connectionLoggingInfo);
}
