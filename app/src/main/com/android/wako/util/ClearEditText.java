package com.android.wako.util;

import com.android.wako.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

public class ClearEditText extends EditText implements OnFocusChangeListener, TextWatcher {
    private Drawable mClearDrawable;
    private boolean hasFoucs;
    private TextWatcher mWatcherOther;
    private OnFocusChangeListener mFocusChangeOther;

    public ClearEditText(Context context) {
        this(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mClearDrawable = getCompoundDrawables()[2];
        if (mClearDrawable == null) {
            // throw new
            // NullPointerException("You can add drawableRight attribute in XML");
            mClearDrawable = getResources().getDrawable(R.drawable.txt_clear);
        }
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth() + 12 , mClearDrawable.getIntrinsicHeight() +12 );
        setClearIconVisible(false);
        setOnFocusChangeListener(this);
        addTextChangedListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {
                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight()) && (event.getX() < ((getWidth() - getPaddingRight())));
                if (touchable) {
                    this.setText("");
                }
            }
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFoucs = hasFocus;
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
        if (mFocusChangeOther != null) {
            mFocusChangeOther.onFocusChange(v, hasFocus);
        }
    }

    public void setClearIconVisible(boolean visible) {
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (hasFoucs) {
            setClearIconVisible(s.length() > 0);
        }
        if (mWatcherOther != null) {
            mWatcherOther.onTextChanged(s, start, before, count);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (mWatcherOther != null) {
            mWatcherOther.beforeTextChanged(s, start, count, after);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mWatcherOther != null) {
            mWatcherOther.afterTextChanged(s);
        }
    }

    public void setShakeAnimation() {
        this.setAnimation(shakeAnimation(5));
    }

    public static Animation shakeAnimation(int counts) {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }

    public boolean isHasFoucs() {
        return hasFoucs;
    }

    public void setHasFoucs(boolean hasFoucs) {
        this.hasFoucs = hasFoucs;
    }

    /**
     * 添加另一个监听
     * @param watcher
     */
    public void addTextChangedListenerOther(TextWatcher watcher) {
        mWatcherOther = watcher;
    }

    /**
     * 添加另一个监听
     * @param focusChange
     */
    public void setOnFocusChangeListenerOther(OnFocusChangeListener focusChange) {
        mFocusChangeOther = focusChange;
    }

}
