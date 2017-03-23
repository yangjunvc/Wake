package com.android.wako.activity.my;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.wako.CommonTitleActivity;
import com.android.wako.MyApplication;
import com.android.wako.R;
import com.android.wako.common.Constants;
import com.android.wako.json.UploadJson;
import com.android.wako.json.UserInfoJson;
import com.android.wako.model.UserInfo;
import com.android.wako.net.DefaultThreadPool;
import com.android.wako.net.FileCallBack;
import com.android.wako.net.ResStatus;
import com.android.wako.net.UploadTask;
import com.android.wako.net.util.RequestParameter;
import com.android.wako.util.BitmapUtil;
import com.android.wako.util.LogUtil;
import com.android.wako.util.StringUtil;
import com.android.wako.widget.SelectPicture;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 个人信息
 * Created by duanmulirui
 */
public class UserInfoActivity extends CommonTitleActivity implements FileCallBack{
    private String TAG = "UserInfoActivity";
    private static final int REQUEST_USERINFO = 1006;
    private static final int REQUEST_MODIFY = 10018;

    private Uri mPhotoUri;

    private ImageView mHeadImg,mHeadImg_bg,mQrCode;
    private TextView mName;

    private String qrCodeStr,inviteStr,filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo_main);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        List<RequestParameter> parameter = new ArrayList<RequestParameter>();
        startHttpRequst(Constants.HTTP_POST, Constants.USERINFO_URL, parameter, false, REQUEST_USERINFO, true, false);
    }

    private void initViews() {
        loadTitleViews();
        setLeftVisibily(View.VISIBLE);
        setTitle(R.string.userinfo_title);

        mHeadImg = (ImageView) findViewById(R.id.userinfo_headimg);
        mHeadImg_bg = (ImageView) findViewById(R.id.userinfo_headimg_bg);
        mHeadImg_bg.getBackground().setAlpha(100);
        mName = (TextView) findViewById(R.id.userinfo_name);
        mQrCode = (ImageView) findViewById(R.id.userinfo_qrcode);
        findViewById(R.id.headimg_view).setOnClickListener(this);
        findViewById(R.id.name_view).setOnClickListener(this);
        findViewById(R.id.qrcode_view).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.headimg_view:
                popupWindow(v);
                break;
            case R.id.name_view:
                startActivity(new Intent(this,ModifyNameActivity.class).putExtra("name",mName.getText().toString()));
                break;
            case R.id.qrcode_view:
                startActivity(new Intent(this,MyQrCodeActivity.class).putExtra("qrcode",qrCodeStr).putExtra("invite",inviteStr));
                break;
            case R.id.top_view://拍照
                SelectPicture.callCameraFile(this, SelectPicture.CAMERA_PATH, SelectPicture.CODE_CAMERA);
                break;
            case R.id.middle_view://从相机选择
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), SelectPicture.CODE_PHOTO);
                break;
            case R.id.blow_view:
            case R.id.null_view:
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onCallback(String resultJson, int code, int resStatus) {
        super.onCallback(resultJson, code, resStatus);
        switch (code){
            case REQUEST_USERINFO:
                if(resStatus == ResStatus.Success){
                    UserInfoJson json = gson.fromJson(resultJson,UserInfoJson.class);
                    if(json != null && json.content != null && json.header != null && json.header.status == 1){
                        UserInfo model = json.content;
                        if(model != null){
                            String headImg = model.headImg;
                            if(!StringUtil.isEmpty(headImg)){
                                MyApplication.imageLoader.displayImage(Constants.DOWNLOAD_URL + model.headImg,mHeadImg);
                            }
                            mName.setText(model.name);
                            String qrcode = model.barcodeUrl;
                            if(!StringUtil.isEmpty(qrcode)){
                                MyApplication.imageLoader.displayImage(Constants.DOWNLOAD_URL + qrcode,mQrCode);
                            }
                            qrCodeStr = model.barcodeUrl;
                            inviteStr = model.inviteCode;
                        }
                    }
                }else{
                    showToast(ResStatus.getTipString(resStatus));
                }
                break;
            case REQUEST_MODIFY:
                if(resStatus == ResStatus.Success){
                    if(baseJson != null && baseJson.header != null && baseJson.header.status == 1){
                        MyApplication.imageLoader.displayImage(Constants.DOWNLOAD_URL+filePath,mHeadImg);
                        showToast(R.string.modify_success);
                    }
                }else{
                    showToast(ResStatus.getTipString(resStatus));
                }
                break;
            default:
                break;
        }
    }

    private void popupWindow(View view) {
        if(mPopupWindow == null){
            LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = lay.inflate(R.layout.popupwindow_main, null);
            // 初始化按钮
            TextView top = (TextView) v.findViewById(R.id.top_view);
            top.setText(R.string.camera);
            top.setOnClickListener(this);
            TextView middle = (TextView) v.findViewById(R.id.middle_view);
            middle.setText(R.string.album);
            middle.setOnClickListener(this);
            v.findViewById(R.id.blow_view).setOnClickListener(this);
            v.findViewById(R.id.null_view).setOnClickListener(this);
            mPopupWindow = new PopupWindow(v, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.update();
        mPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d(TAG, "---onActivityResult---requestCode=" + requestCode + ";resultCode=" + resultCode);
        try {
            if (resultCode == Activity.RESULT_CANCELED)
                return;
            if (requestCode == 110) {// 处理裁剪回来的头像
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                LogUtil.d(TAG, "--------------------data.getData()=" + data.getData());
                Bundle extras = data.getExtras();
                Bitmap bitmap = ((Bitmap) extras.get("data"));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                BitmapUtil.saveBitmap(bitmap, SelectPicture.CROP_PATH);
                File file = new File(SelectPicture.CROP_PATH);
                upLoadImgData(file.getAbsolutePath());
                mPopupWindow.dismiss();
                return;
            }
            if (SelectPicture.CODE_PHOTO == requestCode) {
                LogUtil.d(TAG, "data=" + data);
                if (data != null) {
                    mPhotoUri = data.getData();
                } else {
                    return;
                }
                SelectPicture.getCirImageUri(this, mPhotoUri, 110);
            }
            if (requestCode == SelectPicture.CODE_CAMERA) {
                SelectPicture.getCirImageFile(this, SelectPicture.CAMERA_PATH, 110);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void upLoadImgData(String filePath) {// 上传头像
        showProgress(R.string.uploading_image, true);
        Map<String, String> params = new HashMap<String, String>();
        params.put("fileType", "1");
        params.put("fileExtName", "png");
        UploadTask upload = new UploadTask();
        upload.mUrl = Constants.UPLOAD_URL;
        upload.mFilePath = filePath;
        upload.mParam = params;
        upload.mCallBack = this;
        DefaultThreadPool.getInstance().execute(DefaultThreadPool.TYPE_FILE, upload);
    }

    private void upload(String key,String value){
        List<RequestParameter> parameter = new ArrayList<RequestParameter>();
        parameter.add(new RequestParameter(key, value));
        startHttpRequst(Constants.HTTP_POST, Constants.MODIFYUSRINFO_URL, parameter, false, REQUEST_MODIFY, true, false);
    }

    @Override
    public void callBackDown(int status, int progress, int markid, String result, Object obj, String markStr) {
        LogUtil.d(TAG, "result==" + result);
        if (isDestroy) {
            return;
        }
        if (status == FileCallBack.DONE_RESULT) {
            cancelProgress();
            if (null != result && result.length() > 0) {
                try {
                    UploadJson uploadJson = gson.fromJson(result, UploadJson.class);
                    if (uploadJson != null && uploadJson.header != null && uploadJson.header.status == 1 && uploadJson.content != null) {
                        filePath = uploadJson.content.filePath;
                        upload("headImg",filePath);
                    } else {
                        if (uploadJson != null && uploadJson.header != null && uploadJson.header.status == 0) {
                            String msg = uploadJson.header.message;
                            showToast(msg);
                            return;
                        }
                    }
                } catch (Exception e) {
                    showToast(R.string.upload_image_fail);
                    e.printStackTrace();
                }
            } else {
                showToast(R.string.upload_image_fail);
            }
        } else if (status == FileCallBack.FIAL) {
            cancelProgress();
            showToast(R.string.upload_image_fail);
        }
    }

}
