package com.android.wako.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.wako.CommonTitleActivity;
import com.android.wako.R;
import com.android.wako.WebViewActivity;
import com.android.wako.adapter.MyDouAdapter;
import com.android.wako.common.Constants;
import com.android.wako.json.MyDouJson;
import com.android.wako.model.MyDouModel;
import com.android.wako.net.ResStatus;
import com.android.wako.net.util.RequestParameter;
import com.android.wako.refresh.PullToRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的爱心豆
 * Created by duanmulirui
 */
public class MyDouActivity extends CommonTitleActivity{
    private static final int REQUEST_DOU = 10020;

    private TextView mDouTotal,mNull;
    private ListView mListView;

    private MyDouAdapter mAdapter;
    private ArrayList<MyDouModel> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_dou_main);
        initViews();

        mList = new ArrayList<MyDouModel>();
        mAdapter = new MyDouAdapter(this);
        mListView.setAdapter(mAdapter);
        getData();

    }

    private void getData() {
        List<RequestParameter> parameter = new ArrayList<RequestParameter>();
        parameter.add(new RequestParameter("currentPage","1"));
        parameter.add(new RequestParameter("rows","10"));
        startHttpRequst(Constants.HTTP_POST, Constants.MYDOU_STATEMENT_LIST, parameter, false, REQUEST_DOU, true, false);
    }

    private void initViews() {
        loadTitleViews();
        setLeftVisibily(View.VISIBLE);
        setTitle(R.string.my_dou);
        setRightText(R.string.dou_help);

        mDouTotal = (TextView) findViewById(R.id.dou_total);
        mListView = (ListView) findViewById(R.id.listview);
        findViewById(R.id.more).setOnClickListener(this);
        mNull = (TextView) findViewById(R.id.null_view);
    }

    @Override
    public void onRightTextClickListener(View v) {
        Intent intent = new Intent(this,WebViewActivity.class);
        intent.putExtra("title",getString(R.string.dou_help));
        intent.putExtra("url", Constants.USEHELP_URL);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.more:
                startActivity(new Intent(this,MyDouListActivity.class));
                break;
        }
    }

    @Override
    public void onCallback(String resultJson, int code, int resStatus) {
        super.onCallback(resultJson, code, resStatus);
        switch (code){
            case REQUEST_DOU:
                if(resStatus == ResStatus.Success){
                    MyDouJson json = gson.fromJson(resultJson, MyDouJson.class);
                    if(json != null && json.header != null && json.header.status == 1 && json.content != null){
                        mDouTotal.setText(json.content.loveBeanNum+"");
                        mList = json.content.list;
                        if (mList != null) {
                            if (mList.size() <= 0) {
                                mAdapter.clearData();
                                mNull.setVisibility(View.VISIBLE);
                                mListView.setVisibility(View.GONE);
                            } else {
                                mNull.setVisibility(View.GONE);
                                mAdapter.setData(mList);
                                mListView.setVisibility(View.VISIBLE);
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }else{
                    ResStatus.getTipString(resStatus);
                }
                break;
        }
    }
}
