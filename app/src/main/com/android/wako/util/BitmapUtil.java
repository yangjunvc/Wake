package com.android.wako.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;

/**
 * 图片压缩转换
 *
 * 
 */
public class BitmapUtil {

    private final static String TAG = "BitmapUtil";
    static final int Def_Width = 400;// 默认宽度
    static final int Def_Height = 400;// 默认高度

    static final int Max_Width = 800;// 最大宽度
    static final int Max_Height = 800;// 最大高度

    /**
     * 质量压缩
     * 
     * @param image
     * @param maxKB
     *            ，最大KB
     * @return
     */
    public static Bitmap compressImage(Bitmap image, int maxKB) {

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 100;
            while (baos.toByteArray().length / 1024 > maxKB) { // 循环判断如果压缩后图片是否大于maxKB
                // kb,大于继续压缩
                baos.reset();// 重置baos即清空baos
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
                options -= 10;// 每次都减少10
            }
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
            Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            System.gc();
        }
        return null;
    }

    /**
     * 根据路径获取图片并压缩
     * 
     * @param srcPath
     * @param maxKB
     * @return
     */
    public static Bitmap compressBySrc(String srcPath, int maxKB, int width, int height) {
        try {
            Bitmap image = BitmapFactory.decodeFile(srcPath);
            if (image == null) {
                return null;
            }
            int w = image.getWidth();
            int h = image.getHeight();
            LogUtil.d(TAG, "-------------w=" + w + ";h=" + h + ";width=" + width + ";height=" + height);
            float hh = height;
            float ww = width;
            if (ww == 0) {
                ww = Def_Width;
            }
            if (hh == 0) {
                hh = Def_Height;
            }
            if (ww > Max_Width) {
                ww = Max_Width;
            }
            if (hh > Max_Height) {
                hh = Max_Height;
            }
            int newW = w, newH = h;
            if (w > ww) {
                newW = (int) ww;
                newH = h * newW / w;
            } else if (h > hh) {
                newH = (int) hh;
                newW = h * w / newH;
            }
            LogUtil.d(TAG, "-------------------newW=" + newW + ";newH=" + newH);
            return zoomImage(image, newW, newH);// 压缩好比例大小后再进行质量压缩
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 压缩图片
     * 
     * @param image
     * @param maxKB
     * @param width
     * @param height
     * @return
     */
    public static Bitmap compressByBitmap(Bitmap image, int maxKB, int width, int height) {

        try {
            int w = image.getWidth();
            int h = image.getHeight();
            LogUtil.d(TAG, "-------------w=" + w + ";h=" + h + ";width=" + width + ";height=" + height);
            float hh = height;
            float ww = width;
            if (ww == 0) {
                ww = Def_Width;
            }
            if (hh == 0) {
                hh = Def_Height;
            }
            if (ww > Max_Width) {
                ww = Max_Width;
            }
            if (hh > Max_Height) {
                hh = Max_Height;
            }
            int newW = w, newH = h;
            if (w > ww) {
                newW = (int) ww;
                newH = h * newW / w;
            } else if (h > hh) {
                newH = (int) hh;
                newW = h * w / newH;
            }
            LogUtil.d(TAG, "-------------------newW=" + newW + ";newH=" + newH);
            return zoomImage(image, newW, newH);// 压缩好比例大小后再进行质量压缩
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        try {
            Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            System.gc();
        }
        return null;
    }

    /**
     * bitmap转inputstream
     * 
     * @param bm
     * @return
     */
    public static InputStream BitmapToStream(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        InputStream sbs = new ByteArrayInputStream(baos.toByteArray());
        return sbs;
    }

    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap fileToBitmap(String file) {
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            System.gc();
        }
        return null;
    }

    /**
     * 
     * @Description: TODO 将图片转换为圆形
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = null;
        try {
            output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
            final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
            final RectF rectF = new RectF(dst);

            paint.setAntiAlias(true);

            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bitmap, src, dst, paint);
        } catch (Exception e) {
            e.printStackTrace();
            System.gc();
        }

        return output;
    }

    public static void saveBitmap(Bitmap bm, String path) {
        File f = new File(path);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 计算图片的缩放值
     * 
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 处理图片旋转
     * 
     * @param path
     * @return
     */
    private static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    private static Bitmap rotateBitmap(Bitmap bitmap, int rotate) {
        if (bitmap == null)
            return null;

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        // Setting post rotate to 90
        Matrix mtx = new Matrix();
        mtx.postRotate(rotate);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    public static Bitmap getimage(String srcPath) {
        FileInputStream fs = null;
        try {
            File file = new File(srcPath);
            if (!file.exists()) {
                return null;
            }
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
            newOpts.inJustDecodeBounds = true;
            fs = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, newOpts);
            newOpts.inSampleSize = calculateInSampleSize(newOpts, 480, 800);
            newOpts.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, newOpts);
            if (bitmap == null) {
                return null;
            }
            int degree = readPictureDegree(srcPath);
            bitmap = rotateBitmap(bitmap, degree);
            ByteArrayOutputStream baos = null;
            try {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
            } finally {
                try {
                    if (baos != null)
                        baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            System.gc();
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
