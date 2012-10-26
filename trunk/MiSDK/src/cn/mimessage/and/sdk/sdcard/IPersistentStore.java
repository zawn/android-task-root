package cn.mimessage.and.sdk.sdcard;

import java.io.IOException;

public interface IPersistentStore
{
    public static final int PERSISTENT_STORE_FULL = 254;
    public static final int PERSISTENT_STORE_WRITE_FAILED = 255;

    public long getFreeSpace(int type);

    // 系统属性部分
    public void clearPreferences();

    public boolean setPreference(String key, byte[] data);

    public byte[] readPreference(String key);

    // 文件系统部分
    public void savePreferences();

    public String[] listBlocks(String name);

    public int writeBlock(byte[] data, String name)
            throws IPersistentStore.PersistentStoreException;

    public boolean deleteBlock(String name);

    public void deleteAllBlocks(String name);

    public byte[] readBlock(String name);

    public int getBlockSize(String name);

    public int getDataSize(String name);

    public class PersistentStoreException extends IOException
    {
        /**
         * 序列化UID
         */
        private static final long serialVersionUID = -9117014394101743387L;
        private final int type;

        public PersistentStoreException(int arg2)
        {
            super();
            this.type = 0;
        }

        public int getType()
        {
            return this.type;
        }
    }
}
