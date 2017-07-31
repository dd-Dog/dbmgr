package com.example.dbmgr.db;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by bianjb on 2017/7/31.
 */

public class ShopDAORemote {
    public Connection conn;
    private final SharedPreferences preferences;
    private final String remoteIP;
    private final String loginName;
    private final String loginPsw;

    public ShopDAORemote(Connection conn, Activity context) {
        this.conn = conn;
        preferences = context.getPreferences(Context.MODE_PRIVATE);
        remoteIP = preferences.getString(DbConstants.REMOTE_IP, "");
        loginName = preferences.getString(DbConstants.LOGIN_NAME, "");
        loginPsw = preferences.getString(DbConstants.LOGIN_PSW, "");
    }

    public boolean insert(String serial, String price) {
        PreparedStatement stmt = null;

        try {
            conn = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.0.101:1433/shop", "sa", "test");
            stmt = conn.prepareStatement("INSERT INTO " + DbConstants.TABLE_NAME + " VALUES(?,?)");
            stmt.setString(1, serial);
            stmt.setString(2, price);
            int i = stmt.executeUpdate();
            if (i > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean update(String serial, String price) {
        PreparedStatement stmt = null;

        try {
            conn = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.0.101:1433/shop", "sa", "test");
            stmt = conn.prepareStatement("UPDATE " + DbConstants.TABLE_NAME + " SET price=? WHERE serialnum=?");
            stmt.setString(1, price);
            stmt.setString(2, serial);

            int i = stmt.executeUpdate();
            if (i > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean delete(String serial) {
        Statement stmt = null;

        try {
            conn = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.0.101:1433/shop", "sa", "test");
            stmt = conn.createStatement();
            int i = stmt.executeUpdate("DELETE FROM " + DbConstants.TABLE_NAME + " WHERE serialnum=" + serial);
            if (i > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<ShopInfo> findAll() {
        Statement stmt = null;
        ArrayList<ShopInfo> list = new ArrayList<>();
        try {
            conn = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.0.101:1433/shop", "sa", "test");
            stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery("select * FROM " + DbConstants.TABLE_NAME);
            while (resultSet.next()) {
                list.add(new ShopInfo(resultSet.getString(1), resultSet.getString(2)));
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
