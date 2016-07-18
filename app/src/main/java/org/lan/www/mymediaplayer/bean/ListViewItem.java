package org.lan.www.mymediaplayer.bean;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/17.
 */
public class ListViewItem implements Serializable {
    public String title;
    public int duration;
    public int size;
    public int id;
    public String path;

    @Override
    public String toString() {
        return "ListViewItem{" +
                "title='" + title + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", id=" + id +
                ", path='" + path + '\'' +
                '}';
    }

    public ListViewItem() {
    }

    public ListViewItem(String title, int duration, int size, int id, String path) {

        this.title = title;
        this.duration = duration;
        this.size = size;
        this.id = id;
        this.path = path;
    }

    public static ListViewItem instanceFromCursor(Cursor cursor) {
        ListViewItem item = new ListViewItem();

        item.title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
        item.duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
        item.id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));
        item.size = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
        item.path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));


        return item;
    }

    public static ArrayList<ListViewItem> getItemsFromCursor(Cursor cursor) {
        ArrayList<ListViewItem> items = new ArrayList<>();
        cursor.moveToPosition(-1);//
        while(cursor.moveToNext()){
            ListViewItem item = instanceFromCursor(cursor);
            items.add(item);
        }
        return items;
    }

}
