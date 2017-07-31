package com.example.dbmgr.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dbmgr.R;
import com.example.dbmgr.db.ShopDAO;
import com.example.dbmgr.db.ShopDAORemote;
import com.example.dbmgr.db.ShopInfo;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by bianjb on 2017/7/31.
 */

public class ActivityRemote extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ActivityRemote";
    private TextView tvResult;
    private EditText etUpdatePrice;
    private EditText etUpdateSerial;
    private EditText etQuerySerial;
    private EditText etDelSerial;
    private EditText etAddSerial;
    private EditText etAddPrice;
    private ShopDAORemote shopDAORemote;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote);
        getSupportActionBar().setTitle("远端数据库管理");
        initView();
        shopDAORemote = new ShopDAORemote(ActivityHome.conn);
    }

    private void initView() {
        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.btn_del).setOnClickListener(this);
        findViewById(R.id.btn_query).setOnClickListener(this);
        findViewById(R.id.btn_update).setOnClickListener(this);
        tvResult = (TextView) findViewById(R.id.tv_result);
        etAddPrice = (EditText) findViewById(R.id.et_add_price);
        etAddSerial = (EditText) findViewById(R.id.et_add_serial);
        etDelSerial = (EditText) findViewById(R.id.et_del_serial);
        etQuerySerial = (EditText) findViewById(R.id.et_query_serial);
        etUpdateSerial = (EditText) findViewById(R.id.et_update_serial);
        etUpdatePrice = (EditText) findViewById(R.id.et_update_price);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                final String addPrice = etAddPrice.getText().toString().trim();
                final String addSerial = etAddSerial.getText().toString().trim();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final boolean add = shopDAORemote.insert(addSerial, addPrice);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ActivityRemote.this, add ? "添加成功" : "添加失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
                updateData();
                break;
            case R.id.btn_del:
                final String delSerial = etDelSerial.getText().toString().trim();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final boolean delete = shopDAORemote.delete(delSerial);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ActivityRemote.this, delete ? "删除成功" : "删除失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
                updateData();
                break;
            case R.id.btn_query:
                final String querySerail = etQuerySerial.getText().toString().trim();
//                int type = shopDAORemote.findAll(querySerail);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final boolean delete = shopDAORemote.delete(querySerail);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });
                    }
                }).start();
                updateData();
                break;
            case R.id.btn_update:
                final String queryPrice = etUpdatePrice.getText().toString().trim();
                final String querySerial = etUpdateSerial.getText().toString().trim();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final boolean update = shopDAORemote.update(querySerial, queryPrice);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ActivityRemote.this, update ? "修改成功" : "修改失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
                updateData();
                break;
        }
    }

    private void updateData() {
        ArrayList<ShopInfo> all = shopDAORemote.findAll();
        String result = "";
        if (all == null) return;
        for (int i = 0; i < all.size(); i++) {
            ShopInfo shopInfo = all.get(i);
            result += shopInfo.serial + " : ";
            result += shopInfo.price + "\n";
        }
        tvResult.setText(result);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            ActivityHome.conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
