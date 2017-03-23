package com.android.wako.model;

import java.io.Serializable;

/**
 * Created by duanmulirui
 */
public class MessageModel implements Serializable{

    public String messageId;//消息记录Id
    public int messageType;//消息类型（1:回复提醒，2:问题被置为无效）
    public String userAnswerUid;//状元uid
    public String content;//消息描述
    public long createDate;//消息时间
    public String questionId;//对应问题id（该字段不可为空 没有返回空串，点击item界面跳转至问答详情)
}
