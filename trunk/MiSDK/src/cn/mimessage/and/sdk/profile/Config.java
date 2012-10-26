package cn.mimessage.and.sdk.profile;

import cn.mimessage.and.sdk.net.IHttpConnectionFactory;
import cn.mimessage.and.sdk.sdcard.IPersistentStore;
import cn.mimessage.and.sdk.thread.IThreadPool;

public abstract class Config
{
    public static final String BUILD_NUMBER = "1000";
    protected static final String CONFIG_SOURCE = "CONFIG";
    public static final int SIGNAL_LOW = 3;
    public static final int SIGNAL_NONE = 255;
    public static final int STANDARD_SCREEN_DPI = 160;

    public abstract int getPixelsPerInch();

    public abstract IHttpConnectionFactory getConnectionFactory();

    public abstract IPersistentStore getPersistentStore();

    public abstract String getVersion();

    public abstract double getScreenDensityScale();

    public abstract String getNetworkType();

    public abstract IThreadPool getThreadPool();

}
