package com.android.wako;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.wako.activity.base.BaseActivity;
import com.android.wako.util.SystemInfoUtil;

/**
 * 公共Title
 *Created by duanmulirui
 */
public class CommonTitleActivity extends BaseActivity{

    private TextView mLeft,mContent,mRight;
    private ImageView mRightIcon;

    public void loadTitleViews(){
        mLeft = (TextView)findViewById(R.id.title_left);
        mLeft.setVisibility(View.GONE);
        mContent = (TextView)findViewById(R.id.title_content);
        mRight = (TextView)findViewById(R.id.title_right_text);
        mRightIcon = (ImageView) findViewById(R.id.title_right_icon);
        mLeft.setOnClickListener(this);
        mRight.setOnClickListener(this);
        mRightIcon.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.title_left:
                onLeftTextClickListener(v);
                break;
            case R.id.title_right_icon:
                onRightIconClickListener(v);
                break;
            case R.id.title_right_text:
                onRightTextClickListener(v);
                break;
        }
    }

    public void setTitle(int resid) {
        mContent.setText(resid);
    }

    public void setTitle(String title) {
        mContent.setText(title);
    }

    public void setRightText(String resid) {
        mRightIcon.setVisibility(View.GONE);
        mRight.setText(resid);
        mRight.setVisibility(View.VISIBLE);
    }

    public void setRightText(int resid) {
        mRightIcon.setVisibility(View.GONE);
        mRight.setText(resid);
        mRight.setVisibility(View.VISIBLE);
    }

    public void setRightIcon(int resid) {
        mRight.setVisibility(View.GONE);
        mRightIcon.setImageResource(resid);
        mRightIcon.setVisibility(View.VISIBLE);
    }

    public void setLeftVisibily(int visibility) {
        mLeft.setVisibility(visibility);
    }

    public void onLeftTextClickListener(View v) {
        SystemInfoUtil.closeSoftKeyBoard(this);
        finish();
    }

    public void onRightIconClickListener(View v) {

    }

    public void onRightTextClickListener(View v) {

    }
}
