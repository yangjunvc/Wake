package com.android.wako;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.android.wako.common.Constants;
import com.android.wako.net.DefaultThreadPool;
import com.android.wako.util.LogUtil;
import com.android.wako.util.PreferencesUtil;
import com.android.wako.util.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.UUID;

import cn.jpush.android.api.JPushInterface;

public class MyApplication extends Application implements Thread.UncaughtExceptionHandler{

    private static final String TAG = "MyApplication";
    public static Context mCon;
    public static ImageLoader imageLoader;//第三方下载

    @Override
    public void onCreate() {
        super.onCreate();
        mCon = this.getApplicationContext();

        if (Constants.enableEngineerMode) {
            LogUtil.IS_LOG = true;
        } else {
            LogUtil.IS_LOG = false;
        }

        Constants.DEVICEINFO += "-" + Build.BRAND + "-" + Build.MODEL + "-" + Build.VERSION.RELEASE;
        LogUtil.d(TAG, "UA:" + Constants.DEVICEINFO);
        int versionCode = 0;
        String versionName = "";
        try {
            versionCode = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
            versionName = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.d(TAG, "get version info error:" + e.getMessage());
        }
        Constants.VERSION = versionCode + "";
        Constants.VERSION_NAME = versionName;
        // Constants.CHANNEL = getChannel();
        Constants.appInstance = this;

        String uuid = PreferencesUtil.getWakoString(mCon, PreferencesUtil.Prefe_Forever, PreferencesUtil.KEY_UUID, "");
        if (StringUtil.isEmpty(uuid)) {
            PreferencesUtil.saveWakoString(mCon, PreferencesUtil.Prefe_Forever, PreferencesUtil.KEY_UUID, UUID.randomUUID().toString()
                    .replace("-", ""));
        }
        Constants.deviceID = PreferencesUtil.getWakoString(mCon, PreferencesUtil.Prefe_Forever, PreferencesUtil.KEY_UUID, "");

        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(this);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(configuration);

        //Jpush
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }

    @Override
    public void onLowMemory() {
        /**
         * 低内存的时候主动释放所有线程和资源
         *
         * PS:这里不一定每被都调用
         */
        DefaultThreadPool.shutdown();
        LogUtil.i(this.getClass().getName(), "MyApplication  onError  onLowMemory");
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        /**
         * 系统退出的时候主动释放所有线程和资源 PS:这里不一定被都调用
         */
        DefaultThreadPool.shutdown();
        LogUtil.i(this.getClass().getName(), "MyApplication  onError  onTerminate");
        super.onTerminate();

    }

    /**
     * 捕获整个应用中的异常
     */
    @Override
    public void uncaughtException(Thread arg0, Throwable arg1) {
        arg1.printStackTrace();
        DefaultThreadPool.clearMap();
    }

    private String getChannel() {
        String channelID = "";
        try {
            ApplicationInfo ai = this.getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
            Object value = ai.metaData.get("BaiduMobAd_CHANNEL"); // manifest里面的name值
            if (value != null) {
                channelID = value.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d(this.getClass().getName(), "get external cache error: " + e);
        }

        if (LogUtil.IS_LOG)
            LogUtil.d(this.getClass().getName(), "channelID: " + channelID);
        return channelID;
    }

}
