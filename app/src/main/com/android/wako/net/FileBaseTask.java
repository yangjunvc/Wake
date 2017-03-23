package com.android.wako.net;

import android.os.Handler;
import android.os.Message;

import java.util.Map;

/**
 * 下载，上传文件
 *
 * 
 */
public class FileBaseTask implements Runnable {

    static final String TAG = "FileBaseTask";
    private static final int Msg_CALLBACK = 1;
    public boolean mCancel = false;
    public FileCallBack mCallBack;
    int mReadTimeOut = 10 * 1000; // 读取超时
    int mConnectTimeout = 10 * 1000; // 超时时间

    public String mUrl;
    public String mFilePath;
    public Map<String, String> mParam;
    public String mFileKey;
    public int mMarkId = 0;
    public String mMarkStr;//如果不传，默认为mFilePath上传时的本地路径,主要用于区分多文件上传
    String mResult = "";
    Object mObj;

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case Msg_CALLBACK:
                if (mCallBack != null && !mCancel) {
                    mCallBack.callBackDown(msg.arg1, msg.arg2, mMarkId, mResult , mObj,mMarkStr);
                }
                break;
            }
        }
    };

    @Override
    public void run() {

    }

    public void sendMessage(int status, int progress) {
        Message msg = mHandler.obtainMessage(Msg_CALLBACK, status, progress);
        mHandler.sendMessage(msg);
    }

}
