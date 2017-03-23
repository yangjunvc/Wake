package com.android.wako.net;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.android.wako.common.FilePathManager;
import com.android.wako.util.BitmapUtil;
import com.android.wako.util.FileUtil;
import com.android.wako.util.LogUtil;
import com.android.wako.util.StringUtil;

/**
 * 加载图片
 *
 * 
 */
public class AsyncImageLoader {
    private static final String TAG = AsyncImageLoader.class.getSimpleName();

    public static final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    public static final int cacheSize = maxMemory / 6;// 不能太大，防止内存溢出
    public static LruCache<String, Bitmap> imageCache = new LruCache<String, Bitmap>(cacheSize) {
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount() / 1024;
        }

        protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
            LogUtil.d(TAG, "entryRemoved oldValue=" + oldValue + ";evicted=" + evicted + ";cacheSize=" + cacheSize);
            if (evicted) {
                oldValue.recycle();
            }
        }
    };

    public static ExecutorService pool = Executors.newFixedThreadPool(5);

    /**
     * -1表示全部
     * 
     * @param size
     */
    public static void clearImageCache(int size) {
        synchronized (imageCache) {
            imageCache.trimToSize(size);
        }
    }

    /**
     * 只从缓存中查找
     * 
     * @param url
     * @param canClear
     * @return
     */
    public static Bitmap getBitmapFormCache(String url, boolean canClear) {
        Bitmap bitmap = null;
        bitmap = imageCache.get(url);
        if (bitmap != null && bitmap.isRecycled()) {
            return null;
        }
        return bitmap;
    }

    /**
     * 从缓存中文件中查找，因为图片压缩时要cpu,如果放在UI线程中，会卡
     * 
     * @param url
     * @param canClear
     * @return
     */
    public static Bitmap getBitmapFormCacheAndFile(String url, boolean canClear, int width, int height) {
        Bitmap bitmap = null;
        bitmap = imageCache.get(url);
        if (bitmap == null || bitmap.isRecycled()) {
            String path = FilePathManager.getImgPath(canClear) + StringUtil.getMD5(url);
            File file = new File(path);
            if (file.exists()) {
                bitmap = BitmapUtil.getimage(path);
            }
        }
        return bitmap;
    }

    public static Bitmap loadDrawable(final String imageUrl, final int requestWidth, final int requestHeight, final boolean canClear,
            final boolean fitXY, final ImageCallback callback, final boolean round) {
        Bitmap drawable = null;
        // 1、从缓存中取bitmap
        synchronized (imageCache) {
            drawable = (Bitmap) imageCache.get(imageUrl);
        }
        if (drawable != null) {
            return drawable;
        }

        String fileName = StringUtil.getMD5(imageUrl);// 获取md5值作为文件名
        drawable = BitmapUtil.compressBySrc(FilePathManager.getImgPath(canClear) + fileName, 100, requestWidth, requestHeight);
        if (drawable != null) {
            synchronized (imageCache) {
                if (round) {
                    drawable = BitmapUtil.toRoundBitmap(drawable);// 切圆
                }
                imageCache.put(imageUrl, drawable);
            }
            return drawable;
        }

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (callback != null) {
                    callback.imageLoaded((Bitmap) msg.obj, imageUrl);
                }
            }
        };
        Runnable task = new Runnable() {
            public void run() {
                Bitmap drawable = loadImageFromUrl(imageUrl, requestWidth, requestHeight, canClear, fitXY, round);

                if (drawable != null) {
                    handler.sendMessage(handler.obtainMessage(0, drawable));
                }
            };
        };

        LogUtil.d(TAG, "-----execute image---");
        pool.execute(task);
        return null;
    }

    protected static Bitmap loadImageFromUrl(String imageUrl, int requestWidth, int requestHeight, boolean canClear, boolean fitXY, boolean round) {
        LogUtil.d(TAG, "[loadImageFromUrl] begin fitXY=" + fitXY);
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10 * 1000);
            conn.connect();
            InputStream inputStream = conn.getInputStream();

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap != null) {
                bitmap = BitmapUtil.compressByBitmap(bitmap, 500, requestWidth, requestHeight);
            }

            if (null != inputStream) {
                inputStream.close();
                inputStream = null;
            }
            conn.disconnect();
            if (bitmap != null) {
                LogUtil.d("from net", "get bitmap from net"); // imageCache中bitmap易被recycle()
                savePic(bitmap, imageUrl, canClear);// 保存图片
                synchronized (imageCache) {
                    if (round) {
                        bitmap = BitmapUtil.toRoundBitmap(bitmap);// 切圆
                    }
                    imageCache.put(imageUrl, bitmap);
                }
            }
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            System.gc();
            return null;
        }
    }

    public static void savePic(Bitmap bitmap, String imageUrl, boolean canClear) {
        if (bitmap != null && imageUrl != null && !"".equals(imageUrl)) {
            FileUtil.saveImage(bitmap, StringUtil.getMD5(imageUrl), FilePathManager.getImgPath(canClear));
        }
    }

    public static void savePicByFileName(Bitmap bitmap, String fileName, boolean canClear) {
        if (bitmap != null && fileName != null) {
            FileUtil.saveImage(bitmap, fileName, FilePathManager.getImgPath(canClear));
        }
    }

    public interface ImageCallback {
        public void imageLoaded(Bitmap imageDrawable, String imageUrl);
    }

    /**
     * 异步加载图片
     * 
     * @param view
     * @param url
     * @param width
     * @param height
     * @param canClear
     *            此图片是否可以被清除
     * @param fitXY
     *            是否要进行
     */
    public static void AsyncSetImage(final ImageView view, final String url, final int width, final int height, final boolean canClear,
            final boolean fitXY) {
        if (!StringUtil.isEmpty(url)) {
            view.setTag(url);
            Bitmap bitmap = AsyncImageLoader.loadDrawable(url, width, height, canClear, fitXY, new ImageCallback() {
                @Override
                public void imageLoaded(Bitmap imageDrawable, String imageUrl) {
                    LogUtil.d(TAG, "imageDrawable=" + imageDrawable + ";view=" + imageDrawable + ";imageUrl=" + imageUrl);
                    if (view != null) {
                        LogUtil.d(TAG, "view.getTag=" + view.getTag());
                    }
                    if (imageDrawable != null && view != null && imageUrl.equals((String) view.getTag())) {
                        view.setImageBitmap(imageDrawable);
                    }
                }
            }, false);

            if (bitmap != null && !bitmap.isRecycled()) {
                view.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * 
     * @param view
     * @param url
     * @param round
     *            是否要圆角
     * @param resId
     */
    public static void AsyncSetImageMain(final ImageView view, final String url, final boolean round, int resId) {
        if (!StringUtil.isEmpty(url)) {
            view.setTag(url);
            loadImage(view, url, round, resId, view.getMeasuredWidth(), view.getMeasuredHeight());
        }
    }

    /**
     * 加载图片
     * 
     * @param view
     * @param url
     * @param round
     *            是否要圆角
     * @param resId
     * @param width
     *            所要宽
     * @param height
     *            高
     */
    public static void AsyncSetImageMain(final ImageView view, final String url, final boolean round, int resId, int width, int height) {
        if (!StringUtil.isEmpty(url)) {
            view.setTag(url);
            loadImage(view, url, round, resId, width, height);
        }
    }

    static void loadImage(final ImageView view, final String url, final boolean round, int resId, int width, int height) {
        Bitmap bitmap = AsyncImageLoader.loadDrawable(url, width, height, true, false, new ImageCallback() {
            @Override
            public void imageLoaded(Bitmap imageDrawable, String imageUrl) {
                LogUtil.d(TAG, "imageDrawable=" + imageDrawable + ";view=" + imageDrawable + ";imageUrl=" + imageUrl);
                if (view != null) {
                    LogUtil.d(TAG, "view.getTag=" + view.getTag());
                }
                if (imageDrawable != null && view != null && imageUrl.equals((String) view.getTag())) {
                    view.setImageBitmap(imageDrawable);
                }
            }
        }, round);
        if (bitmap != null) {
            view.setImageBitmap(bitmap);
        } else if (resId > 0) {
            view.setImageResource(resId);
        }
    }

    /**
     * 主要是下载那些不清清除的图片，如头像
     */
    public static Bitmap AsyncSetImage(final ImageView view, final String url, final int width, final int height, final boolean canClear,
            final boolean fitXY, final String fileName) {
        if (!StringUtil.isEmpty(url)) {
            view.setTag(url);
            Bitmap bitmap = AsyncImageLoader.loadDrawable(url, width, height, canClear, fitXY, new ImageCallback() {
                @Override
                public void imageLoaded(Bitmap imageDrawable, String imageUrl) {
                    LogUtil.d(TAG, "---imageDrawable=" + imageDrawable + ";view=" + imageDrawable + ";imageUrl=" + imageUrl);
                    if (view != null) {
                        LogUtil.d(TAG, "view.getTag=" + view.getTag());
                    }
                    if (imageDrawable != null && view != null && imageUrl.equals((String) view.getTag())) {
                        LogUtil.d(TAG, "-------BitmapUtil------fileName=" + fileName);
                        File file = new File(FilePathManager.getImgPath(canClear) + fileName);
                        if (file.exists()) {
                            file.delete();
                        }
                        savePicByFileName(imageDrawable, fileName, false);
                        view.setImageBitmap(imageDrawable);
                    }
                }
            }, true);

            if (bitmap != null) {
                view.setImageBitmap(bitmap);
                File file = new File(FilePathManager.getImgPath(canClear) + fileName);
                if (file.exists()) {
                    file.delete();
                }
                savePicByFileName(bitmap, fileName, false);
            }
            return bitmap;
        }
        return null;
    }
}
