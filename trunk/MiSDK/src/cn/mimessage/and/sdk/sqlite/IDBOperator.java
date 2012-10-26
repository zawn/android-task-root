package cn.mimessage.and.sdk.sqlite;

public interface IDBOperator
{
    public boolean activateEditor(IDBEditor editor);

    public boolean unRegistEditor(IDBEditor editor);

    public IDBEditor getEditor(Class<?> editor);

    public boolean registObserver(IDBObserver ob);

    public boolean unRegistObserver(IDBObserver ob);

    public boolean doAdd(Class<?> editor, Object bundle);

    public boolean doDelete(Class<?> editor, Object bundle);

    public boolean doUpdate(Class<?> editor, Object bundle);

    public boolean doQuery(Class<?> editor, Object bundle);

    public void doBroadcast(int what, Object bundle);

    public void close();
}
