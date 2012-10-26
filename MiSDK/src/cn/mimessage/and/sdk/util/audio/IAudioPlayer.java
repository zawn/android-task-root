package cn.mimessage.and.sdk.util.audio;

public interface IAudioPlayer
{
    public static final int PLAYING = 3;
    public static final int READY = 2;
    public static final int STOP = 4;
    public static final int UNREALIZED = 0;

    public void destroy();

    public int getStatus();

    public boolean prepare(String path);

    public boolean prepare(int resid);

    public void startAudio();

    public void stopAudio();
}
