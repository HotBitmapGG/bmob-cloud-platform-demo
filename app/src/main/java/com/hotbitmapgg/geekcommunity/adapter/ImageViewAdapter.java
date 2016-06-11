package com.hotbitmapgg.geekcommunity.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.hotbitmapgg.geekcommunity.utils.ImageLoadUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class ImageViewAdapter extends PagerAdapter
{

	private Context mContext;
	private List<String> mImages;

	public ImageViewAdapter(Context context, List<String> imgs)
	{
		mContext = context;
		mImages = imgs;
	}

	@Override
	public int getCount()
	{
		return mImages.size();
	}

	@Override
	public int getItemPosition(Object object)
	{
		return POSITION_NONE;
	}

	@Override
	public boolean isViewFromObject(View view, Object object)
	{
		return view == object;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object)
	{
		container.removeView((View) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position)
	{
		ImageView imageView = new ImageView(mContext);
		imageView.setScaleType(ScaleType.CENTER);
		ImageLoader.getInstance().displayImage("file://" + mImages.get(position), imageView, ImageLoadUtil.defaultOptions());
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		container.addView(imageView, params);
		return imageView;
	}

}
