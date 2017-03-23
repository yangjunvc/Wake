package com.android.wako.json;

/**
 * Created by duanmulirui
 */
public class UploadJson extends BaseJson {

    public Content content;
    public class Content{
        public String filePath;//相对路径 配节下载图片地址
        public String path;//上传文件的ID 	Y
        public double width;//图片宽 	N
        public double height;//图片高 	N
    }
}
