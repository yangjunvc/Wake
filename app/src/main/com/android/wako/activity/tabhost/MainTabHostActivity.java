package com.android.wako.activity.tabhost;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.android.wako.MyReceiver;
import com.android.wako.R;
import com.android.wako.UtilManager;
import com.android.wako.activity.main.MainActivity;
import com.android.wako.activity.meng.MengActivity;
import com.android.wako.activity.my.MyActivity;
import com.android.wako.util.LogUtil;
import com.android.wako.util.PreferencesUtil;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;

/**
 * maint_abhost
 * Created by duanmulirui
 */
public class MainTabHostActivity extends TabActivity implements View.OnClickListener {
    private static final String TAB_MAIN = "main";
    private static final String TAB_CART = "cart";
    private static final String TAB_MY = "my";
    private static final int TAB_SIZE = 3;

    private View view[] = new View[TAB_SIZE];
    private int[] view_id = {R.id.tab_main_view,R.id.tab_meng_view,R.id.tab_my_view};
    private ImageView iv[] = new ImageView[TAB_SIZE];
    private int[] iv_id = {R.id.tab_main_iv,R.id.tab_meng_iv,R.id.tab_my_iv};
    private TextView text[] = new TextView[TAB_SIZE];
    private int[] text_id = {R.id.tab_main_text,R.id.tab_meng_text,R.id.tab_my_text};
    private int iv_drawable_id[] = { R.drawable.shouyedaohang2,R.drawable.zhumengdaohang2, R.drawable.wo2};
    private int selector_id[] = { R.drawable.shouyedaohang, R.drawable.zhumengdaohang,R.drawable.wo};

    private TabHost mTabHost;

    private ImageView mainDot;
    NoticeReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabhost_main);
        initViews();
        initTab();

        Set<String> tags = new HashSet<String>();
        tags.add("男");
        JPushInterface.setAliasAndTags(this, PreferencesUtil.getWakoString(this,PreferencesUtil.Pre_Name,PreferencesUtil.Key_Uid,""), tags);
        JPushInterface.resumePush(this);

        receiver = new NoticeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyReceiver.NEW_MSG);
        // 动态注册BroadcastReceiver
        registerReceiver(receiver, filter);


        UtilManager.getInstance().requestMessageCount();
    }

    private void initTab() {
        mTabHost = getTabHost();
        mTabHost.addTab(mTabHost.newTabSpec(TAB_MAIN).setIndicator(TAB_MAIN)
                .setContent(new Intent(this, MainActivity.class)));
        mTabHost.addTab(mTabHost.newTabSpec(TAB_CART).setIndicator(TAB_CART)
                .setContent(new Intent(this, MengActivity.class)));
        mTabHost.addTab(mTabHost.newTabSpec(TAB_MY).setIndicator(TAB_MY)
                .setContent(new Intent(this, MyActivity.class)));
    }

    private void initViews() {
        for (int i = 0; i < TAB_SIZE; i++) {
            view[i] = findViewById(view_id[i]);
            view[i].setOnClickListener(this);
            iv[i] = (ImageView) findViewById(iv_id[i]);
            text[i] = (TextView) findViewById(text_id[i]);
        }

        mainDot = (ImageView) findViewById(R.id.main_iv_dot);
        mainDot.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_main_view:
                switchTabTheme(1);
                mTabHost.setCurrentTabByTag(TAB_MAIN);
                break;
            case R.id.tab_meng_view:
                switchTabTheme(2);
                mTabHost.setCurrentTabByTag(TAB_CART);
                break;
            case R.id.tab_my_view:
                switchTabTheme(3);
                mTabHost.setCurrentTabByTag(TAB_MY);
                break;

            default:
                break;
        }
    }

    private void switchTabTheme(int i) {
        for (int j = 0; j < TAB_SIZE; j++) {
            if (j + 1 == i) {
                iv[j].setImageResource(selector_id[j]);
                text[j].setTextColor(getResources().getColor(R.color.text_FF9211_color));
                continue;
            }
            iv[j].setImageResource(iv_drawable_id[j]);
            text[j].setTextColor(getResources().getColor(R.color.text_4C4C4C_color));
        }
    }


    public class NoticeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 输出日志信息
            if (MyReceiver.NEW_MSG.equals(intent.getAction())) {
                int count = intent.getIntExtra("count", 0);
                if(mainDot!=null){
                    if(count > 0){
                        mainDot.setVisibility(View.VISIBLE);
                    }else{
                        mainDot.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

}
