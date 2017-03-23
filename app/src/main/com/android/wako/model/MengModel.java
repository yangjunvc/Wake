package com.android.wako.model;

/**
 * Created by duanmulirui
 */
public class MengModel {

    public String uid;// 	状元Id
    public String name;// 	姓名
    public String university;//大学
    public String headImg;//头像
    public String achievement;//xxx年xx省xx科状元（类似的这样一段描述）

    public String major;//学部
    public int isShareBonus;//是否分红
    public String shareBonusDescribe;//分红项目描述
    public String achievementDescribe;//高考成绩（一段文字描述）
    public int  monthService;//月服务是否开通 	Y 	0-未开通 1-开通
    public int yearService;//年服务是否开通 	Y 	0-未开通 1-开通
    public double monthMoney;//月服务价格
    public double yearMoney;//年服务价格

}
