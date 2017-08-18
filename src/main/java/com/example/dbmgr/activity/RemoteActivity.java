package com.example.dbmgr.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dbmgr.R;
import com.example.dbmgr.db.ShopDAORemote;
import com.example.dbmgr.db.ShopInfo;
import com.example.dbmgr.utils.Task;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by bianjb on 2017/7/31.
 */

public class RemoteActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "RemoteActivity";
    private TextView tvResult;
    private EditText etUpdatePrice;
    private EditText etUpdateSerial;
    private EditText etQuerySerial;
    private EditText etDelSerial;
    private EditText etAddSerial;
    private EditText etAddPrice;
    private ShopDAORemote shopDAORemote;
    private ArrayList<String> tables;
    private Spinner spTables;
    private TextView tvQueryResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        shopDAORemote = new ShopDAORemote(HomeActivity.conn, this);
        Task.asyncTask(new Runnable() {
            @Override
            public void run() {
                tables = shopDAORemote.getTables();
                final ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.item, R.id.tv_item, tables);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        spTables.setAdapter(adapter);
                    }
                });
            }
        });
    }

    @Override
    public void setContentViewId() {
        setContentView(R.layout.activity_remote);
    }

    private void initView() {
        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.btn_del).setOnClickListener(this);
        findViewById(R.id.btn_query).setOnClickListener(this);
        findViewById(R.id.btn_update).setOnClickListener(this);
        spTables = (Spinner) findViewById(R.id.spinner);
        tvResult = (TextView) findViewById(R.id.tv_result);
        tvQueryResult = (TextView) findViewById(R.id.tv_query_result);
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
                Task.asyncTask(new Runnable() {
                    @Override
                    public void run() {
                        final boolean add = shopDAORemote.insert(addSerial, addPrice);
                        updateData();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RemoteActivity.this, add ? "添加成功" : "添加失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                break;
            case R.id.btn_del:
                final String delSerial = etDelSerial.getText().toString().trim();
                Task.asyncTask(new Runnable() {
                    @Override
                    public void run() {
                        final boolean delete = shopDAORemote.delete(delSerial);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RemoteActivity.this, delete ? "删除成功" : "删除失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                        updateData();
                    }

                });
                break;
            case R.id.btn_query:
                final String querySerail = etQuerySerial.getText().toString().trim();
                final ArrayList<ShopInfo> query = shopDAORemote.query(querySerail);
                Task.asyncTask(new Runnable() {
                    @Override
                    public void run() {
                        final boolean delete = shopDAORemote.delete(querySerail);
                        updateData();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (query.size() > 0) {
                                    if (query == null) return;
                                    result = "查询结果：\n";
                                    for (int i = 0; i < query.size(); i++) {
                                        ShopInfo shopInfo = query.get(i);
                                        result += shopInfo.serial + " : ";
                                        result += shopInfo.price + "\n";
                                    }
                                    tvQueryResult.setText(result);
                                    Toast.makeText(RemoteActivity.this, "查询成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RemoteActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                break;
            case R.id.btn_update:
                final String queryPrice = etUpdatePrice.getText().toString().trim();
                final String querySerial = etUpdateSerial.getText().toString().trim();
                Task.asyncTask(new Runnable() {
                    @Override
                    public void run() {
                        final boolean update = shopDAORemote.update(querySerial, queryPrice);
                        updateData();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RemoteActivity.this, update ? "修改成功" : "修改失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                break;
        }
    }

    String result = "";

    private void updateData() {
        ArrayList<ShopInfo> all = shopDAORemote.findAll();
        if (all == null) return;
        result = "";
        for (int i = 0; i < all.size(); i++) {
            ShopInfo shopInfo = all.get(i);
            result += shopInfo.serial + " : ";
            result += shopInfo.price + "\n";
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvResult.setText(result);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            HomeActivity.conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
