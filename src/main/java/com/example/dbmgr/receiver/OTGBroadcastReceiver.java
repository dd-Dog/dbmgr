package com.example.dbmgr.receiver;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

import com.example.dbmgr.activity.HomeActivity;
import com.example.dbmgr.activity.MgrActivity;

import static com.example.dbmgr.activity.MgrActivity.ACTION_USB_PERMISSION;


/**
 * Created by bianjb on 2017/9/12.
 */

public class OTGBroadcastReceiver extends BroadcastReceiver{

    private MgrActivity activity;
    public OTGBroadcastReceiver(Activity activity) {
        this.activity = (MgrActivity) activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case ACTION_USB_PERMISSION://接受到自定义广播
                synchronized (this) {
                    UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {  //允许权限申请
                        if (usbDevice != null) {
                            //Do something
                            activity.applyPermission();
                        }
                    } else {
                        Toast.makeText(context,"用户未授权，读取失败",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case UsbManager.ACTION_USB_DEVICE_ATTACHED://接收到存储设备插入广播
                UsbDevice device_add = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device_add != null) {
                    Toast.makeText(context,"接收到存储设备插入广播",Toast.LENGTH_SHORT).show();
                    activity.applyPermission();
                }
                break;
            case UsbManager.ACTION_USB_DEVICE_DETACHED://接收到存储设备拔出广播
                UsbDevice device_remove = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device_remove != null) {
                    Toast.makeText(context,"接收到存储设备拔出广播",Toast.LENGTH_SHORT).show();
                //拔出或者碎片 Activity销毁时 释放引用
                //device.arrow();
                }
                break;
        }
    }
}
