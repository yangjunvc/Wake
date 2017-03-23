
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

import com.android.wako.common.Constants;
import com.android.wako.net.exception.RequestException;
import com.android.wako.net.util.RequestParameter;
import com.android.wako.net.util.Utils;
import com.android.wako.util.LogUtil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

public class AsyncHttpGet extends BaseRequest{
	private static final String TAG = "AsyncHttpGet";
	
	private static final long serialVersionUID = 2L;
	DefaultHttpClient httpClient;
	public AsyncHttpGet(ThreadCallBack callBack, String url, List<RequestParameter> parameter, boolean isShowLoadingDialog, String loadingCode, boolean isHideCloseBtn){
		this.callBack = callBack;
		if(isShowLoadingDialog){
			
		}
		this.url = url;
		this.parameter = parameter;
		if(httpClient == null)
			httpClient = new DefaultHttpClient();
	}
	
	public AsyncHttpGet(ThreadCallBack callBack,String url,List<RequestParameter> parameter,boolean isShowLoadingDialog,String loadingCode,boolean isHideCloseBtn, int requestCode){
		this(callBack, url, parameter, isShowLoadingDialog, loadingCode, isHideCloseBtn);
		this.requestCode = requestCode;
	}
	
	public AsyncHttpGet(ThreadCallBack callBack, String url,List<RequestParameter> parameter,boolean isShowLoadingDialog,int connectTimeout,int readTimeout) {
		this(callBack,url,parameter,isShowLoadingDialog,"",false);
		if(connectTimeout>0){
			this.connectTimeout = connectTimeout;
		}
		if(readTimeout>0){
			this.readTimeout = readTimeout;
		}
	}
	public AsyncHttpGet(ThreadCallBack callBack, String url,List<RequestParameter> parameter,boolean isShowLoadingDialog,String loadingDialogContent,boolean isHideCloseBtn,int connectTimeout,int readTimeout) {
		this(callBack,url,parameter,isShowLoadingDialog,loadingDialogContent,isHideCloseBtn);
		if(connectTimeout>0){
			this.connectTimeout = connectTimeout;
		}
		if(readTimeout>0){
			this.readTimeout = readTimeout;
		}
	}

    @Override
    public void run() {
        super.run();
    }

    @Override
    boolean process() {
        try {
            if (parameter != null && parameter.size() > 0) {
                StringBuilder bulider = new StringBuilder();
                for (RequestParameter p : parameter) {
                    if (bulider.length() != 0) {
                        bulider.append("&");
                    }

                    bulider.append(Utils.encode(p.getName()));
                    bulider.append("=");
                    bulider.append(Utils.encode(p.getValue()));
                }
                url += "?" + bulider.toString();
            }
            if (LogUtil.IS_LOG)
                LogUtil.d(TAG, "AsyncHttpGet  request to url :" + url);
            request = new HttpGet(url);
            /*
             * if(Constants.isGzip){ request.addHeader("Accept-Encoding",
             * "gzip"); }else{ request.addHeader("Accept-Encoding", "default");
             * }
             */
            // 请求超时
            httpClient.getParams().setParameter(
                    CoreConnectionPNames.CONNECTION_TIMEOUT, connectTimeout);
            // 读取超时
            httpClient.getParams().setParameter(
                    CoreConnectionPNames.SO_TIMEOUT, readTimeout);
            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (LogUtil.IS_LOG)
                LogUtil.d(TAG, "statusCode=" + statusCode);
            if (statusCode == HttpStatus.SC_OK) {
                InputStream is = response.getEntity().getContent();
                BufferedInputStream bis = new BufferedInputStream(is);
                bis.mark(2);
                // 取前两个字节
                byte[] header = new byte[2];
                int result = bis.read(header);
                // reset输入流到开始位置
                bis.reset();
                // 判断是否是GZIP格式
                int headerData = getShort(header);
                // Gzip 流 的前两个字节是 0x1f8b
                if (result != -1 && headerData == 0x1f8b) {
                    if (LogUtil.IS_LOG)
                        LogUtil.d(TAG, " use GZIPInputStream  ");
                    is = new GZIPInputStream(bis);
                } else {
                    if (LogUtil.IS_LOG)
                        LogUtil.d(TAG, " not use GZIPInputStream");
                    is = bis;
                }
                InputStreamReader reader = new InputStreamReader(is, "utf-8");
                char[] data = new char[100];
                int readSize;
                StringBuffer sb = new StringBuffer();
                while ((readSize = reader.read(data)) > 0) {
                    sb.append(data, 0, readSize);
                }
                ret = sb.toString();
                bis.close();
                reader.close();
                return true;

            } else {
                mRetStatus = ResStatus.Error_Code;
                RequestException exception = new RequestException(
                        RequestException.IO_EXCEPTION, "响应码异常,响应码："
                                + statusCode);
            }

            if (LogUtil.IS_LOG)
                LogUtil.d(TAG, "AsyncHttpGet  request to url :" + url
                        + "  finished !");

        } catch (IllegalArgumentException e) {
            mRetStatus = ResStatus.Error_IllegalArgument;
            e.printStackTrace();
            RequestException exception = new RequestException(
                    RequestException.IO_EXCEPTION, Constants.ERROR_MESSAGE);
            if (LogUtil.IS_LOG)
                LogUtil.d(TAG, "AsyncHttpGet  request to url :" + url
                        + "  onFail  " + e.getMessage());
        } catch (org.apache.http.conn.ConnectTimeoutException e) {
            mRetStatus = ResStatus.Error_Connect_Timeout;
            e.printStackTrace();
            RequestException exception = new RequestException(
                    RequestException.SOCKET_TIMEOUT_EXCEPTION,
                    Constants.ERROR_MESSAGE);
            if (LogUtil.IS_LOG)
                LogUtil.d(TAG, "AsyncHttpGet  request to url :" + url
                        + "  onFail  " + e.getMessage());
        } catch (java.net.SocketTimeoutException e) {
            mRetStatus = ResStatus.Error_Socket_Timeout;
            e.printStackTrace();
            RequestException exception = new RequestException(
                    RequestException.SOCKET_TIMEOUT_EXCEPTION,
                    Constants.ERROR_MESSAGE);
            if (LogUtil.IS_LOG)
                LogUtil.d(TAG, "AsyncHttpGet  request to url :" + url
                        + "  onFail  " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            mRetStatus = ResStatus.Error_Unsupport_Encoding;
            e.printStackTrace();
            RequestException exception = new RequestException(
                    RequestException.UNSUPPORTED_ENCODEING_EXCEPTION, "编码错误");
            if (LogUtil.IS_LOG)
                LogUtil.d(TAG, "AsyncHttpGet  request to url :" + url
                        + "  UnsupportedEncodingException  " + e.getMessage());
        } catch (org.apache.http.conn.HttpHostConnectException e) {
            mRetStatus = ResStatus.Error_HttpHostConnect;
            e.printStackTrace();
            RequestException exception = new RequestException(
                    RequestException.CONNECT_EXCEPTION, "连接错误");
            if (LogUtil.IS_LOG)
                LogUtil.d(TAG, "AsyncHttpGet  request to url :" + url
                        + "  HttpHostConnectException  " + e.getMessage());
        } catch (ClientProtocolException e) {
            mRetStatus = ResStatus.Error_Client_Protocol;
            e.printStackTrace();
            RequestException exception = new RequestException(
                    RequestException.CLIENT_PROTOL_EXCEPTION, "客户端协议异常");
            e.printStackTrace();
            if (LogUtil.IS_LOG)
                LogUtil.d(TAG, "AsyncHttpGet  request to url :" + url
                        + "  ClientProtocolException " + e.getMessage());
        } catch (IOException e) {
            mRetStatus = ResStatus.Error_IOException;
            e.printStackTrace();
            RequestException exception = new RequestException(
                    RequestException.IO_EXCEPTION, "数据读取异常");
            e.printStackTrace();
            if (LogUtil.IS_LOG)
                LogUtil.d(TAG, "AsyncHttpGet  request to url :" + url
                        + "  IOException  " + e.getMessage());
        } catch (Exception e){
            mRetStatus = ResStatus.Error_IOException;
            e.printStackTrace();
        }
        return false;
    }
	
	private int getShort(byte[] data) {
        return (int)((data[0]<<8) | data[1]&0xFF);
    }
}
