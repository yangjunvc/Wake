package com.android.wako.json;

import com.android.wako.model.MessageModel;

import java.util.ArrayList;

/**
 * Created by duanmulirui
 */
public class MessageListJson extends BaseJson{
    public Content content;
    public class Content{
        public int totalPage;
        public ArrayList<MessageModel> list;
    }
}
