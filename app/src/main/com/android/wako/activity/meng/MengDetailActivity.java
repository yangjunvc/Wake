package com.android.wako.activity.meng;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.wako.CommonTitleActivity;
import com.android.wako.MyApplication;
import com.android.wako.R;
import com.android.wako.WebViewActivity;
import com.android.wako.common.Constants;
import com.android.wako.json.MengDetailJson;
import com.android.wako.json.OrderJson;
import com.android.wako.model.MengModel;
import com.android.wako.model.OrderModel;
import com.android.wako.net.ResStatus;
import com.android.wako.net.util.RequestParameter;
import com.android.wako.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 筑梦空间个人主页
 * Created by duanmulirui
 */
public class MengDetailActivity extends CommonTitleActivity{
    private static final int REQUEST_DETAIL = 10010;
    private static final int REQUEST_ORDER = 10011;
    private static final int TYPE_MONTH = 0;
    private static final int TYPE_YEAR = 1;

    private ImageView mHeadImage,mHeadImage_bg;
    private TextView mName,mUniversity,mMajor,mShareDesc,mAchievement,mAchieveDesc,mMonthPrice,mYearPrice;
    private View mShareView,mMonthView,mYearView;

    private String mengId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meng_detail_main);
        initViews();
        mengId = getIntent().getStringExtra("mengId");
        getDetailData();
    }

    private void getDetailData() {
        List<RequestParameter> parameter = new ArrayList<RequestParameter>();
        parameter.add(new RequestParameter("userAnswerUid",mengId));
        startHttpRequst(Constants.HTTP_POST, Constants.MENG_DETAIL, parameter, false, REQUEST_DETAIL, true, false);
    }

    private void initViews() {
        loadTitleViews();
        setLeftVisibily(View.VISIBLE);

        mHeadImage = (ImageView) findViewById(R.id.detail_headimg);
        mHeadImage_bg = (ImageView) findViewById(R.id.detail_headimg_bg);
        mHeadImage_bg.getBackground().setAlpha(100);
        mName = (TextView) findViewById(R.id.detail_name);
        mUniversity = (TextView) findViewById(R.id.detail_university);
        mMajor = (TextView) findViewById(R.id.detail_major);
        mShareDesc = (TextView) findViewById(R.id.share_describe);
        mShareView = findViewById(R.id.share_view);
        mShareView.setVisibility(View.GONE);
        mAchievement = (TextView) findViewById(R.id.detail_achievement);
        mAchieveDesc = (TextView) findViewById(R.id.achievement_describe);
//        findViewById(R.id.meng_help).setOnClickListener(this);
        mMonthView = findViewById(R.id.service_month);
        mMonthView.setOnClickListener(this);
        mMonthView.setVisibility(View.GONE);
        mMonthPrice = (TextView) findViewById(R.id.service_month_price);
        mYearView = findViewById(R.id.service_year);
        mYearView.setOnClickListener(this);
        mYearView.setVisibility(View.GONE);
        mYearPrice = (TextView) findViewById(R.id.service_year_price);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.meng_help:
                Intent intent = new Intent(this,WebViewActivity.class);
                intent.putExtra("title",R.string.meng_service_help);
                intent.putExtra("url", Constants.SERVICE_URL);
                startActivity(intent);
                break;
            case R.id.service_month:
                setOrderData(TYPE_MONTH);
                break;
            case R.id.service_year:
                setOrderData(TYPE_YEAR);
                break;
            default:
                break;
        }
    }

    private void setOrderData(int type) {
        List<RequestParameter> parameter = new ArrayList<RequestParameter>();
        parameter.add(new RequestParameter("userAnswerUid",mengId));
        parameter.add(new RequestParameter("serviceType",type+""));
        startHttpRequst(Constants.HTTP_POST, Constants.ORDER_CREATE, parameter, true, REQUEST_ORDER, true, false);
    }

    @Override
    public void onCallback(String resultJson, int code, int resStatus) {
        super.onCallback(resultJson, code, resStatus);
        switch (code){
            case REQUEST_DETAIL:
                if(resStatus == ResStatus.Success){
                    MengDetailJson json = gson.fromJson(resultJson,MengDetailJson.class);
                    if(json != null && json.header != null && json.header.status == 1 && json.content != null){
                        MengModel model = json.content;
                        if(model != null){
                            String headImg = model.headImg;
                            if(!StringUtil.isEmpty(headImg)){
                                MyApplication.imageLoader.displayImage(Constants.DOWNLOAD_URL + model.headImg,mHeadImage);
                            }
                            setTitle(model.name);
                            mName.setText(model.name);
                            mUniversity.setText(model.university);
                            mMajor.setText(model.major);
                            int isShare = model.isShareBonus;
                            if(isShare != 0){
                                mShareView.setVisibility(View.VISIBLE);
                                mShareDesc.setText(model.shareBonusDescribe);
                            }else{
                                mShareView.setVisibility(View.GONE);
                            }
                            mAchievement.setText(model.achievement);
                            mAchieveDesc.setText(model.achievementDescribe);
                            int isMonth = model.monthService;
                            int isYear = model.yearService;
                            if(isMonth != 0){
                                mMonthView.setVisibility(View.VISIBLE);
                                mMonthPrice.setText(getString(R.string.order_price,model.monthMoney));
                            }else mMonthView.setVisibility(View.GONE);
                            if(isYear != 0){
                                mYearView.setVisibility(View.VISIBLE);
                                mYearPrice.setText(getString(R.string.order_price,model.yearMoney));
                            }else mYearView.setVisibility(View.GONE);
                        }
                    }
                }else{
                    showToast(ResStatus.getTipString(resStatus));
                }
                break;
            case REQUEST_ORDER:
                if(resStatus == ResStatus.Success){
                    if(resStatus == ResStatus.Success) {
                        OrderJson json = gson.fromJson(resultJson, OrderJson.class);
                        if (json != null && json.header != null && json.header.status == 1 && json.content != null) {
                            OrderModel model = json.content;
                            startActivity(new Intent(this,OrderActivity.class).putExtra("model",model));
                        }else{
                            showToast(json.header.message);
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
