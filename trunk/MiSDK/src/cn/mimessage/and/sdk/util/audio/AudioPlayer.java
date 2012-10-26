package cn.mimessage.and.sdk.util.audio;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import cn.mimessage.and.sdk.util.log.LogX;

public class AudioPlayer implements IAudioPlayer, OnCompletionListener
{
    private MediaPlayer mMediaPlayer;
    private Context mContext;
    private int mStatus;

    public AudioPlayer(Context context)
    {
        mMediaPlayer = new MediaPlayer();
        mContext = context;
        mStatus = UNREALIZED;
        mMediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void destroy()
    {
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    @Override
    public int getStatus()
    {
        return mStatus;
    }

    @Override
    public boolean prepare(String path)
    {
        if (mMediaPlayer == null || mStatus == PLAYING)
        {
            return false;
        }

        final MediaPlayer mp = mMediaPlayer;
        try
        {
            mp.setDataSource(path);
            mp.prepare();
            mStatus = READY;
            return true;
        }
        catch (IllegalArgumentException e)
        {
            LogX.jw(this, e);
        }
        catch (IllegalStateException e)
        {
            LogX.jw(this, e);
        }
        catch (IOException e)
        {
            LogX.jw(this, e);
        }
        mStatus = UNREALIZED;
        return false;
    }

    @Override
    public boolean prepare(int resid)
    {
        if (mMediaPlayer == null || mStatus == PLAYING)
        {
            return false;
        }

        try
        {
            AssetFileDescriptor afd = mContext.getResources().openRawResourceFd(resid);
            if (afd == null)
            {
                mStatus = UNREALIZED;
                return false;
            }

            final MediaPlayer mp = mMediaPlayer;
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mp.prepare();

            mStatus = READY;
            return true;
        }
        catch (IOException ex)
        {
            LogX.jw(this, ex);
        }
        catch (IllegalArgumentException ex)
        {
            LogX.jw(this, ex);
        }
        catch (SecurityException ex)
        {
            LogX.jw(this, ex);
        }
        mStatus = UNREALIZED;
        return false;
    }

    @Override
    public void startAudio()
    {
        mStatus = PLAYING;
        mMediaPlayer.start();
    }

    @Override
    public void stopAudio()
    {
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mStatus = STOP;
    }

    @Override
    public void onCompletion(MediaPlayer mp)
    {
        stopAudio();
    }
}
