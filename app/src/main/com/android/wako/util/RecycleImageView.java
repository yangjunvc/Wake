package com.android.wako.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.android.wako.net.AsyncImageLoader;

/**
 * 免出现异常
 *
 */
public class RecycleImageView extends ImageView {

    static final String TAG = "RecycleImageView";

    public RecycleImageView(Context context) {
        super(context);
    }

    public RecycleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecycleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Exception e) {
            LogUtil.e(TAG, "RecycleImageView  -> onDraw() Canvas: trying to use a recycled bitmap");
            if(String.class.isInstance(this.getTag())){
                Bitmap bt = AsyncImageLoader.getBitmapFormCacheAndFile((String)this.getTag(),true,this.getMeasuredWidth(),this.getMeasuredHeight());
                this.setImageBitmap(bt);
            }
        }
    }

}
