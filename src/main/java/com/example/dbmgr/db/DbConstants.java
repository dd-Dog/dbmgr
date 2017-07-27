package com.example.dbmgr.db;

/**
 * Created by bianjb on 2017/7/28.
 */

class DbConstants {
    public static final String DB_NAME = "test.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "puchase";
    public static final String CREATE_TABLE = "create table " + TABLE_NAME +
            " (_id integer primary key autoincrement," +
            "serialnum varchar(20) unique, price double)";

}
