package com.android.wako;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.android.wako.activity.base.BaseActivity;
import com.android.wako.activity.login.LoginActivity;
import com.android.wako.util.PreferencesUtil;

/**
 * 账号异常
 * 
 */
public class AccountExceptionActivity extends BaseActivity{
    static final String TAG = "AccountExceptionActivity";
    Button ok;
    TextView content;
    int type = 0;//0表示异地登陆，1表示账号被锁定

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.remote_popuwindow);
        content = (TextView) findViewById(R.id.content);
        ok = (Button) findViewById(R.id.ok);
        type = this.getIntent().getIntExtra("type", 0);
        if(type==0){
            content.setText("你的账号在另一台设备登录。");
        }else if(type ==1){
            content.setText("你账号被锁定，请重新登录");
        }

        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	PreferencesUtil.clearPartData(MyApplication.mCon);
                Intent intent = new Intent(AccountExceptionActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        ok.requestFocus();

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
