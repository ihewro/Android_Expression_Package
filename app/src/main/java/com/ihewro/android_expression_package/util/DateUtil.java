package com.ihewro.android_expression_package.util;

import com.blankj.ALog;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/04/24
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class DateUtil {

    public static String getNowDateStr() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(d);
    }

    public static String getTimeStringByInt(long time){
        SimpleDateFormat format =  new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        return format.format(time);
    }

    public static boolean isTimeout(String now,String last){

        long to = date2TimeStamp(now);
        long from = date2TimeStamp(last);
        int days = (int) ((to - from)/(1000* 60 * 60 * 24));
        ALog.d("days",days + "diff:" + (to - from));
        return days >= 1;
    }

    /**
     * 日期格式字符串转换成时间戳
     * @param date_str 字符串日期
     * @return
     */
    public static long date2TimeStamp(String date_str){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.parse(date_str).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
