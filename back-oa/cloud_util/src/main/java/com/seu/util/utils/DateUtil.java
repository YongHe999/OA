package com.seu.util.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * TODO
 *
 * @author z-ajie@qq.com
 * @version 1.0
 * @date 2021/10/28 19:05
 */
public class DateUtil {
    public static String [] getBeforeSixDay(){
        String [] arr = new String[6];
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = null;
        for (int i=0;i<6;i++){
            c=Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, - i-1);
            arr[5-i] =sdf.format(c.getTime());

        }
        return arr;
    }
}
