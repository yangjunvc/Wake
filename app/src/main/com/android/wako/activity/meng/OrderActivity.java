package com.android.wako.activity.meng;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Xml;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.android.wako.CommonTitleActivity;
import com.android.wako.MyApplication;
import com.android.wako.R;
import com.android.wako.common.Constants;
import com.android.wako.json.OrderConfirmJson;
import com.android.wako.json.OrderRechargeJson;
import com.android.wako.model.OrderModel;
import com.android.wako.net.ResStatus;
import com.android.wako.net.util.RequestParameter;
import com.android.wako.pay.Keys;
import com.android.wako.pay.PayResult;
import com.android.wako.pay.Rsa;
import com.android.wako.pay.WXpayUtils;
import com.android.wako.util.LogUtil;
import com.android.wako.util.PreferencesUtil;
import com.android.wako.util.StringUtil;
import com.android.wako.wxapi.WXPayEntryActivity;
import com.android.wako.wxapi.WXPayEntryListener;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 订单确认
 * Created by duanmulirui
 */
public class OrderActivity extends CommonTitleActivity implements WXPayEntryListener{
    private String TAG = "OrderActivity";
    private static final int REQUEST_ORDER = 10017;
    private static final int REQUEST_COFIRM = 10021;
    private static final int SDK_PAY_FLAG = 1;

    private ImageView mHeadImg;
    private TextView mName,mServicePrice,mTotal,order_dou_count;
    private RadioGroup radioGroup;
    private RadioButton alipay,weixin;
    private ImageView douChoose;
    private int douFlag = 0;
    private String orderId,rechargeId;

    private OrderModel model;
    private double mFinal;

    //weixin
    private IWXAPI mWX_api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_main);
        initViews();

        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        mWX_api = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID, true);
        // 将该app注册到微信
        mWX_api.registerApp(Constants.WX_APP_ID);

        model = new OrderModel();
        model = (OrderModel) getIntent().getSerializableExtra("model");
        if(model != null){
            orderId = model.orderId;
            String headImg = model.headImg;
            if(!StringUtil.isEmpty(headImg)){
                MyApplication.imageLoader.displayImage(Constants.DOWNLOAD_URL + model.headImg,mHeadImg);
            }
            mName.setText(model.name);
            mServicePrice.setText(getString(R.string.order_service_price,model.moneyTotal));
            order_dou_count.setText(getString(R.string.order_dou,model.deductionLoveBean,model.moneyDeduction));
            mTotal.setText(getString(R.string.order_service_price,StringUtil.sub(model.moneyTotal,model.moneyDeduction)));
            mFinal = StringUtil.sub(model.moneyTotal,model.moneyDeduction);
        }
    }

    private void initViews() {
        loadTitleViews();
        setLeftVisibily(View.VISIBLE);
        setTitle(R.string.meng_order);

        mHeadImg = (ImageView) findViewById(R.id.order_headimg);
        mName = (TextView) findViewById(R.id.order_name);
        mServicePrice = (TextView) findViewById(R.id.order_service_price);
        radioGroup = (RadioGroup) findViewById(R.id.order_radio);
        alipay = (RadioButton) findViewById(R.id.alipay_pay);
        weixin = (RadioButton) findViewById(R.id.weixin_pay);
        order_dou_count = (TextView) findViewById(R.id.order_dou_count);
        douChoose = (ImageView) findViewById(R.id.order_dou_img);
        douChoose.setOnClickListener(this);
        mTotal = (TextView) findViewById(R.id.order_all);
        findViewById(R.id.order_to_pay).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.order_dou_img:
                if(douFlag != 0){
                    douChoose.setImageResource(R.drawable.dakai);
                    mTotal.setText(getString(R.string.order_service_price,StringUtil.sub(model.moneyTotal,model.moneyDeduction)));
                    mFinal = StringUtil.sub(model.moneyTotal,model.moneyDeduction);
                    douFlag = 0;
                }else{
                    douChoose.setImageResource(R.drawable.dakai1);
                    mTotal.setText(getString(R.string.order_service_price,model.moneyTotal));
                    mFinal = model.moneyTotal;
                    douFlag = 1;
                }
                break;
            case R.id.order_to_pay:
                setRechargOrdereData();
                break;
            default:
                break;
        }
    }

    private void setRechargOrdereData() {
        List<RequestParameter> parameter = new ArrayList<RequestParameter>();
        parameter.add(new RequestParameter("orderId",orderId));
        int flag = 0;
        if(douFlag != 0){
            flag = 0;
        }else{
            flag = 1;
        }
        parameter.add(new RequestParameter("isUseLoveBean",flag+""));
        parameter.add(new RequestParameter("moneyFinal",mFinal+""));
        startHttpRequst(Constants.HTTP_POST,Constants.ORDER_REACHRGE,parameter,true,REQUEST_ORDER,true,false);
    }

    @Override
    public void onCallback(String resultJson, int code, int resStatus) {
        super.onCallback(resultJson, code, resStatus);
        switch (code){
            case REQUEST_ORDER:
                if(resStatus == ResStatus.Success){
                    OrderRechargeJson json = gson.fromJson(resultJson,OrderRechargeJson.class);
                    if(json != null && json.content != null && json.header != null && json.header.status == 1){
                        rechargeId = json.content.rechargeId;
                        int id = getPayType();
                        switch (id) {
                            case R.id.alipay_pay:
                                setAlipayData();
                                break;
                            case R.id.weixin_pay:
                                setWeiXinData();
//                                showToast("暂不支持微信支付");
                                break;
                        }
                    }else{
                        showToast(json.header.message);
                    }
                }else{
                    showToast(ResStatus.getTipString(resStatus));
                }
                break;
            case Constants.RequestCode.WeiXin_Rechange :
                if (resStatus == ResStatus.Success) {
                    Map<String, String> result = WXpayUtils.decodeXml(resultJson);
                    if(result == null) {
                        showToast(R.string.wx_pay_fail);
                        return;
                    }
                    String prepay_id = result.get("prepay_id");
                    if(StringUtil.isEmpty(prepay_id)) {
                        showToast(R.string.wx_pay_fail);
                        return;
                    }
                    pay(prepay_id);
                } else {
                    showToast(ResStatus.getTipString(resStatus));
                }
                break;
            case REQUEST_COFIRM:
                if(resStatus == ResStatus.Success){
                    OrderConfirmJson json = gson.fromJson(resultJson,OrderConfirmJson.class);
                    if(json != null && json.content != null && json.header != null && json.header.status == 1){
                        int payStatus = json.content.payStatus;
                        if(payStatus == 0){//0-失败 1-成功 2-未支付
                            showToast(json.content.reason);
                        }else if(payStatus == 2){
                            showToast("支付确认中，请稍后查看订单状态");
                        }else if(payStatus == 1){
                            startActivity(new Intent(this,PaySuccessActivity.class));
                            finish();
                        }
                    }
                }else{
                    showToast(ResStatus.getTipString(resStatus));
                }
                break;
            default:
                break;
        }
    }

    /**
     * WenXin pay
     */
    private void setWeiXinData() {
        String ip = WXpayUtils.getLocalIp(this);
        String sum = (int)(mFinal*100) +"";//微信是“分”
//        String sum = 1+"";//test

        String uid = PreferencesUtil.getWakoString(this,PreferencesUtil.Pre_Name,PreferencesUtil.Key_Uid,"");

        List<RequestParameter> parameter = new ArrayList<RequestParameter>();
        parameter.add(new RequestParameter("appid", Constants.WX_APP_ID));
        parameter.add(new RequestParameter("body", getString(R.string.app_pay)));
        parameter.add(new RequestParameter("detail", getString(R.string.app_name)));
        parameter.add(new RequestParameter("mch_id", Constants.WX_MCH_ID));
        parameter.add(new RequestParameter("nonce_str", WXpayUtils.genNonceStr()));
        parameter.add(new RequestParameter("notify_url", Constants.NOTIFY_URL_WECHAT));
        parameter.add(new RequestParameter("out_trade_no", rechargeId + "_"+uid+ "_" + orderId));
        parameter.add(new RequestParameter("spbill_create_ip", ip));
        parameter.add(new RequestParameter("total_fee", sum));
        parameter.add(new RequestParameter("trade_type", "APP"));
        String sign = WXpayUtils.genProductSign(parameter);
        parameter.add(new RequestParameter("sign", sign));
        startHttpsRequst(Constants.HTTP_POST, Constants.URL_GET_TRADE_INFO, parameter, true, Constants.RequestCode.WeiXin_Rechange, true, false);
    }

    private void pay(String prepayId) {
        LogUtil.d(TAG,"--wx pay -- ");
        WXPayEntryActivity.registerListener(this);

        PayReq mRequest = new PayReq();
        mRequest.appId = Constants.WX_APP_ID;
        mRequest.partnerId = Constants.WX_MCH_ID;
        mRequest.prepayId = prepayId;
        mRequest.packageValue = "prepay_id=" + prepayId;
        mRequest.nonceStr = WXpayUtils.genNonceStr();
        mRequest.timeStamp = String.valueOf(WXpayUtils.genTimeStamp());

        List<RequestParameter> parameter = new LinkedList<RequestParameter>();
        parameter.add(new RequestParameter("appid", mRequest.appId));
        parameter.add(new RequestParameter("noncestr", mRequest.nonceStr));
        parameter.add(new RequestParameter("package", mRequest.packageValue));
        parameter.add(new RequestParameter("partnerid", mRequest.partnerId));
        parameter.add(new RequestParameter("prepayid", mRequest.prepayId));
        parameter.add(new RequestParameter("timestamp", mRequest.timeStamp));

        mRequest.sign = WXpayUtils.genAppSign(parameter);
        if (mWX_api.isWXAppInstalled()) {
            mWX_api.sendReq(mRequest);
        } else {
            showToast(R.string.wx_not_install);
        }
    }

    @Override
    public void onFinish() {
        setSuccessStatusData();
    }

    /**
     * alipay pay
     */
    private void setAlipayData() {
        String info = buildOrderforAlipay();
        String sign = Rsa.sign(info, Keys.PRIVATE);
        sign = URLEncoder.encode(sign);
        info += "&sign=\"" + sign + "\"&" + getSignType();
        LogUtil.i(TAG, "start pay");
        // start the pay.
        LogUtil.i(TAG, "info = " + info);

        final String orderInfo = info;

        new Thread(){

            @Override
            public void run() {
                PayTask alipay = new PayTask(OrderActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                LogUtil.i(TAG, result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    protected String getSignType() {
        return "sign_type=\"RSA\"";
    }

    private String  buildOrderforAlipay() {
        StringBuilder sb = new StringBuilder();
        sb.append("partner=\"");
        sb.append(Constants.ALIPAY_PARTNER);
        sb.append("\"&out_trade_no=\"");// 订单号..
        sb.append(makePayId());// 订单号
        sb.append("\"&subject=\"");// 产品名称：支付
        sb.append(getResources().getString(R.string.app_name));
        sb.append("\"&body=\"");// 产品介绍
        sb.append(getResources().getString(R.string.app_name)+":android付费");

        sb.append("\"&total_fee=\"");// 付费额度
        sb.append(mFinal);//mFinal

        sb.append("\"&notify_url=\"");
        sb.append(URLEncoder.encode(Constants.NOTIFY_URL));// 网址需要做URL编码
        sb.append("\"&service=\"mobile.securitypay.pay");
        sb.append("\"&_input_charset=\"UTF-8");
        sb.append("\"&payment_type=\"1");
        sb.append("\"&seller_id=\"");
        sb.append(Constants.ALIPAY_SELLER);

        sb.append("\"&it_b_pay=\"10m");
        sb.append("\"");
        return new String(sb);
    }

    private String makePayId(){
        String uid = PreferencesUtil.getWakoString(this, PreferencesUtil.Pre_Name, PreferencesUtil.Key_Uid, "");
        String payId= rechargeId+"_"+uid+"_"+orderId;
        return payId;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG:
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    String resultStatus = payResult.getResultStatus();
                    if(!StringUtil.isEmpty(resultStatus)){
                        showToast(payResult.getResultMessage(resultStatus));
                        if (resultStatus.equals("9000")) {//支付成功
                            setSuccessStatusData();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void setSuccessStatusData() {
        List<RequestParameter> parameter = new ArrayList<RequestParameter>();
        parameter.add(new RequestParameter("orderId",orderId));
        startHttpRequst(Constants.HTTP_POST,Constants.ORDER_CONFIRM,parameter,false,REQUEST_COFIRM,true,false);
    }

    private int getPayType(){
        if(weixin.isChecked()) {
            return R.id.weixin_pay;
        }else {
            return R.id.alipay_pay;
        }
    }

}
