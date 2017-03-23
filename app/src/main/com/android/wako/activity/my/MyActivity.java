package com.android.wako.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.wako.CommonTitleActivity;
import com.android.wako.MyApplication;
import com.android.wako.R;
import com.android.wako.common.Constants;
import com.android.wako.json.UserInfoJson;
import com.android.wako.model.UserInfo;
import com.android.wako.net.ResStatus;
import com.android.wako.net.util.RequestParameter;
import com.android.wako.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * mine
 * Created by duanmulirui
 */
public class MyActivity extends CommonTitleActivity{
    private static final int REQUEST_MY = 1006;

    private ImageView mHeadImage,mHeadImage_bg,mQrcode;
    private TextView mNickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_main);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        List<RequestParameter> parameter = new ArrayList<RequestParameter>();
        startHttpRequst(Constants.HTTP_POST, Constants.USERINFO_URL, parameter, false, REQUEST_MY, true, false);
    }

    @Override
    public void onRightIconClickListener(View v) {
        startActivity(new Intent(this,SettingActivity.class));
    }

    private void initViews() {
        loadTitleViews();
        setTitle(R.string.my_title);
        setRightIcon(R.drawable.shezhi);

        findViewById(R.id.my_userinfo_view).setOnClickListener(this);
        findViewById(R.id.my_question_view).setOnClickListener(this);
        findViewById(R.id.my_meng_view).setOnClickListener(this);
        findViewById(R.id.my_order_view).setOnClickListener(this);
        findViewById(R.id.my_dou_view).setOnClickListener(this);
        mHeadImage = (ImageView) findViewById(R.id.my_headimg);
        mHeadImage_bg = (ImageView) findViewById(R.id.my_headimg_bg);
        mHeadImage_bg.getBackground().setAlpha(100);
        mQrcode = (ImageView) findViewById(R.id.my_qrcode);
        mNickName = (TextView) findViewById(R.id.my_name);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.my_userinfo_view:
                startActivity(new Intent(this,UserInfoActivity.class));
                break;
            case R.id.my_question_view:
                startActivity(new Intent(this,MyQuestionActivity.class));
                break;
            case R.id.my_meng_view:
                startActivity(new Intent(this,MyMengActivity.class));
                break;
            case R.id.my_order_view:
                startActivity(new Intent(this,MyOrderListActivity.class));
                break;
            case R.id.my_dou_view:
                startActivity(new Intent(this,MyDouActivity.class));
                break;
            default: break;
        }
    }

    @Override
    public void onCallback(String resultJson, int code, int resStatus) {
        super.onCallback(resultJson, code, resStatus);
        switch (code){
            case REQUEST_MY:
                if(resStatus == ResStatus.Success){
                    UserInfoJson json = gson.fromJson(resultJson,UserInfoJson.class);
                    if(json != null && json.content != null && json.header != null && json.header.status == 1){
                        UserInfo model = json.content;
                        if(model != null){
                            String headImg = model.headImg;
                            if(!StringUtil.isEmpty(headImg)){
                                MyApplication.imageLoader.displayImage(Constants.DOWNLOAD_URL + model.headImg,mHeadImage);
                            }
                            mNickName.setText(model.name);
                            String qrcode = model.barcodeUrl;
                            if(!StringUtil.isEmpty(qrcode)){
                                MyApplication.imageLoader.displayImage(Constants.DOWNLOAD_URL + qrcode,mQrcode);
                            }
                        }
                    }
                }else{
                    showToast(ResStatus.getTipString(resStatus));
                }
                break;
            default:
                break;
        }
    }
}
