package com.example.dbmgr.activity;

import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.dbmgr.R;
import com.example.dbmgr.db.DbConstants;
import com.example.dbmgr.db.ShopDAO;
import com.example.dbmgr.db.ShopDAORemote;
import com.example.dbmgr.db.ShopInfo;
import com.example.dbmgr.receiver.OTGBroadcastReceiver;
import com.example.dbmgr.utils.ArraysUtil;
import com.example.dbmgr.utils.Task;
import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.partition.Partition;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

/**
 * Created by bianjb on 2017/7/31.
 */

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "HomeActivity";
    private static final int DOWNLOAD = 100;
    private static final int UPNLOAD = 101;
    private static final int APPEND = 201;
    private static final int CLEAR_ADD = 200;
    private int selectedDownMode;
    private int selectedUpMode;
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
    private ShopDAORemote shopDAORemote;
    private ShopDAO shopDAO;
    private Spinner downSpinner;
    private Spinner upSpinner;
    private Dialog remoteConfigDialog;
    private Dialog mEditRemoteConfigDialog;
    private int currentMotivation;
    private AlertDialog mAlertDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setTitle("数据库管理");
        initView();
        initData();

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        Log.e("HomeActivity", "screen width=" + width + ",height=" + height);

        //test

    }





    @Override
    public void setContentViewId() {
        setContentView(R.layout.activity_home);
    }

    private void initData() {
        SharedPreferences preferences = getSharedPreferences(DbConstants.SHARED_PREFENCES, Context.MODE_PRIVATE);
        remoteIP = preferences.getString(DbConstants.REMOTE_IP, "");
        loginName = preferences.getString(DbConstants.LOGIN_NAME, "");
        loginPsw = preferences.getString(DbConstants.LOGIN_PSW, "");
        remoteDb = preferences.getString(DbConstants.REMTOE_DB, "");

        String[] downItems = getResources().getStringArray(R.array.downItems);
        downSpinner.setAdapter(new ArrayAdapter(getApplicationContext(),
                R.layout.item, R.id.tv_item, downItems));
        String[] upItems = getResources().getStringArray(R.array.upItems);
        upSpinner.setAdapter(new ArrayAdapter(getApplicationContext(),
                R.layout.item, R.id.tv_item, upItems));
    }

    private void initView() {
        findViewById(R.id.btn_native).setOnClickListener(this);
        findViewById(R.id.btn_remote).setOnClickListener(this);
        findViewById(R.id.btn_syncDown).setOnClickListener(this);
        findViewById(R.id.btn_syncUp).setOnClickListener(this);
        findViewById(R.id.btn_mgr).setOnClickListener(this);
        downSpinner = (Spinner) findViewById(R.id.sp_download);
        upSpinner = (Spinner) findViewById(R.id.sp_upload);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_mgr:
                startActivity(new Intent(this, MgrActivity.class));
                break;
            case R.id.btn_native:
                startActivity(new Intent(this, LocalActivity.class));
                break;
            case R.id.btn_remote:
                showRemoteConfigDialog();
                break;
            case R.id.btn_syncDown:
                if (TextUtils.isEmpty(remoteDb) || TextUtils.isEmpty(remoteIP)
                        || TextUtils.isEmpty(loginPsw) || TextUtils.isEmpty(loginName)) {
                    editRemoteConfigDialog();
                    return;
                }
                currentMotivation = DOWNLOAD;
                checkDb(DOWNLOAD);
                break;
            case R.id.btn_syncUp:
                if (TextUtils.isEmpty(remoteDb) || TextUtils.isEmpty(remoteIP)
                        || TextUtils.isEmpty(loginPsw) || TextUtils.isEmpty(loginName)) {
                    editRemoteConfigDialog();
                    return;
                }
                currentMotivation = UPNLOAD;
                checkDb(UPNLOAD);
                break;
            case R.id.btn_confirm:
                remoteIP = etIP.getText().toString().trim();
                loginName = etName.getText().toString().trim();
                loginPsw = etPsw.getText().toString().trim();
                remoteDb = etDb.getText().toString().trim();
                progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("正在连接远端数据库");
                progressDialog.show();
                new ConnThread().start();
                break;
            case R.id.btn_cancel:
                dismissRemoteConfigDialog();
                break;
        }
    }

    private void showAlertDialog() {
        mAlertDialog = new AlertDialog.Builder(this).create();
        mAlertDialog.setContentView(R.layout.dialog_alert);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_cancel:
                        mAlertDialog.dismiss();
                        break;
                    case R.id.btn_confirm:
                        checkDb(currentMotivation);
                        break;
                }
            }
        };
        mAlertDialog.findViewById(R.id.btn_cancel).setOnClickListener(onClickListener);
        mAlertDialog.findViewById(R.id.btn_confirm).setOnClickListener(onClickListener);
        mAlertDialog.show();

    }

    private void dismissAlertDialog() {
        if (mAlertDialog != null)
            mAlertDialog.dismiss();
    }

    /**
     * 编辑连接
     */
    private void editRemoteConfigDialog() {
        mEditRemoteConfigDialog = new Dialog(this, R.style.dialog_style);
        mEditRemoteConfigDialog.setContentView(R.layout.edit_dialog_remote_config);
        mEditRemoteConfigDialog.setCanceledOnTouchOutside(false);
        etIP = (EditText) mEditRemoteConfigDialog.findViewById(R.id.et_ip);
        etName = (EditText) mEditRemoteConfigDialog.findViewById(R.id.et_name);
        etPsw = (EditText) mEditRemoteConfigDialog.findViewById(R.id.et_psw);
        etDb = (EditText) mEditRemoteConfigDialog.findViewById(R.id.et_database);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_edit_cancel:
                        mEditRemoteConfigDialog.dismiss();
                        break;
                    case R.id.btn_save:
                        remoteIP = etIP.getText().toString().trim();
                        loginName = etName.getText().toString().trim();
                        loginPsw = etPsw.getText().toString().trim();
                        remoteDb = etDb.getText().toString().trim();
                        saveParams();
                        showAlertDialog();
                        break;
                }
            }
        };
        mEditRemoteConfigDialog.findViewById(R.id.btn_edit_cancel).setOnClickListener(onClickListener);
        mEditRemoteConfigDialog.findViewById(R.id.btn_save).setOnClickListener(onClickListener);
        mEditRemoteConfigDialog.show();
    }


    private void saveParams() {
        SharedPreferences preferences = getSharedPreferences(DbConstants.SHARED_PREFENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(DbConstants.REMOTE_IP, remoteIP);
        edit.putString(DbConstants.REMTOE_DB, remoteDb);
        edit.putString(DbConstants.LOGIN_NAME, loginName);
        edit.putString(DbConstants.LOGIN_PSW, loginPsw);
        edit.commit();
    }

    private void showRemoteConfigDialog() {
        remoteConfigDialog = new Dialog(this, R.style.dialog_style);
        remoteConfigDialog.setContentView(R.layout.dialog_remote_config);
        remoteConfigDialog.setCanceledOnTouchOutside(false);
        etIP = (EditText) remoteConfigDialog.findViewById(R.id.et_ip);
        etName = (EditText) remoteConfigDialog.findViewById(R.id.et_name);
        etPsw = (EditText) remoteConfigDialog.findViewById(R.id.et_psw);
        etDb = (EditText) remoteConfigDialog.findViewById(R.id.et_database);
        remoteConfigDialog.findViewById(R.id.btn_cancel).setOnClickListener(this);
        remoteConfigDialog.findViewById(R.id.btn_confirm).setOnClickListener(this);
        etIP.setText(remoteIP);
        etName.setText(loginName);
        etPsw.setText(loginPsw);
        etDb.setText(remoteDb);
        remoteConfigDialog.show();
    }

    private void dismissRemoteConfigDialog() {
        if (remoteConfigDialog != null) {
            remoteConfigDialog.dismiss();
        }
    }

    /**
     * 检查数据库回调
     */
    CheckDbCallback checkDbCallback = new CheckDbCallback() {

        @Override
        public void isEqual(int status, int behavior) {
            switch (status) {
                case DbConstants.DB_EMPTY:
                    Toast.makeText(getApplicationContext(), "数据库为空", Toast.LENGTH_SHORT).show();
                    break;
                case DbConstants.DB_NOT_EQAUL:
                    Toast.makeText(getApplicationContext(), "数据库字段不匹配", Toast.LENGTH_SHORT).show();
                    break;
                case DbConstants.DB_EQUAL:
                    if (behavior == DOWNLOAD) {
                        long selectedItemId = downSpinner.getSelectedItemId();
                        switch ((int) selectedItemId) {
                            case 0:
                                selectedDownMode = CLEAR_ADD;
                                break;
                            case 1:
                                selectedDownMode = APPEND;
                                break;
                        }
                        //如果表结构一致，就下载数据
                        ArrayList<ShopInfo> remoteData = shopDAORemote.findAll();
                        //添加到当前数据库
                        add2Local(remoteData, selectedDownMode);

                    } else if (behavior == UPNLOAD) {
                        long selectedItemId = upSpinner.getSelectedItemId();
                        switch ((int) selectedItemId) {
                            case 0:
                                selectedUpMode = CLEAR_ADD;
                                break;
                            case 1:
                                selectedUpMode = APPEND;
                                break;
                        }
                        //从本地读取数据并上传
                        if (shopDAO != null) {
                            ArrayList<ShopInfo> localData = shopDAO.findAll();
                            add2Remote(localData, selectedUpMode);
                        }
                    }
                    break;
            }
        }
    };


    /**
     * 对比数据库是否一致
     */
    private void checkDb(final int behavior) {
        shopDAO = new ShopDAO(this);
        final List<String> nativeCol = shopDAO.getColumns();
        Task.asyncTask(new Runnable() {
            List<String> remoteCol;

            @Override
            public void run() {
                try {
                    Class.forName("net.sourceforge.jtds.jdbc.Driver"); //加载驱动
                    conn = DriverManager.getConnection("jdbc:jtds:sqlserver://" + remoteIP + ":1433/" + remoteDb, "sa", "test"); //建立连接字符串
                } catch (Exception e) {
                    e.printStackTrace();
                }
                shopDAORemote = new ShopDAORemote(conn, HomeActivity.this);
                remoteCol = shopDAORemote.getColumns();
                int compare = ArraysUtil.compare(nativeCol, remoteCol);
                checkDbCallback.isEqual(compare, behavior);
            }
        });
    }

    /**
     * 更新到远端
     *
     * @param localData
     * @param type
     */
    private void add2Remote(ArrayList<ShopInfo> localData, int type) {
        if (shopDAORemote == null) return;
        switch (type) {
            case APPEND:
                shopDAORemote.append(localData);
                printArr(shopDAORemote.findAll());
                break;
            case CLEAR_ADD:
                shopDAORemote.clearAndAdd(localData);
                printArr(shopDAORemote.findAll());
                break;
        }
    }

    /**
     * 更新到本地
     *
     * @param remoteData
     * @param type
     */
    private void add2Local(ArrayList<ShopInfo> remoteData, int type) {
        if (shopDAO == null) return;
        switch (type) {
            case APPEND:
                shopDAO.append(remoteData);
                printArr(shopDAO.findAll());
                break;
            case CLEAR_ADD:
                shopDAO.clearAndAdd(remoteData);
                printArr(shopDAO.findAll());
                break;
        }
    }

    public void printArr(ArrayList<ShopInfo> remoteData) {
        for (int i = 0; i < remoteData.size(); i++) {
            Log.e(TAG, remoteData.get(i).toString());
        }
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
                            Toast.makeText(HomeActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                            saveParams();
                        }
                    });
                    startActivity(new Intent(HomeActivity.this, MgrActivity.class));
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(HomeActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(HomeActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (SQLException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(HomeActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
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

    interface CheckDbCallback {
        void isEqual(int status, int behavior);
    }
}
