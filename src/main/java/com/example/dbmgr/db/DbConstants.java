package com.example.dbmgr.db;

/**
 * Created by bianjb on 2017/7/28.
 */

public class DbConstants {
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
            "serialnum varchar(20) unique, price double)";

    public static final String SHARED_PREFENCES = "dbinfo";
    public static final int DB_NOT_EQAUL = -1;
    public static final int DB_EMPTY = -2;
    public static final int DB_EQUAL = 0;
}
