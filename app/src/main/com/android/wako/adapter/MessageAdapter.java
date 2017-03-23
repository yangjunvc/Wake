package com.android.wako.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.wako.R;
import com.android.wako.model.MessageModel;
import com.android.wako.model.MyDouModel;
import com.android.wako.util.DateUtil;

import java.util.ArrayList;

/**
 * Created by duanmulirui
 */
public class MessageAdapter extends BaseAdapter{
    public ArrayList<MessageModel> mList = new ArrayList<MessageModel>();
    private Context ctx;

    public MessageAdapter(Context context){
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
            convertView = LayoutInflater.from(ctx).inflate(R.layout.message_list_item,null);
            holder.type = (TextView) convertView.findViewById(R.id.item_type);
            holder.time = (TextView) convertView.findViewById(R.id.item_time);
            holder.content = (TextView) convertView.findViewById(R.id.item_content);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        MessageModel model = mList.get(position);
        holder.type.setText("系统消息");
        holder.content.setText(model.content);
        holder.time.setText(DateUtil.getStringByLong(model.createDate,"yyyy-MM-dd HH:mm"));

        return convertView;
    }

    public static class ViewHolder{
        TextView type,time,content;
    }

    public void clearData(){
        mList.clear();
    }
    public void setData(ArrayList<MessageModel> list){
        mList = list;
    }
    public void addData(ArrayList<MessageModel> list){
        for(int i=0;i<list.size();i++){
            mList.add(list.get(i));
        }
    }
}
