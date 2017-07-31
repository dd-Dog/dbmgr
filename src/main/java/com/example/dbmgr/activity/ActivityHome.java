package com.example.dbmgr.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dbmgr.R;
import com.example.dbmgr.db.DbConstants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bianjb on 2017/7/31.
 */

public class ActivityHome extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog progressDialog;
    public static Connection conn;
    public EditText etIP;
    public EditText etName;
    public EditText etPsw;
    private String remoteIP;
    private String loginName;
    private String loginPsw;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setTitle("数据库管理");
        findViewById(R.id.btn_native).setOnClickListener(this);
        findViewById(R.id.btn_remote).setOnClickListener(this);
        etIP = (EditText) findViewById(R.id.et_ip);
        etName = (EditText) findViewById(R.id.et_name);
        etPsw = (EditText) findViewById(R.id.et_psw);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_native:
                startActivity(new Intent(this, ActivityNative.class));
                break;
            case R.id.btn_remote:
                remoteIP = etIP.getText().toString().trim();
                loginName = etName.getText().toString().trim();
                loginPsw = etPsw.getText().toString().trim();
                progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("正在连接远端数据库");
                progressDialog.show();
                new ConnThread().start();
                break;
        }
    }

    class ConnThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver"); //加载驱动
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://" + remoteIP + ":1433/shop", loginName, loginPsw); //建立连接字符串
                if (conn != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ActivityHome.this, "连接成功", Toast.LENGTH_SHORT).show();
                            SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = preferences.edit();
                            edit.putString(DbConstants.REMOTE_IP, remoteIP);
                            edit.putString(DbConstants.LOGIN_NAME, loginName);
                            edit.putString(DbConstants.LOGIN_PSW, loginPsw);
                            edit.commit();
                        }
                    });
                    startActivity(new Intent(ActivityHome.this, ActivityRemote.class));
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ActivityHome.this, "连接失败", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ActivityHome.this, "连接失败", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (SQLException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ActivityHome.this, "连接失败", Toast.LENGTH_SHORT).show();
                    }
                });
            } finally {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    if (conn != null) {
                        try {
                            conn.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
