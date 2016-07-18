package org.lan.www.mymediaplayer.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.lan.www.mymediaplayer.R;

/**
 * Created by Administrator on 2016/7/16.
 */
public abstract class BaseFragment extends Fragment {

    protected Context context ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        context = getActivity();
        View view = initView();
        initListener();
        initData();
        return view;
    }

    /**
     * 确定布局
     * @return
     */
    protected abstract View initView() ;

    /**
     * 注册布局中的按键的监听事件
     */
    protected abstract void initListener();

    /**
     * 给控件赋值，设置数据
     */
    protected abstract void initData();

}
