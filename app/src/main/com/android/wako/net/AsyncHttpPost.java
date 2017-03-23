/*
 * Copyright 2011 meiyitian
 * Blog  :http://www.cnblogs.com/meiyitian
 * Email :haoqqemail@qq.com
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
import com.android.wako.util.LogUtil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;

/**
 * 
 * 异步HTTPPOST请求
 * 
 * 线程的终止工作交给线程池，当activity停止的时候，设置回调函数为false ，就不会执行回调方法。
 * 
 *
 */
public class AsyncHttpPost extends BaseRequest {
    private static final long serialVersionUID = 2L;
    DefaultHttpClient httpClient;

    public AsyncHttpPost(ThreadCallBack callBack, String url,
                         List<RequestParameter> parameter, boolean isShowLoadingDialog,
                         String loadingCode, boolean isHideCloseBtn) {
        this.callBack = callBack;
        if (isShowLoadingDialog) {
        }
        this.url = url;
        this.parameter = parameter;
        if (httpClient == null)
            httpClient = new DefaultHttpClient();
    }

    public AsyncHttpPost(ThreadCallBack callBack, String url,
            List<RequestParameter> parameter, boolean isShowLoadingDialog,
            String loadingCode, boolean isHideCloseBtn, int requestCode) {
        this(callBack, url, parameter, isShowLoadingDialog, loadingCode,
                isHideCloseBtn);
        this.requestCode = requestCode;
    }

    public AsyncHttpPost(ThreadCallBack callBack, String url,
            List<RequestParameter> parameter, boolean isShowLoadingDialog,
            int connectTimeout, int readTimeout) {
        this(callBack, url, parameter, isShowLoadingDialog, "", false);
        if (connectTimeout > 0) {
            this.connectTimeout = connectTimeout;
        }
        if (readTimeout > 0) {
            this.readTimeout = readTimeout;
        }
    }

    public AsyncHttpPost(ThreadCallBack callBack, String url,
            List<RequestParameter> parameter, boolean isShowLoadingDialog,
            String loadingDialogContent, boolean isHideCloseBtn,
            int connectTimeout, int readTimeout) {
        this(callBack, url, parameter, isShowLoadingDialog,
                loadingDialogContent, isHideCloseBtn);
        if (connectTimeout > 0) {
            this.connectTimeout = connectTimeout;
        }
        if (readTimeout > 0) {
            this.readTimeout = readTimeout;
        }
    }

    @Override
    public void run() {
        super.run();
	}

    public boolean process() {
        try {
//            LogUtil.d(Tag, "---process---url="+url+"?"+getParames());
            request = new HttpPost(url);
            if (Constants.isGzip) {
                request.addHeader("Accept-Encoding", "gzip");
            } else {
                request.addHeader("Accept-Encoding", "default");
            }
            request.setHeader("Content-Type",
                    "application/x-www-form-urlencoded; charset=utf-8");
//            LogUtil.d(AsyncHttpPost.class.getName(),
//                    "AsyncHttpPost  request to url :" + url);

            if (parameter != null && parameter.size() > 0) {
                List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
                for (RequestParameter p : parameter) {
                    list.add(new BasicNameValuePair(p.getName(), p.getValue()));
                }

                ((HttpPost) request).setEntity(new UrlEncodedFormEntity(list,
                        HTTP.UTF_8));
            }
            httpClient.getParams().setParameter(
                    CoreConnectionPNames.CONNECTION_TIMEOUT, connectTimeout);
            httpClient.getParams().setParameter(
                    CoreConnectionPNames.SO_TIMEOUT, readTimeout);
            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
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
                    LogUtil.d("HttpTask", " use GZIPInputStream  ");
                    is = new GZIPInputStream(bis);
                } else {
                    LogUtil.d("HttpTask", " not use GZIPInputStream");
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
//                ret = ErrorUtil.errorJson("ERROR.HTTP.001",
//                        exception.getMessage());
            }

            LogUtil.d(AsyncHttpPost.class.getName(),
                    "AsyncHttpPost  request to url :" + url + "  finished !");
        }catch(IllegalArgumentException e){
            mRetStatus = ResStatus.Error_IllegalArgument;
//            RequestException exception = new RequestException(
//                    RequestException.IO_EXCEPTION, Constants.ERROR_MESSAGE);
//            ret = ErrorUtil.errorJson("ERROR.HTTP.002", exception.getMessage());
            LogUtil.d(AsyncHttpGet.class.getName(),
                    "AsyncHttpPost  request to url :" + url + "  onFail  "
                            + e.getMessage());
        }  catch (org.apache.http.conn.ConnectTimeoutException e) {
            mRetStatus = ResStatus.Error_Connect_Timeout;
//            RequestException exception = new RequestException(
//                    RequestException.SOCKET_TIMEOUT_EXCEPTION, Constants.ERROR_MESSAGE);
//            ret = ErrorUtil.errorJson("ERROR.HTTP.003", exception.getMessage());
            LogUtil.d(AsyncHttpPost.class.getName(),
                    "AsyncHttpPost  request to url :" + url + "  onFail  "
                            + e.getMessage());
        } catch (java.net.SocketTimeoutException e) {
            mRetStatus = ResStatus.Error_Socket_Timeout;
//            RequestException exception = new RequestException(
//                    RequestException.SOCKET_TIMEOUT_EXCEPTION, Constants.ERROR_MESSAGE);
//            ret = ErrorUtil.errorJson("ERROR.HTTP.004", exception.getMessage());
            LogUtil.d(AsyncHttpPost.class.getName(),
                    "AsyncHttpPost  request to url :" + url + "  onFail  "
                            + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            mRetStatus = ResStatus.Error_Unsupport_Encoding;
            e.printStackTrace();
//            RequestException exception = new RequestException(
//                    RequestException.UNSUPPORTED_ENCODEING_EXCEPTION, "编码错误");
//            ret = ErrorUtil.errorJson("ERROR.HTTP.005", exception.getMessage());
            LogUtil.d(AsyncHttpPost.class.getName(),
                    "AsyncHttpPost  request to url :" + url + "  UnsupportedEncodingException  "
                            + e.getMessage());
        } catch (org.apache.http.conn.HttpHostConnectException e) {
            mRetStatus = ResStatus.Error_HttpHostConnect;
            e.printStackTrace();
//            RequestException exception = new RequestException(
//                    RequestException.CONNECT_EXCEPTION, "连接错误");
//            ret = ErrorUtil.errorJson("ERROR.HTTP.006", exception.getMessage());
            LogUtil.d(AsyncHttpPost.class.getName(),
                    "AsyncHttpPost  request to url :" + url + "  HttpHostConnectException  "
                            + e.getMessage());
        } catch (ClientProtocolException e) {
            mRetStatus = ResStatus.Error_Client_Protocol;
            e.printStackTrace();
//            RequestException exception = new RequestException(
//                    RequestException.CLIENT_PROTOL_EXCEPTION, "客户端协议异常");
//            ret = ErrorUtil.errorJson("ERROR.HTTP.007", exception.getMessage());
            LogUtil.d(AsyncHttpPost.class.getName(),
                    "AsyncHttpPost  request to url :" + url + "  ClientProtocolException "
                            + e.getMessage());
        } catch (IOException e ) {
            mRetStatus = ResStatus.Error_IOException;
//            RequestException exception = new RequestException(
//                    RequestException.IO_EXCEPTION, "数据读取异常");
//            ret = ErrorUtil.errorJson("ERROR.HTTP.008", exception.getMessage());
            e.printStackTrace();
            LogUtil.d(AsyncHttpPost.class.getName(),
                    "AsyncHttpPost  request to url :" + url + "  IOException  "
                            + e.getMessage());
        } catch (Exception e){
            mRetStatus = ResStatus.Error_IOException;
            e.printStackTrace();
        }
        return false;
    }

	protected int getShort(byte[] data) {
        return (int)((data[0]<<8) | data[1]&0xFF);
    }
}
