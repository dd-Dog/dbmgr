package com.example.dbmgr.db;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.dbmgr.utils.ArraysUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bianjb on 2017/7/31.
 */

public class ShopDAORemote {
    private static final String TAG = "ShopDAORemote";
    private final String remoteDb;
    public Connection conn;
    private final SharedPreferences preferences;
    private final String remoteIP;
    private final String loginName;
    private final String loginPsw;

    public ShopDAORemote(Connection conn, Activity context) {
        this.conn = conn;
        preferences = context.getSharedPreferences(DbConstants.SHARED_PREFENCES, Context.MODE_PRIVATE);
        remoteIP = preferences.getString(DbConstants.REMOTE_IP, "");
        loginName = preferences.getString(DbConstants.LOGIN_NAME, "");
        loginPsw = preferences.getString(DbConstants.LOGIN_PSW, "");
        remoteDb = preferences.getString(DbConstants.REMTOE_DB, "");
    }

    public boolean insert(String serial, String price) {
        PreparedStatement stmt = null;

        try {
            if (conn == null || conn.isClosed())
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://" + remoteIP + ":1433/" + remoteDb, loginName, loginPsw);
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
            if (conn == null || conn.isClosed())
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://" + remoteIP + ":1433/" + remoteDb, loginName, loginPsw);
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
            if (conn == null || conn.isClosed())
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://" + remoteIP + ":1433/" + remoteDb, loginName, loginPsw);
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

    public ArrayList<ShopInfo> query(String serail) {
        Statement stmt = null;
        ArrayList<ShopInfo> list = new ArrayList<>();
        try {
            if (conn == null || conn.isClosed())
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://" + remoteIP + ":1433/" + remoteDb, loginName, loginPsw);
            stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery("select * FROM " + DbConstants.TABLE_NAME + "where serialnum=" + serail);
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

    /**
     * 查询所有字段
     *
     * @return
     */
    public ArrayList<ShopInfo> findAll() {
        Statement stmt = null;
        ArrayList<ShopInfo> list = new ArrayList<>();
        try {
            if (conn == null || conn.isClosed())
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://" + remoteIP + ":1433/" + remoteDb, loginName, loginPsw);
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

    /**
     * 获取表中所有字段名
     *
     * @return
     */
    public List<String> getColumns() {
        List<String> columns = new ArrayList<>();
        Statement stmt = null;
        try {
            if (conn == null || conn.isClosed())
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://" + remoteIP + ":1433/" + remoteDb, loginName, loginPsw);
            stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery("select name from syscolumns Where ID=OBJECT_ID('purchase')");
            while (resultSet.next()) {
                String str = resultSet.getString(1);
                Log.e(TAG, str);
                columns.add(resultSet.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        columns = ArraysUtil.sort(columns);
        return columns;
    }

    /**
     * 查询所有表
     *
     * @return
     */
    public ArrayList<String> getTables() {
        Statement stmt = null;
        ArrayList<String> list = new ArrayList<>();
        try {
            if (conn == null || conn.isClosed())
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://" + remoteIP + ":1433/" + remoteDb, loginName, loginPsw);
            stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery("select name from sysobjects where xtype='u'");
            while (resultSet.next()) {
                list.add(resultSet.getString(1));
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
