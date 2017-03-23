package com.android.wako;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.widget.RemoteViews;

import com.android.wako.activity.login.LoginActivity;
import com.android.wako.common.Constants;
import com.android.wako.common.FilePathManager;
import com.android.wako.json.BaseJson;
import com.android.wako.json.CheckVisionJson;
import com.android.wako.json.NewMessageJson;
import com.android.wako.model.Header;
import com.android.wako.net.AsyncHttpPost;
import com.android.wako.net.BaseRequest;
import com.android.wako.net.DefaultThreadPool;
import com.android.wako.net.FileCallBack;
import com.android.wako.net.ResStatus;
import com.android.wako.net.ThreadCallBack;
import com.android.wako.net.util.RequestParameter;
import com.android.wako.util.LogUtil;
import com.android.wako.util.NoticeHelper;
import com.android.wako.util.PreferencesUtil;
import com.android.wako.util.SystemInfoUtil;
import com.google.gson.Gson;

/**
 * 此类主要是一此共用的，不信赖于activity，比如检查升级，验证码倒计时
 * 
 */
public class UtilManager implements ThreadCallBack {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static String TAG = "UtilManager";
    private static UtilManager mInstance;
    private static final int Msg_Update_time = 2;
    private static final int Msg_CALLBACK = 3;
    private static final int Msg_DOWNLOAD = 4;

    public static int Verify_Time = 120;
    public static int mRemainTime = Verify_Time;

    private static final int REQUEST_CHECK = 1;
    private static final int REQUEST_UNREAD_MESSAGE = 3;

    private Gson mGson = new Gson();
    private boolean checkIng = false;
    private FileCallBack mCallBack;
    public static final String APKPATH = FilePathManager.getFilePath() + "wako.apk";
    private DownThread mDownThread;
    NotificationManager nm;
    Notification notification;

    public static UtilManager getInstance() {
        if (mInstance == null) {
            synchronized (UtilManager.class) {
                if (mInstance == null) {
                    mInstance = new UtilManager();
                }
            }
        }
        return mInstance;
    }

    private UtilManager() {
        LogUtil.d(TAG, " UtilManager ");
    }

    Handler mHandler = new Handler() {
        @SuppressWarnings("deprecation")
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case Msg_Update_time:
                if (mRemainTime <= 0) {
                    mHandler.removeMessages(Msg_Update_time);
                    return;
                }
                mRemainTime--;
                mHandler.sendEmptyMessageDelayed(Msg_Update_time, 1000);
                break;
            case Msg_CALLBACK:
                if (nm == null) {
                    nm = (NotificationManager) MyApplication.mCon.getSystemService(Context.NOTIFICATION_SERVICE);
                    notification = new Notification(R.mipmap.ic_launcher, MyApplication.mCon.getString(R.string.app_name)
                            + MyApplication.mCon.getString(R.string.downloading), System.currentTimeMillis());
                    notification.contentView = new RemoteViews(MyApplication.mCon.getPackageName(), R.layout.download_notification_layout);
                    notification.contentView.setProgressBar(R.id.progress, 100, 0, false);
                    notification.contentView.setTextViewText(R.id.rate, "0%");
                    notification.contentIntent = null;
                }
                if (msg.arg1 == FileCallBack.COMPLETE) {// 下载完成
                    Intent i = new Intent();
                    i.setAction(Intent.ACTION_VIEW);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setDataAndType(Uri.fromFile(new File(APKPATH)), "application/vnd.android.package-archive");
                    MyApplication.mCon.startActivity(i);

                    notification.contentView.setTextViewText(R.id.rate, "100%  " + MyApplication.mCon.getString(R.string.install_click));
                    notification.contentView.setProgressBar(R.id.progress, 100, 100, false);
                    PendingIntent contentIntent = PendingIntent.getActivity(MyApplication.mCon, 0, i, 0);
                    notification.contentIntent = contentIntent;
                    nm.notify(NoticeHelper.Id_Update, notification);
                } else if (msg.arg1 == FileCallBack.FIAL) {// 下载失败，清掉，进来再弹出提示
                    notification.contentView.setTextViewText(R.id.rate, MyApplication.mCon.getString(R.string.download_fail));
                    notification.contentIntent = null;
                    nm.notify(NoticeHelper.Id_Update, notification);
                } else if (msg.arg1 == FileCallBack.START) {
                    notification.contentView.setTextViewText(R.id.rate, msg.arg2 + "%");
                    notification.contentView.setProgressBar(R.id.progress, 100, msg.arg2, false);
                    notification.contentIntent = null;
                    nm.notify(NoticeHelper.Id_Update, notification);
                }
                break;
            case Msg_DOWNLOAD:
                if (mDownThread == null || !mDownThread.isAlive()) {
                    mDownThread = new DownThread();
                    mDownThread.fileUrl = (String) msg.obj;
                    mDownThread.start();
                }
                break;
            }
        }
    };

    public FileCallBack getmCallBack() {
        return mCallBack;
    }

    public void setmCallBack(FileCallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    /**
     * 检查是否有升级
     */
    public void requestCheck() {
        if (!SystemInfoUtil
                .isNetworkConnected(MyApplication.mCon) || checkIng) {
            return;
        }
        checkIng = true;
        List<RequestParameter> parameter = new ArrayList<RequestParameter>();
        setParams(parameter);
        BaseRequest httpRequest = new AsyncHttpPost(this, Constants.CHECK_VERSION, parameter, false, "", false, REQUEST_CHECK);
        httpRequest.times = 1;
        httpRequest.expire = 0;
        DefaultThreadPool.getInstance().execute(httpRequest);
    }

    /**
     * 获取未读消息个数
     */
    public void requestMessageCount() {
        if (!SystemInfoUtil.isNetworkConnected(MyApplication.mCon)) {
            return;
        }
        List<RequestParameter> parameter = new ArrayList<RequestParameter>();
        setParams(parameter);
        BaseRequest httpRequest = new AsyncHttpPost(this, Constants.MESSAGE_COUNT, parameter, false, "", false, REQUEST_UNREAD_MESSAGE);
        httpRequest.times = 1;
        httpRequest.expire = 0;
        DefaultThreadPool.getInstance().execute(httpRequest);
    }

    /**
     * 设置公共参数
     * 
     * @param parameter
     */
    private void setParams(List<RequestParameter> parameter) {
        parameter.add(new RequestParameter("appId", Constants.appID));
        parameter.add(new RequestParameter("appBuildCode", Constants.VERSION));
        parameter.add(new RequestParameter("appVersion", Constants.VERSION_NAME));
        parameter.add(new RequestParameter("channel", Constants.CHANNEL));
         parameter.add(new RequestParameter("deviceInfo",Constants.DEVICEINFO));
        parameter.add(new RequestParameter("deviceId", Constants.deviceID));
        String uid = PreferencesUtil.getWakoString(MyApplication.mCon, PreferencesUtil.Pre_Name, PreferencesUtil.Key_Uid, "");
        parameter.add(new RequestParameter("uid", "" + uid));
        String token = PreferencesUtil.getWakoString(MyApplication.mCon, PreferencesUtil.Pre_Name, PreferencesUtil.Key_Token, "");
        LogUtil.d(TAG, "-----token=" + token);
        parameter.add(new RequestParameter("token", token));
    }

    /**
     * 调用自动登陆
     */
    public void requestAutoLogin() {
        LogUtil.d(TAG, "requestAutoLogin");
        List<RequestParameter> parameter = new ArrayList<RequestParameter>();
        setParams(parameter);
        BaseRequest httpRequest = new AsyncHttpPost(this, Constants.AutoLogin, parameter, false, "", false, Constants.RequestCode.Auto_Login);
        DefaultThreadPool.getInstance().execute(httpRequest);
    }

    /**
     * 下载文件
     * 
     * @param url
     */
    public void requestDownload(String url) {
        mHandler.sendMessage(mHandler.obtainMessage(Msg_DOWNLOAD, url));
    }

    public void startRemainTime() {
        mRemainTime = Verify_Time;
        mHandler.sendEmptyMessageDelayed(Msg_Update_time, 1000);
    }

    public void resetRemainTime() {
        mRemainTime = Verify_Time;
        mHandler.removeMessages(Msg_Update_time);
    }

    @Override
    public void onCallbackFromThread(String resultJson, int requestCode, int resStatus) {
            LogUtil.d(TAG, "resStatus=" + resStatus + ";resultJson=" + resultJson);
        switch (requestCode) {
        case REQUEST_CHECK:
            checkIng = false;
            if (resStatus == ResStatus.Success) {
                try {
                    CheckVisionJson json = mGson.fromJson(resultJson, CheckVisionJson.class);
                    if (json != null && json.header != null && json.header.status == 1 && json.content != null) {
                        if (json.content.result == -1 || json.content.result == 0) {
                            Intent intent = new Intent(MyApplication.mCon, UpdateActivity.class);
                            intent.putExtra("version", json.content);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            MyApplication.mCon.startActivity(intent);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
        case REQUEST_UNREAD_MESSAGE:
            if (resStatus == ResStatus.Success) {
                NewMessageJson json = mGson.fromJson(resultJson, NewMessageJson.class);
                if (json != null && json.header != null && json.header.status == 1 && json.content != null) {
                    Intent msgIntent = new Intent(MyReceiver.NEW_MSG);
                    msgIntent.putExtra("count", json.content.count);
                    MyApplication.mCon.sendBroadcast(msgIntent);
                }
            }
            break;
        case Constants.RequestCode.Auto_Login:
            if (resStatus == ResStatus.Success) {
                BaseJson baseJson = null;
                try {
                    baseJson = new Gson().fromJson(resultJson, BaseJson.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    baseJson = new BaseJson();
                    baseJson.header = new Header();
                    return;
                }
                int status = baseJson.header.status;
                if (status == Constants.HeaderStatus.Token_Error || status == Constants.HeaderStatus.User_Forbid) {
                    PreferencesUtil.clearPartData(MyApplication.mCon);
                }
                if (SystemInfoUtil.isRunningForeground(MyApplication.mCon) && !LoginActivity.mIsShow) {
                    if (status == Constants.HeaderStatus.Token_Error) {
                        Intent intent = new Intent(MyApplication.mCon, AccountExceptionActivity.class);
                        intent.putExtra("type", 0);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        MyApplication.mCon.startActivity(intent);
                    } else if (status == Constants.HeaderStatus.User_Forbid) {
                        Intent intent = new Intent(MyApplication.mCon, AccountExceptionActivity.class);
                        intent.putExtra("type", 1);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        MyApplication.mCon.startActivity(intent);
                    }
                }
            }
            break;
        }
    }

    class DownThread extends Thread {

        private boolean cancel = false;
        public String fileUrl;

        public void run() {
            File file = new File(APKPATH);
            // 如果目标文件已经存在，则删除。产生覆盖旧文件的效果
            if (file.exists()) {
                file.delete();
            }
            LogUtil.d(TAG, "-----------DownThread begin-------------");
            InputStream is = null;
            OutputStream os = null;
            long total = 0, count = 0;
            try {
                sendMessage(FileCallBack.START, 0);
                // 构造URL
                URL url = new URL(fileUrl);
                URLConnection con = url.openConnection();
                total = con.getContentLength();
                if (total == 0) {
                    sendMessage(FileCallBack.FIAL, 0);
                    return;
                }
                System.out.println("长度 :" + total);
                is = con.getInputStream();
                // 2K的数据缓冲
                byte[] bs = new byte[2 * 1024];
                int len, rate = 0;
                os = new FileOutputStream(APKPATH);
                // 开始读取
                while ((len = is.read(bs)) != -1) {
                    if (cancel) {
                        return;
                    }
                    rate += len;
                    os.write(bs, 0, len);
                    count += len;
                    Thread.sleep(15);
                    int pro = (int) (count * 100 / total);
                    if (rate > total / 100) {
                        rate = 0;
                        sendMessage(FileCallBack.START, pro);
                    }
                }
                if (os != null) {
                    os.close();
                }
                if (is != null) {
                    is.close();
                }

                if (count == total) {
                    sendMessage(FileCallBack.COMPLETE, 0);
                }
            } catch (IOException e) {
                e.printStackTrace();
                sendMessage(FileCallBack.FIAL, 0);
            } catch (Exception e) {
                e.printStackTrace();
                sendMessage(FileCallBack.FIAL, 0);
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void setCancel(boolean isCancel) {
            cancel = isCancel;
        }

        public void sendMessage(int status, int progress) {
            Message msg = mHandler.obtainMessage(Msg_CALLBACK, status, progress);
            mHandler.sendMessage(msg);
        }

    }

}
