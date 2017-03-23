package com.android.wako.wxapi;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.android.wako.R;
import com.android.wako.common.Constants;
import com.android.wako.util.LogUtil;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    static final String TAG = "WXPayEntryActivity";
    private IWXAPI api;
    private static List<WXPayEntryListener> listeners = new LinkedList<WXPayEntryListener>();

    public static void registerListener(WXPayEntryListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public static void unregisterListener(WXPayEntryListener listener) {
        if (listener != null && listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);

        api = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

	@Override
	public void onResp(BaseResp resp) {
		String res = null;
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
		    LogUtil.d(TAG, "-----------resp.errCode="+resp.errCode);
			switch (resp.errCode){
				case 0:
                    if (listeners.size() > 0) {
                        for (int i = listeners.size(); i >= 0; i--) {
                            if (listeners.size() > i) {
                                listeners.get(i).onFinish();
                            }
                        }
                    }
					break;
				case -1:
					res = getString(R.string.pay_error);
					break;
				case -2:
					res = getString(R.string.pay_cancel);
					break;
				default:
					res = "";
					break;
			}

			if(res != null) {
				Toast.makeText(this, res, Toast.LENGTH_LONG).show();
			}
			finish();
		}
	}
}