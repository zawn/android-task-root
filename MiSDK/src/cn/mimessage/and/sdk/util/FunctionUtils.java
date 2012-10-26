package cn.mimessage.and.sdk.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONStringer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import cn.mimessage.and.sdk.util.log.LogX;

public final class FunctionUtils
{
    static final String TAG = "FunctionUtils";

    public static byte[] bitmapToByte(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Object getValue(Object instance, String fieldName) throws IllegalAccessException,
            NoSuchFieldException
    {
        Class<?> clazz = instance.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field fe : fields)
        {
            LogX.d(TAG, fe.getName());
        }
        Field field = clazz.getDeclaredField(fieldName);
        // 参数值为true，禁用访问控制检查
        field.setAccessible(true);
        return field.get(instance);
    }

    /**
     * 安装应用
     */
    public static void installAPP(Context context, String path)
    {
        File mainFile = new File(path);
        Uri data = Uri.fromFile(mainFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(data, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 获得apk版本号
     */
    public static int getVersionCode(Context context)
    {
        try
        {
            final String PackageName = context.getPackageName();
            return context.getPackageManager().getPackageInfo(PackageName, 0).versionCode;
        }
        catch (NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return -1;
    }

    public static void showIMFPanel(final Context context, Timer timer, int delay)
    {
        if (delay <= 0)
        {
            showIMFPanel(context);
            return;
        }
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                showIMFPanel(context);
            }
        }, delay);
    }

    public static void showIMFPanel(final Context context)
    {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void hideIMFPanel(final Context context, View text)
    {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(text.getWindowToken(), 0);
    }

    /**
     * 编码url请求参数
     * 
     * @param params
     * @return
     * @throws HttpException
     */
    public static String encodeParameters(List<NameValuePair> params) throws HttpException
    {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < params.size(); i++)
        {
            if (i != 0)
            {
                buf.append("&");
            }
            try
            {
                buf.append(URLEncoder.encode(params.get(i).getName(), "UTF-8")).append("=")
                        .append(URLEncoder.encode(params.get(i).getValue(), "UTF-8"));
            }
            catch (java.io.UnsupportedEncodingException neverHappen)
            {
                throw new HttpException(neverHappen.getMessage(), neverHappen);
            }
        }
        return buf.toString();
    }

    /**
     * 构造用于post网络访问的JSON对象
     * 
     * @param params
     *            存放参数的键值对
     * @return　JSON 的string格式
     * @throws HttpException
     */
    public static String getRequestJson(List<NameValuePair> params)
    {
        JSONStringer js = new JSONStringer();
        try
        {
            js.object();
            for (int i = 0; i < params.size(); i++)
            {
                js.key(params.get(i).getName()).value(params.get(i).getValue());
            }
            js.endObject();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
        return js.toString();
    }

    /**
     * inputStream转String方法
     * 
     * @param in
     * @return
     * @throws IOException
     */
    public static String inputStream2String(InputStream in) throws IOException
    {
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1;)
        {
            out.append(new String(b, 0, n));
        }
        return out.toString();
    }

    /**
     * activity 跳转
     * 
     * @param context
     * @param dstClass
     */
    public static void redirectActivity(Context context, Class<?> dstClass)
    {
        final Intent i = new Intent();
        i.setClass(context, dstClass);
        context.startActivity(i);
    }

    /**
     * 显示Toast：Toast.LENGTH_LONG
     * 
     * @param context
     * @param msg
     */
    public static void showToastLong(Context context, String msg)
    {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * 显示Toast：Toast.LENGTH_SHORT
     * 
     * @param context
     * @param msg
     */
    public static void showToastShort(Context context, String msg)
    {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 直接拨打电话
     */
    public static void call(Context context, String phoneNumber)
    {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber))
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 进入拨号界面
     */
    public static void dial(Context context, String phoneNumber)
    {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber))
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void sendSMS()
    {
        String smsContent = "10sssss2";
        // note: SMS must be divided before being sent
        android.telephony.SmsManager sms = android.telephony.SmsManager.getDefault();
        List<String> texts = sms.divideMessage(smsContent);
        for (String text : texts)
        {
            sms.sendTextMessage("5554", null, text, null, null);
        }

        LogX.e("sendSMS()", "sendSMS() finished.");
    }

    /**
     * 进入发送短信界面
     */
    public static void composeSMS(Context context, String phoneNumber, String content)
    {
        Uri uri = Uri.parse("smsto:" + phoneNumber);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        it.putExtra("sms_body", content);
        context.startActivity(it);
    }

    /**
     * 发送邮件
     */
    public static void mailTo(Context context)
    {
        // Setup the recipient in a String array
        String[] mailto = { "noam@gmail.com" };
        // Create a new Intent to send messages
        Intent sendIntent = new Intent(Intent.ACTION_SEND).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Write the body of theEmail
        String emailBody = "You're password is: ";
        // Add attributes to the intent
        // sendIntent.setType("text/plain"); // use this line for testing
        // in the emulator
        sendIntent.setType("message/rfc822"); // use this line for testing
                                              // on the real phone
        sendIntent.putExtra(Intent.EXTRA_EMAIL, mailto);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Your Password");
        sendIntent.putExtra(Intent.EXTRA_TEXT, emailBody);
        context.startActivity(sendIntent);
    }
}
