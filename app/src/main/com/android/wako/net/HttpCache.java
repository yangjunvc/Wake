package com.android.wako.net;

import com.android.wako.common.FilePathManager;
import com.android.wako.util.FileUtil;
import com.android.wako.util.LogUtil;
import com.android.wako.util.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 缓存json数据，如果超过最大个数进行自动清理
 * @author chenggang
 *
 */
public class HttpCache {
    private static String Tag = "HttpCache";
    private static int mMax_Count = 500;//缓存json数据最大个数
    private static String filePath;
    private static HttpCache instance;

    public static HttpCache getInstance() {
        if (instance == null) {
            instance = new HttpCache();
        }
        return instance;
    }

    private HttpCache() {
        filePath = FilePathManager.getJsonCache();
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 从缓存中获取内容
     * 
     * @param fileName
     * @param expire
     * @return
     */
    public synchronized String getCacheJsonByFile(String fileName, long expire) {
        File file = new File(filePath + "/" + fileName);
        LogUtil.d(Tag, "---getCacheJsonByFile---fileName="+fileName+";expire="+expire+";file.exists()="+file.exists());
        if (file.exists()) {
            long last = file.lastModified();
            long now = System.currentTimeMillis();
            LogUtil.d(Tag, "---getCacheJsonByFile last="+last+";now="+now+";expire="+expire);
            if (now >= last) {
                if ((last + expire) >= now) {
                    return FileUtil.readFile(file.getAbsolutePath());
                }
            }
        }
        return "";

    }

    /**
     * 通过url，从缓存中获取内容
     * 
     * @param url,包括参数的唯一地址
     * @param expire 过期时间
     * @return
     */
    public synchronized String getCacheJsonByUrl(String url, long expire) {
        String fileName = StringUtil.stringToMD5(url);
        return getCacheJsonByFile(fileName, expire);

    }

    /**
     * 把内容存到缓存中
     * 
     * @param url
     * @param json
     */
    public synchronized void addCacheJson(String url, String json) {
        String fileName = StringUtil.stringToMD5(url);
        File file = new File(filePath);
        try{
            if (!file.exists()) {
                file.mkdirs();
            }
        }catch(Exception e){
            e.printStackTrace();
            return;
        }
        File[] fileArry = file.listFiles();
        if (fileArry == null || fileArry.length < mMax_Count) {
            FileUtil.createNewFile(filePath + "/" + fileName, json);
        } else {
            List<File> list = new ArrayList<File>();  
            list.addAll(Arrays.asList(fileArry));
            Collections.sort(list, new FileComparator());
            int size = list.size();
            while (size >= mMax_Count) {
                File temp = list.get(size - 1);
                FileUtil.deleteFile(temp.getAbsolutePath());
                list.remove(temp);
                size = list.size();
            }
        }
    }

    public class FileComparator implements Comparator<File> {
        public int compare(File file1, File file2) {
            if (file1.lastModified() < file2.lastModified()) {
                return 1;
            } else {
                return -1;
            }
        }
    }

}
