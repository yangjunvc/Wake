package com.android.wako.json;

import com.android.wako.model.BannerModel;

import java.util.ArrayList;

/**
 * Created by duanmulirui
 */
public class BannerJson extends BaseJson{

    public Content content;
    public class Content{
        public ArrayList<BannerModel> list;
    }
}
