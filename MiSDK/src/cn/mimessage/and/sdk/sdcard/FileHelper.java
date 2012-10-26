package cn.mimessage.and.sdk.sdcard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.http.util.EncodingUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;

import cn.mimessage.and.sdk.sdcard.exception.ExternalStorageFullException;
import cn.mimessage.and.sdk.sdcard.exception.ExternalStorageUnmountedException;
import cn.mimessage.and.sdk.sdcard.exception.InternalStorageFullException;
import cn.mimessage.and.sdk.util.log.LogX;

public final class FileHelper
{
    static final String TAG = "FileHelper";

    public static final int ERROR_UNKNOW = -1;

    public static final int ERROR_SDCARD_FULL = -2;

    public static final int ERROR_SDCARD_UNMOUNTED = -3;

    public static final int STORAGE_TYPE_INTERNAL = 0;

    public static final int STORAGE_TYPE_SD = 1;

    private final static int FILE_BUFFER_LENGTH = 512;

    /**
     * 文件是否已经存在
     */
    public static boolean checkFileExist(final String fileName)
    {
        if (fileName == null)
        {
            return false;
        }

        final File file = new File(fileName);
        if (file.exists())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * 文件是否已经存在
     */
    public static boolean checkFileExist(final File file)
    {
        if (file == null)
        {
            return false;
        }
        else if (file.exists())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * 检查SD卡
     */
    public static void checkExternalStorage() throws ExternalStorageFullException, ExternalStorageUnmountedException
    {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            throw new ExternalStorageFullException("External Storage is full.");
        }
        else
        {
            throw new ExternalStorageUnmountedException("External Storage Unmounted.");
        }
    }

    /**
     * 获取剩余存储大小
     */
    public static long getAvailStorage(int type)
    {
        long available = ERROR_UNKNOW;
        if (type == STORAGE_TYPE_INTERNAL)
        {
            StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
            available = stat.getBlockSize() * stat.getAvailableBlocks();
        }
        else if (type == STORAGE_TYPE_SD)
        {
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
            available = stat.getBlockSize() * stat.getAvailableBlocks();
        }
        return available;
    }

    /**
     * 获取文件大小
     */
    public static long getFileSize(final String fileName)
    {
        long length = ERROR_UNKNOW;
        File file = new File(fileName);
        if (checkFileExist(file))
        {
            length = file.length();
        }
        return length;
    }

    /**
     * 文件关闭
     */
    public static void closeRandomAccessFile(RandomAccessFile file)
    {
        if (file != null)
        {
            try
            {
                file.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建文件夹
     */
    public static File createDir(String fileName)
    {
        File dir = new File(fileName);
        if (!checkFileExist(dir))
        {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 通过提供的文件名生成文件
     */
    public static RandomAccessFile createRAFile(String fileFullName) throws IOException
    {
        return new RandomAccessFile(createFile(fileFullName), "rw");
    }

    /**
     * 通过提供的文件名在默认路径下生成文件
     */
    public static File createFile(String fileName) throws IOException
    {
        File file = new File(fileName);
        if (!checkFileExist(file))
        {
            file.createNewFile();
            return file;
        }
        if (!file.isDirectory())
        {
            file.delete();
        }
        file.createNewFile();
        return file;
    }

    /**
     * 将文件保存在缓存目录中
     */
    public static void cacheFile(byte[] data, String path, String fileName) throws ExternalStorageFullException,
            ExternalStorageUnmountedException, InternalStorageFullException
    {
        saveAsFile(data, path, getCachedFileFullName(path, fileName, true));
    }

    /**
     * 将文件保存在缓存目录中
     */
    public static void cacheFile(InputStream is, String path, String fileName) throws ExternalStorageFullException,
            ExternalStorageUnmountedException, InternalStorageFullException
    {
        saveAsFile(is, path, getCachedFileFullName(path, fileName, true));
    }

    /**
     * 从缓存中获取文件
     */
    public static byte[] getCachedFileData(String internalPath, String fileName) throws FileNotFoundException,
            InternalStorageFullException, ExternalStorageFullException, ExternalStorageUnmountedException
    {
        byte[] data = null;
        try
        {
            openFileInputStream(getCachedFileFullName(internalPath, fileName, false)).read(data);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            whyIOException(internalPath, fileName);
        }
        return data;
    }

    /**
     * 从缓存中获取图片
     */
    public static Bitmap getCachedBitmap(String internalPath, String fileName) throws FileNotFoundException,
            InternalStorageFullException
    {
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeStream(openFileInputStream(getCachedFileFullName(internalPath, fileName, true)));
        return bitmap;
    }

    /**
     * 将数据保存为文件
     */
    public static void saveAsFile(byte[] data, String internalPath, String fileFullName)
            throws InternalStorageFullException, ExternalStorageFullException, ExternalStorageUnmountedException
    {
        if (data == null || data.length <= 0 || fileFullName == null || "".equals(fileFullName))
        {
            LogX.e(TAG, "saveAsFile(byte[]) : save file error.");
            return;
        }
        LogX.d(TAG, "saveAsFile(byte[]) data.length : " + data.length);

        RandomAccessFile file = null;
        try
        {
            file = new RandomAccessFile(createFile(fileFullName), "rw");
            file.write(data);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            whyIOException(internalPath, fileFullName);
        }
        finally
        {
            closeRandomAccessFile(file);
        }
    }

    /**
     * 将数据保存为文件,改方法没有finally closeRandomAccessFile(file),
     * 需要手动closeRandomAccessFile(file);
     */
    public static void saveAsFileByAppend(byte[] data, RandomAccessFile file) throws InternalStorageFullException,
            ExternalStorageFullException, ExternalStorageUnmountedException
    {
        if (data == null || data.length <= 0 || file == null)
        {
            LogX.e(TAG, "saveAsFileByAppend(byte[]) : save file error.");
            return;
        }
        LogX.d(TAG, "saveAsFile(byte[]) data.length : " + data.length);

        try
        {
            file.seek(file.length());
            file.write(data);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 将数据保存为文件
     */
    public static void saveAsFile(InputStream is, String internalPath, String fileFullName)
            throws InternalStorageFullException, ExternalStorageFullException, ExternalStorageUnmountedException
    {
        if (is == null || fileFullName == null || "".equals(fileFullName))
        {
            LogX.e(TAG, "saveAsFile(InputStream) : save file error.");
            return;
        }

        byte[] buffer = new byte[FILE_BUFFER_LENGTH];
        RandomAccessFile file = null;
        try
        {
            file = new RandomAccessFile(createFile(fileFullName), "rw");
            while (is.read(buffer) != -1)
            {
                file.write(buffer);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            whyIOException(internalPath, fileFullName);
        }
        finally
        {
            closeRandomAccessFile(file);
        }
    }

    /**
     * 从本地文件中读取文件信息
     */
    public static String readTextFile(String fileName, String encode)
    {
        RandomAccessFile rndFile = null;
        byte[] buffer = new byte[FILE_BUFFER_LENGTH];
        StringBuffer content = new StringBuffer();
        try
        {
            LogX.d(TAG, "readFile() -> fileName : " + fileName.toString());
            File file = new File(fileName);
            if (!checkFileExist(file))
            {
                return content.toString();
            }
            if (file.length() > 1024 * 1024)
            {
                throw new IOException("The file to read is too large.");
            }

            rndFile = new RandomAccessFile(file, "r");
            while (rndFile.read(buffer) != -1)
            {
                content.append(new String(buffer, encode));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            closeRandomAccessFile(rndFile);
        }
        return content.toString();
    }

    /**
     * 获取文件输入流
     */
    public static FileInputStream openFileInputStream(String fileFullName) throws FileNotFoundException
    {
        LogX.d(TAG, "getFileInputStream() fileFullName : " + fileFullName);
        File file = new File(fileFullName);
        return new FileInputStream(file);
    }

    /**
     * 从assets资源中获取文件并读取数据
     */
    public static String getTextFromAssets(final Context context, String fileName, String encode)
    {
        String result = "";
        try
        {
            InputStream in = openAssets(context, fileName);
            // 获取文件的字节数
            int lenght = in.available();
            // 创建byte数组
            byte[] buffer = new byte[lenght];
            // 将文件中的数据读到byte数组中
            in.read(buffer);
            result = EncodingUtils.getString(buffer, encode.toLowerCase());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 从assets资源中获取输入流
     */
    public static InputStream openAssets(final Context context, String name) throws IOException
    {
        return context.getResources().getAssets().open(name);
    }

    /**
     * 从raw资源中获取文件并读取数据
     */
    public static String getTextFromRaw(final Context context, int res, String encode)
    {
        String result = "";
        try
        {
            InputStream in = openRaw(context, res);
            // 获取文件的字节数
            int lenght = in.available();
            // 创建byte数组
            byte[] buffer = new byte[lenght];
            // 将文件中的数据读到byte数组中
            in.read(buffer);
            result = EncodingUtils.getString(buffer, encode);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 从raw资源中获取输入流
     */
    public static InputStream openRaw(final Context context, int res) throws IOException
    {
        return context.getResources().openRawResource(res);
    }

    /**
     * 删除文件
     */
    public static boolean deleteFile(final String fileName)
    {
        boolean sucess = false;
        File file = new File(fileName);
        if (file.exists())
        {
            sucess = file.delete();
        }
        else
        {
            sucess = true;
        }
        return sucess;
    }

    /**
     * 文件分割
     */
    public static void split(String internalPath, String fileName, int sizeKB) throws FileNotFoundException,
            InternalStorageFullException, ExternalStorageFullException, ExternalStorageUnmountedException
    {
        sizeKB *= 1024;
        // 待分割的文件
        final File splitFile = new File(fileName);
        // 取得文件的大小
        final long fileLength = splitFile.length();
        // 取得要分割的个数
        final int count = (int) (fileLength / sizeKB);
        // 打开要分割的文件
        RandomAccessFile in = null;
        try
        {
            in = new RandomAccessFile(splitFile, "r");
            // 根据要分割的数目输出文件
            for (int i = 0; i <= count; i++)
            {
                if (i == count)
                {
                    // 最后的分割文件
                    int leftLength = (int) (fileLength - (count * sizeKB));
                    copyFile(fileName + "." + i + ".part", leftLength, in);
                }
                else
                {
                    copyFile(fileName + "." + i + ".part", sizeKB, in);
                }
            }
            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            whyIOException(internalPath, fileName);
        }
        finally
        {
            closeRandomAccessFile(in);
        }
    }

    /**
     * 文件合并
     */
    public static void concat(String internalPath, String fullFileName, String splitedFileDir,
            final String endNameFilter) throws InternalStorageFullException, ExternalStorageFullException,
            ExternalStorageUnmountedException, FileNotFoundException
    {
        String[] fileNames = null;
        // 在当前目录下的文件
        File splitedFile = new File(splitedFileDir);
        // 取得输出名
        File outFile = new File(fullFileName);
        if (checkFileExist(outFile))
        {
            outFile.delete();
        }

        // 取得符合条件的文件名
        fileNames = splitedFile.list(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                String nameStr = new File(name).toString();
                return nameStr.endsWith(endNameFilter);
            }
        });

        RandomAccessFile out = null;
        try
        {
            out = new RandomAccessFile(outFile, "rw");
            byte[] buffer = new byte[FILE_BUFFER_LENGTH];
            // 打开所有的文件再写入到一个文件里
            for (int i = fileNames.length - 1; i >= 0; i--)
            {
                LogX.d(TAG, fileNames[i]);
                splitedFile = new File(splitedFileDir + File.separator + fileNames[i]);
                RandomAccessFile in = new RandomAccessFile(splitedFile, "r");
                while (in.read(buffer) != -1)
                {
                    out.write(buffer);
                }
                in.close();
            }
            out.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            whyIOException(internalPath, fullFileName);
        }
        finally
        {
            closeRandomAccessFile(out);
        }
    }

    /**
     * 非递归遍历文件路径
     */
    public static LinkedList<File> listLinkedFiles(String strPath)
    {
        LinkedList<File> list = new LinkedList<File>();
        File dir = new File(strPath);
        File file[] = dir.listFiles();
        for (int i = 0; i < file.length; i++)
        {
            if (file[i].isDirectory())
                list.add(file[i]);
            else
                System.out.println(file[i].getAbsolutePath());
        }
        File tmp;
        while (!list.isEmpty())
        {
            tmp = list.removeFirst();
            if (tmp.isDirectory())
            {
                file = tmp.listFiles();
                if (file == null)
                    continue;
                for (int i = 0; i < file.length; i++)
                {
                    if (file[i].isDirectory())
                        list.add(file[i]);
                    else
                        System.out.println(file[i].getAbsolutePath());
                }
            }
            else
            {
                System.out.println(tmp.getAbsolutePath());
            }
        }
        return list;
    }

    /**
     * 递归遍历文件路径
     */
    public static ArrayList<File> listFiles(String strPath)
    {
        ArrayList<File> filelist = new ArrayList<File>();
        File dir = new File(strPath);
        File[] files = dir.listFiles();

        if (files == null)
            return null;
        for (int i = 0; i < files.length; i++)
        {
            if (files[i].isDirectory())
            {
                listFiles(files[i].getAbsolutePath());
            }
            else
            {
                if (files[i].getName().toLowerCase().endsWith("zip"))
                    filelist.add(files[i]);
            }
        }
        return filelist;
    }

    /**
     * 从输入文件中复制定长的数据,并且保存成新文件
     * 
     * @param fileName
     *            保存的文件名
     * @param size
     *            保存文件的大小
     * @param in
     *            源文件
     * @throws IOException
     */
    private static void copyFile(String fileName, int size, RandomAccessFile in) throws IOException
    {
        byte[] buffer = new byte[FILE_BUFFER_LENGTH];
        File outFile = new File(fileName);
        if (checkFileExist(outFile))
        {
            outFile.delete();
        }
        RandomAccessFile out = new RandomAccessFile(outFile, "rw");
        int position = 0;
        while (position < size)
        {
            int length = 0;
            if (position + FILE_BUFFER_LENGTH <= size)
            {
                length = FILE_BUFFER_LENGTH;
            }
            else
            {
                length = size - position;
            }
            position += length;
            in.read(buffer, 0, length);
            out.write(buffer);
        }
        out.close();
    }

    /**
     * 获取缓存文件绝对路径
     */
    private static String getCachedFileFullName(String path, String fileName, boolean needSuffix)
            throws InternalStorageFullException
    {
        StringBuffer fullName = new StringBuffer(path);
        int dotIndex = fileName.indexOf('.');
        LogX.d(TAG, "getCachedFileFullName() dotIndex : " + dotIndex);
        if (needSuffix && dotIndex < 0)
        {
            throw new InternalStorageFullException("File name doesn't contain suffix name.");
        }

        int lastSepratorIndex = fileName.lastIndexOf(File.separator, dotIndex);
        LogX.d(TAG, "getCachedFileFullName() lastSepratorIndex : " + lastSepratorIndex);
        if (lastSepratorIndex >= 0)
        {
            fullName.append(fileName);
        }
        else
        {
            fullName.append(File.separator + fileName);
        }
        LogX.d(TAG, "getCachedFileFullName() fullName.toString() : " + fullName.toString());
        return fullName.toString();
    }

    /**
     * 获取IOException的原因
     */
    private static void whyIOException(String internalPath, String fileName) throws InternalStorageFullException,
            ExternalStorageFullException, ExternalStorageUnmountedException
    {
        if (fileName.contains(internalPath))
        {
            throw new InternalStorageFullException(TAG + ".whyIOException()");
        }
        else
        {
            checkExternalStorage();
        }
    }
}
