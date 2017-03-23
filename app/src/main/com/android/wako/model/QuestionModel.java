package com.android.wako.model;

/**
 * Created by duanmulirui
 */
public class QuestionModel {

    public String questionId;//问答Id
    public String headImg;//头像
    public String name;//姓名
//    public int serverTypes;//0-月 1-年
    public int answerStatus;//问题解答状态 0-未解答 1-已解答
    public String questionContent;//问题
    public String answerContent;//回复
    public long createDate;// 	提问时间

}
