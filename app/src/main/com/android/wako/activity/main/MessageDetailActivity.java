package com.android.wako.activity.main;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.wako.CommonTitleActivity;
import com.android.wako.R;
import com.android.wako.model.MessageModel;

/**
 * messageDetail
 * Created by duanmulirui
 */
public class MessageDetailActivity extends CommonTitleActivity{

    private TextView mContent;
    private MessageModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_detail_main);
        initViews();

        model = new MessageModel();
        model = (MessageModel) getIntent().getSerializableExtra("model");
        if(model != null){
            mContent.setText(model.content);
        }
    }

    private void initViews() {
        loadTitleViews();
        setLeftVisibily(View.VISIBLE);
        setTitle(R.string.message_detail_title);

        mContent = (TextView) findViewById(R.id.content);
    }
}
