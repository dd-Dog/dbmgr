package com.example.dbmgr.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dbmgr.R;
import com.example.dbmgr.db.ShopDAORemote;
import com.example.dbmgr.db.ShopInfo;
import com.example.dbmgr.receiver.OTGBroadcastReceiver;
import com.example.dbmgr.utils.Task;
import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.partition.Partition;

import java.util.ArrayList;

import static android.support.v7.appcompat.R.id.add;
import static android.support.v7.appcompat.R.id.multiply;

/**
 * Created by bianjb on 2017/9/23.
 */

public class MgrActivity extends BaseActivity implements View.OnClickListener {
    public static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private static final String TAG = "MgrActivity";
    private Spinner spProduct;
    private EditText etID;
    private TextView tvUnit;
    private TextView tvCount;
    private LinearLayout mContent;
    private ShopDAORemote shopDAORemote;
    private ArrayList<String> mNames;
    private ArrayList<Integer> mCounts;
    private ArrayList<String> mUnits;
    private ArrayList<ShopInfo> mProduct;
    private OTGBroadcastReceiver mUsbReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shopDAORemote = new ShopDAORemote(HomeActivity.conn, this);
        initView();
        initData();
        initUsb();
    }

    private void initUsb() {
        mUsbReceiver = new OTGBroadcastReceiver(this);
        sendBroadcast();
    }

    private void initData() {

    }

    public void applyPermission() {
        //申请
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        //枚举设备
        UsbMassStorageDevice[] storageDevices = UsbMassStorageDevice.getMassStorageDevices(this);//获取存储设备
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        for (UsbMassStorageDevice device : storageDevices) {
            //可能有几个 一般只有一个 因为大部分手机只有1个otg插口
            if (usbManager.hasPermission(device.getUsbDevice())) {
                read(device);
            } else {
                usbManager.requestPermission(device.getUsbDevice(), pendingIntent); //该代码执行后，系统弹出一个对话框，
            }
        }
    }

    public void sendBroadcast() {
        IntentFilter usbDeviceStateFilter = new IntentFilter();
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        usbDeviceStateFilter.addAction(ACTION_USB_PERMISSION);
        //IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        //registerReceiver(mUsbReceiver, filter);
        registerReceiver(mUsbReceiver, usbDeviceStateFilter);//这里我用的碎片
    }

    public void read(UsbMassStorageDevice massDevice) {
        // before interacting with a device you need to call init()!
        try {
            massDevice.init();
            //Only uses the first partition on the device
            Partition partition = massDevice.getPartitions().get(0);
            FileSystem currentFs = partition.getFileSystem();
            //fileSystem.getVolumeLabel()可以获取到设备的标识
            //通过FileSystem可以获取当前U盘的一些存储信息，包括剩余空间大小，容量等等
            Log.d(TAG, "Capacity: " + currentFs.getCapacity());
            Log.d(TAG, "Occupied Space: " + currentFs.getOccupiedSpace());
            Log.d(TAG, "Free Space: " + currentFs.getFreeSpace());
            Log.d(TAG, "Chunk size: " + currentFs.getChunkSize());
            StringBuilder sb = new StringBuilder();
            sb.append("Capacity: " + currentFs.getCapacity() + "\n");
            sb.append("Occupied Space: " + currentFs.getOccupiedSpace() + "\n");
            sb.append("Free Space: " + currentFs.getFreeSpace() + "\n");
            sb.append("Chunk size: " + currentFs.getChunkSize() + "\n");
//            Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
            UsbFile file = currentFs.getRootDirectory();
            writeUsb(file);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "读取USB存储设备失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void writeUsb(UsbFile file) {
        String name = file.getName();
        Log.e(TAG,"FILE NAME =" + name);
        Toast.makeText(this, "FILE NAME =" + name, Toast.LENGTH_SHORT).show();
    }

    private void initView() {
        findViewById(R.id.btn_get).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);
        findViewById(R.id.btn_insert).setOnClickListener(this);
        findViewById(R.id.ll_sp).setOnClickListener(this);
        mContent = (LinearLayout) findViewById(R.id.ll_content);
        spProduct = (Spinner) findViewById(R.id.sp_product);
        etID = (EditText) findViewById(R.id.et_id);
        tvUnit = (TextView) findViewById(R.id.tv_unit);
        tvCount = (TextView) findViewById(R.id.tv_count);
        spProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    if (mProduct != null) {
                        final ShopInfo shopInfo = mProduct.get(position);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvUnit.setText("单位(" + shopInfo.unit + ")");
                                tvCount.setText(shopInfo.count + "");
                            }
                        });
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void setContentViewId() {
        setContentView(R.layout.activity_mgr);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get:
                final String id = etID.getText().toString();
                if (TextUtils.isEmpty(id)) {
                    Toast.makeText(this, "请输入单品ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                Task.asyncTask(new Runnable() {
                    @Override
                    public void run() {
                        updateData(id);
                        hideSoftInput();
                    }
                });
                break;
            case R.id.ll_sp:
                spProduct.performClick();
                break;
            case R.id.btn_save:

                break;
            case R.id.btn_insert:

                break;
        }
    }

    private void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etID.getWindowToken(), 0);
    }

    String result = "";

    /**
     * 更新数据
     *
     * @param id
     */
    private void updateData(String id) {
        mProduct = shopDAORemote.findById(id);
        mNames = new ArrayList<>();
        mNames.add("请选择单品");
        mUnits = new ArrayList<>();
        mCounts = new ArrayList<>();
        Log.e(TAG, "product=" + mProduct);
        if (mProduct == null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    View view = View.inflate(MgrActivity.this, R.layout.summary_item, null);
                    ((TextView) view.findViewById(R.id.name)).setText("单品");
                    ((TextView) view.findViewById(R.id.unit)).setText("单位");
                    ((TextView) view.findViewById(R.id.price)).setText("单价");
                    ((TextView) view.findViewById(R.id.count)).setText("数量");
                    mContent.addView(view);
                    Toast.makeText(MgrActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        result = "";
        for (int i = 0; i < mProduct.size(); i++) {
            ShopInfo shopInfo = mProduct.get(i);
            result += shopInfo.price + "\n";
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < mProduct.size() + 1; i++) {
                    View view = View.inflate(MgrActivity.this, R.layout.summary_item, null);
                    if (i == 0) {
                        ((TextView) view.findViewById(R.id.name)).setText("单品");
                        ((TextView) view.findViewById(R.id.unit)).setText("单位");
                        ((TextView) view.findViewById(R.id.price)).setText("单价");
                        ((TextView) view.findViewById(R.id.count)).setText("数量");
                    } else {
                        ShopInfo shopInfo = mProduct.get(i - 1);
                        mNames.add(shopInfo.name);
                        mUnits.add(shopInfo.unit);
                        mCounts.add(shopInfo.count);
                        ((TextView) view.findViewById(R.id.name)).setText(shopInfo.name);
                        ((TextView) view.findViewById(R.id.unit)).setText(shopInfo.unit);
                        ((TextView) view.findViewById(R.id.price)).setText(shopInfo.price);
                        ((TextView) view.findViewById(R.id.count)).setText(shopInfo.count + "");
                    }
                    mContent.addView(view);
                }
                Toast.makeText(MgrActivity.this, "查询成功", Toast.LENGTH_SHORT).show();
                spProduct.setAdapter(getArrayAdapter());
            }
        });

    }

    /**
     * 自定义Spinner的条目布局，目的使文字居中
     *
     * @return
     */
    public ArrayAdapter<String> getArrayAdapter() {

        ArrayAdapter<String> spAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mNames) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return setCentered(super.getView(position, convertView, parent));
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                return setCentered(super.getDropDownView(position, convertView, parent));
            }

            private View setCentered(View view) {
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                //设置居中
                textView.setGravity(Gravity.CENTER_VERTICAL);
                return view;
            }
        };
        return spAdapter;
    }
}
