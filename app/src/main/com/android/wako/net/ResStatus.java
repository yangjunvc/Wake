package com.android.wako.net;



import android.content.Context;

import com.android.wako.MyApplication;
import com.android.wako.R;

/**
 * 数据请求返回状态信息
 * 
 * @author android
 * 
 */
public class ResStatus {

    public static final int Success = 1;
    public static final int Error_Code = 2;
    public static final int Error_IllegalArgument = 3;
    public static final int Error_Connect_Timeout = 4;
    public static final int Error_Socket_Timeout = 5;
    public static final int Error_Unsupport_Encoding = 6;
    public static final int Error_HttpHostConnect = 7;
    public static final int Error_Client_Protocol = 8;
    public static final int Error_IOException = 9;

    public static String getTipString(int status) {
        Context con = MyApplication.mCon;
        String res = con.getString(R.string.network_error);
//        switch (status) {
//        case Error_Code:
//            res = con.getString(R.string.error_code);
//            break;
//        case Error_Client_Protocol:
//            res = con.getString(R.string.error_clientProtocol);
//            break;
//        case Error_Connect_Timeout:
//            res = con.getString(R.string.error_connectTimeout);
//            break;
//        case Error_HttpHostConnect:
//            res = con.getString(R.string.error_httpHostConnect);
//            break;
//        case Error_IllegalArgument:
//            res = con.getString(R.string.error_illegalArgument);
//            break;
//        case Error_IOException:
//            res = con.getString(R.string.error_iOException);
//            break;
//        case Error_Socket_Timeout:
//            res = con.getString(R.string.error_socketTimeout);
//            break;
//        case Error_Unsupport_Encoding:
//            res = con.getString(R.string.error_unsupportedEncoding);
//            break;
//        default:
//            res = con.getString(R.string.error_common);
//            break;
//        }
        return res;
    }
}
