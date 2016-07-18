package org.lan.www.mymediaplayer.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.lan.www.mymediaplayer.R;

public class SplashActivity extends BaseActivity {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_splash;

    }

    /**
     * 初始化view 控件，执行findViewById的操作等
     */
    @Override
    protected void initView() {

    }

    /**
     * 注册监听
     */
    @Override
    protected void initListener() {

    }

    /**
     * 填充数据。
     */
    @Override
    protected void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }

    Handler handler = new Handler();
}
