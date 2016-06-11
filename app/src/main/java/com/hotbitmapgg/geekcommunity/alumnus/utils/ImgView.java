package com.hotbitmapgg.geekcommunity.alumnus.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.adapter.AlumnusPhotoGridAdapter;
import com.hotbitmapgg.geekcommunity.bean.ImagePackage;
import com.hotbitmapgg.geekcommunity.utils.ImageLoadUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImgView
{

	public static Context mcontext;
	public static LinearLayout lytall;

	public static int width = 0;
	public static int height = 0;

	public static float DP300 = 300, DP400 = 400;

	static com.hotbitmapgg.geekcommunity.alumnus.utils.ImageLoader imgload;

	public ImgView()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public static View backview(final Context context, final List<String> urls, final List<String> burls, final List<String> purls)
	{

		imgload = new com.hotbitmapgg.geekcommunity.alumnus.utils.ImageLoader(context);

		WindowManager manager = ((Activity) context).getWindowManager();
		width = manager.getDefaultDisplay().getWidth();
		height = manager.getDefaultDisplay().getHeight() / 2;

		// 将dp 转为px
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, ((Activity) context).getResources().getDisplayMetrics());

		DP300 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, ((Activity) context).getResources().getDisplayMetrics());
		DP400 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, ((Activity) context).getResources().getDisplayMetrics());

		int fixed = 0;
		if (null == urls || urls.size() == 0)
		{
			return null;
		}

		List<String> images = new ArrayList<String>();

		if (urls.size() > 9)
		{
			images = urls.subList(0, 9);
		}
		else
		{
			images = urls;
		}
		lytall = new LinearLayout(context);
		lytall.setOrientation(LinearLayout.VERTICAL);

		for (int i = 0; i < burls.size(); i++)
		{
			if (burls.get(i) == null || burls.get(i).length() <= 0)
			{
				burls.set(i, purls.get(i));
			}
		}

		int count = images.size();
		// 单张图
		if (count == 1)
		{
			// ImageView imageview = new ImageView(context);
			// String url = images.get(0); // 取第一条

			/***** 0407s *****/
			String url = images.get(0); // 取第一条
			Log.i("test", url);
			try
			{
				url = java.net.URLDecoder.decode(url, "UTF-8");
			} catch (UnsupportedEncodingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int imgwidth = getImgWidthOrHeight(true, url);
			int imgheight = getImgWidthOrHeight(false, url);

			ImageView imageview = new ImageView(context);
			if (imgwidth == 0 || imgheight == 0)
			{
				imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);

				imageview.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
				imageview.setLayoutParams(new LinearLayout.LayoutParams((int) DP300, (int) DP300));

				Log.i("uuu", width / 3 + "##0##" + width / 3);

				lytall.setLayoutParams(new LayoutParams((int) DP300, (int) DP300));
				lytall.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
			}
			else if (imgwidth >= DP400 && imgheight > imgwidth)
			{
				imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
				// imageview.setAdjustViewBounds(true);
				imageview.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));

				int h = (int) (imgheight * (DP300 / (float) imgwidth));
				imageview.setLayoutParams(new LinearLayout.LayoutParams((int) DP300, h));

				Log.i("uuu", 400 + "##1##" + h);

				lytall.setLayoutParams(new LayoutParams((int) DP300, h));
				lytall.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
			}
			else if (imgwidth >= DP400 && imgheight < imgwidth)
			{
				imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
				// imageview.setAdjustViewBounds(true);
				imageview.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
				int h = (int) (imgheight * (DP400 / (float) imgwidth));
				imageview.setLayoutParams(new LinearLayout.LayoutParams((int) DP400, h));

				Log.i("uuu", 400 + "##2##" + h);

				lytall.setLayoutParams(new LayoutParams((int) DP400, h));
				lytall.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
			}
			else if (imgheight >= DP300 && imgheight < imgwidth)
			{

				imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
				// imageview.setAdjustViewBounds(true);
				imageview.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
				// imageview.setLayoutParams(new
				// LinearLayout.LayoutParams(300,400));

				int w = (int) (imgwidth * (DP300 / (float) imgheight));
				imageview.setLayoutParams(new LinearLayout.LayoutParams(w, (int) DP400));

				Log.i("uuu", w + "##3##" + 300);

				lytall.setLayoutParams(new LayoutParams(w, (int) DP400));
				lytall.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
			}
			else if (imgheight >= DP300 && imgheight > imgwidth)
			{

				imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
				// imageview.setAdjustViewBounds(true);
				imageview.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
				// imageview.setLayoutParams(new
				// LinearLayout.LayoutParams(300,400));

				int h = (int) (imgheight * (DP300 / (float) imgwidth));
				imageview.setLayoutParams(new LinearLayout.LayoutParams((int) DP300, h));

				Log.i("uuu", "##4##" + 300);

				lytall.setLayoutParams(new LayoutParams((int) DP300, h));
				lytall.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
			}
			else if (imgwidth < DP400 || imgheight < DP300)
			{

				Log.i("uuu", "##5##");

				imageview.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));

				if (imgwidth > imgheight)
				{
					int h = (int) (imgheight * (DP400 / (float) imgwidth));
					imageview.setLayoutParams(new LinearLayout.LayoutParams((int) DP400, h));
					imageview.setScaleType(ImageView.ScaleType.FIT_XY);
					imageview.setAdjustViewBounds(true);
					lytall.setLayoutParams(new LayoutParams((int) DP400, LinearLayout.LayoutParams.WRAP_CONTENT));
					lytall.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
				}
				else
				{
					int w = (int) (imgwidth * (DP300 / (float) imgheight));
					imageview.setLayoutParams(new LinearLayout.LayoutParams(w, (int) DP300));
					imageview.setScaleType(ImageView.ScaleType.FIT_XY);
					imageview.setAdjustViewBounds(true);
					lytall.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (int) DP300));
					lytall.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
				}

			}
			else
			{
				imageview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				imageview.setAdjustViewBounds(true);
				imageview.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
				imageview.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

				Log.i("uuu", imgwidth + "##6###" + imgheight);
				lytall.setLayoutParams(new LayoutParams(imgwidth, imgheight));
				lytall.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
			}

			if (!TextUtils.isEmpty(url))
			{
				if (!url.startsWith("http"))
				{
					url = "file://" + url;
					ImageLoader.getInstance().displayImage(url, imageview, ImageLoadUtil.defaultOptions());
				}
				else
				{
					DisplayImageOptions options;
					if (url.contains(".webp"))
					{
						options = ImageLoadUtil.defaultOptions();
					}
					else
					{
						options = ImageLoadUtil.defaultOptions();
					}
					ImageLoader.getInstance().displayImage(url, imageview, options);
				}
			}

			imageview.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View view)
				{

					ImagePackage imagePackage = new ImagePackage();
					imagePackage.setUrls(burls);
					Utils.openPic(context, imagePackage, 0);
				}
			});

			lytall.addView(imageview);
			return lytall;

		}

		// 多张图 用 gridview
		if (count > 1)
		{
			View view1 = ((Activity) context).getLayoutInflater().inflate(R.layout.archives_item_picture_grid, null);
			MGridView gv = (MGridView) view1.findViewById(R.id.gv);
			// 加载页面
			gv.setAdapter(new AlumnusPhotoGridAdapter(context, images, burls, false));

			return view1;
		}

		// return lytall;
		return null;
	}

	public static View backview(final Context context, final List<String> urls, final List<String> burls)
	{

		WindowManager manager = ((Activity) context).getWindowManager();
		width = manager.getDefaultDisplay().getWidth();
		height = manager.getDefaultDisplay().getHeight() / 2;

		// 将dp 转为px
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, ((Activity) context).getResources().getDisplayMetrics());

		int fixed = 0;
		if (null == urls || urls.size() == 0)
		{
			return null;
		}

		List<String> images = new ArrayList<String>();

		if (urls.size() > 9)
		{
			images = urls.subList(0, 9);
		}
		else
		{
			images = urls;
		}
		lytall = new LinearLayout(context);
		lytall.setOrientation(LinearLayout.VERTICAL);

		int count = images.size();
		// 单张图
		if (count == 1)
		{
			// ImageView imageview = new ImageView(context);
			// String url = images.get(0); // 取第一条

			/***** 0407s *****/
			String url = images.get(0); // 取第一条
			Log.i("test", url);
			try
			{
				url = java.net.URLDecoder.decode(url, "UTF-8");
			} catch (UnsupportedEncodingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int imgwidth = getImgWidthOrHeight(true, url);
			int imgheight = getImgWidthOrHeight(false, url);
			// 从这儿提取图片宽高 与 屏幕宽高做对比
			// #e0e0e0
			ImageView imageview = new ImageView(context);
			if (imgwidth == 0 || imgheight == 0)
			{
				imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
				// imageview.setAdjustViewBounds(true);
				imageview.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
				imageview.setLayoutParams(new LinearLayout.LayoutParams(width / 3, width / 3));

				Log.i("uuu", width / 3 + "##0##" + width / 3);

				lytall.setLayoutParams(new LayoutParams(width / 3, width / 3));
				lytall.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
			}
			else if (imgwidth >= 400 && imgheight > imgwidth)
			{
				imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
				// imageview.setAdjustViewBounds(true);
				imageview.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));

				int h = (int) (imgheight * (400.0 / (float) imgwidth));
				imageview.setLayoutParams(new LinearLayout.LayoutParams(400, h));

				Log.i("uuu", 400 + "##1##" + h);

				lytall.setLayoutParams(new LayoutParams(400, h));
				lytall.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
			}
			else if (imgwidth >= 400 && imgheight < imgwidth)
			{
				imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
				// imageview.setAdjustViewBounds(true);
				imageview.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
				int h = (int) (imgheight * (400.0 / (float) imgwidth));
				imageview.setLayoutParams(new LinearLayout.LayoutParams(400, h));

				Log.i("uuu", 400 + "##2##" + h);

				lytall.setLayoutParams(new LayoutParams(400, h));
				lytall.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
			}
			else if (imgheight >= 400 && imgheight < imgwidth)
			{

				imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
				// imageview.setAdjustViewBounds(true);
				imageview.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
				// imageview.setLayoutParams(new
				// LinearLayout.LayoutParams(300,400));

				int w = (int) (imgwidth * (400.0 / (float) imgheight));
				imageview.setLayoutParams(new LinearLayout.LayoutParams(w, 400));

				Log.i("uuu", w + "##3##" + 300);

				lytall.setLayoutParams(new LayoutParams(w, 400));
				lytall.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
			}
			else if (imgheight >= 400 && imgheight > imgwidth)
			{

				imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
				// imageview.setAdjustViewBounds(true);
				imageview.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
				// imageview.setLayoutParams(new
				// LinearLayout.LayoutParams(300,400));

				int h = (int) (imgheight * (400.0 / (float) imgwidth));
				imageview.setLayoutParams(new LinearLayout.LayoutParams(400, h));

				Log.i("uuu", "##4##" + 300);

				lytall.setLayoutParams(new LayoutParams(400, h));
				lytall.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
			}
			else if (imgwidth < 400 || imgheight < 400)
			{

				Log.i("uuu", "##5##");

				imageview.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));

				if (imgwidth < imgheight)
				{
					int h = (int) (imgheight * ((float) 400.0 / (float) imgwidth));
					imageview.setLayoutParams(new LinearLayout.LayoutParams(400, h));
					imageview.setScaleType(ImageView.ScaleType.FIT_XY);
					imageview.setAdjustViewBounds(true);
					lytall.setLayoutParams(new LayoutParams(400, LinearLayout.LayoutParams.WRAP_CONTENT));
					lytall.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
				}
				else
				{
					int w = (int) (imgwidth * ((float) 400.0 / (float) imgheight));
					imageview.setLayoutParams(new LinearLayout.LayoutParams(w, 400));
					imageview.setScaleType(ImageView.ScaleType.FIT_XY);
					imageview.setAdjustViewBounds(true);
					lytall.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 400));
					lytall.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
				}

			}
			else
			{
				imageview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				imageview.setAdjustViewBounds(true);
				imageview.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
				imageview.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

				Log.i("uuu", imgwidth + "##6###" + imgheight);
				lytall.setLayoutParams(new LayoutParams(imgwidth, imgheight));
				lytall.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
			}

			/***** 0407e *****/

			if (!TextUtils.isEmpty(url))
			{
				if (!url.startsWith("http"))
				{
					url = "file://" + url;
					ImageLoader.getInstance().displayImage(url, imageview, ImageLoadUtil.defaultOptions());
				}
				else
				{
					DisplayImageOptions options;
					if (url.contains(".webp"))
					{
						options = ImageLoadUtil.defaultOptions();
					}
					else
					{
						options = ImageLoadUtil.defaultOptions();
					}
					ImageLoader.getInstance().displayImage(url, imageview, options);
				}
			}

			// if (!TextUtils.isEmpty(url)) {
			// if(url.contains(".png")){
			// // imgload.DisplayImage(url, imageview);
			// ImageLoader.getInstance().displayImage(url, imageview,
			// ArchivesUtil.pngOptions());
			// }
			// else
			// if (!url.startsWith("http")) {
			// url = "file://" + url;
			// ImageLoader.getInstance().displayImage(url, imageview,
			// ArchivesUtil.defaultOptions(false));
			// } else {
			// ImageLoader.getInstance().displayImage(url, imageview,
			// ArchivesUtil.defaultOptions(true));
			// }
			// }

			imageview.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					// TODO Auto-generated method stub
					// Gson gson = new Gson();
					// Intent intent = new Intent(context,
					// ArchivesImagePraiseActivity.class);
					// ArchivesShow archivesShow = new ArchivesShow();
					// archivesShow.setPicUrl(gson.toJson(burls));
					// intent.putExtra("index", (Integer) arg0.getTag());
					// intent.putExtra(ArchivesShow.class.getSimpleName(),
					// archivesShow);
					// context.startActivity(intent);

					ImagePackage imagePackage = new ImagePackage();
					imagePackage.setUrls(burls);
					Utils.openPic(context, imagePackage, (Integer) view.getTag());
				}
			});

			lytall.addView(imageview);
			return lytall;

			// return imageview;
		}

		// 多张图 用 gridview
		if (count > 1)
		{
			View view1 = ((Activity) context).getLayoutInflater().inflate(R.layout.archives_item_picture_grid, null);
			MGridView gv = (MGridView) view1.findViewById(R.id.gv);
			// 加载页面
			// gv.setAdapter(new Myadapter(context, images,burls, false));
			gv.setAdapter(new AlumnusPhotoGridAdapter(context, images, burls, false));
			// lytall.addView(view1);
			return view1;
		}

		// return lytall;
		return null;
	}

	private static int getImgWidthOrHeight(boolean isw, String url)
	{
		Log.i("uuu", url);
		Pattern p = null;
		if (isw)
		{
			p = Pattern.compile("width=(.*?)&height");
		}
		else
		{
			p = Pattern.compile("height=(.*)");
		}
		Matcher m = p.matcher(url);

		String result = "0";
		while (m.find())
		{
			result = m.group(1);
		}
		Log.i("uuu", "result" + result);
		try
		{
			return (int) Double.parseDouble(result);
		} catch (NumberFormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	public static View backview(final Context context, final String picturesPath)
	{

		imgload = new com.hotbitmapgg.geekcommunity.alumnus.utils.ImageLoader(context);

		final List<String> urls = new ArrayList<String>();
		// String pics[] = picturesPath.split("#@@#");
		//
		// for (String string : pics)
		// {
		// urls.add(string);
		// }
		urls.add(picturesPath);

		WindowManager manager = ((Activity) context).getWindowManager();
		width = manager.getDefaultDisplay().getWidth();
		height = manager.getDefaultDisplay().getHeight() / 2;

		// 将dp 转为px
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, ((Activity) context).getResources().getDisplayMetrics());

		DP300 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, ((Activity) context).getResources().getDisplayMetrics());
		DP400 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, ((Activity) context).getResources().getDisplayMetrics());

		int fixed = 0;
		if (null == urls || urls.size() == 0)
		{
			return null;
		}

		List<String> images = new ArrayList<String>();

		if (urls.size() > 9)
		{
			images = urls.subList(0, 9);
		}
		else
		{
			images = urls;
		}
		lytall = new LinearLayout(context);
		lytall.setOrientation(LinearLayout.VERTICAL);

		int count = images.size();
		// 单张图
		if (count == 1)
		{

			String url = images.get(0); // 取第一条

			int imgwidth = getImgWidthOrHeight(true, url);
			int imgheight = getImgWidthOrHeight(false, url);

			// #e0e0e0
			ImageView imageview = new ImageView(context);
			if (imgwidth == 0 || imgheight == 0)
			{
				imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
				// imageview.setAdjustViewBounds(true);
				imageview.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
				imageview.setLayoutParams(new LinearLayout.LayoutParams((int) DP300, (int) DP300));

				Log.i("uuu", width / 3 + "##0##" + width / 3);

				lytall.setLayoutParams(new LayoutParams((int) DP300, (int) DP300));
				lytall.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
			}
			else if (imgwidth >= DP400 && imgheight > imgwidth)
			{
				imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
				// imageview.setAdjustViewBounds(true);
				imageview.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));

				int h = (int) (imgheight * (DP300 / (float) imgwidth));
				imageview.setLayoutParams(new LinearLayout.LayoutParams((int) DP300, h));

				Log.i("uuu", 400 + "##1##" + h);

				lytall.setLayoutParams(new LayoutParams((int) DP300, h));
				lytall.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
			}
			else if (imgwidth >= DP400 && imgheight < imgwidth)
			{
				imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
				// imageview.setAdjustViewBounds(true);
				imageview.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
				int h = (int) (imgheight * (DP400 / (float) imgwidth));
				imageview.setLayoutParams(new LinearLayout.LayoutParams((int) DP400, h));

				Log.i("uuu", 400 + "##2##" + h);

				lytall.setLayoutParams(new LayoutParams((int) DP400, h));
				lytall.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
			}
			else if (imgheight >= DP300 && imgheight < imgwidth)
			{

				imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
				// imageview.setAdjustViewBounds(true);
				imageview.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
				// imageview.setLayoutParams(new
				// LinearLayout.LayoutParams(300,400));

				int w = (int) (imgwidth * (DP300 / (float) imgheight));
				imageview.setLayoutParams(new LinearLayout.LayoutParams(w, (int) DP400));

				Log.i("uuu", w + "##3##" + 300);

				lytall.setLayoutParams(new LayoutParams(w, (int) DP400));
				lytall.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
			}
			else if (imgheight >= DP300 && imgheight > imgwidth)
			{

				imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
				// imageview.setAdjustViewBounds(true);
				imageview.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
				// imageview.setLayoutParams(new
				// LinearLayout.LayoutParams(300,400));

				int h = (int) (imgheight * (DP300 / (float) imgwidth));
				imageview.setLayoutParams(new LinearLayout.LayoutParams((int) DP300, h));

				Log.i("uuu", "##4##" + 300);

				lytall.setLayoutParams(new LayoutParams((int) DP300, h));
				lytall.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
			}
			else if (imgwidth < DP400 || imgheight < DP300)
			{

				Log.i("uuu", "##5##");

				imageview.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));

				if (imgwidth > imgheight)
				{
					int h = (int) (imgheight * (DP400 / (float) imgwidth));
					imageview.setLayoutParams(new LinearLayout.LayoutParams((int) DP400, h));
					imageview.setScaleType(ImageView.ScaleType.FIT_XY);
					imageview.setAdjustViewBounds(true);
					lytall.setLayoutParams(new LayoutParams((int) DP400, LinearLayout.LayoutParams.WRAP_CONTENT));
					lytall.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
				}
				else
				{
					int w = (int) (imgwidth * (DP300 / (float) imgheight));
					imageview.setLayoutParams(new LinearLayout.LayoutParams(w, (int) DP300));
					imageview.setScaleType(ImageView.ScaleType.FIT_XY);
					imageview.setAdjustViewBounds(true);
					lytall.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (int) DP300));
					lytall.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
				}

			}
			else
			{
				imageview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				imageview.setAdjustViewBounds(true);
				imageview.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
				imageview.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

				Log.i("uuu", imgwidth + "##6###" + imgheight);
				lytall.setLayoutParams(new LayoutParams(imgwidth, imgheight));
				lytall.setBackgroundColor(android.graphics.Color.parseColor("#e0e0e0"));
			}

			if (!TextUtils.isEmpty(url))
			{
				Bitmap bm = BitmapFactory.decodeFile(url);
				imageview.setImageBitmap(bm);
			}

			// imageview.setOnClickListener(new OnClickListener() {
			// @Override
			// public void onClick(View arg0) {
			// TODO Auto-generated method stub
			// Gson gson = new Gson();
			// Intent intent = new
			// Intent(context,ArchivesImagePraiseActivity.class);
			// ArchivesShow archivesShow = new ArchivesShow();
			// archivesShow.setPicUrl(gson.toJson(urls.get(0)));
			// intent.putExtra("index", (Integer) arg0.getTag());
			// intent.putExtra(ArchivesShow.class.getSimpleName(),archivesShow);
			// context.startActivity(intent);
			// }
			// });

			lytall.addView(imageview);
			return lytall;

			// return imageview;
		}

		// 多张图 用 gridview
		if (count > 1)
		{
			View view1 = ((Activity) context).getLayoutInflater().inflate(R.layout.archives_item_picture_grid, null);
			MGridView gv = (MGridView) view1.findViewById(R.id.gv);
			// 加载页面
			gv.setAdapter(new AlumnusPhotoGridAdapter(context, images, null, true));
			// lytall.addView(view1);
			return view1;
		}
		// return lytall;
		return null;
	}
}

// class ViewHolder{
// LinearLayout lyt;
// ImageView img;
// }
//
// class Myadapter extends BaseAdapter {
//
// List<String> infos = new ArrayList<String>();
// List<String> burls = new ArrayList<String>();
// Context context;
// boolean isWebp = false;
//
// public Myadapter() {
// super();
// // TODO Auto-generated constructor stub
// }
//
// public Myadapter(Context context, List<String> infos, List<String>
// burls,boolean iswebp) {
// super();
// this.context = context;
// this.infos = infos;
// this.isWebp = iswebp;
// this.burls=burls;
// }
//
// public int getCount() {
// // TODO Auto-generated method stub
// // return bms.size();
// return infos.size();
// }
//
// public Object getItem(int arg0) {
// // TODO Auto-generated method stub
// Log.i("li", arg0 + "bbbbb");
// return null;
// }
//
// public long getItemId(int arg0) {
// // TODO Auto-generated method stub
// // item = arg0;
// Log.i("li", arg0 + "aaaa");
// return arg0;
// }
//
// @Override
// public View getView(int position, View convertView, ViewGroup parent) {
// // TODO Auto-generated method stub
// // ImageView img = new ImageView(context);
// // img.setScaleType(ImageView.ScaleType.CENTER_CROP);
// // img.setPadding(10,0,10,0);
// // img.setLayoutParams(new GridView.LayoutParams((ImgView.width - 40) /
// // 3,(ImgView.width - 40)/ 3));
// // img.setTag(position);
// //
// // // RelativeLayout.LayoutParams lp = new
// // RelativeLayout.LayoutParams(img.getLayoutParams());
// // // lp.setMargins(10, 0, 10, 0);
// // // img.setLayoutParams(lp);
// //
// // // img.setLayoutParams(new GridView.LayoutParams((w - 100) / 7,(w -
// // 100)/ 7));
// // // // img.setImageBitmap(bms.get(arg0));
// // // try {
// // // if (infos.get(position) != null) {
// // //
// // img.setImageBitmap(imgeloader.getBitmap(userinfos.get(arg0).getImgurl()));
// // // }
// // // } catch (Exception e) {
// // //
// // img.setImageDrawable(getResources().getDrawable(R.drawable.wall_ac_07));
// // // }
// //
// // img.setOnClickListener(new OnClickListener() {
// //
// // @Override
// // public void onClick(View arg0) {
// // // TODO Auto-generated method stub
// // Gson gson = new Gson();
// // Intent intent = new Intent(context,
// // ArchivesImagePraiseActivity.class);
// // ArchivesShow archivesShow = new ArchivesShow();
// // archivesShow.setPicUrl(gson.toJson(infos));
// // if (isWebp) {
// // archivesShow.setWebpUrl(gson.toJson(infos));
// // }
// // intent.putExtra("index", (int)arg0.getTag());
// // intent.putExtra(ArchivesShow.class.getSimpleName(), archivesShow);
// // context.startActivity(intent);
// //
// // }
// // });
// //
// // String url = infos.get(position); //取第一条
// // // Log.i("aaa", url);
// // if (!TextUtils.isEmpty(url)) {
// // if (!url.startsWith("http")) {
// // url = "file://"+url;
// //
// ImageLoader.getInstance().displayImage(url,img,ArchivesUtil.defaultOptions(false));
// // }else {
// // ImageLoader.getInstance().displayImage(url,
// // img,ArchivesUtil.defaultOptions(isWebp));
// // }
// // }
// //
// //
// // return img;
//
//
// ViewHolder viewholder = new ViewHolder();
//
// if(convertView==null){
// viewholder = new ViewHolder();
// convertView =
// LayoutInflater.from(context).inflate(R.layout.archives_item_picture_grid_item,
// null);
// viewholder.lyt=(LinearLayout) convertView.findViewById(R.id.lyt_item);
//
// float
// offset=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8+8+11+11+1*6,
// context.getResources().getDisplayMetrics());
//
// // viewholder.lyt.setLayoutParams(new
// LinearLayout.LayoutParams((int)((ImgView.width - offset)
// /3),(int)((ImgView.width - offset)/ 3)));
// viewholder.lyt.setLayoutParams(new
// LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,(int)((ImgView.width -
// offset)/ 3)));
// viewholder.img = (ImageView) convertView.findViewById(R.id.img_item);
//
// convertView.setTag(viewholder);
// }else{
// viewholder = (ViewHolder) convertView.getTag();
//
// }
// viewholder.img .setTag(position);
// viewholder.img .setOnClickListener(new OnClickListener() {
//
// @Override
// public void onClick(View arg0) {
// // TODO Auto-generated method stub
// Gson gson = new Gson();
// Intent intent = new Intent(context,
// ArchivesImagePraiseActivity.class);
// ArchivesShow archivesShow = new ArchivesShow();
// archivesShow.setPicUrl(gson.toJson(burls));
// // if (isWebp) {
// // archivesShow.setWebpUrl(gson.toJson(burls));
// // }
// intent.putExtra("index", (Integer) arg0.getTag());
// intent.putExtra(ArchivesShow.class.getSimpleName(),archivesShow);
// context.startActivity(intent);
//
// }
// });
//
//
// try {
// String url = infos.get(position); // 取第一条
// // Log.i("aaa", url);
//
// if (!TextUtils.isEmpty(url)) {
// if (!url.startsWith("http")) {
// url = "file://" + url;
// ImageLoader.getInstance().displayImage(url, viewholder.img ,
// ArchivesUtil.defaultOptions(false));
// } else {
// ImageLoader.getInstance().displayImage(url, viewholder.img ,
// ArchivesUtil.defaultOptions(isWebp));
// }
// }
// } catch (Exception e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// Log.e("eee", "ImgView "+e.toString());
// }
//
// return convertView;
//
// }
//
// }

