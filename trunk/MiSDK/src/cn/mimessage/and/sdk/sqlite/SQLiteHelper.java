package cn.mimessage.and.sdk.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cn.mimessage.and.sdk.util.log.LogX;

public abstract class SQLiteHelper extends SQLiteOpenHelper
{
    /**
     * 构造函数
     *
     * @param context
     */
    public SQLiteHelper(Context context, String dbName, int version)
    {
        super(context, dbName, null, version);
    }

    /**
     * 插入数据
     */
    public long doInsert(String tableName, ContentValues values)
    {
        long rowId = 0;
        final SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try
        {
            rowId = db.insert(tableName, null, values);
            db.setTransactionSuccessful();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            db.endTransaction();
        }
        return rowId;
    }

    /**
     * 对数据库中数据的删除操作
     *
     * @param tableName
     *            删除操作中要操作的数据表的名称
     * @param whereArgs
     *            要删除的数据中提供的条件参数的名称
     * @param whereArgsValues
     *            要删除的数据中提供的条件参数的值
     */
    public int doDelete(String tableName, String[] whereArgs,
            String[] whereArgsValues)
    {
        int count = 0;
        final SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try
        {
            count = db.delete(tableName, buildQuerySQLParm(whereArgs),
                    whereArgsValues);
            db.setTransactionSuccessful();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            db.endTransaction();
        }
        return count;
    }

    /**
     * 对数据的更新操作
     *
     * @param tableName
     *            是所对应的操作表
     * @param values
     *            更新的数据名称和值组成的键值对
     * @param whereArgs
     *            要更新的数据集的条件参数
     * @param whereArgsValues
     *            要更新的数据集的条件参数的值
     */
    public int doUpdate(String tableName, ContentValues values,
            String[] whereArgs, String[] whereArgsValues)
    {
        int count = 0;
        final SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try
        {
            count = db.update(tableName, values, buildQuerySQLParm(whereArgs),
                    whereArgsValues);
            db.setTransactionSuccessful();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            db.endTransaction();
        }
        return count;
    }

    /**
     * 对数据进行的查询操作
     *
     * @param tableName
     *            表名
     * @param selections
     *            要查询的数据中提供的条件参数的名称
     * @param selectionArgs
     *            要查询的数据中提供的条件参数的值
     * @param column
     *            控制哪些字段返回结果（传null是返回所有字段的结果集）
     * @param orderBy
     *            是否对某字段进行排序（传null不进行排序）
     * @return 查找的数据集的游标
     */
    public Cursor doQuery(String table, String[] columns, String[] selections,
            String[] selectionArgs, String groupBy, String having,
            String orderBy)
    {
        Cursor cursor = null;
        final SQLiteDatabase db = this.getReadableDatabase();

        db.beginTransaction();
        try
        {
            cursor = db.query(table, columns, buildQuerySQLParm(selections),
                    selectionArgs, groupBy, having, orderBy);
            db.setTransactionSuccessful();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            db.endTransaction();
        }
        return cursor;
    }

    /**
     * 直接用SQL语句查询
     */
    public Cursor doQuery(String sql)
    {
        final SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(sql, null);
    }

    /**
     * 直接用SQL语句查询
     *
     * @see doQuery("select * form " + " some_table " + " where " + " id = 123 "
     *      + " AND " + " name = ? ", new String[] {name} );
     */
    public Cursor doQuery(String sql, String[] selectionArgs)
    {
        final SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(sql, selectionArgs);
    }

    /**
     * 直接执行SQL语句
     */
    public void executeSQL(String sql)
    {
        getWritableDatabase().execSQL(sql);
    }

    /**
     * 关闭数据库
     */
    public void closeDatabase()
    {
        try
        {
            close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LogX.e(this, "Close current Database failed!");
        }
    }

    /**
     * 关闭当前正在使用的数据库
     */
    public void closeDB(SQLiteDatabase db)
    {
        try
        {
            if (db != null && db.isOpen())
            {
                db.close();
                db = null;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LogX.e(this, "Close SQLiteDatabase failed!");
        }
    }

    /**
     * 将用户提供的参数拼接成一条完整的删除数据库的SQL语句
     */
    private String buildQuerySQLParm(String[] whereArgs)
    {
        if (whereArgs == null || whereArgs.length <= 0)
        {
            LogX.e(this, "createSQL() : parms are invalid!");
            return null;
        }

        StringBuffer sqlStr = new StringBuffer("");
        final int length = whereArgs.length;
        for (int i = 0; i < length; i++)
        {
            if (i > 0)
            {
                sqlStr.append(" AND ");
            }
            sqlStr.append(whereArgs[i]);
            sqlStr.append("=?");
        }

        LogX.d(this, "createSQL() sqlStr : " + sqlStr.toString());
        return sqlStr.toString();
    }
}
