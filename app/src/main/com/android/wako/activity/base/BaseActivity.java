package com.android.wako.activity.base;

import android.app.Activity;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.android.wako.AccountExceptionActivity;
import com.android.wako.MyApplication;
import com.android.wako.R;
import com.android.wako.common.ActivityManager;
import com.android.wako.common.Constants;
import com.android.wako.json.BaseJson;
import com.android.wako.model.Header;
import com.android.wako.net.AsyncHttpGet;
import com.android.wako.net.AsyncHttpPost;
import com.android.wako.net.AsyncHttpsPost;
import com.android.wako.net.BaseRequest;
import com.android.wako.net.DefaultThreadPool;
import com.android.wako.net.ResStatus;
import com.android.wako.net.ThreadCallBack;
import com.android.wako.net.util.RequestParameter;
import com.android.wako.util.LogUtil;
import com.android.wako.util.PreferencesUtil;
import com.android.wako.util.SystemInfoUtil;
import com.android.wako.widget.CustomProgressDialog;
import com.google.gson.Gson;

import org.apache.http.client.methods.HttpUriRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by duanmulirui
 */
public class BaseActivity extends Activity implements ThreadCallBack,View.OnClickListener{
    private static final String TAG = BaseActivity.class.getSimpleName();

    public boolean isPause=true,isDestroy=true;
    /**
     * 当前activity isPause=true时，是否要再向上返数据，默认不需求，但升级是需求
     */
    public boolean needCall=false;
    /**
     * 是否要取消http请求,默认需要
     */
    public boolean mCancelRequest = true;
    private HashMap<String,String> mRequestMap = new HashMap<String,String>();
    public PopupWindow mPopupWindow;
    public Gson gson=new Gson();
    public BaseJson baseJson=null;
    /**
     * 当前activity所持有的所有请求
     */
    List<BaseRequest> requestList = null;

    protected CustomProgressDialog mProgress;
    protected CustomProgressDialog mProgressFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG,"--on create--");
        requestList = new ArrayList<BaseRequest>();
        ActivityManager.putActivity(this);
        isDestroy = false;
    }
    @Override
    protected void onStart() {
        isPause = false;
        super.onStart();
        LogUtil.d(TAG,"--on start--");
        ActivityManager.putPauseActivity(this);
    }

    @Override
    protected void onStop() {
        isPause=true;
        super.onStop();
        LogUtil.d(TAG,"--on stop--");
        ActivityManager.removePauseActivity(this);
    }

    @Override
    protected void onResume() {
        isPause=false;
        super.onResume();
        SystemInfoUtil.closeSoftKeyBoard(this);
//        /**
//         * FIXME / 统计信息
//         * 此处调用基本统计代码
//         **/
//        StatService.onResume(this);
    }
    @Override
    protected void onPause() {
        /**
         * 在activity销毁的时候同时设置停止请求，停止线程请求回调
         */
        isPause=true;
        super.onPause();
//        /**
//         * FIXME/ 统计信息
//         * 此处调用基本统计代码
//         **/
//        StatService.onPause(this);
    }

    @Override
    protected void onDestroy() {
        /**
         * 在activity销毁的时候同时设置停止请求，停止线程请求回调
         */
        if(mPopupWindow != null && mPopupWindow.isShowing()){
            mPopupWindow.dismiss();
        }
        isDestroy = true;
        LogUtil.d(TAG,"--on destroy--");
        ActivityManager.removeActivity(this);
        mPopupWindow = null;
        cancelRequest();

        super.onDestroy();
    }

    public void cancelRequest() {
        if (requestList != null && requestList.size() > 0) {
            for (int i=requestList.size();i>=0;i--) {
                try {
                    if (i < requestList.size()) {
                        BaseRequest request = requestList.get(i);
                        if (request != null) {
                            requestList.remove(request);
                            DefaultThreadPool.getInstance().removeFormMap(request);
                            if (mCancelRequest && request !=null) {
                                HttpUriRequest http = request.getRequest();
                                if (http != null) {
                                    http.abort();
                                }
                                if (request != null) {
                                    DefaultThreadPool.getInstance().remove(request);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void showProgress(int resId,boolean cancel){
        mProgress = new CustomProgressDialog(this);
        if(resId<=0){
            mProgress.setMessage(R.string.loading_data, cancel);
        }else{
            mProgress.setMessage(resId, cancel);
        }
        mProgress.show();
    }

    public void cancelProgress(){
        if(mProgress!=null){
            mProgress.dismiss();
        }
    }

    public boolean progressIsShow(){
        if(mProgress!=null){
            return mProgress.isShowing();
        }
        return false;
    }

    public void showProgressFile(int resId,boolean cancel){
        mProgressFile = new CustomProgressDialog(this);
        if(resId<=0){
            mProgressFile.setMessage(R.string.loading_data, cancel);
        }else{
            mProgressFile.setMessage(resId, cancel);
        }
        mProgressFile.show();
    }

    public void cancelProgressFile(){
        if(mProgressFile!=null){
            mProgressFile.dismiss();
        }
    }

    public boolean progressFileIsShow(){
        if(mProgressFile!=null){
            return mProgressFile.isShowing();
        }
        return false;
    }

    public void onCallback(String resultJson,int code,int resStatus) {

    }

    @Override
    public void onCallbackFromThread(String resultJson, int code, int resStatus) {
        LogUtil.d(TAG, "--code=" + code + ";resStatus=" + resStatus + ";isPause=" + isPause + ";needCall=" + needCall);
        LogUtil.d(TAG, "BaseActivity resultJson==" + resultJson);
        cancelProgress();
        if (isPause && !needCall) {
            return;
        }
        baseJson = null;
        if (resStatus == ResStatus.Success
                && code != Constants.RequestCode.WeiXin_Rechange
                && code != Constants.RequestCode.REPORT_LAUNCH) {
            int status = Constants.HeaderStatus.Check_Error;
            try {
                baseJson = gson.fromJson(resultJson, BaseJson.class);
                status = baseJson.header.status;
            } catch (Exception e) {
                e.printStackTrace();
                baseJson = new BaseJson();
                baseJson.header = new Header();
                baseJson.header.message = this.getString(R.string.json_error);
                resStatus = ResStatus.Error_Code;
            }if (status == Constants.HeaderStatus.Token_Error) {// 异地登录
                Intent intent = new Intent(MyApplication.mCon,
                        AccountExceptionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MyApplication.mCon.startActivity(intent);
                return;
            }
            else if (status == Constants.HeaderStatus.User_Forbid) {// 用户已停用，被锁住
                Intent intent = new Intent(MyApplication.mCon,
                        AccountExceptionActivity.class);
                intent.putExtra("type", 1);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MyApplication.mCon.startActivity(intent);
                return;
            }
            else if (status == Constants.RESULT_WEB_INTERNAL_ERROR) {
                showToast(baseJson.header.message);
            }
        }
        onCallback(resultJson, code, resStatus);
    }

    /**
     * 网络异常判断
     * @param showToast 是否给提示
     * @return true 表示网络可用，false表示网络不可用
     */
    public boolean checkNetwork(boolean showToast){
        boolean network = SystemInfoUtil.isNetworkConnected(this);
        if(showToast && !network){
            showToast(this.getString(R.string.network_error));
        }
        return network;
    }


    protected boolean isErrorStatus(int resStatus) {
        if (resStatus != ResStatus.Success) {
            showToast(ResStatus.getTipString(resStatus));
            return true;
        }
        return false;
    }

    public int getRequestStatus(String url,List<RequestParameter> parameter){
        StringBuffer sb=new StringBuffer();
        sb.append(url+"?");
        if(parameter!=null){
            for(RequestParameter rp:parameter){
                if(null!=rp){
                    sb.append(rp.getName()+"="+rp.getValue()+"&");
                }
            }
        }
        if(mRequestMap.containsKey(sb.toString())){
            return Constants.RequestStatus.Contians;
        }
        return Constants.RequestStatus.Add;
    }

    private void setParams(List<RequestParameter> parameter) {
        parameter.add(new RequestParameter("appId", Constants.appID));
        parameter.add(new RequestParameter("appBuildCode", Constants.VERSION));
        parameter.add(new RequestParameter("appVersion", Constants.VERSION_NAME));
        parameter.add(new RequestParameter("channel", Constants.CHANNEL));
        parameter.add(new RequestParameter("deviceInfo", Constants.DEVICEINFO));
        parameter.add(new RequestParameter("deviceId", Constants.deviceID));
        parameter.add(new RequestParameter("uid", PreferencesUtil.getWakoString(this,PreferencesUtil.Pre_Name, PreferencesUtil.Key_Uid, "")));
        parameter.add(new RequestParameter("token", PreferencesUtil.getWakoString(this, PreferencesUtil.Pre_Name,PreferencesUtil.Key_Token, "")));
    }

    /**
     * POST 加密的方式
     *
     * @param url 网址
     * @param parameter 设置参数 可以是null
     * @param loadingDialog 是否显示动画
     * @param requestCode 请求的code
     * @param dialogCancel 是否可以取消对话框
     * @param showNetwork 检测 是否联网
     */
    protected void startPostHttpsRequest(String url, List<RequestParameter> parameter, boolean loadingDialog, int requestCode, boolean dialogCancel, boolean showNetwork ) {
        if (!checkNetwork(showNetwork)) {
            // by lidy
            onCallback(null,requestCode,ResStatus.Error_Code);
            return;
        }
        if (loadingDialog) {
            showProgress(0, dialogCancel);
        }
        if(parameter != null ){
            setParams(parameter);
        }
        BaseRequest httpsRequest =  new AsyncHttpsPost(this, url, parameter, loadingDialog, "", false, requestCode, false);

        boolean flag = DefaultThreadPool.getInstance().execute(httpsRequest);
        if(flag){
            this.requestList.add(httpsRequest);
        }
    }


    protected void startHttpsRequst(String requestType,String url,List<RequestParameter> parameter,boolean loadingDialog, int requestCode,boolean dialogCancel,boolean showNetwrok){
        if(!checkNetwork(showNetwrok)){
            return;
        }

        if (loadingDialog) {
            showProgress(0, dialogCancel);
        }

//      setParams(parameter);
        BaseRequest httpsRequest = null;

        if(Constants.HTTP_POST.equalsIgnoreCase(requestType)){
            httpsRequest = new AsyncHttpsPost(this, url, parameter, loadingDialog, "", false, requestCode);
        }else{
            //TODO
        }

        boolean flag = DefaultThreadPool.getInstance().execute(httpsRequest);
        if(flag){
            this.requestList.add(httpsRequest);
        }
    }

    protected void startHttpRequst(String requestType,String url,List<RequestParameter> parameter,boolean loadingDialog, int requestCode,boolean dialogCancel,boolean showNetwrok){
        if(!checkNetwork(showNetwrok)){
            // by lidy
            onCallback(null,requestCode,ResStatus.Error_Code);
            return;
        }
        if (loadingDialog) {
            showProgress(0, dialogCancel);
        }

        setParams(parameter);
        BaseRequest httpRequest = null;

        if("POST".equalsIgnoreCase(requestType)){
            httpRequest = new AsyncHttpPost(this, url, parameter, loadingDialog, "", false, requestCode);
        }else{
            httpRequest = new AsyncHttpGet(this, url, parameter, loadingDialog, "", false, requestCode);
        }
        boolean flag = DefaultThreadPool.getInstance().execute(httpRequest);
        if(flag){
            this.requestList.add(httpRequest);
        }
    }

    /**
     * 请求网络数据
     * @param requestType 请求方式，POST or GET
     * @param url
     * @param parameter
     * @param requestCode
     * @param times 失败时重试次数，要>=1,默认为1
     * @param expire 数据过期时间，如果>0会，存到缓存,默认为0
     * @param call 如果已从缓存中获取，是否还要从网络获取并返回,默认为false
     */

    protected void startHttpRequstNew(String requestType, String url,
                                      List<RequestParameter> parameter,
                                      int requestCode,int times,int expire,boolean call,boolean loadingDialog,boolean dialogCancel,boolean showNetwrok) {
        if(!checkNetwork(showNetwrok) && expire == 0){
            return;
        }
        if (loadingDialog) {
            showProgress(0, dialogCancel);
        }
        if (null != parameter) {
            setParams(parameter);
        }

        BaseRequest httpRequest = null;
        if ("POST".equalsIgnoreCase(requestType)) {
            httpRequest = new AsyncHttpPost(this, url, parameter, false, "", false, requestCode);
        } else {
            httpRequest = new AsyncHttpGet(this, url, parameter, false, "", false, requestCode);
        }
        httpRequest.times = times;
        httpRequest.expire = expire;
        httpRequest.call = call;
        boolean flag = DefaultThreadPool.getInstance().execute(httpRequest);
        if(flag){
            this.requestList.add(httpRequest);
        }
    }

    @Override
    public void onTrimMemory(int level) {
        LogUtil.d(TAG, "onTrimMemory: " + level);
        if (level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
//            AsyncImageLoader.clearImageCache();
        }
    }

    @Override
    public void onClick(View v) {

    }

    public void showToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    public void showToast(int resid){
        Toast.makeText(this,resid,Toast.LENGTH_SHORT).show();
    }

    /**
     * 点击编辑框外软键盘消失
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {

            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();

            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }
    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     *
     * @param token
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_CANCELED) return;
    }
}
