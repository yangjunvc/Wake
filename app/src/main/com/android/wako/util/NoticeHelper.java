package com.android.wako.util;

import java.util.HashSet;
import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;

import com.android.wako.MyApplication;
import com.android.wako.R;
import com.android.wako.UtilManager;

/**
 * 通知的封装
 * 
 */
public class NoticeHelper {

    static String TAG = "NoticeHelper";
    public static Random mRand = new Random();
    private static HashSet<Integer> mRandSet = new HashSet<Integer>();
    public static int Id_Update = 7;//下载升级

    /**
     * 生成不重复的随机数,用于发送不需要更新的通知
     * @return
     */
    public static int getRandom() {
        int rand = mRand.nextInt(1000) + 10;
        for (int i = 0; i < 100; i++) {
            rand = mRand.nextInt(1000) + 10;
            if (!mRandSet.contains(rand)) {
                mRandSet.add(rand);
                return rand;
            }
        }
        mRandSet.add(rand);
        return rand;
    }

    /**
     * 显示通知
     * 
     * @param c
     * @param notifyId
     * @param notifyShowText
     * @param titleText
     * @param contentText
     * @param intent
     * @param flag
     */
    public static void notify(Context c, int notifyId, String notifyShowText,String titleText, String contentText, Intent intent,
            int flag) {
        LogUtil.d(TAG, "--notifyShowText="+notifyShowText+";isFroung="+SystemInfoUtil.isRunningForeground(MyApplication.mCon));
        if( !SystemInfoUtil.isRunningForeground(MyApplication.mCon)){
            Notification.Builder builder = new Notification.Builder(c);
            builder.setSmallIcon(R.mipmap.ic_launcher); //设置图标
            builder.setTicker(notifyShowText);
            builder.setContentTitle(titleText); //设置标题
            builder.setContentText(contentText); //消息内容
//            builder.setWhen(System.currentTimeMillis()); //发送时间
            builder.setDefaults(Notification.DEFAULT_ALL); //设置默认的提示音，振动方式，灯光
//            builder.setAutoCancel(true);//打开程序后图标消失
            // 控制点击通知后显示内容的类
            PendingIntent ip = PendingIntent.getActivity(c, getRandom(), intent, 0);
            builder.setContentIntent(ip);
            Notification n = builder.build();
            // 通知发出的标志设置
            n.flags = flag;
            // 显示通知
            final NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(notifyId, n); // 通过通知管理器发送通知
        }else{
            UtilManager.getInstance().requestMessageCount();
        }
    }

    /**
     * 取消消息
     * 
     * @param c
     * @param notifyId
     * @return void
     */
    public static void cancel(Context c, int notifyId) {
        ((NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(notifyId);
    }

    // flags
    public final static int FLAG_ONGOING_EVENT_AUTO_CANCEL = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONGOING_EVENT;
    public final static int FLAG_ONGOING_EVENT = Notification.FLAG_ONGOING_EVENT;
    public final static int FLAG_NO_CLEAR = Notification.FLAG_NO_CLEAR;
    public final static int FLAG_AUTO_CANCEL = Notification.FLAG_AUTO_CANCEL;

}