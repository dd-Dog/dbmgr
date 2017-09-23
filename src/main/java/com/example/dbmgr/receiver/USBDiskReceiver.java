package com.example.dbmgr.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by bianjb on 2017/9/12.
 */

public class USBDiskReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String path = intent.getData().getPath();
        if (!TextUtils.isEmpty(path)){
            if ("android.intent.action.MEDIA_UNMOUNTED".equals(action)) {
                Log.d("usb","unmounted");
                Toast.makeText(context, "unmounted", Toast.LENGTH_SHORT).show();
            }
            if ("android.intent.action.MEDIA_MOUNTED".equals(action)) {
                Log.d("usb","mounted");
                Toast.makeText(context, "mounted", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
