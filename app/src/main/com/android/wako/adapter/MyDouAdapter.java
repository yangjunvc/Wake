package com.android.wako.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.wako.R;
import com.android.wako.model.MyDouModel;
import com.android.wako.model.MyMengModel;
import com.android.wako.util.DateUtil;

import java.util.ArrayList;

/**
 * Created by duanmulirui
 */
public class MyDouAdapter extends BaseAdapter{
    public ArrayList<MyDouModel> mList = new ArrayList<MyDouModel>();
    private Context ctx;

    public MyDouAdapter(Context context){
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
            convertView = LayoutInflater.from(ctx).inflate(R.layout.my_dou_list_item,null);
            holder.paymentDes = (TextView) convertView.findViewById(R.id.item_paymentdes);
            holder.createDate = (TextView) convertView.findViewById(R.id.item_time);
            holder.loveValue = (TextView) convertView.findViewById(R.id.item_loveValue);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        MyDouModel model = mList.get(position);
        holder.paymentDes.setText(model.paymentDes);
        holder.createDate.setText(DateUtil.getStringByLong(model.createDate,"yyyy-MM-dd HH:mm:ss"));
        String loveValue = model.loveValue;
        if(loveValue.contains("-")){
            holder.loveValue.setTextColor(ctx.getResources().getColor(R.color.text_FF9211_color));
        }else{
            holder.loveValue.setTextColor(ctx.getResources().getColor(R.color.text_3291E9_color));
        }
        holder.loveValue.setText(loveValue);

        return convertView;
    }

    public static class ViewHolder{
        TextView paymentDes,createDate,loveValue;
    }

    public void clearData(){
        mList.clear();
    }
    public void setData(ArrayList<MyDouModel> list){
        mList = list;
    }
    public void addData(ArrayList<MyDouModel> list){
        for(int i=0;i<list.size();i++){
            mList.add(list.get(i));
        }
    }
}
