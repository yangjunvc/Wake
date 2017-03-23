package com.android.wako.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

import com.android.wako.R;


/**
 * 自定义progressdialog
 * 
 * @author chenggang
 * 
 */
public class CustomProgressDialog extends Dialog {


    public CustomProgressDialog(Context context) {
        super(context , R.style.mask_dialog);
        init();
    }

    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    public void init(){
        this.setContentView(R.layout.progress_dialog);
        this.getWindow().getAttributes().gravity = Gravity.CENTER;
    }

    public CustomProgressDialog setMessage(String strMessage, boolean cancel) {
        this.setCancelable(cancel);//按返回键能否消失
        this.setCanceledOnTouchOutside(false);//点击其它区域不关闭
        TextView tvMsg = (TextView) this.findViewById(R.id.text);
        if (tvMsg != null) {
            tvMsg.setText(strMessage);
        }
        return this;
    }

    public CustomProgressDialog setMessage(int resid, boolean cancel) {
        this.setCancelable(cancel);
        this.setCanceledOnTouchOutside(false);
        TextView tvMsg = (TextView) this.findViewById(R.id.text);
        if (tvMsg != null) {
            tvMsg.setText(resid);
        }
        return this;
    }
}
