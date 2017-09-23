package com.example.dbmgr.db;

/**
 * Created by bianjb on 2017/7/31.
 */

public class ShopInfo {
    public String name;
    public String price;
    public String unit;
    public int count;


    public ShopInfo(String name, String unit, String price, int count) {
        this.name = name;
        this.price = price;
        this.unit = unit;
        this.count = count;
    }

    @Override
    public String toString() {
        return "ShopInfo{" +
                "name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", unit='" + unit + '\'' +
                ", count=" + count +
                '}';
    }
}
