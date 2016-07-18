package org.lan.www.mymediaplayer.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.lan.www.mymediaplayer.R;
import org.lan.www.mymediaplayer.activity.VideoPlayActivity;
import org.lan.www.mymediaplayer.adapter.MyCursorAdapter;
import org.lan.www.mymediaplayer.bean.ListViewItem;
import org.lan.www.mymediaplayer.utils.MyAsynQueryHandler;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/16.
 */
public class VideoFragment extends BaseFragment {


    private ListView simpleListView;
    private MyCursorAdapter adapter;
    private MyAsynQueryHandler myAsynQueryHandler ;

    /**
     * 确定布局
     *
     * @return
     */
    @Override
    protected View initView() {
        View view = View.inflate(context, R.layout.video_list, null);
        simpleListView = (ListView) view.findViewById(R.id.simple_listview);
        return view;

    }

    /**
     * 注册布局中的按键的监听事件
     */
    @Override
    protected void initListener() {
        adapter = new MyCursorAdapter(context, null);
        simpleListView.setAdapter(adapter);
        simpleListView.setOnItemClickListener(new MyOnItemClickListener());
    }

    /**
     * 给控件赋值，设置数据
     */
    @Override
    protected void initData() {
        String[] projection = {MediaStore.Video.Media._ID,  //这一列必须写
                MediaStore.Video.Media.TITLE,   //标题文件名
                MediaStore.Video.Media.DURATION, //文件的时间长度
                MediaStore.Video.Media.DATA,        //文件的路径
                MediaStore.Video.Media.SIZE};   //文件的大小

//        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI
//                , projection, null, null, null);
        myAsynQueryHandler = new MyAsynQueryHandler(context.getContentResolver());
        myAsynQueryHandler.startQuery(88,adapter,MediaStore.Video.Media.EXTERNAL_CONTENT_URI,projection,null,null,null);
//        adapter.swapCursor(cursor);
    }

    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            {
                //点击条目的时候，播放条目对应的视频,跳转到视频播放页面
                Intent intent = new Intent(context, VideoPlayActivity.class);
                //这个时候要传入数据，要播放哪个视频，要将item传过去。怎么得到这个item文件。
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
//            ListViewItem item = ListViewItem.instanceFromCursor(cursor);
//            intent.putExtra("item", item); //要传递引用数据类型，这个引用数据类型必须要实现序列化接口
                ArrayList<ListViewItem> items = ListViewItem.getItemsFromCursor(cursor);
                intent.putExtra("items",items);
                intent.putExtra("position",position);
                startActivity(intent);

            }
        }
    }
}
