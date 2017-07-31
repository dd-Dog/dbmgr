package com.example.dbmgr.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class ShopDAO {

	private Context mContext;
	private ShopDbHelper mDbHelper;

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
			String[] whereArgs = new String[] { number };
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
			String[] whereArgs = new String[] { number };
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
	public int findType(String number) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		int type = -1;
		if (db != null) {
			String sql = "select " + DbConstants.PRICE
					+ " from " + DbConstants.TABLE_NAME + " where " + DbConstants.SERIAL_NUM
					+ " = ?";
			String[] selectionArgs = new String[] { number };
			Cursor cursor = db.rawQuery(sql, selectionArgs);
			if (cursor != null) {
				if (cursor.moveToNext()) {
					type = cursor.getInt(0);
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
