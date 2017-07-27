package com.example.dbmgr.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BlackDAO {

	private Context mContext;
	private BalckDbHelper mDbHelper;

	public BlackDAO(Context mContext) {
		super();
		this.mContext = mContext;
		mDbHelper = new BalckDbHelper(mContext);
	}

	/**
	 * 添加一条黑名单
	 * 
	 * @param number
	 * @param type
	 * @return
	 */
	public boolean add(String number, int type) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		long id = -1;
		if (db != null) {
			ContentValues values = new ContentValues();
			values.put("name", number);
			values.put("price", type);
			// 参1 表明 参2数据为空时的默认值 参3要添加的数据
			id = db.insert(DbConstants.TABLE_NAME, null, values);
			db.close();
		}
		return id != -1;
	}

	/**
	 * 删除一条黑名单
	 * 
	 * @param number
	 * @return
	 */
	public boolean delete(String number) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		int num = 0;
		if (db != null) {
			// 条件
//			String whereClause = DbConstants.COLUMN_NUMBER + " = ?";
//			// 条件里?对应的值
//			String[] whereArgs = new String[] { number };
//			num = db.delete(DbConstants.TABLE_NAME, whereClause, whereArgs);
//			db.close();
		}
		return num > 0;
	}

	/**
	 * 更新一条黑名单
	 * 
	 * @param number
	 * @return
	 */
	public boolean update(String number, int newType) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		int num = 0;
		if (db != null) {
			// 更新的数据
//			ContentValues values = new ContentValues();
//			// values.put(DbConstants.COLUMN_NUMBER, number);
//			values.put(DbConstants.COLUMN_TYPE, newType);
//			// 条件
//			String whereClause = DbConstants.COLUMN_NUMBER + " = ?";
//			// 条件里?对应的值
//			String[] whereArgs = new String[] { number };
//			num = db.update(DbConstants.TABLE_NAME, values, whereClause,
//					whereArgs);
//			db.close();
		}
		return num > 0;
	}

	/**
	 * 根据电话查询拦截方式
	 * 
	 * @return
	 */
	public int findType(String number) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
//		int type = BlackInfo.TYPE_NONE;
//		if (db != null) {
//			String sql = "select " + DbConstants.COLUMN_TYPE
//					+ " from blacklist where " + DbConstants.COLUMN_NUMBER
//					+ " = ?";
//			String[] selectionArgs = new String[] { number };
//			Cursor cursor = db.rawQuery(sql, selectionArgs);
//			if (cursor != null) {
//				if (cursor.moveToNext()) {
//					type = cursor.getInt(0);
//				}
//				cursor.close();
//			}
//			db.close();
//		}
		return 0;
	}

	/**
	 * 查询所有的黑名单数据
	 * 
	 * @return
	 */
//	public ArrayList<BlackInfo> findAll() {
//		ArrayList<BlackInfo> infos = new ArrayList<BlackInfo>();
//
//		SQLiteDatabase db = mDbHelper.getWritableDatabase();
//		if (db != null) {
//			String sql = "select " + DbConstants.COLUMN_NUMBER + ","
//					+ DbConstants.COLUMN_TYPE + " from blacklist";
//			Cursor cursor = db.rawQuery(sql, null);
//			if (cursor != null) {
//				while (cursor.moveToNext()) {
//					String number = cursor.getString(0);
//					int type = cursor.getInt(1);
//					BlackInfo info = new BlackInfo(number, type);
//					infos.add(info);
//				}
//				cursor.close();
//			}
//			db.close();
//		}
//		return infos;
//	}

	/**
	 * 分页查询的黑名单数据
	 * 
	 * @param size
	 *            查询的数量
	 * @param offset
	 *            偏移量   如果offset是10  从第11条开始查
	 * 
	 * @return
	 */
//	public ArrayList<BlackInfo> findPart(int size, int offset) {
//		ArrayList<BlackInfo> infos = new ArrayList<BlackInfo>();
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
//					BlackInfo info = new BlackInfo(number, type);
//					infos.add(info);
//				}
//				cursor.close();
//			}
//			db.close();
//		}
//		return infos;
//	}
}
