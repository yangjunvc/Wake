package com.android.wako.activity.my;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.wako.CommonTitleActivity;
import com.android.wako.R;
import com.android.wako.model.MyOrderModel;
import com.android.wako.util.DateUtil;

/**
 * 我的订单detail
 * Created by duanmulirui
 */
public class MyOrderDetailActivity extends CommonTitleActivity{

    private TextView mName,mOrderPrice,mDeduction,mTotal,mOrderNum,mCreateTime,
            mPayWay,mStatementNum,mPayTime;

    private MyOrderModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_order_detail_main);
        initViews();

        model = new MyOrderModel();
        model = (MyOrderModel) getIntent().getSerializableExtra("model");
        if(model != null){
            mName.setText(model.name);
            mOrderPrice.setText(model.moneyTotal+"");
            mDeduction.setText(model.moneyDeduction+"");
            mTotal.setText("¥"+model.moneyFinal);
            mOrderNum.setText(model.orderNum);
            mCreateTime.setText(DateUtil.getStringByLong(model.createDate,"yyyy.MM.dd HH:mm:ss"));
            if(model.payType != 0){
                mPayWay.setText(R.string.alipay_pay);
            }else{
                mPayWay.setText(R.string.weixin_pay);
            }
            mStatementNum.setText(model.statementNum);
            mPayTime.setText(DateUtil.getStringByLong(model.payTime,"yyyy.MM.dd HH:mm:ss"));
        }
    }

    private void initViews() {
        loadTitleViews();
        setLeftVisibily(View.VISIBLE);
        setTitle(R.string.my_order_detail);

        mName = (TextView) findViewById(R.id.detail_name);
        mOrderPrice = (TextView) findViewById(R.id.detail_orderprice);
        mDeduction = (TextView) findViewById(R.id.detail_deduction);
        mTotal = (TextView) findViewById(R.id.detail_total);
        mOrderNum = (TextView) findViewById(R.id.detail_ordernum);
        mCreateTime = (TextView) findViewById(R.id.detail_createtime);
        mPayWay = (TextView) findViewById(R.id.detail_payway);
        mStatementNum = (TextView) findViewById(R.id.detail_statementnum);
        mPayTime = (TextView) findViewById(R.id.detail_paytime);
    }
}
