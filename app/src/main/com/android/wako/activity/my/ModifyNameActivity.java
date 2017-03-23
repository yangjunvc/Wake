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
 * 修改姓名
 * Created by duanmulirui
 */
public class ModifyNameActivity extends CommonTitleActivity{
    private static final int REQUEST_MODIFY = 10018;

    private EditText mName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_name_main);
        initViews();

        mName.setText(getIntent().getStringExtra("name"));
        mName.setSelection(mName.length());
    }

    @Override
    public void onRightTextClickListener(View v) {
        String content = mName.getText().toString().trim();
        if(StringUtil.isEmpty(content)){
            showToast(R.string.modify_name_null);
        }else if(!StringUtil.isNickName(content)){
            showToast(R.string.modify_name_error);
        }else modifyData();
    }

    private void modifyData() {
        List<RequestParameter> parameter = new ArrayList<RequestParameter>();
        parameter.add(new RequestParameter("name",mName.getText().toString().trim()));
        startHttpRequst(Constants.HTTP_POST, Constants.MODIFYUSRINFO_URL, parameter, true, REQUEST_MODIFY, true, false);
    }

    private void initViews() {
        loadTitleViews();
        setLeftVisibily(View.VISIBLE);
        setTitle(R.string.modify_name);
        setRightText(R.string.save);

        mName = (EditText) findViewById(R.id.modify_name);
    }

    @Override
    public void onCallback(String resultJson, int code, int resStatus) {
        super.onCallback(resultJson, code, resStatus);
        switch (code){
            case REQUEST_MODIFY:
                if(resStatus == ResStatus.Success){
                    if(baseJson != null && baseJson.header != null && baseJson.header.status == 1){
                        showToast(R.string.modify_success);
                        finish();
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
