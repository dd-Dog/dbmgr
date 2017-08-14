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
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dbmgr.R;
import com.example.dbmgr.db.DbConstants;
import com.example.dbmgr.db.ShopDAO;
import com.example.dbmgr.db.ShopDAORemote;
import com.example.dbmgr.utils.ArraysUtil;
import com.example.dbmgr.utils.Task;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.security.AccessController.getContext;

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
    private EditText etDb;
    private String remoteDb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setTitle("数据库管理");
        initView();
        initData();

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        Log.e("HomeActivity", "screen width=" + width + ",height=" + height);
    }

    private void initData() {
        SharedPreferences preferences = getSharedPreferences(DbConstants.SHARED_PREFENCES, Context.MODE_PRIVATE);
        remoteIP = preferences.getString(DbConstants.REMOTE_IP, "");
        loginName = preferences.getString(DbConstants.LOGIN_NAME, "");
        loginPsw = preferences.getString(DbConstants.LOGIN_PSW, "");
        remoteDb = preferences.getString(DbConstants.REMTOE_DB, "");
        etIP.setText(remoteIP);
        etName.setText(loginName);
        etPsw.setText(loginPsw);
        etDb.setText(remoteDb);
    }

    private void initView() {
        findViewById(R.id.btn_native).setOnClickListener(this);
        findViewById(R.id.btn_remote).setOnClickListener(this);
        findViewById(R.id.btn_syncDown).setOnClickListener(this);
        findViewById(R.id.btn_syncUp).setOnClickListener(this);
        etIP = (EditText) findViewById(R.id.et_ip);
        etName = (EditText) findViewById(R.id.et_name);
        etPsw = (EditText) findViewById(R.id.et_psw);
        etDb = (EditText) findViewById(R.id.et_database);
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
                remoteDb = etDb.getText().toString().trim();
                progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("正在连接远端数据库");
                progressDialog.show();
                new ConnThread().start();
                break;

            case R.id.btn_syncDown:
                checkDb();
                break;
            case R.id.btn_syncUp:

                break;
        }
    }

    /**
     * 对比数据库是否一致
     */
    private void checkDb() {
        ShopDAO shopDAO = new ShopDAO(this);
        final List<String> nativeCol = shopDAO.getColumns();
        Task.asyncTask(new Runnable() {
            List<String> remoteCol;

            @Override
            public void run() {
                try {
                    Class.forName("net.sourceforge.jtds.jdbc.Driver"); //加载驱动
                    conn = DriverManager.getConnection("jdbc:jtds:sqlserver://" + "192.168.0.103" + ":1433/" + "shop", "sa", "test"); //建立连接字符串
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ShopDAORemote shopDAORemote = new ShopDAORemote(conn, ActivityHome.this);
                remoteCol = shopDAORemote.getColumns();

                int compare = ArraysUtil.compare(nativeCol, remoteCol);
                switch (compare) {
                    case DbConstants.DB_EMPTY:
                        Toast.makeText(getApplicationContext(), "数据库为空", Toast.LENGTH_SHORT).show();
                        break;
                    case DbConstants.DB_NOT_EQAUL:
                        Toast.makeText(getApplicationContext(), "数据库字段不匹配", Toast.LENGTH_SHORT).show();
                        break;
                    case DbConstants.DB_EQUAL:

                        break;
                }
            }
        });
    }

    class ConnThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver"); //加载驱动
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://" + remoteIP + ":1433/" + remoteDb, loginName, loginPsw); //建立连接字符串
                if (conn != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ActivityHome.this, "连接成功", Toast.LENGTH_SHORT).show();
                            SharedPreferences preferences = getSharedPreferences(DbConstants.SHARED_PREFENCES, Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = preferences.edit();
                            edit.putString(DbConstants.REMOTE_IP, remoteIP);
                            edit.putString(DbConstants.REMTOE_DB, remoteDb);
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

    @Override
    protected void onPause() {
        super.onPause();
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        event.isTracking();
        return super.onKeyUp(keyCode, event);
    }
}
