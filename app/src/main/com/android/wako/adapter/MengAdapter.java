package com.android.wako.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.wako.MyApplication;
import com.android.wako.R;
import com.android.wako.common.Constants;
import com.android.wako.model.MengModel;
import com.android.wako.model.MessageModel;
import com.android.wako.net.AsyncImageLoader;
import com.android.wako.util.DateUtil;
import com.android.wako.util.StringUtil;

import java.util.ArrayList;

/**
 * Created by duanmulirui
 */
public class MengAdapter extends BaseAdapter{
    public ArrayList<MengModel> mList = new ArrayList<MengModel>();
    private Context ctx;

    public MengAdapter(Context context){
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
            convertView = LayoutInflater.from(ctx).inflate(R.layout.meng_list_item,null);
            holder.headImg = (ImageView) convertView.findViewById(R.id.item_headimg);
            holder.name = (TextView) convertView.findViewById(R.id.item_name);
            holder.university = (TextView) convertView.findViewById(R.id.item_university);
            holder.achievement = (TextView) convertView.findViewById(R.id.item_achievement);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        MengModel model = mList.get(position);

        String headImg = model.headImg;
        if(!StringUtil.isEmpty(headImg)){
//            MyApplication.imageLoader.displayImage(Constants.DOWNLOAD_URL + model.headImg,holder.headImg);
            AsyncImageLoader.AsyncSetImage(holder.headImg, Constants.DOWNLOAD_URL + model.headImg,holder.headImg.getMeasuredWidth(), holder.headImg.getMeasuredHeight(),true,false);
        }else{
            holder.headImg.setImageResource(R.drawable.meng_bg);
        }
        holder.name.setText(model.name);
        holder.university.setText(model.university);
        holder.achievement.setText(model.achievement);

        return convertView;
    }

    public static class ViewHolder{
        ImageView headImg;
        TextView name,university,achievement;
    }

    public void clearData(){
        mList.clear();
    }
    public void setData(ArrayList<MengModel> list){
        mList = list;
    }
    public void addData(ArrayList<MengModel> list){
        for(int i=0;i<list.size();i++){
            mList.add(list.get(i));
        }
    }

}
