package com.android.wako.common;

import com.android.wako.MyApplication;

/**
 * Created by duanmulirui
 */
public class Constants {
    // 客户端版本相关
    public static String deviceID = "";
    public static final String appID = "2";
    public static String VERSION = "1";// version code
    public static String VERSION_NAME = "1.0.0";
    public static String CHANNEL = "";
    public static String DEVICEINFO = "android";

    // 应用相关
    public static boolean isGzip = false;// 是否启用gzip
    public static boolean IS_STOP_REQUEST = false;// 请求线程是否停止

    // Network related.
    public static final String HTTP_POST = "POST";
    public static final String HTTP_GET = "GET";

    public static MyApplication appInstance;

    //product
    public static String BASE_URL = "http://60.205.125.97/";//阿里云
    public static String DOWNLOAD_URL = "http://heguangwang.oss-cn-beijing.aliyuncs.com/";// 文件下载

    public static final boolean log2File = true;// 写日志到ＳＤ卡文件
    public static final boolean enableEngineerMode = false;

    //test
//    public static String BASE_URL = "http://60.205.125.181:8088/";
//    public static String DOWNLOAD_URL = "http://heguangwang.oss-cn-beijing.aliyuncs.com/";// 文件下载
//
//    public static final boolean log2File = true;// 写日志到ＳＤ卡文件
//    public static final boolean enableEngineerMode = true;

    // develop
//    public static String BASE_URL = "http://172.16.230.131/";
//    public static String DOWNLOAD_URL = "http://heguangwang.oss-cn-beijing.aliyuncs.com/";// 文件下载
//
//    public static final boolean log2File = true;// 写日志到ＳＤ卡文件
//    public static final boolean enableEngineerMode = true;

    // 支付宝
    public static final String ALIPAY_PARTNER = "2088421603237341";//支付宝帐号ID
    public static final String ALIPAY_SELLER = "heguangwang777@126.com";//支付宝帐号
    public static final String NOTIFY_URL = BASE_URL + "api/common/rechargeCallbackForAlipay";//支付宝回调

    //微信
    public static final String WX_APP_ID = "wx056c812b27852a06";//app ID
    public static final String WX_APP_KEY = "Ky5e2g1peqIWL9sHyRDFxO9egQgv3Vrn";//app KEY（密钥）
    public static final String WX_MCH_ID = "1392010802";//mch ID
    public static final  String URL_GET_TRADE_INFO = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    public static final String NOTIFY_URL_WECHAT = BASE_URL + "api/common/rechargeCallbackForWx";//微信回调

    public static String UPLOAD_URL = BASE_URL + "api/login/uploadFile";// 文件上传
    public static String CHECK_VERSION = BASE_URL + "api/login/checkVersion";// 版本校验
    public static String Get_Valid_Code = BASE_URL + "api/login/getValidCode";// 获取验证码
    public static String Register = BASE_URL + "api/login/regist";// 注册
    public static String Login = BASE_URL + "api/login/login";// 登陆
    public static String AutoLogin = BASE_URL + "api/login/autoLogin";// 自动登陆
    public static String Logout = BASE_URL + "api/login/logout";// 登出
    public static String FORGET_PASSWORD = BASE_URL + "api/login/forgetPassword";// 忘记密码
    public static String MESSAGE_COUNT = BASE_URL + "api/common/getUnreadMessageCount";//获取未读消息数
    public static String BANNER_LIST = BASE_URL + "api/common/getBannerList";//轮播图
    public static String MAINMENG_LIST = BASE_URL + "api/common/getRecommendExamineeList";//获取mainmengist
    public static String MENG_DETAIL = BASE_URL + "api/common/getExamineeDetail";//获取梦主页
    public static String ORDER_CREATE = BASE_URL + "api/common/createOrder";//生成订单
    public static String MENG_LIST = BASE_URL + "api/common/getExamineeList";//筑梦空间状元list
    public static String USERINFO_URL = BASE_URL + "api/common/getUserInfo";//获取个人信息
    public static String MODIFYUSRINFO_URL = BASE_URL + "api/common/modifyUserInfo";//修改姓名
    public static String ORDER_REACHRGE = BASE_URL + "api/common/updateOrderInfo";//生成充值订单
    public static String ORDER_CONFIRM = BASE_URL + "api/common/checkOrder";//校验订单支付状态
    public static String MESSAGE_URL = BASE_URL + "api/common/getMessageList";//消息list
    public static String MYQUESTION_LIST = BASE_URL + "api/common/getAnswerList";//我的问答list
    public static String MYQUESTION_DETAIL = BASE_URL + "api/common/getAnswerRecord";//我的问答detail
    public static String MYMENG_LIST = BASE_URL + "api/common/getServerExamineeList";//获取筑梦之旅list
    public static String MYMENG_QUESTION = BASE_URL + "api/common/question";//对指定状元提问
    public static String MODIFY_PASSWORD = BASE_URL + "api/common/modifyPassword";//修改密码
    public static String MYORDER_LIST = BASE_URL + "api/common/getOrderList";//我的订单list
    public static String MYDOU_STATEMENT_LIST = BASE_URL + "api/common/getGivingBeanList";//获取我的爱心豆总数及爱心豆收支明细list




    public static final String ERROR_MESSAGE = "网络异常，请重试！";

    public static final int CONNECTION_SHORT_TIMEOUT = 5000;// 连接超时 5s
    public static final int READ_SHORT_TIMEOUT = 5000;// 连接超时 5s

    public static final int CONNECTION_MIDDLE_TIMEOUT = 10000;// 连接超时 10s
    public static final int READ_MIDDLE_TIMEOUT = 10000;// 连接超时 10s

    public static final int CONNECTION_LONG_TIMEOUT = 20000;// 连接超时20s
    public static final int READ_LONG_TIMEOUT = 20000;// 连接超时 20s

    // result of webservice request
    public static int RESULT_VERSION_ERROR = -1;
    public static int RESULT_ID_VERIFY_ERROR = -2;
    public static int RESULT_ID_BLOCKED = -3;
    public static int RESULT_ID_ROLE_CHANGED = -4;
    public static int RESULT_WEB_INTERNAL_ERROR = 0;
    public static int RESULT_OK = 1;

    public static class RequestStatus {
        public static final int Add = 1;
        public static final int Contians = 2;
    }

    public static class RequestCode {
        public static final int Check_Update = 99999;
        public static final int Auto_Login = 99998;
        public static final int WeiXin_Rechange = 99997;
        public static final int REPORT_LAUNCH = 99996;
    }

    public static class CodeType {
        public static final int Register = 0;
        public static final int Forget = 1;
    }

    /**
     * 请求head状态
     *
     */
    public static class HeaderStatus {
        public static final int Version_Update = -1;// 版本不一致，强制更新
        public static final int Token_Error = -2;// uID和token不匹配(异地登陆)
        public static final int User_Forbid = -3;// 用户已停用
        public static final int Check_Error = 0;// 访问接口成功，业务校验失败或服务器内部错误
        public static final int Success = 1;// 成功
        public static final int Other = 2;// TBD
    }

    public static final String BANNER_URL = "";//轮播图的基地址（目前没有）
    public static final String USERRULE_URL = BASE_URL + "api/wap/userService";//用户协议
    public static final String ABOUT_URL = BASE_URL + "api/wap/aboutHeguang";//关于和光网
    public static final String USEHELP_URL = BASE_URL + "api/wap/beanUseHelp";//使用帮助(爱心豆help)
    public static final String SERVICE_URL = BASE_URL + "api/wap/serviceExplain";//服务说明(主页的help)

}
