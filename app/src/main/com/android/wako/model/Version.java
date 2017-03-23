package com.android.wako.model;

import java.io.Serializable;

/**
 * Created by duanmulirui
 */
public class Version implements Serializable{

    public int result = 1;//验证结果  -1=强制更新版本 0=有非强制更新版本 1=当前为最新版本
    public String versionDesc;// 	版本描述
    public long systemDate;//系统时间
    public String versionUrl;//下载地址 	需要更新时返回此字段，不需要更新不返回
    public String version;//版本号 	需要更新时返回此字段，不需要更新不返回
    public int buildCode;//构建版本号 	需要更新时返回此字段，不需要更新不返回

}
