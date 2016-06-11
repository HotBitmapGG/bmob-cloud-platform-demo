
package com.hotbitmapgg.geekcommunity.pick;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.activity.BaseActivity;
import com.hotbitmapgg.geekcommunity.utils.ImageLoadUtil;
import com.hotbitmapgg.geekcommunity.utils.StatusBarCompat;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class CustomGalleryPreActivity extends BaseActivity implements OnClickListener
{

	private ImageButton leftBtn;
	private Button rightBtn2;
	private TextView title;

	private ViewPager viewPager;
	private ImageView iv_check;
	private Album album;
	private ImageItem imageItem;
	private ImagePreAdapter adapter;
	private int p, limit;
	private String action;

	@Override
	protected void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		setContentView(R.layout.gallery_pre);
		
		//设置状态栏颜色
		StatusBarCompat.compat(this);
		
		album = (Album) getIntent().getSerializableExtra(Album.class.getSimpleName());
		p = getIntent().getIntExtra("p", 0);
		limit = getIntent().getIntExtra("limit", 0);
		imageItem = album.getImageAt(p);

		action = getIntent().getStringExtra("action");
		initTitle();
		viewPager = (ViewPager) findViewById(R.id.pic_viewpage);
		iv_check = (ImageView) findViewById(R.id.imgQueueMultiSelected);
		adapter = new ImagePreAdapter(CustomGalleryPreActivity.this, album.getImages());
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener()
		{

			@Override
			public void onPageSelected(int position)
			{
				p = position;
				title.setText((position + 1) + "/" + album.getCount());
				check();
			}

			private void check()
			{
				imageItem = album.getImageAt(p);
				if (ImageUtils.sImageItems.contains(imageItem))
				{
					iv_check.setSelected(true);
				}
				else
				{
					iv_check.setSelected(false);
				}
			}

			@Override
			public void onPageScrolled(int position, float offset, int state)
			{

			}

			@Override
			public void onPageScrollStateChanged(int position)
			{

			}
		});
		viewPager.setCurrentItem(p);

		iv_check.setOnClickListener(this);

		if (ImageUtils.sImageItems.contains(imageItem))
		{
			iv_check.setSelected(true);
		}
		else
		{
			iv_check.setSelected(false);
		}
	}

	private void initTitle()
	{
		leftBtn = (ImageButton) findViewById(R.id.leftBtn);
		leftBtn.setVisibility(View.VISIBLE);
		title = (TextView) findViewById(R.id.title);
		title.setText((p + 1) + "/" + album.getCount());
		leftBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finishActivity();
			}
		});

		rightBtn2 = (Button) findViewById(R.id.rightBtn2);
		if (!TextUtils.isEmpty(action))
		{
			if (action.equalsIgnoreCase(Action.ACTION_MULTIPLE_PICK_TO_TEACHER_UPLOAD) || action.equalsIgnoreCase(Action.ACTION_MULTIPLE_PICK_TO_STUDENT_UPLOAD))
			{
				rightBtn2.setVisibility(View.GONE);
			}
			else
			{
				rightBtn2.setVisibility(View.VISIBLE);
			}
		}
		rightBtn2.setText("完成");
		if (ImageUtils.sImageItems.size() == 0)
		{
			rightBtn2.setEnabled(false);
		}
		else
		{
			rightBtn2.setEnabled(true);
			rightBtn2.setText("完成(" + ImageUtils.sImageItems.size() + "/" + limit + ")");
		}
		rightBtn2.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent();
				intent.putExtra("complete", true);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	@Override
	public void onBackPressed()
	{
		finishActivity();
	}

	private void finishActivity()
	{
		Intent intent = new Intent();
		intent.putExtra("complete", false);
		setResult(RESULT_OK, intent);
		finish();
	}

	class ImagePreAdapter extends PagerAdapter
	{

		private Context mContext;
		private List<ImageItem> mImages;

		public ImagePreAdapter(Context context, List<ImageItem> imgs)
		{
			mContext = context;
			mImages = imgs;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.support.v4.view.PagerAdapter#getCount()
		 */
		@Override
		public int getCount()
		{
			return mImages.size();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.view.PagerAdapter#isViewFromObject(android.view
		 * .View, java.lang.Object)
		 */
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
			final ImageItem imageItem = mImages.get(position);
			Display d = ((Activity) mContext).getWindow().getWindowManager().getDefaultDisplay(); // 获取屏幕宽�?高用
			ImageView imageView = new ImageView(mContext);
			imageView.setAdjustViewBounds(true);
			imageView.setScaleType(ScaleType.FIT_CENTER);
			imageView.setMaxHeight(d.getHeight() * 4 / 9);
			imageView.setMaxWidth(d.getWidth());
			String url = imageItem.getPath();
			ImageLoader.getInstance().displayImage("file://" + url, imageView, ImageLoadUtil.defaultOptions());
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			container.addView(imageView, params);
			return imageView;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.imgQueueMultiSelected:
			if (ImageUtils.sImageItems.contains(imageItem))
			{
				ImageUtils.sImageItems.remove(imageItem);
				iv_check.setSelected(false);
				if (ImageUtils.sImageItems.size() == 0)
				{
					rightBtn2.setEnabled(false);
				}
			}
			else
			{
				if (ImageUtils.sImageItems.size() >= limit)
				{
					Toast.makeText(getBaseContext(), getString(R.string.zone_choose_pic_num, limit), Toast.LENGTH_SHORT).show();
					return;
				}
				ImageUtils.sImageItems.add(imageItem);
				iv_check.setSelected(true);
				if (ImageUtils.sImageItems.size() > 0)
				{
					rightBtn2.setEnabled(true);
				}
			}
			rightBtn2.setText("完成(" + ImageUtils.sImageItems.size() + "/" + limit + ")");
			break;

		default:
			break;
		}
	}
}
