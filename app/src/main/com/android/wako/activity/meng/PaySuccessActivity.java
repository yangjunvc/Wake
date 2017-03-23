package com.android.wako.activity.meng;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.wako.CommonTitleActivity;
import com.android.wako.R;
import com.android.wako.activity.my.MyMengActivity;

/**
 * 支付成功提示
 * Created by duanmulirui
 */
public class PaySuccessActivity extends CommonTitleActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_success_main);
        initViews();
    }

    private void initViews() {
        loadTitleViews();
        setLeftVisibily(View.VISIBLE);

        findViewById(R.id.to_go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PaySuccessActivity.this, MyMengActivity.class));
                finish();
            }
        });
    }

}
