package org.lan.www.mymediaplayer.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import org.lan.www.mymediaplayer.R;
import org.lan.www.mymediaplayer.adapter.MyFragmentPagerAdapter;
import org.lan.www.mymediaplayer.fragment.AudioFragment;
import org.lan.www.mymediaplayer.fragment.VideoFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/16.
 */
public class MainActivity extends BaseActivity {


    private TextView tvAudio;
    private TextView tvVideo;
    private ViewPager viewPager;

    private View indecateLine;
    private List<Fragment> fragments = new ArrayList<>();
    private int lineWidth;
    ;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    /**
     * 初始化view 控件，执行findViewById的操作等
     */
    @Override
    protected void initView() {

        tvVideo = (TextView) this.findViewById(R.id.main_tv_video);
        tvAudio = (TextView) this.findViewById(R.id.main_tv_audio);
        viewPager = (ViewPager) this.findViewById(R.id.main_viewpager);
        indecateLine = this.findViewById(R.id.main_indicate_line);

    }

    private MyFragmentPagerAdapter myFragmentPagerAdapter;

    /**
     * 注册监听
     */
    @Override
    protected void initListener() {
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(myFragmentPagerAdapter);
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener()); //没有鞋这哥方法，怎么实现滚动

    }

    /**
     * 填充数据。因为填充数据有可能会需要一段时间，所以先初始化，适配器，然后在更新数据，更新数据的时候要提醒数据已经更新
     */
    @Override
    protected void initData() {

        fragments.add(new VideoFragment());
        fragments.add(new AudioFragment());
        myFragmentPagerAdapter.notifyDataSetChanged();  //因为在父类中，这个方法，在initListener后面，所以，这里一定要写notify

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        lineWidth = screenWidth / fragments.size();
        indecateLine.getLayoutParams().width = lineWidth;

    }


    private final class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            float disX = positionOffset * lineWidth; //屏幕划过一个整凭，线划过一个线条的宽度。
            float startX = position * lineWidth; //线的起始位置就是 索引 * 线长 。
            float endX = startX + disX;

            ViewHelper.setTranslationX(indecateLine, endX);

        }

        @Override
        public void onPageSelected(int position) {
            int green = getResources().getColor(R.color.green);
            int halfWhite = getResources().getColor(R.color.halfwhite);
            tvVideo.setTextColor(position == 0 ? green : halfWhite);
            tvAudio.setTextColor(position == 1 ? green : halfWhite);
            ViewPropertyAnimator.animate(tvVideo).scaleX(position == 0 ? 1.2f : 1f).scaleY(position == 0 ? 1.2f : 1f);
            ViewPropertyAnimator.animate(tvAudio).scaleX(position == 0 ? 1f : 1.2f).scaleY(position == 0 ? 1f : 1.2f);

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
