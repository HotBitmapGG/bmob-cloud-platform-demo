package com.hotbitmapgg.geekcommunity.alumnus.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

public class MGridView extends GridView
{
	public MGridView(Context context)
	{
		super(context);
	}

	public MGridView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public MGridView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{

		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

	// @Override
	// public boolean onInterceptTouchEvent(MotionEvent ev) {
	// return true;// true 拦截事件自己处理，禁止向下传递
	// }

	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		return false;// false 自己不处理此次事件以及后续的事件，那么向上传递给外层view
	}

}
