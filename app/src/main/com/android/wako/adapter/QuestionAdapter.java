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
import com.android.wako.model.QuestionModel;
import com.android.wako.net.AsyncImageLoader;
import com.android.wako.util.DateUtil;
import com.android.wako.util.StringUtil;

import java.util.ArrayList;

/**
 * Created by duanmulirui
 */
public class QuestionAdapter extends BaseAdapter{
    public ArrayList<QuestionModel> mList = new ArrayList<QuestionModel>();
    private Context ctx;

    public QuestionAdapter(Context context){
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
            convertView = LayoutInflater.from(ctx).inflate(R.layout.question_list_item,null);
            holder.mHeadImg = (ImageView) convertView.findViewById(R.id.item_headimg);
            holder.mName = (TextView) convertView.findViewById(R.id.item_name);
            holder.mStatus = (TextView) convertView.findViewById(R.id.item_status);
            holder.mQuestion = (TextView) convertView.findViewById(R.id.item_question);
            holder.mTime = (TextView) convertView.findViewById(R.id.item_time);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        QuestionModel model = mList.get(position);

        String headImg = model.headImg;
        if(!StringUtil.isEmpty(headImg)){
//            MyApplication.imageLoader.displayImage(Constants.DOWNLOAD_URL+headImg,holder.mHeadImg);
            AsyncImageLoader.AsyncSetImage(holder.mHeadImg, Constants.DOWNLOAD_URL + model.headImg,holder.mHeadImg.getMeasuredWidth(), holder.mHeadImg.getMeasuredHeight(),true,false);
        }else{
            holder.mHeadImg.setImageResource(R.drawable.touxiang);
        }
        holder.mName.setText(model.name);
        int status = model.answerStatus;
        if(status != 0){
            holder.mStatus.setText(R.string.question_status_1);
            holder.mStatus.setTextColor(ctx.getResources().getColor(R.color.text_FF9211_color));
        }else{
            holder.mStatus.setText(R.string.question_status_0);
            holder.mStatus.setTextColor(ctx.getResources().getColor(R.color.text_999999_color));
        }
        holder.mQuestion.setText(model.questionContent);
        holder.mTime.setText(DateUtil.getStringByLong(model.createDate,"yyyy-MM-dd HH:mm"));

        return convertView;
    }

    public static class ViewHolder{
        ImageView mHeadImg;
        TextView mName,mStatus,mQuestion,mTime;
    }

    public void clearData(){
        mList.clear();
    }
    public void setData(ArrayList<QuestionModel> list){
        mList = list;
    }
    public void addData(ArrayList<QuestionModel> list){
        for(int i=0;i<list.size();i++){
            mList.add(list.get(i));
        }
    }
}
