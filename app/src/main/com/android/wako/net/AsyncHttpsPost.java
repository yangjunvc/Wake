package com.android.wako.net;

import com.android.wako.common.Constants;
import com.android.wako.net.exception.RequestException;
import com.android.wako.net.util.RequestParameter;
import com.android.wako.util.LogUtil;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class AsyncHttpsPost extends AsyncHttpPost {

    private  boolean isPayEntity = true;

    private void init(){
        this.httpClient = getNewHttpClient();
    }

    private static class CinyiSSLSocketFactory extends SSLSocketFactory {

        SSLContext sslContext = SSLContext.getInstance("TLS");

        public CinyiSSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
                }
                @Override
                public void checkServerTrusted(X509Certificate[] chain,	String authType) throws java.security.cert.CertificateException {
                }
            };
            sslContext.init(null, new TrustManager[] { tm }, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host,	port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }

    private DefaultHttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            SSLSocketFactory sf = new CinyiSSLSocketFactory(trustStore);
            HttpParams params = new BasicHttpParams();
            SchemeRegistry registry = new SchemeRegistry();

            trustStore.load(null, null);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    private String toXml(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version='1.0' encoding='UTF-8' standalone='yes' ?><xml>");
        for (int i = 0; i < params.size(); i++) {
            sb.append("<"+params.get(i).getName()+">");
            sb.append(params.get(i).getValue());
            sb.append("</"+params.get(i).getName()+">");
        }
        sb.append("</xml>");

        try {
            return new String(sb.toString().getBytes(), "ISO8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "";
    }

    //----------------------------------------------------------

    public AsyncHttpsPost(ThreadCallBack callBack, String url, List<RequestParameter> parameter, boolean isShowLoadingDialog, String loadingCode, boolean isHideCloseBtn, int requestCode) {
        super(callBack, url, parameter, isShowLoadingDialog, loadingCode, isHideCloseBtn, requestCode);
        init();
    }

    public AsyncHttpsPost(ThreadCallBack callBack, String url, List<RequestParameter> parameter, boolean isShowLoadingDialog, String loadingCode, boolean isHideCloseBtn) {
        super(callBack, url, parameter, isShowLoadingDialog, loadingCode, isHideCloseBtn);
        init();
    }

    public AsyncHttpsPost(ThreadCallBack callBack, String url, List<RequestParameter> parameter, boolean isShowLoadingDialog, int connectTimeout, int readTimeout) {
        super(callBack, url, parameter, isShowLoadingDialog, connectTimeout, readTimeout);
        init();
    }

    public AsyncHttpsPost(ThreadCallBack callBack, String url, List<RequestParameter> parameter,
                          boolean isShowLoadingDialog, String loadingDialogContent,
                          boolean isHideCloseBtn, int connectTimeout, int readTimeout) {
        super(callBack, url, parameter, isShowLoadingDialog, loadingDialogContent, isHideCloseBtn, connectTimeout, readTimeout);
        init();
    }

    /**
     *
     * @param isPayEntity default false
     */
    public AsyncHttpsPost(ThreadCallBack callBack, String url, List<RequestParameter> parameter,
                          boolean isShowLoadingDialog, String loadingCode,
                          boolean isHideCloseBtn, int requestCode,boolean isPayEntity) {
        super(callBack, url, parameter, isShowLoadingDialog, loadingCode, isHideCloseBtn, requestCode);
        this.isPayEntity = isPayEntity;
        init();
    }


    @Override
    public boolean process() {
        try {
            request = new HttpPost(url);
            if (Constants.isGzip) {
                request.addHeader("Accept-Encoding", "gzip");
            } else {
                request.addHeader("Accept-Encoding", "default");
            }

            if (isPayEntity) {
                request.setHeader("Accept", "application/json");
                request.setHeader("Content-type", "application/json");
            } else{
                request.setHeader("Content-Type",
                        "application/x-www-form-urlencoded; charset=utf-8");
            }



            if (parameter != null && parameter.size() > 0) {
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                for (RequestParameter p : parameter) {
                    list.add(new BasicNameValuePair(p.getName(), p.getValue()));
                }
                if (isPayEntity) {

                    ((HttpPost) request).setEntity(new StringEntity(toXml(list)));

                } else {

                    ((HttpPost) request).setEntity(new UrlEncodedFormEntity(list,
                            HTTP.UTF_8));

                }
            }

            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectTimeout);
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, readTimeout);
            HttpResponse response = httpClient.execute(request);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                InputStream is = response.getEntity().getContent();
                BufferedInputStream bis = new BufferedInputStream(is);
                bis.mark(2);

                byte[] header = new byte[2];
                int result = bis.read(header);
                bis.reset();
                int headerData = getShort(header);

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
                RequestException exception = new RequestException(RequestException.IO_EXCEPTION, "响应码异常,响应码：" + statusCode);
            }
            LogUtil.d(AsyncHttpPost.class.getName(), "AsyncHttpPost  request to url :" + url + "  finished !");

        }catch(IllegalArgumentException e){
            mRetStatus = ResStatus.Error_IllegalArgument;
            LogUtil.d(AsyncHttpGet.class.getName(), "AsyncHttpPost  request to url :" + url + "  onFail  " + e.getMessage());
        }  catch (org.apache.http.conn.ConnectTimeoutException e) {
            mRetStatus = ResStatus.Error_Connect_Timeout;
            LogUtil.d(AsyncHttpPost.class.getName(), "AsyncHttpPost  request to url :" + url + "  onFail  " + e.getMessage());
        } catch (java.net.SocketTimeoutException e) {
            mRetStatus = ResStatus.Error_Socket_Timeout;
            LogUtil.d(AsyncHttpPost.class.getName(), "AsyncHttpPost  request to url :" + url + "  onFail  " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            mRetStatus = ResStatus.Error_Unsupport_Encoding;
            e.printStackTrace();
            LogUtil.d(AsyncHttpPost.class.getName(), "AsyncHttpPost  request to url :" + url + "  UnsupportedEncodingException  " + e.getMessage());
        } catch (org.apache.http.conn.HttpHostConnectException e) {
            mRetStatus = ResStatus.Error_HttpHostConnect;
            e.printStackTrace();
            LogUtil.d(AsyncHttpPost.class.getName(), "AsyncHttpPost  request to url :" + url + "  HttpHostConnectException  " + e.getMessage());
        } catch (ClientProtocolException e) {
            mRetStatus = ResStatus.Error_Client_Protocol;
            e.printStackTrace();
            LogUtil.d(AsyncHttpPost.class.getName(), "AsyncHttpPost  request to url :" + url + "  ClientProtocolException " + e.getMessage());
        } catch (IOException e) {
            mRetStatus = ResStatus.Error_IOException;
            e.printStackTrace();
            LogUtil.d(AsyncHttpPost.class.getName(), "AsyncHttpPost  request to url :" + url + "  IOException  " + e.getMessage());
        } catch (Exception e){
            mRetStatus = ResStatus.Error_IOException;
            e.printStackTrace();
        }
        return false;
    }
}
