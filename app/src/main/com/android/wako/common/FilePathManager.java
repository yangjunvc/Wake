package com.android.wako.common;

import com.android.wako.MyApplication;
import com.android.wako.util.SystemInfoUtil;

import java.io.File;

/**
 * 所有涉及到文件的路径在这里设置和获取，要清除的文件也在这里配置
 * 
 * @author chenggang
 * 
 */
public class FilePathManager {

    private static String mJsonPath;// json数据的缓存
    private static String mCanClearImg;// 可以清理的图片
    private static String mNoClearImg;// 不可以清理的图片
    private static String mFilePath;// 下载文件路径

    /**
     * 获取json的缓存路径
     * 
     * @return
     */
    public synchronized static String getJsonCache() {
        if (mJsonPath == null) {
            String temp = getBasePath();
            mJsonPath = temp + "/json/";
            File tempFile = new File(mJsonPath);
            if (!tempFile.exists()) {
                tempFile.mkdir();
            }
        }
        return mJsonPath;
    }

    /**
     * 获取图片保存路径
     * 
     * @param canClear
     *            此图片是否可被清除
     * @return
     */
    public static String getImgPath(boolean canClear) {
        if (canClear) {
            return getCanClearImg();
        } else {
            return getNoClearImg();
        }
    }

    /**
     * 获取可以清除图片的路径
     * 
     * @return
     */
    public static String getCanClearImg() {
        if (mCanClearImg == null) {
            String temp = getBasePath();
            mCanClearImg = temp + "/mayclearimg/";
            File tempFile = new File(mCanClearImg);
            if (!tempFile.exists()) {
                tempFile.mkdir();
            }
        }
        return mCanClearImg;
    }

    /**
     * 获取不可以清除图片的路径
     * 
     * @return
     */
    public static String getNoClearImg() {
        if (mNoClearImg == null) {
            String temp = getBasePath();
            mNoClearImg = temp + "/noclearimg/";
            File tempFile = new File(mNoClearImg);
            if (!tempFile.exists()) {
                tempFile.mkdir();
            }
        }
        return mNoClearImg;
    }

    /**
     * 获取文件下载的路径
     * 
     * @return
     */
    public static String getFilePath() {
        if (mFilePath == null) {
            String temp = getBasePath();
            mFilePath = temp + "/download/";
            File tempFile = new File(mFilePath);
            if (!tempFile.exists()) {
                tempFile.mkdir();
            }
        }
        return mFilePath;
    }

    public static String getBasePath() {
        String basePath = null;
        try {
            if (SystemInfoUtil.avaiableSdcard()) {
                basePath = MyApplication.mCon.getExternalFilesDir(null).getAbsolutePath();
            } else {
                basePath = MyApplication.mCon.getFilesDir().getAbsolutePath();
            }
        } catch (Exception e) {
            basePath = MyApplication.mCon.getFilesDir().getAbsolutePath();
        }
        if (basePath != null) {
            File tempFile = new File(basePath);
            if (!tempFile.exists()) {
                tempFile.mkdir();
            }
        }
        return basePath;
    }
    /**
     * 删除缓存
     * @param dir
     */
    public static void cleanFiles(String dir) {
		if (dir == null) {
			return;
		}
		
		File dirFile = new File(dir);
		
		if(dirFile.exists()) {
			if (dirFile.isDirectory()) {
				File[] files = dirFile.listFiles();
				
				for (File file : files) {
					if (file.isDirectory()) {
						cleanFiles(file.getAbsolutePath());
					}
					file.delete();
				}
			}
//			dirFile.delete();
		}
	}
}
