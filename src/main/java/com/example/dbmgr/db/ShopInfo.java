package com.example.dbmgr.db;

/**
 * Created by bianjb on 2017/7/31.
 */

public class ShopInfo {
    public String serial;
    public String price;
    public ShopInfo(String serial, String price) {
        this.serial = serial;
        this.price = price + "";
    }
}
