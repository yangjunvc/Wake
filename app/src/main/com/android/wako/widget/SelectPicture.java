package com.android.wako.widget;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import com.android.wako.common.FilePathManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 照相 选图片
 *
 * 
 */
public class SelectPicture {

    public static final int CODE_CAMERA = 0;// 相机
    public static final int CODE_PHOTO = 1;// 图片

    /**
     * 裁剪存放路径
     */
    public static String CROP_PATH = FilePathManager.getImgPath(true) + "crop_phone.png";
    /**
     * 照相存放路径
     */
    public static String CAMERA_PATH = FilePathManager.getImgPath(true) + "camera_phone.jpg";

    /**
     * 通过uri返回数据，最后测试有些手机不会通过此方式返回
     * 
     * @param act
     * @param mPhotoUri
     * @param code
     */
    public static void callCameraUri(Activity act, Uri mPhotoUri, int code) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values = new ContentValues();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fileName = format.format(new Date());
        values.put("title", fileName);// 标题
        values.put("_display_name", fileName);// 显示名字
        values.put("picasa_id", fileName);// picasa_id
        values.put("description", fileName);// 描述
        mPhotoUri = act.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra("output", mPhotoUri);
        act.startActivityForResult(intent, code);
    }

    /**
     * 直接把照片存到相应sd卡上
     * 
     * @param act
     * @param path
     * @param code
     */
    public static void callCameraFile(Activity act, String path, int code) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(path);
        Uri u = Uri.fromFile(file);
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
        act.startActivityForResult(intent, code);
    }

    /**
     * 裁剪图片
     * 
     * @param uri
     */
    public static void getCirImageUri(Activity act, Uri uri, int code) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        act.startActivityForResult(intent, code);
    }

    /**
     * 裁剪图片
     * 
     * @param
     */
    public static void getCirImageFile(Activity act, String path, int code) {
        File file = new File(path);
        Uri u = Uri.fromFile(file);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(u, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        act.startActivityForResult(intent, code);
    }

}
