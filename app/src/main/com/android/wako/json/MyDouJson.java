package com.android.wako.json;

import com.android.wako.model.MyDouModel;

import java.util.ArrayList;

/**
 * Created by duanmulirui
 */
public class MyDouJson extends BaseJson{

    public Content content;
    public class Content{
        public int totalPage;
        public ArrayList<MyDouModel> list;
        public long loveBeanNum;
    }
}
