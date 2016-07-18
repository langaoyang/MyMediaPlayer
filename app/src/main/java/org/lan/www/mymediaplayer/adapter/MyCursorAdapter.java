package org.lan.www.mymediaplayer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import org.lan.www.mymediaplayer.R;
import org.lan.www.mymediaplayer.bean.ListViewItem;
import org.lan.www.mymediaplayer.utils.StringUitls;

/**
 * Created by Administrator on 2016/7/16.
 */
public class MyCursorAdapter extends CursorAdapter {

    public MyCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = View.inflate(context, R.layout.video_list_item, null);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.video_list_item_tv_duration = (TextView) view.findViewById(R.id.video_list_item_tv_duration);
        viewHolder.video_list_item_tv_title = (TextView) view.findViewById(R.id.video_list_item_tv_title);
        viewHolder.video_list_item_tv_size = (TextView) view.findViewById(R.id.video_list_item_tv_size);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        ListViewItem item = ListViewItem.instanceFromCursor(cursor);
        //对listview 中的一个item中的三个子控件进行赋值。
        viewHolder.video_list_item_tv_title.setText(item.title);
        viewHolder.video_list_item_tv_duration.setText(String.valueOf(StringUitls.formatMS(item.duration)));
        String size = Formatter.formatFileSize(context,item.size);
        viewHolder.video_list_item_tv_size.setText(size);



    }

    private class ViewHolder {
        TextView video_list_item_tv_title, video_list_item_tv_duration, video_list_item_tv_size;


    }
}
