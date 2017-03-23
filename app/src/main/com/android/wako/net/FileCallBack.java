package com.android.wako.net;

import java.io.Serializable;

/**
 * 文件上传下载的回调
 * 
 */
public interface FileCallBack extends Serializable {

    public static final int IDLE = 1;// 空闲，表示线程已生成，但没有执行
    public static final int START = 2;// 下载,上传中
    public static final int COMPLETE = 3;// 下载,上传完成
    public static final int DONE_RESULT = 4;// 上传完成并返回结果
    public static final int FIAL = 5;// 下载,上传失败

    /**
     * 
     * @param status 状态
     * @param progress 进度，只有status==START时，才会有意思
     * @param markid 标识当时线程id
     * @param result 例如:返回结果上传时会返回json
     * @param obj 例如:下载时，可以返回bitmap
     * @param markStr 标识当时线程,主要用于区分多文件上传
     */
    void callBackDown(int status, int progress, int markid, String result, Object obj, String markStr);
}
