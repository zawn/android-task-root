package cn.mimessage.and.sdk.sdcard.exception;

import java.io.IOException;

@SuppressWarnings("serial")
public class ExternalStorageUnmountedException extends IOException
{

    public ExternalStorageUnmountedException()
    {
    }

    public ExternalStorageUnmountedException(String detailMessage)
    {
        super(detailMessage);
    }

}
