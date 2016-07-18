package org.lan.www.mymediaplayer.utils;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.widget.CursorAdapter;

/**
 * Created by Administrator on 2016/7/18.
 */
public class MyAsynQueryHandler extends AsyncQueryHandler {
    public MyAsynQueryHandler(ContentResolver cr) {
        super(cr);
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        if(cookie instanceof CursorAdapter){ //这个instanceof必须要熟记
            CursorAdapter adapter = ((CursorAdapter) cookie);
            adapter.swapCursor(cursor);

        }

    }
}
