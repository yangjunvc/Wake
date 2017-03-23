package com.android.wako.json;

import com.android.wako.model.QuestionModel;

import java.util.ArrayList;

/**
 * Created by duanmulirui
 */
public class MyQuestionJson extends BaseJson{

    public Content content;
    public class Content{
    public ArrayList<QuestionModel> list;
        public int totalPage;
    }
}
