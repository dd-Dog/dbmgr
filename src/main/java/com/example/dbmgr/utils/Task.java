package com.example.dbmgr.utils;

/**
 * Created by bianjb on 2017/8/9.
 */

public class Task {

    public static void asyncTask(Runnable runnable) {
        new Thread(runnable).start();
    }
}
