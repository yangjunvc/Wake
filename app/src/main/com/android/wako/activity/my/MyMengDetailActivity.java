package com.android.wako.activity.my;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.android.wako.CommonTitleActivity;
import com.android.wako.R;
import com.android.wako.common.Constants;
import com.android.wako.net.ResStatus;
import com.android.wako.net.util.RequestParameter;
import com.android.wako.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 提问
 * Created by duanmulirui
 */
public class MyMengDetailActivity extends CommonTitleActivity{
    private static final int REQUEST_QUESTION = 10015;

    private EditText mContent;
    private String userAnswerUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_meng_detail_main);
        initViews();

        userAnswerUid = getIntent().getStringExtra("userAnswerUid");

    }

    @Override
    public void onRightTextClickListener(View v) {
        if(StringUtil.isEmpty(mContent.getText().toString())){
            showToast(R.string.my_meng_detail_hint);
        } else if(!StringUtil.containEmoji(mContent.getText().toString())){
            showToast(R.string.contain_emoji);
        }else{
            setData();
        }
    }

    private void setData() {
        List<RequestParameter> parameter = new ArrayList<RequestParameter>();
        parameter.add(new RequestParameter("userAnswerUid",userAnswerUid));
        parameter.add(new RequestParameter("questionContent",mContent.getText().toString()));
        startHttpRequst(Constants.HTTP_POST, Constants.MYMENG_QUESTION, parameter, true, REQUEST_QUESTION, true, false);
    }

    private void initViews() {
        loadTitleViews();
        setLeftVisibily(View.VISIBLE);
        setTitle(R.string.my_meng_detail);
        setRightText(R.string.sumbit);

        mContent = (EditText) findViewById(R.id.content);
    }

    @Override
    public void onCallback(String resultJson, int code, int resStatus) {
        super.onCallback(resultJson, code, resStatus);
        switch (code){
            case REQUEST_QUESTION:
                if(resStatus == ResStatus.Success){
                    if(baseJson != null && baseJson.header != null && baseJson.header.status == 1){
                        showToast(R.string.oprate_success);
                        finish();
                    }
                }else{
                    showToast(ResStatus.getTipString(resStatus));
                }
                break;
        }
    }
}
