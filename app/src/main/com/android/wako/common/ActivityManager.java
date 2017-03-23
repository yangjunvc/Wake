package com.android.wako.common;

import java.util.ArrayList;

import android.app.Activity;

import com.android.wako.Splash;
import com.android.wako.UtilManager;
import com.android.wako.activity.login.LoginActivity;
import com.android.wako.util.LogUtil;
import com.android.wako.util.PreferencesUtil;
import com.android.wako.util.StringUtil;

/**
 * activity的管理
 *
 */
public class ActivityManager {

    static final String TAG = "ActivityManager";
    public static boolean mIsFromLogin = false;//是否要再调用自动登陆的标识，与登陆页区分开，以防重复调用自动登陆接口
    public static boolean mIsSplashShow = false;

    static ArrayList<Activity> mList = new ArrayList<Activity>();//主要用于存放没有destory的activity
    static ArrayList<Activity> mPauseList = new ArrayList<Activity>();//存放最前面的activity,用于表示应用是否处理前后台

    public static void putActivity(Activity act) {
        mList.add(act);
    }

    public static void removeActivity(Activity act) {
        mList.remove(act);
    }

    public static void putPauseActivity(Activity act) {
        String token = PreferencesUtil.getWakoString(act, PreferencesUtil.Pre_Name, PreferencesUtil.Key_Token, "");
        if (StringUtil.isEmpty(token)) {
            return;
        }
        LogUtil.d(TAG,"mPauseList.size = "+mPauseList.size()+"--mIsFromLogin:= "+mIsFromLogin +"--mIsSplashShow= "+mIsSplashShow);
        if (mPauseList.size() <= 0) {// 表示第一次进入,或者切换到前台
        	if(!mIsFromLogin && !mIsSplashShow){
          		 //应用从后台切回前台时，调用自动登录
          		 UtilManager.getInstance().requestAutoLogin();
        }else{
                mIsFromLogin = false;
                mIsSplashShow = false;
            }
   	 }
        mPauseList.add(act);
    }

    public static void removePauseActivity(Activity act) {
        mPauseList.remove(act);
    }
    /**
     * 关闭当前所有的activity，用于强制升级时关闭
     */
    public static void finishAll() {
        if (mList.size() > 0) {
            for (Activity act : mList) {
                act.finish();
            }
        }
    }
}
