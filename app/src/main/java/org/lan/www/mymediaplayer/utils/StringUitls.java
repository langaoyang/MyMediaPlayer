package org.lan.www.mymediaplayer.utils;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/7/16.
 * 内有方法： 1. 将字符串转化为时长
 */
public class StringUitls {

    public static String formatMS(int ms) {
        // TODO: 2016/7/18 查找日期格式的规范
        SimpleDateFormat format = new SimpleDateFormat("mm:ss"); //这里的格式一定要是小写。
        return format.format(new Date(ms));
    }

    /**
     * 用来格式化时间
     * @param currentTimeMillis
     * @return
     */
    public static String formatTime(long currentTimeMillis) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return  format.format(new Date(currentTimeMillis));
    }
}
