package com.android.wako.json;

import com.android.wako.model.MyMengModel;

import java.util.ArrayList;

/**
 * Created by duanmulirui
 */
public class MyMengListJson extends BaseJson{

    public Content content;
    public class Content{
        public int totalPage;
        public ArrayList<MyMengModel> list;
    }
}
