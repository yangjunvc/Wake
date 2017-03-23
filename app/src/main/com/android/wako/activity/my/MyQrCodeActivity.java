package com.android.wako.activity.my;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.wako.CommonTitleActivity;
import com.android.wako.MyApplication;
import com.android.wako.R;
import com.android.wako.common.Constants;
import com.android.wako.util.StringUtil;

/**
 * 二维码
 * Created by duanmulirui
 */
public class MyQrCodeActivity extends CommonTitleActivity{

    private ImageView mQrCode;
    private TextView mInvite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcode_main);
        initViews();

        String qrcode = getIntent().getStringExtra("qrcode");
        if(!StringUtil.isEmpty(qrcode)){
            MyApplication.imageLoader.displayImage(Constants.DOWNLOAD_URL + qrcode,mQrCode);
        }
        mInvite.setText(getString(R.string.qrcode_invite,getIntent().getStringExtra("invite")));
    }

    private void initViews() {
        loadTitleViews();
        setLeftVisibily(View.VISIBLE);
        setTitle(R.string.userinfo_qrcode);

        mQrCode = (ImageView) findViewById(R.id.qr_code);
        mInvite = (TextView) findViewById(R.id.qrcode_invite);
    }
}
