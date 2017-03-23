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
 * Created by duanmulirui
 */
public class ModifyPwActivity extends CommonTitleActivity {
    private static final int REQUEST_MODIFY = 1005;

    private EditText mOld,mNew,mConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_pw_main);
        initViews();
    }

    private void initViews() {
        loadTitleViews();
        setLeftVisibily(View.VISIBLE);
        setTitle(R.string.setting_modify);

        mOld = (EditText) findViewById(R.id.modify_old);
        mNew = (EditText) findViewById(R.id.modify_new);
        mConfirm = (EditText) findViewById(R.id.modify_confirm);
        findViewById(R.id.to_modify).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.to_modify:
                if(StringUtil.isEmpty(mOld.getText().toString())){
                    showToast(R.string.modify_old_null);
                }else if(!StringUtil.isPassword(mOld.getText().toString())){
                    showToast(R.string.modify_old_error);
                }else if(StringUtil.isEmpty(mNew.getText().toString())){
                    showToast(R.string.modify_new_null);
                }else if(!StringUtil.isPassword(mNew.getText().toString())) {
                    showToast(R.string.modify_new_error);
                }else if(StringUtil.isEmpty(mConfirm.getText().toString())){
                    showToast(R.string.modify_confirm_null);
                }else if(!mNew.getText().toString().equals(mConfirm.getText().toString())){
                    showToast(R.string.modify_new_confirm);
                }else modifyData();
                break;
        }
    }

    private void modifyData() {
        List<RequestParameter> parameter = new ArrayList<RequestParameter>();
        parameter.add(new RequestParameter("passwordOld", mOld.getText().toString().trim()));
        parameter.add(new RequestParameter("passwordNew", mNew.getText().toString().trim()));
        parameter.add(new RequestParameter("rePasswordNew", mConfirm.getText().toString().trim()));
        startHttpRequst(Constants.HTTP_POST, Constants.MODIFY_PASSWORD, parameter, true, REQUEST_MODIFY, true, false);
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
