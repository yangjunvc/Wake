package com.android.wako.net;

import android.graphics.Bitmap;
import android.util.Log;

import com.android.wako.common.Constants;
import com.android.wako.common.FilePathManager;
import com.android.wako.json.UploadJson;
import com.android.wako.util.BitmapUtil;
import com.android.wako.util.FileUtil;
import com.android.wako.util.LogUtil;
import com.android.wako.util.StringUtil;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.UUID;

/**
 * 文件上传
 *
 */
public class UploadTask extends FileBaseTask {

    private static final String BOUNDARY = UUID.randomUUID().toString(); // 边界标识随机生成
    private static final String PREFIX = "--";
    private static final String LINE_END = "\r\n";
    private static final String CONTENT_TYPE = "multipart/form-data"; // 内容类型

    private static final String TAG = "UploadUtil";

    private static final String CHARSET = "utf-8"; // 设置编码

    public UploadTask() {
    }

    @Override
    public void run() {
        super.run();
        LogUtil.d(TAG, " run ");
        if(StringUtil.isEmpty(mMarkStr)){
            mMarkStr = mFilePath;
        }
        long total = 0, count = 0;
        InputStream is = null;
        DataOutputStream dos = null;
        
        try {
            URL url = new URL(mUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(mReadTimeOut);
            conn.setConnectTimeout(mConnectTimeout);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", CHARSET); // 设置编码
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

            /**
             * 当文件不为空，把文件包装并且上传
             */
            dos = new DataOutputStream(conn.getOutputStream());
            StringBuffer sb = null;
            String params = "";

            /***
             * 以下是用于上传参数
             */
            if (mParam != null && mParam.size() > 0) {
                Iterator<String> it = mParam.keySet().iterator();
                while (it.hasNext()) {
                    sb = null;
                    sb = new StringBuffer();
                    String key = it.next();
                    String value = mParam.get(key);
                    sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
                    sb.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append(LINE_END).append(LINE_END);
                    sb.append(value).append(LINE_END);
                    params = sb.toString();
                    LogUtil.d(TAG, key + "=" + params + "##");
                    dos.write(params.getBytes());
                    // dos.flush();
                }
            }
            LogUtil.d(TAG, " --------param--- ");

            sb = null;
            params = null;
            sb = new StringBuffer();
            /**
             * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
             * filename是文件的名字，包含后缀名的 比如:abc.png
             */
            sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
            sb.append("Content-Disposition:form-data; name=\"" + "file" + "\"; filename=\"" + "file" + "\"" + LINE_END);
            sb.append("Content-Type:image/pjpeg" + LINE_END); // 这里配置的Content-type很重要的,用于服务器端辨别文件的类型的
            sb.append(LINE_END);
            params = sb.toString();
            sb = null;

            dos.write(params.getBytes());
            /** 上传文件 */
            if(mParam.get("fileType").equals("1") || mParam.get("fileType").equals("2")){//头像和图片
            	Bitmap bitmap = BitmapUtil.getimage(mFilePath);//进行压缩
            	if(bitmap == null){
            	    sendMessage(FileCallBack.FIAL, 0);
                    return;
            	}
            	FileOutputStream fos = new FileOutputStream(new File(FilePathManager.getCanClearImg()+"temp.jpg"));
            	fos.write(BitmapUtil.Bitmap2Bytes(bitmap));
            	fos.close();
            	FileInputStream fStream = new FileInputStream(FilePathManager.getCanClearImg()+"temp.jpg");
            	total = fStream.available();
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = fStream.read(bytes)) != -1) {
                    if (mCancel) {
                        fStream.close();
                        return;
                    }
                    dos.write(bytes, 0, len);
                    count += len;
                    int pro = (int) (count * 100 / total);
                    sendMessage(FileCallBack.START, pro);
                }
                fStream.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);
            	LogUtil.d("上传", "---2-bitmap--"+bitmap);
            }

            dos.flush();
            sendMessage(FileCallBack.COMPLETE, 0);
            /**
             * 获取响应码 200=成功 当响应成功，获取响应的流
             */
            int res = conn.getResponseCode();
            Log.d(TAG, "response code:" + res);
            if (res == 200) {
                if (mCancel) {
                    return;
                }
                LogUtil.d(TAG, "request success");
                is = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, CHARSET));
                String line = "";
                while (null != (line = br.readLine())) {
                    mResult += line;
                }
                is.close();
                
                try {
                    String fileAllPath=null;
                    if(!StringUtil.isEmpty(mResult)){
                    	LogUtil.d(TAG, "url----"+mResult);
                        UploadJson uploadJson = new Gson().fromJson(mResult, UploadJson.class);
                        String fileId = uploadJson.content.filePath;
                        if(!StringUtil.isEmpty(fileId)){
                            LogUtil.d(TAG, "-------fileId="+fileId);
                            String fileTemp = Constants.DOWNLOAD_URL +fileId;
                            fileAllPath = FilePathManager.getCanClearImg() + StringUtil.getMD5(fileTemp);
                        }
                    }
                    if(StringUtil.isEmpty(fileAllPath)){
                        fileAllPath = FilePathManager.getCanClearImg() + FileUtil.getFileName(mFilePath);
                    }
                	LogUtil.d(TAG, "filePath : " + mFilePath);
					FileUtil.copyFile(mFilePath,fileAllPath );
					mObj = fileAllPath ;
					LogUtil.d("mObj : ", mObj.toString() );
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
                sendMessage(FileCallBack.DONE_RESULT, 0);
                
            } else {
                LogUtil.e(TAG, "request error");
                sendMessage(FileCallBack.FIAL, 0);
            }
        } catch (MalformedURLException e) {
            sendMessage(FileCallBack.FIAL, 0);
            e.printStackTrace();
            return;
        } catch (IOException e) {
            sendMessage(FileCallBack.FIAL, 0);
            e.printStackTrace();
            return;
        }catch(Exception e){
            sendMessage(FileCallBack.FIAL, 0);
            e.printStackTrace();
            return;
        }finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (dos != null) {
                    dos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}