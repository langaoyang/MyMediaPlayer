package org.lan.www.mymediaplayer.utils;

import android.database.Cursor;
import android.util.Log;

/**
 * Created by Administrator on 2016/7/17.
 */
public class CursorUtils {
    /**
     * 打印cursor
     */
    public static void printCursor(Cursor cursor){
        if(cursor!=null)
            while(cursor.moveToNext()){
                Log.i("CursorUtils", "-------------------");
                //打印每一列数据
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    Log.i("CursorUtils", "name:"+cursor.getColumnName(i)+",value:"+cursor.getString(i));
                }
            }
    }

}
