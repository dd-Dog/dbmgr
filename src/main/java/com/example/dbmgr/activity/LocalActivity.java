package com.example.dbmgr.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dbmgr.R;
import com.example.dbmgr.db.ShopDAO;
import com.example.dbmgr.db.ShopInfo;

import java.util.ArrayList;

public class LocalActivity extends BaseActivity implements View.OnClickListener {

    private ShopDAO blackDAO;
    private TextView tvResult;
    private EditText etUpdatePrice;
    private EditText etUpdateSerial;
    private EditText etQuerySerial;
    private EditText etDelSerial;
    private EditText etAddSerial;
    private EditText etAddPrice;
    private LinearLayout llResult;
    private TextView tvQueryResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        blackDAO = new ShopDAO(this);
    }

    @Override
    public void setContentViewId() {
        setContentView(R.layout.activity_native);
    }

    private void initView() {
        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.btn_del).setOnClickListener(this);
        findViewById(R.id.btn_query).setOnClickListener(this);
        findViewById(R.id.btn_update).setOnClickListener(this);
        tvResult = (TextView) findViewById(R.id.tv_result);
        tvQueryResult = (TextView) findViewById(R.id.tv_query_result);
        etAddPrice = (EditText) findViewById(R.id.et_add_price);
        etAddSerial = (EditText) findViewById(R.id.et_add_serial);
        etDelSerial = (EditText) findViewById(R.id.et_del_serial);
        etQuerySerial = (EditText) findViewById(R.id.et_query_serial);
        etUpdateSerial = (EditText) findViewById(R.id.et_update_serial);
        etUpdatePrice = (EditText) findViewById(R.id.et_update_price);
        llResult = (LinearLayout) findViewById(R.id.ll_result);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                String addPrice = etAddPrice.getText().toString().trim();
                String addSerial = etAddSerial.getText().toString().trim();
                boolean add = blackDAO.add(addSerial, addPrice);
                Toast.makeText(this, add ? "添加成功" : "添加失败", Toast.LENGTH_SHORT).show();
                updateData();
                break;
            case R.id.btn_del:
                String delSerial = etDelSerial.getText().toString().trim();
                boolean delete = blackDAO.delete(delSerial);
                Toast.makeText(this, delete ? "删除成功" : "删除失败", Toast.LENGTH_SHORT).show();
                updateData();
                break;
            case R.id.btn_query:
                String querySerail = etQuerySerial.getText().toString().trim();
                String type = blackDAO.findType(querySerail);
                if (TextUtils.isEmpty(type)) {
                    tvQueryResult.setText("查询结果：\n 未查询到");
                    Toast.makeText(this, "查询失败", Toast.LENGTH_SHORT).show();
                } else {
                    tvQueryResult.setText("查询结果：\n" + querySerail + ":" + type);
                    Toast.makeText(this, "查询成功", Toast.LENGTH_SHORT).show();
                }
                updateData();
                break;
            case R.id.btn_update:
                String queryPrice = etUpdatePrice.getText().toString().trim();
                String querySerial = etUpdateSerial.getText().toString().trim();
                boolean update = blackDAO.update(querySerial, queryPrice);
                Toast.makeText(this, update ? "修改成功" : "修改失败", Toast.LENGTH_SHORT).show();
                updateData();
                break;
        }
    }

    private void updateData() {
        ArrayList<ShopInfo> all = blackDAO.findAll();
        String result = "";
        for (int i = 0; i < all.size(); i++) {
            ShopInfo shopInfo = all.get(i);
            result += shopInfo.serial + " : ";
            result += shopInfo.price + "\n";
        }
        tvResult.setText(result);
    }


}
