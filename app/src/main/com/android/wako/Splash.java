package com.android.wako;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.android.wako.activity.base.BaseActivity;
import com.android.wako.activity.login.LoginActivity;
import com.android.wako.activity.tabhost.MainTabHostActivity;
import com.android.wako.common.ActivityManager;
import com.android.wako.common.Constants;
import com.android.wako.net.ResStatus;
import com.android.wako.net.util.RequestParameter;
import com.android.wako.util.LogUtil;
import com.android.wako.util.PreferencesUtil;
import com.android.wako.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Splash
 *Created by duanmulirui
 */
public class Splash extends BaseActivity{
    private static String TAG = "Splash";

    public static final int MSG_LOGIN = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_LOGIN:
                    String token = PreferencesUtil.getWakoString(Splash.this,PreferencesUtil.Pre_Name, PreferencesUtil.Key_Token, "");
                    if (!StringUtil.isEmpty(token)) {
                        LogUtil.d(TAG, "auto login...");
                        List<RequestParameter> parameter = new ArrayList<RequestParameter>();
                        startHttpRequst(Constants.HTTP_POST, Constants.AutoLogin, parameter, false, Constants.RequestCode.Auto_Login, false, false);
                    }else {
                        LogUtil.d(TAG, "login...");
                        UtilManager.getInstance().requestCheck();
                        startActivity(new Intent(Splash.this, LoginActivity.class));
                        finish();
                    }
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityManager.mIsSplashShow = true;
        super.onCreate(savedInstanceState);
        View view = View.inflate(this, R.layout.splash, null);
        setContentView(R.layout.splash);
        AlphaAnimation anim = new AlphaAnimation(0.2f, 1.0f);
        anim.setDuration(2400);
        view.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                mHandler.sendEmptyMessageDelayed(0, 10);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }

        });
        mHandler.sendEmptyMessageDelayed(MSG_LOGIN, 2000);
    }

    @Override
    public void onCallback(String resultJson, int code, int resStatus) {
        super.onCallback(resultJson, code, resStatus);
        switch (code){
            case Constants.RequestCode.Auto_Login:
                if(baseJson != null && baseJson.header.status == 1){
                    UtilManager.getInstance().requestCheck();
                    startActivity(new Intent(Splash.this, MainTabHostActivity.class));
                    finish();
                }else{
                    showToast(ResStatus.getTipString(resStatus));
                }
                break;
            default: break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }
}
