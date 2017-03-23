package com.android.wako;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.android.wako.activity.base.BaseActivity;
import com.android.wako.common.ActivityManager;
import com.android.wako.model.Version;

/**
 * 版本升级
 * Created by duanmulirui
 */
public class UpdateActivity extends BaseActivity{

    Version mVersion;
    static final String TAG = "UpdateActivity";
    Button ok,cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mVersion = (Version) this.getIntent().getSerializableExtra("version");
        this.setContentView(R.layout.update_popuwindow);
        TextView content = (TextView) findViewById(R.id.content);
        Button update = (Button) findViewById(R.id.update);
        View layout = findViewById(R.id.btn_layout);
        ok = (Button) findViewById(R.id.ok);
        cancel = (Button) findViewById(R.id.cancel);
        content.setText(mVersion.versionDesc);

        if (mVersion.result == -1) {//强制更新
            layout.setVisibility(View.GONE);
            update.setVisibility(View.VISIBLE);
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addDownloadManager();
                    UpdateActivity.this.finish();
                    ActivityManager.finishAll();
                }
            });
        } else {
            layout.setVisibility(View.VISIBLE);
            update.setVisibility(View.GONE);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addDownloadManager();
                    UpdateActivity.this.finish();
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UpdateActivity.this.finish();
                }
            });
            ok.requestFocus();
        }

    }

    public void addDownloadManager() {
        try{
            showToast(R.string.downloading_back);
            UtilManager.getInstance().requestDownload(mVersion.versionUrl);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mVersion.result == -1) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);

    }

}
