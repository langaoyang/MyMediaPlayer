package org.lan.www.mymediaplayer.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import org.lan.www.mymediaplayer.R;

/**
 * Created by Administrator on 2016/7/16.
 */
public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener {

    public  Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        context = this;

        initView();
        initListener();
        initData();
        dealCommon();
    }

    protected abstract int getLayoutResId();

    /**
     * 初始化view 控件，执行findViewById的操作等
     */
    protected abstract void initView();

    /**
     * 注册监听
     */
    protected abstract void initListener();

    /**
     * 填充数据。
     */
    protected abstract void initData();

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            default:
                onInnerClick(view);

                break;

        }
    }

    /**
     *  用来处理非后退按钮的点击事件
     * @param view 要点击的按钮。
     */
    public void onInnerClick(View view) {

    }

    public void dealCommon(){
        View back = this.findViewById(R.id.back);
        if (back != null) //判断非空，以防页面没有返回按钮。
            back.setOnClickListener(this);
    }



}
