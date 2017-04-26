package com.hutao.magicviewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;

/**
 * 本adapter内部添加一个ViewPager
 * 以下提到
 * 外部：指布局文件配置
 * 内部：指外部嵌套的一个相反方向viewpager
 *
 * @ hutao
 * @ 2017-4-26 026.
 */

public abstract class MagicViewPagerAdapter extends PagerAdapter {

    public final static int Inside_Type_VerticalViewPager = 1;
    public final static int Inside_Type_ViewPager = 2;

    private Context mContext;
    private Map<Integer, View> insideViewPagerMap;
    private ViewGroup.LayoutParams layoutParams;
    private InsidePagerAdapter insideViewPagerAdapter;
    private ViewPager.OnPageChangeListener onInsidePageChangeListener;
    private int insideType;

    public MagicViewPagerAdapter(Context mContext, int insideType) {
        this.mContext = mContext;
        //insideType默认Inside_Type_VerticalViewPager
        this.insideType = insideType == Inside_Type_ViewPager ? Inside_Type_ViewPager : Inside_Type_VerticalViewPager;
        insideViewPagerMap = new HashMap<>();
        layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    }

    @Override
    public int getCount() {
        return getOutsideCount();
    }

    /**
     * 添加内部viewpagerPageChangeListener
     *
     * @param onInsidePageChangeListener
     */
    public void setOnInsidePageChangeListener(ViewPager.OnPageChangeListener onInsidePageChangeListener) {
        this.onInsidePageChangeListener = onInsidePageChangeListener;

    }

    /**
     * 设置当前内部位置
     *
     * @param currentOutsidePosition 当前外部位置
     * @param item                   要设置内部位置
     */
    public void setInsideCurrentItem(int currentOutsidePosition, int item) {
        if (null != insideViewPagerMap.get(currentOutsidePosition)) {
            if (insideType == Inside_Type_VerticalViewPager) {
                VerticalViewPager verticalViewPager = (VerticalViewPager) insideViewPagerMap.get(currentOutsidePosition);
                verticalViewPager.setCurrentItem(item);
            } else {
                ViewPager viewPager = (ViewPager) insideViewPagerMap.get(currentOutsidePosition);
                viewPager.setCurrentItem(item);
            }
        }

    }


    /**
     * 获取当前内部位置
     *
     * @param outsidePosition
     * @return
     */
    public int getInsideSelectedPosition(int outsidePosition) {
        if (null != insideViewPagerMap.get(outsidePosition)) {
            if (insideType == Inside_Type_VerticalViewPager) {
                VerticalViewPager verticalViewPager = (VerticalViewPager) insideViewPagerMap.get(outsidePosition);
                if (null != verticalViewPager) {
                    return verticalViewPager.getCurrentItem();
                }
            } else {
                ViewPager viewPager = (ViewPager) insideViewPagerMap.get(outsidePosition);
                if (null != viewPager) {
                    return viewPager.getCurrentItem();
                }
            }
        }
        return 0;
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        View view = insideViewPagerMap.get(position);
        if (null != view) {
            if (null != view.getParent()) {
                ((ViewGroup) container).removeView(view);
            }
            insideViewPagerMap.remove(position);
            view = null;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //内部添加ViewPager 当前是上下方向切换 如果想左右直接换成ViewPager
        if (insideType == Inside_Type_VerticalViewPager) {
            VerticalViewPager insideViewPager = new VerticalViewPager(mContext);
            insideViewPager.setOffscreenPageLimit(1);
            if (null != onInsidePageChangeListener) {
                insideViewPager.setOnPageChangeListener(onInsidePageChangeListener);
            }
            insideViewPagerAdapter = new InsidePagerAdapter();
            insideViewPagerAdapter.setOutsidePosition(position);
            insideViewPager.setAdapter(insideViewPagerAdapter);
            ((ViewGroup) container).addView(insideViewPager, layoutParams);
            insideViewPagerMap.put(position, insideViewPager);
            return insideViewPager;
        } else {
            ViewPager insideViewPager = new ViewPager(mContext);
            insideViewPager.setOffscreenPageLimit(1);
            if (null != onInsidePageChangeListener) {
                insideViewPager.setOnPageChangeListener(onInsidePageChangeListener);
            }
            insideViewPagerAdapter = new InsidePagerAdapter();
            insideViewPagerAdapter.setOutsidePosition(position);
            insideViewPager.setAdapter(insideViewPagerAdapter);
            ((ViewGroup) container).addView(insideViewPager, layoutParams);
            insideViewPagerMap.put(position, insideViewPager);
            return insideViewPager;
        }
    }

    /**
     * 获取外面数量
     *
     * @return
     */

    public abstract int getOutsideCount();

    /**
     * 获取内部数量
     *
     * @return
     */
    public abstract int getInsideCount();

    /**
     * 获取相应页面view
     *
     * @param outsidePosition 外部位置
     * @param insidePosition  内部位置
     * @return
     */
    public abstract View getView(int outsidePosition, int insidePosition);

    class InsidePagerAdapter extends PagerAdapter {
        private int outsidePosition;
        private Map<Integer, View> viewMap;

        InsidePagerAdapter() {
            viewMap = new HashMap<>();
        }

        public void setOutsidePosition(int outsidePosition) {
            this.outsidePosition = outsidePosition;
        }

        @Override
        public int getCount() {
            return getInsideCount();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = viewMap.get(position);
            if (null != view) {
                if (null != view.getParent()) {
                    ((ViewGroup) container).removeView(viewMap.get(position));
                }
                viewMap.remove(position);
                view = null;
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = getView(outsidePosition, position);
            if (null != view.getParent()) {
                ViewGroup viewGroup = (ViewGroup) view.getParent();
                viewGroup.removeView(view);
            }
            ((ViewGroup) container).addView(view);
            viewMap.put(position, view);
            return view;
        }
    }

}
