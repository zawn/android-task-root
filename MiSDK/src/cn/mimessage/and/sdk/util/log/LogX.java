package cn.mimessage.and.sdk.util.log;

import android.util.Log;
import cn.mimail.misdk.BuildConfig;

public class LogX extends Thread
{
    /**
     * 日志名
     */
    private static final String TAG = "== LogTrace ==";

    public static void e(Object object, String err)
    {
        if (BuildConfig.DEBUG)
        {
            Log.e(getPureClassName(object), err);
        }
    }

    public static void d(Object object, String debug)
    {
        if (BuildConfig.DEBUG)
        {
            Log.d(getPureClassName(object), debug);
        }
    }

    public static void i(Object object, String info)
    {
        if (BuildConfig.DEBUG)
        {
            Log.i(getPureClassName(object), info);
        }
    }

    public static void w(Object object, String info)
    {
        if (BuildConfig.DEBUG)
        {
            Log.w(getPureClassName(object), info);
        }
    }
    
    public static void v(Object object, String info)
    {
    	if (BuildConfig.DEBUG)
    	{
    		Log.v(getPureClassName(object), info);
    	}
    }

    /**
     * 可跳转的LOG日志
     * 
     * @param object
     *            日志发起类
     * @param tr
     *            定位行异常
     */
    public static void jw(Object object, Throwable tr)
    {
        if (BuildConfig.DEBUG)
        {
            Log.w(getPureClassName(object), "", filterThrowable(tr));
        }
    }

    /**
     * 可跳转的LOG日志
     * 
     * @param object
     *            日志发起类
     * @param tr
     *            定位行异常
     */
    public static void je(Object object, Throwable tr)
    {
        if (BuildConfig.DEBUG)
        {
            Log.e(getPureClassName(object), "", filterThrowable(tr));
        }
    }

    private static Throwable filterThrowable(Throwable tr)
    {
        StackTraceElement[] ste = tr.getStackTrace();
        tr.setStackTrace(new StackTraceElement[] { ste[0] });
        return tr;
    }

    private static String getPureClassName(Object object)
    {
        if (object == null)
        {
            Log.e(TAG, "getPureClassName() : object is null.");
        }
        String name = object.getClass().getName();
        if ("java.lang.String".equals(name))
        {
            return object.toString();
        }
        int idx = name.lastIndexOf('.');
        if (idx > 0)
        {
            return name.substring(idx + 1);
        }
        return name;
    }
}
