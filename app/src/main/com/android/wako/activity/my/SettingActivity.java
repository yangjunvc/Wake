package com.android.wako.activity.my;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.wako.CommonTitleActivity;
import com.android.wako.R;
import com.android.wako.WebViewActivity;
import com.android.wako.activity.login.LoginActivity;
import com.android.wako.common.Constants;
import com.android.wako.net.util.RequestParameter;
import com.android.wako.util.PreferencesUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * setting
 * Created by duanmulirui
 */
public class SettingActivity extends CommonTitleActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_main);
        initViews();
    }

    private void initViews() {
        loadTitleViews();
        setLeftVisibily(View.VISIBLE);
        setTitle(R.string.setting_title);

        findViewById(R.id.setting_modify_view).setOnClickListener(this);
        findViewById(R.id.setting_about_view).setOnClickListener(this);
        findViewById(R.id.logout).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.logout:
                popupWindow(v);
                break;
            case R.id.setting_modify_view:
                startActivity(new Intent(this,ModifyPwActivity.class));
                break;
            case R.id.setting_about_view:
                Intent intent = new Intent(this,WebViewActivity.class);
                intent.putExtra("title",getString(R.string.setting_about));
                intent.putExtra("url", Constants.ABOUT_URL);
                startActivity(intent);
                break;
            case R.id.middle_view:
                setLogoutData();
                PreferencesUtil.clearPartData(SettingActivity.this);
                Intent intent2 = new Intent();
                intent2.setClass(SettingActivity.this, LoginActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent2);
                finish();
                break;
            case R.id.blow_view:
            case R.id.null_view:
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
                break;
            default:
                break;
        }
    }

    private void setLogoutData() {
        List<RequestParameter> parameter = new ArrayList<RequestParameter>();
        startHttpRequst(Constants.HTTP_POST, Constants.Logout, parameter, true, 111, true, false);
    }

    private void popupWindow(View view) {
        if(mPopupWindow == null){
            LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = lay.inflate(R.layout.popupwindow_main, null);
            // 初始化按钮
            TextView top = (TextView) v.findViewById(R.id.top_view);
            top.setTextColor(getResources().getColor(R.color.text_999999_color));
            top.setText(R.string.logout_text);
            TextView middle = (TextView) v.findViewById(R.id.middle_view);
            middle.setText(R.string.ok);
            middle.setOnClickListener(this);
            v.findViewById(R.id.blow_view).setOnClickListener(this);
            v.findViewById(R.id.null_view).setOnClickListener(this);
            mPopupWindow = new PopupWindow(v, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.update();
        mPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }
}
