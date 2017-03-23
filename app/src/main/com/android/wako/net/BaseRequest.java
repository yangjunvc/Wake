/*
 * Copyright 2011 爱知世元
 * Website:http://www.azsy.cn/
 * Email:info＠azsy.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.android.wako.net;

import android.os.Handler;
import android.os.Message;

import com.android.wako.common.Constants;
import com.android.wako.net.util.RequestParameter;
import com.android.wako.net.util.Utils;
import com.android.wako.util.LogUtil;

import org.apache.http.client.methods.HttpUriRequest;

import java.io.Serializable;
import java.util.List;

/**
 * 目标：
 * 1、安全有序
 * 2、高效
 * 3、易用、易控制
 * 4、activity停止后停止该activity所用的线程。
 * 5、监测内存，当内存溢出的时候自动垃圾回收，清理资源 ，当程序退出之后终止线程池
 *
 */
public abstract class BaseRequest implements   Runnable, Serializable {
    public static String Tag = "BaseRequest";
    HttpUriRequest request = null;
    List<RequestParameter> parameter = null;
    ThreadCallBack callBack;
    /**
     * 请求类型
     */
    int requestCode = -1;
    /**
     * 重试次数
     */
    public int times = 1;
    /**
     * 过期时间(毫秒)
     */
    public long expire = 0;
    /**
     * 返回的值
     */
    String ret;
    /**
     * 请求结果状态
     */
    int mRetStatus = ResStatus.Success;
    /**
     * 如果已从缓存中获取，是否还要从网络获取并返回
     */
    public boolean call=false;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
//	protected ParseHandler handler = null;
	protected String url = null;
	/**
	 * default is 5 ,to set .
	 */
	protected int connectTimeout = 10000;
	/**
	 * default is 5 ,to set .
	 */
	protected int readTimeout = 20000;
//	protected RequestResultCallback requestCallback = null;
	
	Handler resultHandler = new Handler() {
		public void handleMessage(Message msg) {
			String resultData = (String) msg.obj;
			ThreadCallBack callBack = (ThreadCallBack) msg.getData().getSerializable("callback");
			if (requestCode != -1) {
				callBack.onCallbackFromThread(resultData, requestCode, mRetStatus);
			}
		}
	};
	
    public String getParames(){
        StringBuilder bulider = new StringBuilder();
        if (parameter != null && parameter.size() > 0) {
            for (RequestParameter p : parameter) {
                if (bulider.length() != 0) {
                    bulider.append("&");
                }
                bulider.append(Utils.encode(p.getName()));
                bulider.append("=");
                bulider.append(Utils.encode(p.getValue()));
            }
        }
        return bulider.toString();
    }
	
	@Override
	public void run() {
        String urlStr = url +  "?" +getParames();
        LogUtil.d(Tag, "---run---urlStr="+urlStr+";expire="+expire);
        if (expire > 0) {
            ret = HttpCache.getInstance().getCacheJsonByUrl(urlStr, expire);
        }
        //如果缓存有有效数据，先向上返回，如果需要再重新获取，那获取后了再向上返；如果没有有效数据，重新获取，再向上返；
//        LogUtil.d(Tag, "------cache ret:"+ret);
        if (ret != null && ret.trim().length() > 0) {
            sendData();
        }
        if(ret==null || ret.trim().length()<=0){
            getData(urlStr);
            sendData();
        }else{
            if (call) {
                getData(urlStr);
                sendData();
            }
        }
        DefaultThreadPool.getInstance().remove(this);
	}
	
	public void getData(String url){
	    int count=0;
	    while (count < times) {
            if (process()) {
                break;
            }
            if (Constants.IS_STOP_REQUEST) {
                break;
            }
            count++;
        }
        if (expire > 0 && mRetStatus == ResStatus.Success) {
            HttpCache.getInstance().addCacheJson(url, ret);
        }
	}
	
	public void sendData(){
	    if (!Constants.IS_STOP_REQUEST) {
            Message msg = new Message();
            msg.obj = ret;
            msg.getData().putSerializable("callback", callBack);
            resultHandler.sendMessage(msg);
        }
	}
	
	abstract boolean process();
	
	protected void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
	
	protected void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}
	
	public HttpUriRequest getRequest() {
		return request;
	}

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public int getmRetStatus() {
        return mRetStatus;
    }

    public void setmRetStatus(int mRetStatus) {
        this.mRetStatus = mRetStatus;
    }
	
	
}
