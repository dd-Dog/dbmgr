package com.example.dbmgr.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.dbmgr.R;

/**
 * Created by bianjb on 2017/8/18.
 */

public class HelpActivity extends AppCompatActivity{

    private TextView tips;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        getSupportActionBar().hide();
        String[] stringArray = getResources().getStringArray(R.array.tips);
        tips = (TextView) findViewById(R.id.tips);
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<stringArray.length; i++) {
            sb.append(stringArray[i]);
        }
        tips.setText(sb.toString());
    }
}
