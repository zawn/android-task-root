package cn.mimessage.and.sdk.sqlite;

public interface IDBEditor
{
    public static final int OP_ADD = 0;
    public static final int OP_DEL = 1;
    public static final int OP_UPDATE = 2;
    public static final int OP_QUERY = 3;
    public static final int OP_BROADCAST = 4;
    public static final int OP_ON_ACTIVATE = 5;
    public static final int OP_ON_DESTORY = 6;

    public boolean onAdd(Object bundle);

    public boolean onDelete(Object bundle);

    public boolean onUpdate(Object bundle);

    public boolean onQuery(Object bundle);

    public void onBroadCast(int what, Object bundle);

    public void onActivate(IDBOperator op);

    public void onDestory(IDBOperator op);
}
