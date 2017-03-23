package com.android.wako.json;

/**
 * Created by duanmulirui
 */
public class OrderConfirmJson extends BaseJson{

    public Content content;
    public class Content{
        public int payStatus;//订单付费状态（0-失败 1-成功 2-未支付）
        public String reason;//订单支付失败原因（字段无值 返回空串）
    }
}
