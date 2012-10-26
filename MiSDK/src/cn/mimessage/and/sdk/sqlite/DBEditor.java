package cn.mimessage.and.sdk.sqlite;

public abstract class DBEditor implements IDBEditor
{
    protected static SQLiteHelper mSQLiteHelper;

    @Override
    public boolean onAdd(Object bundle)
    {
        return false;
    }

    @Override
    public boolean onDelete(Object bundle)
    {
        return false;
    }

    @Override
    public boolean onUpdate(Object bundle)
    {
        return false;
    }

    @Override
    public boolean onQuery(Object bundle)
    {
        return false;
    }

    @Override
    public void onBroadCast(int what, Object bundle)
    {
    }

    @Override
    public void onActivate(IDBOperator op)
    {
    }

    @Override
    public void onDestory(IDBOperator op)
    {
    }
}
