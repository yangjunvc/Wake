package com.android.wako.activity.my;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.wako.CommonTitleActivity;
import com.android.wako.R;
import com.android.wako.adapter.MyDouAdapter;
import com.android.wako.common.Constants;
import com.android.wako.json.MyDouJson;
import com.android.wako.model.MyDouModel;
import com.android.wako.net.ResStatus;
import com.android.wako.net.util.RequestParameter;
import com.android.wako.refresh.PullToRefreshLayout;
import com.android.wako.util.SystemInfoUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 爱心豆list
 * Created by duanmulirui
 */
public class MyDouListActivity extends CommonTitleActivity{
    private static final int REQUEST_DOU = 10020;

    private PullToRefreshLayout mPtrl;
    private ListView mListView;

    private MyDouAdapter mAdapter;
    private ArrayList<MyDouModel> mList;

    private int totalPage,page;

    private TextView mNull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_list_main);
        initViews();

        mList = new ArrayList<MyDouModel>();
        mAdapter = new MyDouAdapter(this);
        mListView.setAdapter(mAdapter);

        getListData();
    }

    private void getListData() {
        List<RequestParameter> parameter = new ArrayList<RequestParameter>();
        parameter.add(new RequestParameter("currentPage",page+1+""));
        parameter.add(new RequestParameter("rows","15"));
        startHttpRequst(Constants.HTTP_POST, Constants.MYDOU_STATEMENT_LIST, parameter, false, REQUEST_DOU, true, false);
    }

    private void initViews() {
        loadTitleViews();
        setLeftVisibily(View.VISIBLE);
        setTitle(R.string.my_dou_payment);

        mPtrl = (PullToRefreshLayout) findViewById(R.id.refresh_view);
        mPtrl.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                if (SystemInfoUtil.isNetworkConnected(MyDouListActivity.this)) {
                    page = 0;
                    getListData();
                } else {
                    mPtrl.refreshFinish(PullToRefreshLayout.FAIL);
                    showToast(R.string.network_error);
                }
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                if (page < totalPage && SystemInfoUtil.isNetworkConnected(MyDouListActivity.this)) {
                    getListData();
                }else{
                    if(!SystemInfoUtil.isNetworkConnected(MyDouListActivity.this)){
                        showToast(R.string.network_error);
                        mPtrl.refreshFinish(PullToRefreshLayout.FAIL);
                    }else{
                        if (page >= totalPage) {
                            mPtrl.mCanPull = false;
                        } else {
                            mPtrl.mCanPull = true;
                        }
                        mPtrl.refreshFinish(PullToRefreshLayout.SUCCEED);
                    }
                }
            }
        });
        mListView = (ListView) findViewById(R.id.listview);
        mNull = (TextView) findViewById(R.id.null_view);
    }

    @Override
    public void onCallback(String resultJson, int code, int resStatus) {
        super.onCallback(resultJson, code, resStatus);
        switch (code){
            case REQUEST_DOU:
                if(resStatus == ResStatus.Success){
                    MyDouJson json = gson.fromJson(resultJson, MyDouJson.class);
                    if(json != null && json.header != null && json.header.status == 1 && json.content != null){
                        mPtrl.refreshFinish(PullToRefreshLayout.SUCCEED);
                        mList = json.content.list;
                        totalPage = json.content.totalPage;
                        if (mList != null) {
                            if (mList.size() <= 0) {
                                mAdapter.clearData();
                                mNull.setVisibility(View.VISIBLE);
                            } else {
                                mNull.setVisibility(View.GONE);
                                if (page == 0) {
                                    mAdapter.setData(mList);
                                } else {
                                    mAdapter.addData(mList);
                                }
                                if (page >= totalPage - 1) {
                                    mPtrl.mCanPull = false;
                                } else {
                                    page++;
                                    mPtrl.mCanPull = true;
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        }
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
