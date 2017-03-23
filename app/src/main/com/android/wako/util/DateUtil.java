package com.android.wako.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by duanmulirui
 */
public class DateUtil {

    /**
     *long转换成字符串类型
     * @param time
     * @param parsePattern yyyy-MM-dd HH:mm
     *            :转换后的格式，如"MM月dd日 HH:mm" 或"yyyy年MM月dd日"
     * @return
     */
    public static String getStringByLong(long time, String parsePattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(parsePattern);
        String dateStr = dateFormat.format(new Date(time));
        return dateStr;
    }

}
