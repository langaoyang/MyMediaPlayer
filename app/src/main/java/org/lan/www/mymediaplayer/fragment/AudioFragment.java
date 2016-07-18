package org.lan.www.mymediaplayer.fragment;

import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.lan.www.mymediaplayer.R;

/**
 * Created by Administrator on 2016/7/16.
 */
public class AudioFragment extends  BaseFragment {


    private ListView simpleListView;

    /**
     * 确定布局
     *
     * @return
     */
    @Override
    protected View initView() {
//        View view = View.inflate(context, R.layout.video_list,null);
//        simpleListView = (ListView) view.findViewById(R.id.simple_listview);
        TextView textView = new TextView(context);
        textView.setText("视频");
        return textView;
    }

    /**
     * 注册布局中的按键的监听事件
     */
    @Override
    protected void initListener() {

    }

    /**
     * 给控件赋值，设置数据
     */
    @Override
    protected void initData() {

    }
}
