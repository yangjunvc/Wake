package com.android.wako.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.wako.R;
import com.android.wako.activity.base.BaseActivity;
import com.android.wako.activity.tabhost.MainTabHostActivity;
import com.android.wako.common.ActivityManager;
import com.android.wako.common.Constants;
import com.android.wako.json.LoginJson;
import com.android.wako.net.ResStatus;
import com.android.wako.net.util.RequestParameter;
import com.android.wako.util.PreferencesUtil;
import com.android.wako.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * login
 * Created by duanmulirui
 */
public class LoginActivity extends BaseActivity{
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_LOGIN = 1001;
    public static boolean mIsShow = false;

    private EditText mPhone,mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mPhone.setText(PreferencesUtil.getWakoString(this,PreferencesUtil.Pre_Name,PreferencesUtil.Key_Phone,""));
        mPhone.setSelection(mPhone.length());
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.login_to_login:
                if(StringUtil.isEmpty(mPhone.getText().toString())){
                    showToast(R.string.phone_null);
                }else if(!StringUtil.isPhone(mPhone.getText().toString())){
                    showToast(R.string.phone_error);
                }else if(StringUtil.isEmpty(mPassword.getText().toString())){
                    showToast(R.string.password_hint);
                }else if(!StringUtil.isPassword(mPassword.getText().toString())){
                    showToast(R.string.password_error);
                }else setLoginData();
                break;
            case R.id.login_forget:
                startActivity(new Intent(this,ForgetPwActivity.class));
                break;
            case R.id.login_to_register:
                startActivity(new Intent(this,RegisterActivity.class));
                break;
            default:
                break;
        }
    }

    private void setLoginData(){
        List<RequestParameter> parameter = new ArrayList<RequestParameter>();
        parameter.add(new RequestParameter("phoneNum", mPhone.getText().toString().trim()));
        parameter.add(new RequestParameter("password", mPassword.getText().toString().trim()));
        startHttpRequst(Constants.HTTP_POST, Constants.Login, parameter, true, REQUEST_LOGIN, true, false);
    }

    private void initViews(){
        mPhone = (EditText) findViewById(R.id.login_phone);
        mPassword = (EditText) findViewById(R.id.login_password);
        findViewById(R.id.login_to_login).setOnClickListener(this);
        findViewById(R.id.login_forget).setOnClickListener(this);
        findViewById(R.id.login_to_register).setOnClickListener(this);
    }

    @Override
    public void onCallback(String resultJson, int code, int resStatus) {
        super.onCallback(resultJson, code, resStatus);
        switch (code){
            case REQUEST_LOGIN:
                if(resStatus == ResStatus.Success){
                    LoginJson json = gson.fromJson(resultJson,LoginJson.class);
                    if(json != null && json.content != null && json.header.status == 1){
                        ActivityManager.mIsFromLogin = true;
                        PreferencesUtil.saveWakoString(this,PreferencesUtil.Pre_Name,PreferencesUtil.Key_Phone,mPhone.getText().toString());
                        PreferencesUtil.saveWakoString(this,PreferencesUtil.Pre_Name,PreferencesUtil.Key_Uid,json.content.uid);
                        PreferencesUtil.saveWakoString(this,PreferencesUtil.Pre_Name,PreferencesUtil.Key_Token,json.content.token);
                        startActivity(new Intent(this,MainTabHostActivity.class));
                        finish();
                    }
                }else{
                    showToast(ResStatus.getTipString(resStatus));
                }
                break;
            default: break;
        }
    }
}
