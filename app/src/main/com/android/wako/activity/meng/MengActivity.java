package com.android.wako.activity.meng;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.android.wako.CommonTitleActivity;
import com.android.wako.R;
import com.android.wako.adapter.MengAdapter;
import com.android.wako.common.Constants;
import com.android.wako.json.MengJson;
import com.android.wako.model.MengModel;
import com.android.wako.net.ResStatus;
import com.android.wako.net.util.RequestParameter;
import com.android.wako.refresh.PullToRefreshLayout;
import com.android.wako.util.SystemInfoUtil;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * 筑梦空间
 * Created by duanmulirui
 */
public class MengActivity extends CommonTitleActivity{
    private static final int REQUEST_MENGLIST = 1009;

    private PullToRefreshLayout mPtrl;
    private GridView mGridView;

    private ArrayList<MengModel> mList;
    private MengAdapter mAdapter;

    private int totalPage,page;

    private TextView mNull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meng_main);
        initViews();

        mList = new ArrayList<MengModel>();
        mAdapter = new MengAdapter(this);
        mGridView.setAdapter(mAdapter);

        getListData();

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = new Intent(MengActivity.this, MengDetailActivity.class);
                MengModel model = (MengModel) mAdapter.getItem(arg2);
                intent.putExtra("mengId",model.uid);
                startActivity(intent);
            }
        });
    }

    private void getListData() {
        List<RequestParameter> parameter = new ArrayList<RequestParameter>();
        parameter.add(new RequestParameter("currentPage",page+1+""));
        parameter.add(new RequestParameter("rows","10"));
        startHttpRequst(Constants.HTTP_POST, Constants.MENG_LIST, parameter, false, REQUEST_MENGLIST, true, false);
    }

    private void initViews() {
        loadTitleViews();
        setTitle(R.string.meng_title);

        mNull = (TextView) findViewById(R.id.null_view);
        mPtrl = (PullToRefreshLayout) findViewById(R.id.refresh_view);
        mPtrl.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                if (SystemInfoUtil.isNetworkConnected(MengActivity.this)) {
                    page = 0;
                    getListData();
                } else {
                    mPtrl.refreshFinish(PullToRefreshLayout.FAIL);
                    showToast(R.string.network_error);
                }
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                if (page < totalPage && SystemInfoUtil.isNetworkConnected(MengActivity.this)) {
                    getListData();
                }else{
                    if(!SystemInfoUtil.isNetworkConnected(MengActivity.this)){
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
        mGridView = (GridView) findViewById(R.id.meng_grid_view);
    }

    @Override
    public void onCallback(String resultJson, int code, int resStatus) {
        super.onCallback(resultJson, code, resStatus);
        switch (code){
            case REQUEST_MENGLIST:
                if(resStatus == ResStatus.Success){
                    MengJson json = gson.fromJson(resultJson, MengJson.class);
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
