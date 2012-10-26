package cn.mimessage.and.sdk.sdcard.exception;

import java.io.IOException;

@SuppressWarnings("serial")
public class InternalStorageFullException extends IOException
{

    public InternalStorageFullException()
    {
    }

    public InternalStorageFullException(String detailMessage)
    {
        super(detailMessage);
    }

}
