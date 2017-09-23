package com.example.dbmgr.receiver;

/**
 * Created by bianjb on 2017/9/12.
 */


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class USBBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (intent.getAction().equals("android.hardware.usb.action.USB_STATE")) {
            if (intent.getExtras().getBoolean("connected")) {
                // usb 插入
                Toast.makeText(context, "插入", Toast.LENGTH_LONG).show();
            } else {
                //   usb 拔出
                Toast.makeText(context, "拔出", Toast.LENGTH_LONG).show();
            }
        }
    }
}