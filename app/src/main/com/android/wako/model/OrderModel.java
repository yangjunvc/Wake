package com.android.wako.model;

import java.io.Serializable;

/**
 * Created by duanmulirui
 */
public class OrderModel implements Serializable{

    public String orderId;//
    public String headImg;//
    public String name;//
    public int  serviceType;// 	0-月 1-年
    public double moneyTotal;//订单金额（即服务金额）
    public double moneyDeduction;//可抵扣金额
    public int deductionLoveBean;//可抵扣爱心豆数

}
