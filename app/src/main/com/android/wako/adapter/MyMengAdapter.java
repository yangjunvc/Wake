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
import com.android.wako.model.MyMengModel;
import com.android.wako.model.QuestionModel;
import com.android.wako.net.AsyncImageLoader;
import com.android.wako.util.DateUtil;
import com.android.wako.util.StringUtil;

import java.util.ArrayList;

/**
 * Created by duanmulirui
 */
public class MyMengAdapter extends BaseAdapter{
    public ArrayList<MyMengModel> mList = new ArrayList<MyMengModel>();
    private Context ctx;

    public MyMengAdapter(Context context){
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
            convertView = LayoutInflater.from(ctx).inflate(R.layout.my_meng_list_item,null);
            holder.mHeadImg = (ImageView) convertView.findViewById(R.id.item_headimg);
            holder.mName = (TextView) convertView.findViewById(R.id.item_name);
            holder.mUniversity = (TextView) convertView.findViewById(R.id.item_university);
            holder.mAchievement = (TextView) convertView.findViewById(R.id.item_achievement);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        MyMengModel model = mList.get(position);

        String headImg = model.headImg;
        if(!StringUtil.isEmpty(headImg)){
//            MyApplication.imageLoader.displayImage(Constants.DOWNLOAD_URL+headImg,holder.mHeadImg);
            AsyncImageLoader.AsyncSetImage(holder.mHeadImg, Constants.DOWNLOAD_URL + model.headImg,holder.mHeadImg.getMeasuredWidth(), holder.mHeadImg.getMeasuredHeight(),true,false);
        }else{
            holder.mHeadImg.setImageResource(R.drawable.touxiang);
        }
        holder.mName.setText(model.name);
        holder.mUniversity.setText(model.university);
        holder.mAchievement.setText(model.achievement);

        return convertView;
    }

    public static class ViewHolder{
        ImageView mHeadImg;
        TextView mName,mUniversity,mAchievement;
    }

    public void clearData(){
        mList.clear();
    }
    public void setData(ArrayList<MyMengModel> list){
        mList = list;
    }
    public void addData(ArrayList<MyMengModel> list){
        for(int i=0;i<list.size();i++){
            mList.add(list.get(i));
        }
    }
}
