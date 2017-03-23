package com.android.wako.json;

import com.android.wako.model.MyDouModel;
import com.android.wako.model.MyOrderModel;

import java.util.ArrayList;

/**
 * Created by duanmulirui
 */
public class MyOrderListJson extends BaseJson{

    public Content content;
    public class Content{
        public int totalPage;
        public ArrayList<MyOrderModel> list;
    }
}
