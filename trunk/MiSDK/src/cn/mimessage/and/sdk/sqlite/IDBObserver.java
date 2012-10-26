package cn.mimessage.and.sdk.sqlite;

public interface IDBObserver
{
    public int RESULT_SUCCESS = 4;
    public int RESULT_ERROR = 5;

    public void onChange(boolean result, IDBEditor editor, int operation);
}
