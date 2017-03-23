package com.android.wako.json;

import com.android.wako.model.MengModel;

import java.util.ArrayList;

/**
 * Created by duanmulirui
 */
public class MengJson extends BaseJson{
    public Content content;
    public class Content{
        public int totalPage;
        public ArrayList<MengModel> list;
    }
}
