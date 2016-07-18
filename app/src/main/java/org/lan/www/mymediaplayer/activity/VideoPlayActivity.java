package org.lan.www.mymediaplayer.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import org.lan.www.mymediaplayer.R;
import org.lan.www.mymediaplayer.bean.ListViewItem;
import org.lan.www.mymediaplayer.utils.StringUitls;
import org.lan.www.mymediaplayer.view.VideoView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/7/17.
 */
public class VideoPlayActivity extends BaseActivity {

    private static final int FLUSH_BOTTOM = 0;
    private static final int FLUSH_TIME = 1;
    private static final int HIDE_SHOW_CONTROLER = 2;

    private VideoView videoView;
    private TextView video_player_tv_position;
    private TextView video_player_tv_duration;
    private SeekBar video_player_sk_position;
    private ImageView video_player_iv_pause;
    private TextView video_player_tv_system_time;
    private TextView video_player_tv_title;
    private ImageView video_player_iv_battery;
    private MyBatteryReceiver receiver;
    private SeekBar video_player_sk_volume;
    private AudioManager audioManager;
    private ImageView video_player_iv_pre;
    private ImageView video_player_iv_next;
    private int position;
    private ArrayList<ListViewItem> items;
    private ListViewItem item;
    private boolean isShow; //控制条是否显示
    private LinearLayout video_player_ll_top;
    private LinearLayout video_player_ll_bottom;

    private int topHeight;

    private int bottomHeight;
    private float downY;
    private float downX;
    private GestureDetector gestureDetector;
    private int downVolume;
    private float halfScreenWidth;
    private int halfScreenHeight;
    private View video_player_cover;
    private float currAlpha;
    private ImageView video_player_iv_mute;
    private ImageView video_player_iv_fullscreen;
    private int lastVolume;
    private Uri uri;
    private LinearLayout video_player_ll_loading;



    private class MyOnErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("提示").setMessage("无法播放视频").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
            return false;

        }
    }

    private class MyOnBufferingUpdateListener implements MediaPlayer.OnBufferingUpdateListener {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            float secondProgress = percent * video_player_sk_position.getMax() / 100;
            video_player_sk_position.setSecondaryProgress((int) secondProgress);

        }
    }

    private class MySimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            unlockView();
            showOrHideController();


            return super.onSingleTapConfirmed(e);
        }


    }

    private void unlockView() {
        video_player_ll_top.setVisibility(View.VISIBLE);
        video_player_ll_bottom.setVisibility(View.VISIBLE);
    }

    private class OnVideoCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            switchPauseBg();
        }

    }

    private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser && seekBar == video_player_sk_volume) {
                setSysVolume(progress);
            }

            if (fromUser && seekBar == video_player_sk_position) {
                videoView.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }

    }

    private class OnVideoPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            video_player_ll_loading.setVisibility(View.GONE);
            videoView.start();
            if (uri == null) {
                video_player_tv_title.setText(item.title);
            } else {
                video_player_tv_title.setText(uri.toString());
            }
            int duration = videoView.getDuration();
            video_player_tv_duration.setText(StringUitls.formatMS(duration));
            video_player_sk_position.setMax(duration);
            startUpdateProgress();

            switchPauseBg();
        }

    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case FLUSH_BOTTOM:
                    startUpdateProgress();
                    break;
                case FLUSH_TIME:
                    getSysTime();
                    break;
                case HIDE_SHOW_CONTROLER:
                    hideController();
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    protected int getLayoutResId() {
        return R.layout.video_player;
    }

    @Override
    protected void initView() {
        //播放视频的控件
        videoView = (VideoView) this.findViewById(R.id.video_player_videoview);

        //已播放时间
        video_player_tv_position = (TextView) this.findViewById(R.id.video_player_tv_position);
        //视频总的播放的时间
        video_player_tv_duration = (TextView) this.findViewById(R.id.video_player_tv_duration);
        // 播放进度的seekBar
        video_player_sk_position = (SeekBar) this.findViewById(R.id.video_player_sk_position);
        //暂停按钮
        video_player_iv_pause = (ImageView) this.findViewById(R.id.video_player_iv_pause);
        video_player_iv_pre = (ImageView) this.findViewById(R.id.video_player_iv_pre);

        //顶部控件
        //系统时间
        video_player_tv_system_time = (TextView) this.findViewById(R.id.video_player_tv_system_time);
        //文件名
        video_player_tv_title = (TextView) this.findViewById(R.id.video_player_tv_title);
        //系统电量
        video_player_iv_battery = (ImageView) this.findViewById(R.id.video_player_iv_battery);
        video_player_sk_volume = (SeekBar) this.findViewById(R.id.video_player_sk_volume);

        video_player_iv_pre = (ImageView) this.findViewById(R.id.video_player_iv_pre);
        video_player_iv_next = (ImageView) this.findViewById(R.id.video_player_iv_next);
        //顶部控制栏
        video_player_ll_top = (LinearLayout) this.findViewById(R.id.video_player_ll_top);
        //底部控制栏
        video_player_ll_bottom = (LinearLayout) this.findViewById(R.id.video_player_ll_bottom);
        video_player_cover = this.findViewById(R.id.video_player_cover);
        ViewHelper.setAlpha(video_player_cover, 0f);
        video_player_iv_mute = (ImageView) this.findViewById(R.id.video_player_iv_mute);
        video_player_iv_fullscreen = (ImageView) this.findViewById(R.id.video_player_iv_fullscreen);
        video_player_ll_loading = (LinearLayout) this.findViewById(R.id.video_player_ll_loading);
        videoView.setOnBufferingUpdateListener(new MyOnBufferingUpdateListener());
    }

    @Override
    protected void initListener() {

        regeistBatteryReceiver();
        videoView.setOnPreparedListener(new OnVideoPreparedListener());
        videoView.setOnCompletionListener(new OnVideoCompletionListener());
        videoView.setOnErrorListener(new MyOnErrorListener());
        video_player_iv_pause.setOnClickListener(this);
        video_player_iv_pre.setOnClickListener(this);
        video_player_iv_next.setOnClickListener(this);
        video_player_sk_volume.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
        video_player_sk_position.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
        gestureDetector = new GestureDetector(new MySimpleOnGestureListener());
        video_player_iv_mute.setOnClickListener(this);
        video_player_iv_fullscreen.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        uri = intent.getData();
        if (uri == null) { // 说明是从本地播放
            items = (ArrayList<ListViewItem>) intent.getSerializableExtra("items");
            position = intent.getIntExtra("position", 0);
            playItem();
        } else {
            video_player_ll_loading.setVisibility(View.VISIBLE);
            videoView.setVideoURI(uri);

        }
//        ListViewItem item = (ListViewItem) intent.getSerializableExtra("item");
        getSysTime();
        getSysVolume();
        initController();
        halfScreenWidth = getResources().getDisplayMetrics().widthPixels / 2;
        halfScreenHeight = getResources().getDisplayMetrics().heightPixels / 2;
    }


    /**
     * 初始化控制条的高度
     */
    private void initController() {
        video_player_ll_top.measure(0, 0);
        video_player_ll_bottom.measure(0, 0);
        topHeight = video_player_ll_top.getMeasuredHeight();
        bottomHeight = video_player_ll_bottom.getMeasuredHeight();

    }

    private void playItem() {
        item = items.get(position);
        videoView.setVideoPath(item.path);
    }

    /**
     * 获取系统音量
     */
    private void getSysVolume() {
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int streamMaxVolume = getSysMaxVolume();
        int currVolume = getCurrentSysVolume();
        video_player_sk_volume.setMax(streamMaxVolume);
        video_player_sk_volume.setProgress(currVolume);
    }

    private int getSysMaxVolume() {
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    private int getCurrentSysVolume() {
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    private void setSysVolume(int progress) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);

    }

    /**
     * 用来更新播放时候的进度，包括已播放市场，和seekBar的进度
     */
    private void startUpdateProgress() {
        int currPosition = videoView.getCurrentPosition(); // 单位ms

        String text = StringUitls.formatMS(currPosition);
//        Log.i("VideoPlayActivity", "startUpdateProgress: " + text);
        video_player_tv_position.setText(text);

        video_player_sk_position.setProgress(currPosition);

        handler.sendEmptyMessageDelayed(FLUSH_BOTTOM, 500);
    }


    @Override
    public void onInnerClick(View view) {
        switch (view.getId()) {
            case R.id.video_player_iv_pause:
                switchPlayAndPause();
                switchPauseBg();
                break;
            case R.id.video_player_iv_pre:
                playPreVideo();
                break;
            case R.id.video_player_iv_next:
                playNextVideo();
                break;
            case R.id.video_player_iv_mute: //点击静音按钮，音量变0，再次点击，音量恢复。
                switchMute();
                break;
            case R.id.video_player_iv_fullscreen: //点击全屏，再次点击，恢复默认
                switchFullScreen();
                break;
        }
    }

    private void switchPlayAndPause() {
        if (videoView.isPlaying()) {
            videoView.pause();
        } else {
            videoView.start();
        }

    }

    private void switchPauseBg() {
        if (videoView.isPlaying()) {
            video_player_iv_pause.setImageResource(R.drawable.video_pause_selector);
        } else {
            video_player_iv_pause.setImageResource(R.drawable.video_play_selector);
        }
    }

    /**
     * 获取系统时间，并给textview赋值
     */
    public void getSysTime() {
        long currentTimeMillis = System.currentTimeMillis();
        String currTime = StringUitls.formatTime(currentTimeMillis);
        video_player_tv_system_time.setText(currTime);
        handler.sendEmptyMessageDelayed(FLUSH_TIME, 500);
    }

    /**
     * 设置系统电量
     */
    private void regeistBatteryReceiver() {
        receiver = new MyBatteryReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, filter);


    }

    private class MyBatteryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 根据电量等级 -- 切换不同图片
            int level = intent.getIntExtra("level", 0);
            if (level < 10) {
                video_player_iv_battery.setImageResource(R.drawable.ic_battery_0);
            } else if (level < 20) {
                video_player_iv_battery.setImageResource(R.drawable.ic_battery_10);

            } else if (level < 40) {
                video_player_iv_battery.setImageResource(R.drawable.ic_battery_20);

            } else if (level < 60) {

                video_player_iv_battery.setImageResource(R.drawable.ic_battery_40);
            } else if (level < 80) {

                video_player_iv_battery.setImageResource(R.drawable.ic_battery_60);
            } else if (level < 100) {

                video_player_iv_battery.setImageResource(R.drawable.ic_battery_80);
            } else { // 100

                video_player_iv_battery.setImageResource(R.drawable.ic_battery_100);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*
        页面关闭的时候取消广播的注册
         */
        unregisterReceiver(receiver);
        handler.removeCallbacksAndMessages(null); // token == null 表示的意思就是移出所有的信息
    }

    /**
     * 播放前一个视频
     */
    private void playPreVideo() {
        if (uri == null && position > 0) {
            position--;
            playItem();
        }
    }

    /**
     * 播放后一个视频
     */
    private void playNextVideo() {
        if (uri == null && position < items.size() - 1) {
            position++;
            playItem();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        gestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //这里移出消息避免手指滑动的时候自动隐藏
                handler.removeMessages(HIDE_SHOW_CONTROLER);
                downY = event.getY();
                downX = event.getX();
                downVolume = getCurrentSysVolume();
                currAlpha = ViewHelper.getAlpha(video_player_cover);
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = event.getY();
                float disY = moveY - downY;
                float disPer = disY / halfScreenHeight;
                if (downX < halfScreenWidth) {
                    int endVolume = (int) (downVolume - (disPer * getSysMaxVolume()));
                    setSysVolume(endVolume);
                    video_player_sk_volume.setProgress(endVolume);

                } else {
                    // TODO: 2016/7/18 改变系统的亮度
                    float endAlpha = currAlpha + disPer;
                    if (endAlpha >= 0 && endAlpha <= 1) {

                        ViewHelper.setAlpha(video_player_cover, endAlpha);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                //屏幕上没有点击事件的时候，自动隐藏开启
                handler.sendEmptyMessageDelayed(HIDE_SHOW_CONTROLER, 2000);

                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * 显示或者隐藏控制条
     */
    private void showOrHideController() {
        if (isShow) {
            hideController(); // TODO: 2016/7/18 这里remove的位置不对
            //手动点击的时候，不使用自动隐藏
            handler.removeMessages(HIDE_SHOW_CONTROLER);
        } else {

            showController();
        }
    }

    private void hideController() {

        ViewPropertyAnimator.animate(video_player_ll_bottom).translationY(bottomHeight);
        ViewPropertyAnimator.animate(video_player_ll_top).translationY(-topHeight);
        isShow = false;
    }

    private void showController() {
        ViewPropertyAnimator.animate(video_player_ll_bottom).translationY(0);
        ViewPropertyAnimator.animate(video_player_ll_top).translationY(0);
        isShow = true;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                video_player_sk_volume.setProgress(getCurrentSysVolume());

            case KeyEvent.KEYCODE_VOLUME_UP:
                video_player_sk_volume.setProgress(getCurrentSysVolume());
                break;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 静音切换
     */
    private void switchMute() {
        if (getCurrentSysVolume() != 0) {
            lastVolume = getCurrentSysVolume();
            setSysVolume(0);
            video_player_sk_volume.setProgress(0);
        } else {
            setSysVolume(lastVolume);
            video_player_sk_volume.setProgress(lastVolume);
        }
    }

    /**
     * 切换全屏
     */
    private void switchFullScreen() {
        videoView.switchFullScreen();
        switchFullScreenPic();
    }

    /**
     * 改变按钮北京
     */
    private void switchFullScreenPic() {
        if (videoView.isFullScreen()) {
            video_player_iv_fullscreen.setImageResource(R.drawable.video_defaultscreen_selector);
        } else {
            video_player_iv_fullscreen.setImageResource(R.drawable.video_fullscreen_selector);

        }
    }
}
