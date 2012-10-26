package cn.mimessage.and.sdk.sdcard;

public class DefaultPersistentStore implements IPersistentStore
{

    public DefaultPersistentStore()
    {
    }

    @Override
    public void clearPreferences()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAllBlocks(String name)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean deleteBlock(String name)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getBlockSize(String name)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getDataSize(String name)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String[] listBlocks(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public byte[] readBlock(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public byte[] readPreference(String key)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void savePreferences()
    {

    }

    @Override
    public long getFreeSpace(int type)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean setPreference(String key, byte[] data)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int writeBlock(byte[] data, String name)
            throws PersistentStoreException
    {
        // TODO Auto-generated method stub
        return 0;
    }

}
