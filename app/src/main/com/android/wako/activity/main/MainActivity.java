package com.android.wako.activity.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.android.wako.MyApplication;
import com.android.wako.MyReceiver;
import com.android.wako.R;
import com.android.wako.WebViewActivity;
import com.android.wako.activity.base.BaseActivity;
import com.android.wako.activity.meng.MengDetailActivity;
import com.android.wako.adapter.BannerListAdapter;
import com.android.wako.adapter.MengAdapter;
import com.android.wako.common.Constants;
import com.android.wako.json.BannerJson;
import com.android.wako.json.MengJson;
import com.android.wako.model.BannerModel;
import com.android.wako.model.MengModel;
import com.android.wako.net.ResStatus;
import com.android.wako.net.util.RequestParameter;
import com.android.wako.util.RecycleImageView;
import com.android.wako.util.StringUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener{
    private static final String TAG = "MainActivity";
    private static final int REQUEST_MENGLIST = 1008;
    private static final int REQUEST_BANNER = 1007;
    private static final int MSG_Change_Banner = 3;
    private static final int mChangeTime = 3000;

    PullToRefreshScrollView mPullRefreshScrollView;
    ScrollView mScrollView;

    private View mBannerLay;
    private ViewPager mViewPager;
    private LinearLayout mBannerDot;
    private ArrayList<BannerModel> mBannerList;
    private BannerListAdapter mBannerAdapter;

    private ArrayList<View> mViewsList = new ArrayList<View>(); // 导航图片填充器
    private ImageView[] mImageViews;// 小圆点填充器

    private GridView mGridView;
    private ArrayList<MengModel> mMengList;
    private MengAdapter mMengAdapter;

    private ImageView messageDot;
    NoticeReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        getBannerListData();

        mMengList = new ArrayList<MengModel>();
        mMengAdapter = new MengAdapter(this);
        getMengListData();
        mGridView.setAdapter(mMengAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = new Intent(MainActivity.this, MengDetailActivity.class);
                intent.putExtra("mengId",mMengList.get(arg2).uid);
                startActivity(intent);
            }
        });


        receiver = new NoticeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyReceiver.NEW_MSG);
        // 动态注册BroadcastReceiver
        registerReceiver(receiver, filter);
    }

    private void getMengListData() {
        List<RequestParameter> parameter = new ArrayList<RequestParameter>();
        startHttpRequst(Constants.HTTP_POST, Constants.MAINMENG_LIST, parameter, false, REQUEST_MENGLIST, true, false);
    }

    private void getBannerListData() {
        List<RequestParameter> parameter = new ArrayList<RequestParameter>();
        startHttpRequst(Constants.HTTP_POST, Constants.BANNER_LIST, parameter, false, REQUEST_BANNER, true, false);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.main_message:
                startActivity(new Intent(this,MessageListActivity.class));
                break;
            default:
                break;
        }
    }

    private void initViews() {
        mPullRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
        mPullRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                getBannerListData();
                getMengListData();
            }
        });
        mPullRefreshScrollView.onRefreshComplete();

        mScrollView = mPullRefreshScrollView.getRefreshableView();

        mViewPager = (ViewPager)findViewById(R.id.banner);
        mBannerDot = (LinearLayout) findViewById(R.id.banner_dot);
        mBannerLay = findViewById(R.id.banner_lay);
        initBannerLay();
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mHandler.removeMessages(MSG_Change_Banner);
                mHandler.sendEmptyMessageDelayed(MSG_Change_Banner, mChangeTime);
                return false;
            }
        });

        mGridView = (GridView) findViewById(R.id.meng_grid_view);

        findViewById(R.id.main_message).setOnClickListener(this);
        messageDot = (ImageView) findViewById(R.id.message_dot);
        messageDot.setVisibility(View.GONE);
    }

    public void initBannerLay() {
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        float rat = (float)360/(float)720;
        float height = rat * width;
        LayoutParams blay = (LayoutParams)mBannerLay.getLayoutParams();
        blay.height = (int)height;
        mBannerLay.setLayoutParams(blay);
        mBannerLay.setBackgroundResource(R.drawable.banner_bg);
    }

    @Override
    public void onCallback(String resultJson, int code, int resStatus) {
        super.onCallback(resultJson, code, resStatus);
        mPullRefreshScrollView.onRefreshComplete();
        switch (code) {
            case REQUEST_BANNER:
                if(resStatus == ResStatus.Success){
                    BannerJson json = gson.fromJson(resultJson, BannerJson.class);
                    if(json != null && json.header != null && json.header.status == 1 && json.content != null){
                        initBanner(json.content.list);
                        return;
                    }
                    initBanner(null);
                }else{
                    showToast(ResStatus.getTipString(resStatus));
                }
                break;
            case REQUEST_MENGLIST:
                if(resStatus == ResStatus.Success){
                    MengJson json = gson.fromJson(resultJson, MengJson.class);
                    if(json != null && json.header != null && json.header.status == 1 && json.content != null){
                        mMengList = json.content.list;
                        if(mMengList != null){
                            mMengAdapter.setData(mMengList);
                            mMengAdapter.notifyDataSetChanged();
                            mGridView.setVisibility(View.VISIBLE);
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

    private void initBanner(ArrayList<BannerModel> list){
        if (list == null || list.size() <= 0) {
            return;
        }
        if( mBannerList != null ){
            mBannerList.clear();
        }
        if(mViewsList!=null){
            mViewsList.clear();
        }
        mBannerDot.removeAllViews();
        mBannerList = list;
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        float rat = (float)360/(float)720;
        float height = rat * width;
        mImageViews = new ImageView[mBannerList.size()];
        for (int i = 0; i < mBannerList.size(); i++) {
            LinearLayout linear = new LinearLayout(getBaseContext());
            linear.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            RecycleImageView iv = new RecycleImageView(this);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            params.height = width;
            params.height = (int)height;
            iv.setLayoutParams(params);
            if(StringUtil.isEmpty(mBannerList.get(i).imgUrl)){
                iv.setImageResource(R.drawable.banner_bg);
            }else{
                String url = Constants.DOWNLOAD_URL+mBannerList.get(i).imgUrl;
                MyApplication.imageLoader.displayImage(url, iv);
            }
            linear.addView(iv);
            linear.setTag(i);
            linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (Integer)v.getTag();
                    BannerModel banner = mBannerList.get(pos);
                    Intent intent = new Intent(MainActivity.this,WebViewActivity.class);
                    intent.putExtra("url", Constants.BANNER_URL+banner.url);
                    startActivity(intent);
                }
            });
            mViewsList.add(linear);

            ImageView image = new ImageView(this);
            image.setPadding(0, 0, 15, 0);
            mImageViews[i] = image;
            mBannerDot.addView(mImageViews[i]);
        }
        if (mBannerAdapter == null) {
            mBannerAdapter = new BannerListAdapter();
        }
        mBannerAdapter.mViews = mViewsList;
        if (mViewPager.getAdapter() == null) {
            mViewPager.setAdapter(mBannerAdapter);
            mViewPager.setOnPageChangeListener(this);
        }
        mBannerAdapter.notifyDataSetChanged();
        initBannerDot();
        mHandler.sendEmptyMessageDelayed(MSG_Change_Banner, mChangeTime);
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_Change_Banner:
                    if(mBannerList!=null && mBannerList.size()>1){
                        mHandler.removeMessages(MSG_Change_Banner);
                        int index = mViewPager.getCurrentItem();
                        if(index == mBannerList.size()-1){
                            mViewPager.setCurrentItem(0,true);
                        }else{
                            mViewPager.setCurrentItem(index+1,true);
                        }
                        mHandler.sendEmptyMessageDelayed(MSG_Change_Banner, mChangeTime);
                    }
                    break;
            }
        }
    };

    boolean isAutoPlay = false;

    @Override
    public void onPageScrollStateChanged(int arg0) {
        switch (arg0) {
            case 1:// 手势滑动，空闲中
                isAutoPlay = false;
                break;
            case 2:// 界面切换中
                isAutoPlay = true;
                break;
            case 0:// 滑动结束，即切换完毕或者加载完毕
                // 当前为最后一张，此时从右向左滑，则切换到第一张
                if (mViewPager.getCurrentItem() == mViewPager.getAdapter().getCount() - 1 && !isAutoPlay) {
                    mViewPager.setCurrentItem(0);
                }
                // 当前为第一张，此时从左向右滑，则切换到最后一张
                else if (mViewPager.getCurrentItem() == 0 && !isAutoPlay) {
                    mViewPager.setCurrentItem(mViewPager.getAdapter().getCount() - 1);
                }
                break;
        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        initBannerDot();
    }

    public void initBannerDot(){
        int arg0 = mViewPager.getCurrentItem();
        if(mImageViews.length <= 1){
            return;
        }
        mImageViews[arg0].setImageResource(R.drawable.dingweifuchang);
        for (int i = 0; i < mImageViews.length; i++) {
            if (arg0 != i) {
                mImageViews[i].setImageResource(R.drawable.dingweifuyuan);
            }
        }
    }


    public class NoticeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 输出日志信息
            if (MyReceiver.NEW_MSG.equals(intent.getAction())) {
                int count = intent.getIntExtra("count", 0);
                if(messageDot!=null){
                    if(count > 0){
                        messageDot.setVisibility(View.VISIBLE);
                    }else{
                        messageDot.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

}
