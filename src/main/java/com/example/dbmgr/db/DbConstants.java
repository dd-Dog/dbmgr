package com.example.dbmgr.db;

import android.content.Context;

import com.example.dbmgr.R;

import java.util.ArrayList;

/**
 * Created by bianjb on 2017/7/28.
 */

public class DbConstants {

    private final String[] dbColums;

    public DbConstants(Context context) {
        dbColums = context.getResources().getStringArray(R.array.dbColums);
        initSql();
    }

    private void initSql() {
        StringBuilder createTable = new StringBuilder();
        createTable.append("create table ");
        createTable.append(TABLE_NAME);
        createTable.append(" (_id integer primary key autoincrement,");
        for (int i = 0; i < dbColums.length; i++) {
            createTable.append(dbColums[i]);
            createTable.append(" varchar(20) unique,");
        }
    }

    public static final String DB_NAME = "test.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "purchase";
    public static final String SERIAL_NUM = "serialnum";
    public static final String PRICE = "price";
    public static final String REMOTE_IP = "remoteip";
    public static final String LOGIN_NAME = "loginname";
    public static final String LOGIN_PSW = "loingpsw";
    public static final String REMTOE_DB = "remotedb";

    public static final String CREATE_TABLE = "create table " + TABLE_NAME +
            " (_id integer primary key autoincrement," +
            "serialnum varchar(20) unique, price double, product varchar(50), unit varchar(50)), count integer";

    public static final String SHARED_PREFENCES = "dbinfo";
    public static final int DB_NOT_EQAUL = -1;
    public static final int DB_EMPTY = -2;
    public static final int DB_EQUAL = 0;
}
