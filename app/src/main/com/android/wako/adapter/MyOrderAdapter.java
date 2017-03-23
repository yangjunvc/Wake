package com.android.wako.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.wako.R;
import com.android.wako.model.MyMengModel;
import com.android.wako.model.MyOrderModel;
import com.android.wako.util.DateUtil;

import java.util.ArrayList;

/**
 * Created by duanmulirui
 */
public class MyOrderAdapter extends BaseAdapter{
    public ArrayList<MyOrderModel> mList = new ArrayList<MyOrderModel>();
    private Context ctx;

    public MyOrderAdapter(Context context){
        ctx = context;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList == null ? null : mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void remove(int position) {
        if (mList != null) {
            mList.remove(position);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(ctx).inflate(R.layout.my_order_list_item,null);
            holder.mTime = (TextView) convertView.findViewById(R.id.item_time);
            holder.mName = (TextView) convertView.findViewById(R.id.item_name);
            holder.mStatus = (TextView) convertView.findViewById(R.id.item_status);
            holder.mPrice = (TextView) convertView.findViewById(R.id.item_price);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        MyOrderModel model = mList.get(position);
        holder.mTime.setText(DateUtil.getStringByLong(model.createDate,"yyyy-MM-dd HH:mm"));
        holder.mName.setText(model.name);
        holder.mPrice.setText(ctx.getResources().getString(R.string.order_price,model.moneyFinal));
        if(model.payStatus != 1){
            holder.mStatus.setVisibility(View.GONE);
        }else{
            holder.mStatus.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    public static class ViewHolder{
        TextView mTime,mName,mStatus,mPrice;
    }

    public void clearData(){
        mList.clear();
    }
    public void setData(ArrayList<MyOrderModel> list){
        mList = list;
    }
    public void addData(ArrayList<MyOrderModel> list){
        for(int i=0;i<list.size();i++){
            mList.add(list.get(i));
        }
    }
}
