package com.hotbitmapgg.geekcommunity.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class EmoViewPagerAdapter extends PagerAdapter
{

	private List<View> views;

	public EmoViewPagerAdapter(List<View> views)
	{
		this.views = views;
	}

	@Override
	public int getCount()
	{
		return views.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object)
	{
		return view == object;
	}

	@Override
	public Object instantiateItem(View container, int position)
	{
		((ViewPager) container).addView(views.get(position));
		return views.get(position);
	}
	


	@Override
	public void destroyItem(View container, int position, Object object)
	{
		((ViewPager) container).removeView(views.get(position));

	}
	


}
