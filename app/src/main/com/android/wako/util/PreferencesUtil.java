package com.android.wako.util;

import android.content.Context;
import android.content.SharedPreferences;

import cn.jpush.android.api.JPushInterface;

public class PreferencesUtil {
    public static String Prefe_Forever = "forever";// 用于一直存放变量，除非应用被卸载
    public static String KEY_UUID = "uuid";// 系统的唯一标识，只生成一次
    public static String Pre_Name ="wako";
    public static String Key_Phone = "phone";

    public static String Key_Uid = "uid";
    public static String Key_Token = "token";

    public static void saveWakoString(Context context,String pre_name,String name,String value){
        SharedPreferences sp = context.getSharedPreferences(pre_name,Context.MODE_PRIVATE);
        sp.edit().putString(name,value).commit();
    }
    public static String getWakoString(Context context, String pre_name,String name,String def){
        SharedPreferences sp = context.getSharedPreferences(pre_name,Context.MODE_PRIVATE);
        return sp.getString(name,def);
    }

    public static void clearPartData(Context context){
        PreferencesUtil.saveWakoString(context, Pre_Name, Key_Uid, "");
        PreferencesUtil.saveWakoString(context, Pre_Name, Key_Token, "");
        JPushInterface.stopPush(context);
    }

}
