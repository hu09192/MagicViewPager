package com.hutao.magicviewpager;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {

    private ViewPager viewPager;
    private TextView textVeiw;

    private int[] imageRedIds = {R.mipmap.image_1, R.mipmap.image_2, R.mipmap.image_3, R.mipmap.image_4, R.mipmap.image_5};
    private MagicViewPagerAdapter magicViewPagerAdapter;
    private ViewGroup.LayoutParams imageParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initDate();
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.vp_mian_viewpager);
        textVeiw = (TextView) findViewById(R.id.tv_main_text);
    }

    private void initDate() {
        imageParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        magicViewPagerAdapter = new MagicViewPagerAdapter(this, MagicViewPagerAdapter.Inside_Type_VerticalViewPager) {
            @Override
            public int getOutsideCount() {
                return 5;
            }

            @Override
            public int getInsideCount() {
                return imageRedIds.length;
            }

            @Override
            public View getView(int outsidePosition, int insidePosition) {
                ImageView imageView = new ImageView(MainActivity.this);
                imageView.setBackgroundResource(imageRedIds[insidePosition]);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setLayoutParams(imageParams);
                return imageView;
            }
        };
        viewPager.setAdapter(magicViewPagerAdapter);
        viewPager.setOffscreenPageLimit(1);
        magicViewPagerAdapter.setOnInsidePageChangeListener(new MyOnPageChangeListener(MyOnPageChangeListener.Type_Inside_ViewPager));
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener(MyOnPageChangeListener.Type_Outside_ViewPager));
    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        public final static int Type_Outside_ViewPager = 1;
        public final static int Type_Inside_ViewPager = 2;
        private int currentViewPager;

        MyOnPageChangeListener(int currentViewPager) {
            this.currentViewPager = currentViewPager == Type_Outside_ViewPager ? Type_Outside_ViewPager : Type_Inside_ViewPager;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            //不要position值 只是想要切换事件
            //可以根据下面方法判断哪个切换
            if (currentViewPager == Type_Outside_ViewPager) {
                //当前事例外面指viewpager
                textVeiw.setText("当前:外面切换-->" + viewPager.getCurrentItem() + " - " + magicViewPagerAdapter.getInsideSelectedPosition(viewPager.getCurrentItem()));
                //如果想实现外部切换然后内部跳到0
                magicViewPagerAdapter.setInsideCurrentItem(viewPager.getCurrentItem(), 0);
            } else {
                //当前事例内部指VerticalViewPager
                textVeiw.setText("当前:内部切换-->" + viewPager.getCurrentItem() + " - " + magicViewPagerAdapter.getInsideSelectedPosition(viewPager.getCurrentItem()));
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
