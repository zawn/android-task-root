package cn.mimessage.and.sdk.sqlite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DBOperator implements IDBOperator
{
    private List<IDBEditor> mEditors;
    private List<IDBObserver> mObservers;

    public DBOperator(SQLiteHelper helper)
    {
        if (helper == null)
        {
            throw new RuntimeException(
                    "SQLiteHelper should not be null in Constructor.");
        }

        mEditors = new ArrayList<IDBEditor>();
        mObservers = new ArrayList<IDBObserver>();
        DBEditor.mSQLiteHelper = helper;
        helper.getWritableDatabase();
    }

    @Override
    public boolean activateEditor(IDBEditor editor)
    {
        synchronized (mEditors)
        {
            if (mEditors == null || editor == null)
            {
                throw new RuntimeException(
                        "mEditors or editor should not be null in activeEditor().");
            }

            if (mEditors.size() != 0 && getEditor(editor.getClass()) != null)
            {
                throw new IllegalArgumentException(
                        "The editor is already activated, you should unregist it first.");
            }

            boolean isSuccess = mEditors.add(editor);
            editor.onActivate(this);
            notifyObservers(isSuccess, editor, IDBEditor.OP_ON_ACTIVATE);
            return isSuccess;
        }
    }

    @Override
    public boolean unRegistEditor(IDBEditor editor)
    {
        synchronized (mEditors)
        {
            if (mEditors == null)
            {
                return true;
            }
            boolean isSuccess = mEditors.remove(editor);
            editor.onDestory(this);
            notifyObservers(isSuccess, editor, IDBEditor.OP_ON_DESTORY);
            return isSuccess;
        }
    }

    @Override
    public boolean registObserver(IDBObserver ob)
    {
        synchronized (mObservers)
        {
            if (mObservers == null || ob == null)
            {
                throw new RuntimeException(
                        "mObservers or ob should not be null in registObserver().");
            }

            return mObservers.add(ob);
        }
    }

    @Override
    public boolean unRegistObserver(IDBObserver ob)
    {
        synchronized (mObservers)
        {
            if (mObservers == null || ob == null)
            {
                throw new RuntimeException(
                        "mObservers or ob should not be null in unRegistObserver().");
            }

            if (mObservers.size() == 0)
            {
                return true;
            }

            return mObservers.remove(ob);
        }
    }

    @Override
    public boolean doAdd(Class<?> editor, Object bundle)
    {
        synchronized (mEditors)
        {
            final IDBEditor e = getEditor(editor);
            boolean isSuccess = e.onAdd(bundle);
            notifyObservers(isSuccess, e, IDBEditor.OP_ADD);
            return isSuccess;
        }
    }

    @Override
    public boolean doDelete(Class<?> editor, Object bundle)
    {
        synchronized (mEditors)
        {
            final IDBEditor e = getEditor(editor);
            boolean isSuccess = e.onDelete(bundle);
            notifyObservers(isSuccess, e, IDBEditor.OP_DEL);
            return isSuccess;
        }
    }

    @Override
    public boolean doUpdate(Class<?> editor, Object bundle)
    {
        synchronized (mEditors)
        {
            final IDBEditor e = getEditor(editor);
            boolean isSuccess = e.onUpdate(bundle);
            notifyObservers(isSuccess, e, IDBEditor.OP_UPDATE);
            return isSuccess;
        }
    }

    @Override
    public boolean doQuery(Class<?> editor, Object bundle)
    {
        synchronized (mEditors)
        {
            final IDBEditor e = getEditor(editor);
            boolean isSuccess = e.onQuery(bundle);
            notifyObservers(isSuccess, e, IDBEditor.OP_QUERY);
            return isSuccess;
        }
    }

    @Override
    public void doBroadcast(int what, Object bundle)
    {
        if (mEditors == null || bundle == null)
        {
            throw new RuntimeException("One of params should not be null.");
        }

        synchronized (mEditors)
        {
            Iterator<IDBEditor> it = mEditors.iterator();
            while (it.hasNext())
            {
                it.next().onBroadCast(what, bundle);
            }
        }

        notifyObservers(true, null, IDBEditor.OP_BROADCAST);
    }

    @Override
    public IDBEditor getEditor(Class<?> clazz)
    {
        if (mEditors == null || clazz == null)
        {
            throw new RuntimeException("One of params should not be null.");
        }

        synchronized (mEditors)
        {
            if (mEditors.size() == 0)
            {
                throw new RuntimeException(
                        "You should activate your db editor first.");
            }

            IDBEditor e;
            Iterator<IDBEditor> it = mEditors.iterator();
            while (it.hasNext())
            {
                e = it.next();
                if (e.getClass().equals(clazz))
                {
                    return e;
                }
            }
        }

        throw new RuntimeException("You should activate your db editor first.");
    }

    private void notifyObservers(boolean result, IDBEditor editor, int operation)
    {
        if (mObservers == null)
        {
            throw new RuntimeException("One of params should not be null.");
        }

        synchronized (mObservers)
        {
            if (mObservers.size() == 0)
            {
                return;
            }

            Iterator<IDBObserver> it = mObservers.iterator();
            while (it.hasNext())
            {
                it.next().onChange(result, editor, operation);
            }
        }
    }

    @Override
    public void close()
    {
        if (DBEditor.mSQLiteHelper == null)
        {
            throw new RuntimeException("DBEditor.mSQLiteHelper is null.");
        }

        DBEditor.mSQLiteHelper.close();
    }
}
