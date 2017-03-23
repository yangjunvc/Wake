package com.android.wako.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.wako.CommonTitleActivity;
import com.android.wako.R;
import com.android.wako.UtilManager;
import com.android.wako.WebViewActivity;
import com.android.wako.common.Constants;
import com.android.wako.net.ResStatus;
import com.android.wako.net.util.RequestParameter;
import com.android.wako.util.PreferencesUtil;
import com.android.wako.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * register
 * Created by duanmulirui
 */
public class RegisterActivity extends CommonTitleActivity{
    private static final int REQUEST_REGISTER = 1002;
    private static final int REQUEST_GETCODE = 1003;

    private EditText mPhone,mCode,mPassword,mConfirm;
    private Button mCodeBtn;
    private ImageView mAgree;
    private int agreeFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_main);
        initViews();
    }

    @Override
    public void onRightIconClickListener(View v) {
        finish();
    }

    private void initViews(){
        loadTitleViews();
        setTitle(R.string.register);
        setRightIcon(R.drawable.guanbi);

        mPhone = (EditText) findViewById(R.id.register_phone);
        mCode = (EditText) findViewById(R.id.register_code);
        mCodeBtn = (Button) findViewById(R.id.register_code_btn);
        mCodeBtn.setOnClickListener(this);
        mPassword = (EditText) findViewById(R.id.register_password);
        mConfirm = (EditText) findViewById(R.id.register_confirm);
        mAgree = (ImageView) findViewById(R.id.register_agree);
        mAgree.setOnClickListener(this);
        findViewById(R.id.register_protocol).setOnClickListener(this);
        findViewById(R.id.register_to_register).setOnClickListener(this);

        if (UtilManager.mRemainTime < 120 && UtilManager.mRemainTime > 0) {
            mCodeBtn.setText(String.format(RegisterActivity.this.getString(R.string.reamin_time), UtilManager.mRemainTime));
            mHandler.sendEmptyMessageDelayed(MSG_UPDATE, 1000);
        }
    }

    private static final int MSG_UPDATE = 1;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_UPDATE:
                    if (UtilManager.mRemainTime <= 0) {
                        mCodeBtn.setText(R.string.register_code);
                        return;
                    }
                    mCodeBtn.setText(String.format(RegisterActivity.this.getString(R.string.reamin_time), UtilManager.mRemainTime));
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE, 1000);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.register_code_btn:
                if(StringUtil.isEmpty(mPhone.getText().toString())){
                    showToast(R.string.phone_null);
                }else if(!StringUtil.isPhone(mPhone.getText().toString())){
                    showToast(R.string.phone_error);
                }else if (UtilManager.mRemainTime != UtilManager.Verify_Time && UtilManager.mRemainTime > 0) {
                    return;
                } else getCodeData();
                break;
            case R.id.register_agree://默认选中：0
                if(agreeFlag != 0){
                    agreeFlag = 0;
                    mAgree.setImageResource(R.drawable.xuanzhong);
                }else{
                    agreeFlag = 1;
                    mAgree.setImageResource(R.drawable.weixuan);
                }
                break;
            case R.id.register_protocol:
                Intent intent = new Intent(this,WebViewActivity.class);
                intent.putExtra("title",getString(R.string.protocol_title));
                intent.putExtra("url", Constants.USERRULE_URL);
                startActivity(intent);
                break;
            case R.id.register_to_register:
                if(StringUtil.isEmpty(mPhone.getText().toString())){
                    showToast(R.string.phone_null);
                }else if(!StringUtil.isPhone(mPhone.getText().toString())){
                    showToast(R.string.phone_error);
                }else if(StringUtil.isEmpty(mCode.getText().toString())){
                    showToast(R.string.code_null);
                }else if(StringUtil.isEmpty(mPassword.getText().toString())){
                    showToast(R.string.password_hint);
                }else if(!StringUtil.isPassword(mPassword.getText().toString())){
                    showToast(R.string.password_error);
                }else if(StringUtil.isEmpty(mConfirm.getText().toString())){
                    showToast(R.string.confirm_null);
                }else if(!(mConfirm.getText().toString()).equals(mPassword.getText().toString())){
                    showToast(R.string.password_cofirm_error);
                }else if(agreeFlag != 0){
                    showToast(R.string.register_choose_protocol);
                }else setRegisterData();
                break;
        }
    }

    private void getCodeData() {
        List<RequestParameter> parameter = new ArrayList<RequestParameter>();
        parameter.add(new RequestParameter("phoneNum", mPhone.getText().toString().trim()));
        parameter.add(new RequestParameter("type", Constants.CodeType.Register+""));
        startHttpRequst(Constants.HTTP_POST, Constants.Get_Valid_Code, parameter, true, REQUEST_GETCODE, true, false);
    }

    private void setRegisterData() {
        List<RequestParameter> parameter = new ArrayList<RequestParameter>();
        parameter.add(new RequestParameter("phoneNum", mPhone.getText().toString().trim()));
        parameter.add(new RequestParameter("password", mPassword.getText().toString().trim()));
        parameter.add(new RequestParameter("validCode", mCode.getText().toString().trim()));
        parameter.add(new RequestParameter("inviteCode", ""));
        startHttpRequst(Constants.HTTP_POST, Constants.Register, parameter, true, REQUEST_REGISTER, true, false);
    }

    @Override
    public void onCallback(String resultJson, int code, int resStatus) {
        super.onCallback(resultJson, code, resStatus);
        switch (code){
            case REQUEST_REGISTER:
                if(resStatus == ResStatus.Success){
                    if(baseJson != null && baseJson.header.status == 1){
                        PreferencesUtil.saveWakoString(this,PreferencesUtil.Pre_Name,PreferencesUtil.Key_Phone,mPhone.getText().toString());
                        showToast(R.string.register_success);
                        finish();
                    }
                }else{
                    showToast(ResStatus.getTipString(resStatus));
                }
                break;
            case REQUEST_GETCODE:
                if(resStatus == ResStatus.Success){
                    if(baseJson != null && baseJson.header.status == 1){
                        UtilManager.getInstance().startRemainTime();
                        mHandler.sendEmptyMessage(MSG_UPDATE);
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
