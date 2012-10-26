package cn.mimessage.and.sdk.sdcard.exception;

import java.io.IOException;

@SuppressWarnings("serial")
public class ExternalStorageFullException extends IOException
{

    public ExternalStorageFullException()
    {
    }

    public ExternalStorageFullException(String detailMessage)
    {
        super(detailMessage);
    }

}
