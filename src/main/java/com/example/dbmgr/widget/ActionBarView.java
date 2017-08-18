package com.example.dbmgr.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dbmgr.R;

/**
 * Created by bianjb on 2017/8/18.
 */

public class ActionBarView extends FrameLayout {

    private TextView tvTitle;
    private ImageButton rightImage;
    private View actionBar;

    public ActionBarView(@NonNull Context context) {
        this(context, null);
    }

    public ActionBarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ActionBarView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.actionBarAttrs);
        String title = typedArray.getString(R.styleable.actionBarAttrs_title);
        Drawable drawable = typedArray.getDrawable(R.styleable.actionBarAttrs_rightImage);
        int color = typedArray.getColor(R.styleable.actionBarAttrs_actionbar_background,
                context.getResources().getColor(R.color.green));
        typedArray.recycle();
        tvTitle.setText(title);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        tvTitle.getPaint().setFakeBoldText(true);
        rightImage.setImageDrawable(drawable);
        actionBar.setBackgroundColor(color);

    }

    private void init() {
        actionBar = View.inflate(getContext(), R.layout.actionbar, null);
        tvTitle = (TextView) actionBar.findViewById(R.id.tv_title);
        rightImage = (ImageButton) actionBar.findViewById(R.id.rightImage);
    }

}
