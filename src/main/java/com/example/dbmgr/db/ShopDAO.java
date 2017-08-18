package com.example.dbmgr.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.example.dbmgr.utils.ArraysUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ShopDAO {

    private Context mContext;
    private ShopDbHelper mDbHelper;
    private static final String TAG = "ShopDAO";

    public ShopDAO(Context mContext) {
        super();
        this.mContext = mContext;
        mDbHelper = new ShopDbHelper(mContext);
    }

    /**
     * 添加一条
     *
     * @param number
     * @param type
     * @return
     */
    public boolean add(String number, String type) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = -1;
        if (db != null) {
            ContentValues values = new ContentValues();
            values.put("serialnum", number);
            values.put("price", type);
            // 参1 表明 参2数据为空时的默认值 参3要添加的数据
            id = db.insert(DbConstants.TABLE_NAME, null, values);
            db.close();
        }
        return id != -1;
    }

    /**
     * 删除一条
     *
     * @param number
     * @return
     */
    public boolean delete(String number) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int num = 0;
        if (db != null) {
            // 条件
            String whereClause = DbConstants.SERIAL_NUM + " = ?";
            // 条件里?对应的值
            String[] whereArgs = new String[]{number};
            num = db.delete(DbConstants.TABLE_NAME, whereClause, whereArgs);
            db.close();
        }
        return num > 0;
    }

    /**
     * 更新一条
     *
     * @param number
     * @return
     */
    public boolean update(String number, String newType) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int num = 0;
        if (db != null) {
            // 更新的数据
            ContentValues values = new ContentValues();
            // values.put(DbConstants.COLUMN_NUMBER, number);
            values.put(DbConstants.PRICE, newType);
            // 条件
            String whereClause = DbConstants.SERIAL_NUM + " = ?";
            // 条件里?对应的值
            String[] whereArgs = new String[]{number};
            num = db.update(DbConstants.TABLE_NAME, values, whereClause,
                    whereArgs);
            db.close();
        }
        return num > 0;
    }

    /**
     * 根据编号查询
     *
     * @return
     */
    public String findType(String number) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String type = "";
        if (db != null) {
            String sql = "select " + DbConstants.PRICE
                    + " from " + DbConstants.TABLE_NAME + " where " + DbConstants.SERIAL_NUM
                    + " = ?";
            String[] selectionArgs = new String[]{number};
            Cursor cursor = db.rawQuery(sql, selectionArgs);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    type = cursor.getString(0);
                }
                cursor.close();
            }
            db.close();
        }
        return type;
    }

    /**
     * 查询所有的数据
     *
     * @return
     */
    public ArrayList<ShopInfo> findAll() {
        ArrayList<ShopInfo> infos = new ArrayList<ShopInfo>();

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        if (db != null) {
            String sql = "select " + DbConstants.SERIAL_NUM + ","
                    + DbConstants.PRICE + " from " + DbConstants.TABLE_NAME;
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String number = cursor.getString(0);
                    String type = cursor.getString(1);
                    ShopInfo info = new ShopInfo(number, type);
                    infos.add(info);
                }
                cursor.close();
            }
            db.close();
        }
        return infos;
    }

    /**
     * 获取所有列
     *
     * @return
     */
    public List<String> getColumns() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        List<String> columns = new ArrayList<>();
        if (db != null) {
//			String checkTableColumns = "select name from syscolumns Where ID=OBJECT_ID('purchase')";
            //select name from sqlite_master where type='table' order by name;
            String checkTableColumns = "pragma table_info([purchase])";
            Cursor cursor = db.rawQuery(checkTableColumns, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String str = cursor.getString(1);
                    if (TextUtils.equals(str, "_id"))
                        continue;
                    columns.add(str);
//                    Log.e(TAG, str);
                }
            }

            columns = ArraysUtil.sort(columns);
//            for (int i=0; i<columns.size(); i++) {
//                Log.e(TAG, columns.get(i));
//            }
        }
        return columns;
    }

    /**
     * 清空本地表并添加
     */
    public void clearAndAdd(ArrayList<ShopInfo> remoteData) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        if (db != null) {
            //清空表数据
            db.execSQL("DELETE FROM " + DbConstants.TABLE_NAME);
            //自增长ID为0
            db.execSQL("update sqlite_sequence SET seq = 0 where name = '" + DbConstants.TABLE_NAME + "'");
            //添加数据
            ContentValues values = null;
            ShopInfo shopInfo = null;
            long id = -1;
            for (int i = 0; i < remoteData.size(); i++) {
                shopInfo = remoteData.get(i);
                values = new ContentValues();
                values.put("serialnum", shopInfo.serial);
                values.put("price", shopInfo.price);
                // 参1 表明 参2数据为空时的默认值 参3要添加的数据
                id = db.insert(DbConstants.TABLE_NAME, null, values);
                if (id <= 0) {
                    Log.e(TAG, shopInfo + "添加失败");
                }
            }
            db.close();
        }
    }

    /**
     * 向表中追加数据，如果有ID相同的则数据会被覆盖
     */
    public void append(ArrayList<ShopInfo> remoteData) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        if (db != null) {
            //查询本地数据库
            ArrayList<ShopInfo> infos = new ArrayList<ShopInfo>();
            String sql = "select " + DbConstants.SERIAL_NUM + ","
                    + DbConstants.PRICE + " from " + DbConstants.TABLE_NAME;
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String number = cursor.getString(0);
                    String type = cursor.getString(1);
                    ShopInfo info = new ShopInfo(number, type);
                    infos.add(info);
                }
                cursor.close();
            }
            db.close();
            //对比并合并数据
            ArrayList<ShopInfo> union = ArraysUtil.union(infos, remoteData);
            clearAndAdd(union);
        }
    }

    /**
     *
     * @param size
     *            查询的数量
     * @param offset
     *            偏移量   如果offset是10  从第11条开始查
     *
     * @return
     */
//	public ArrayList<ShopInfo> findPart(int size, int offset) {
//		ArrayList<ShopInfo> infos = new ArrayList<ShopInfo>();
//
//		SQLiteDatabase db = mDbHelper.getWritableDatabase();
//
//		// select * from blacklist limit 5 offset 10
//		if (db != null) {
//			String sql = "select " + DbConstants.COLUMN_NUMBER + ","
//					+ DbConstants.COLUMN_TYPE + " from blacklist limit " + size
//					+ " offset " + offset;
//			Cursor cursor = db.rawQuery(sql, null);
//			if (cursor != null) {
//				while (cursor.moveToNext()) {
//					String number = cursor.getString(0);
//					int type = cursor.getInt(1);
//					ShopInfo info = new ShopInfo(number, type);
//					infos.add(info);
//				}
//				cursor.close();
//			}
//			db.close();
//		}
//		return infos;
//	}
}
