package com.ihewro.android_expression_package.util;

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

}
