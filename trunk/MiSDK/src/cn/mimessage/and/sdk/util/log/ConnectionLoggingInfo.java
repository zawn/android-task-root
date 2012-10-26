package cn.mimessage.and.sdk.util.log;

public interface ConnectionLoggingInfo
{
    public static final int PROTOCOL_HTTP = 1;
    public static final int PROTOCOL_HTTPS = 2;
    public static final int PROTOCOL_TCP = 0;
    public static final int PROTOCOL_UNDEFINED = 255;

    public long getConnectTime();

    public int getCreationDuration();

    public long getCreationTime();

    public int getDataLength();

    public int getProtocol();

    public int getResponseDuration();

    public String getSetting();
}
