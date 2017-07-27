package com.example.dbmgr.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BalckDbHelper extends SQLiteOpenHelper {

	public BalckDbHelper(Context context) {
		super(context, DbConstants.DB_NAME, null, DbConstants.DB_VERSION);

	}

	/**
	 * 第一次创建数据库执行
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		//创建表
		db.execSQL(DbConstants.CREATE_TABLE);
	}

	/**
	 * 数据库升级的时候执行
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
