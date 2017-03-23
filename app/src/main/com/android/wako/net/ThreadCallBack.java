package com.android.wako.net;

import java.io.Serializable;

public interface ThreadCallBack extends Serializable {

	/**
	 * 请求回调
	 * @param resultJson 返回json
	 * @param requestCode 请求接口标识
	 * @param resStatus 返回成功或失败的标识
	 */
	public void onCallbackFromThread(String resultJson, int requestCode, int resStatus);
	
}
