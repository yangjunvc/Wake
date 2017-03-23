package com.android.wako.json;

/**
 * login
 * Created by duanmulirui
 */
public class LoginJson extends BaseJson{

    public Content content;
    public class Content{
        public String uid;
        public String token;
    }

}
