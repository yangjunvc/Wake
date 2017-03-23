package com.android.wako.model;

import java.io.Serializable;

/**
 * Created by duanmulirui
 */
public class MyOrderModel implements Serializable{

    public String orderId;//订单Id
    public long createDate;//订单创建时间（格林时间）
    public long payTime;//订单付费时间
    public int payStatus;//订单付费状态（目前只有支付成功 0-失败 1-成功 2-未支付
    public String name;//服务人姓名（即状元姓名）
    public int serviceType;//0-月 1-年
    public double moneyTotal;//订单金额（即服务金额）
    public double moneyFinal;//实付金额
    public double moneyDeduction;//抵扣金额
    public int deductionLoveBean;//抵扣爱心豆数
    public String orderNum;//订单编号
    public int payType;//支付方式0、微信/1、支付宝
    public String statementNum;//交易流水号

}
