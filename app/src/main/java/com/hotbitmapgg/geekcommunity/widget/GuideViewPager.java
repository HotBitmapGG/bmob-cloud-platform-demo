package com.hotbitmapgg.geekcommunity.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.nineoldandroids.view.ViewHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义ViewPager带滑动动画效果
 *
 * @author Administrator
 * @hcc
 */
public class GuideViewPager extends ViewPager
{

    // 设置页面从左边到右边的动画效果 需要找到viewpager的子view
    // left right 设置从左到右的动画的梯值 scale

    private View left;

    private View right;

    // 设置scale 平移
    private float mScale;

    private float mTars;

    // 设置scale的最小值
    private static final float MIN_SCALE = 0.5f;

    // 创建map集合 通过posttion 来获取对应的view
    private Map<Integer,View> maps = new HashMap<Integer,View>();

    public GuideViewPager(Context context, AttributeSet attrs)
    {

        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public GuideViewPager(Context context)
    {

        super(context);
        // TODO Auto-generated constructor stub
    }

    public void setViewForPostion(int position, View view)
    {

        maps.put(position, view);
    }

    public void removeViewForPosition(int position)
    {

        maps.remove(position);
    }

    // 重写ViewPager的页面滚动方法 来设置viewpager切换时的动画效果
    @Override
    protected void onPageScrolled(int position, float offset, int offsetPexils)
    {
        // TODO Auto-generated method stub
        super.onPageScrolled(position, offset, offsetPexils);

        // 通过position来获取left 和 right
        left = maps.get(position);
        right = maps.get(position + 1);

        // 设置pager切换时的动画效果
        startAnimation(left, right, offset, offsetPexils);
    }

    private void startAnimation(View left, View right, float offset, int offsetPexils)
    {
        // 页面切换时候 来设置动画
        if (right != null)
        {
            // 从0页到1页 0~1变化
            mScale = (1 - MIN_SCALE) * offset + MIN_SCALE;

            // 右边页面是隐藏在左边页面的下边 当从0页滑动到1页的时候 慢慢显示
            mTars = -getWidth() - getPageMargin() + offsetPexils;

            ViewHelper.setScaleX(right, mScale);
            ViewHelper.setScaleY(right, mScale);

            ViewHelper.setTranslationX(right, mTars);
        }

        if (left != null)
        {
            // 设置保证left在动画切换过程 一直在上边
            left.bringToFront();
        }
    }
}
