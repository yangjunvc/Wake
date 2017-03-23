/*
 * Copyright 2011 爱知世元
 * Website:http://www.azsy.cn/
 * Email:info＠azsy.cn
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

import com.android.wako.util.LogUtil;
import com.android.wako.util.StringUtil;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 所有线程的线程池，下载，上传，获取json
 * 
 * @author chenggang
 * 
 */
public class DefaultThreadPool {

    public static final int TYPE_JSON = 1;
    public static final int TYPE_FILE = 2;

    static ArrayBlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<Runnable>(15);
    static ThreadPoolExecutor pool = new ThreadPoolExecutor(10, 20, 15L, TimeUnit.SECONDS, blockingQueue,
            new ThreadPoolExecutor.DiscardOldestPolicy());

    static ArrayBlockingQueue<Runnable> mFileQueue = new ArrayBlockingQueue<Runnable>(15);
    static ThreadPoolExecutor mFilePool = new ThreadPoolExecutor(3, 20, 15L, TimeUnit.SECONDS, blockingQueue,
            new ThreadPoolExecutor.DiscardOldestPolicy());

    private static DefaultThreadPool instance = null;
    static HashMap<String,String> mMap = new HashMap<String,String>(); //记录所有json的请求,以防多次请求,文件请求不包括在内

    public static DefaultThreadPool getInstance() {
        if (instance == null) {
            instance = new DefaultThreadPool();
        }
        return instance;
    }

    /**
     * 加入线程池,如果失败，表示线程池中存在
     * @param r
     * @return
     */
    public boolean execute(Runnable r) {
        String flag = getFlag(r);
        if(!StringUtil.isEmpty(flag)){
            if(mMap.containsKey(flag)){
                return false;
            }else{
                mMap.put(flag, flag);
            }
        }
        pool.execute(r);
        return true;
    }

    /**
     * 获取请求中唯一参数
     * @param r
     * @return
     */
    public String getFlag(Runnable r){
        if(BaseRequest.class.isInstance(r)){
            BaseRequest br = (BaseRequest) r;
            return StringUtil.getMD5(br.url+br.getParames());
        }
        return null;
    }

    public void remove(Runnable r){
        pool.remove(r);
        removeFormMap(r);
    }

    /**
     * 从map中删除对应的key
     * @param r
     */
    public synchronized void removeFormMap(Runnable r){
        String flag = getFlag(r);
        if(!StringUtil.isEmpty(flag)){
            mMap.remove(flag);
        }
    }

    public synchronized static void clearMap(){
        mMap.clear();
    }

    /**
     * 加入线程池,如果失败，表示线程池中存在
     * @param type 1表示json,2表示文件
     * @param r
     * @return
     */
    public boolean execute(int type, Runnable r) {
        if (type == TYPE_JSON) {
            String flag = getFlag(r);
            if(!StringUtil.isEmpty(flag)){
                if(mMap.containsKey(flag)){
                    return false;
                }else{
                    mMap.put(flag, flag);
                }
            }
            pool.execute(r);
        } else if (type == TYPE_FILE) {
            mFilePool.execute(r);
        }
        return true;
    }

    /**
     * 关闭，并等待任务执行完成，不接受新任务
     */
    public static void shutdown() {
        if (pool != null) {
            pool.shutdown();
            LogUtil.i(DefaultThreadPool.class.getName(), "DefaultThreadPool shutdown");
        }
    }

    /**
     * 关闭，立即关闭，并挂起所有正在执行的线程，不接受新任务
     */
    public static void shutdownRightnow() {
        if (pool != null) {
            pool.shutdownNow();
            try {
                // 设置超时极短，强制关闭所有任务
                pool.awaitTermination(1, TimeUnit.MICROSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally{
                clearMap();
            }
            LogUtil.i(DefaultThreadPool.class.getName(), "DefaultThreadPool shutdownRightnow");
        }
    }

}
