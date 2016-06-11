package com.hotbitmapgg.geekcommunity.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.alumnus.utils.ImgView;
import com.hotbitmapgg.geekcommunity.alumnus.utils.Utils;
import com.hotbitmapgg.geekcommunity.bean.ImagePackage;
import com.hotbitmapgg.geekcommunity.utils.ImageLoadUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class AlumnusPhotoGridAdapter extends BaseAdapter
{

	static class ViewHolder
	{
		LinearLayout lyt;
		ImageView img;
	}

	List<String> infos = new ArrayList<String>();
	List<String> burls = new ArrayList<String>();
	Context context;
	boolean isformnet = true;

	com.hotbitmapgg.geekcommunity.alumnus.utils.ImageLoader imgload;

	public AlumnusPhotoGridAdapter()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public AlumnusPhotoGridAdapter(Context context, List<String> infos, List<String> burls, boolean isformnet)
	{
		super();
		this.context = context;
		this.infos = infos;
		this.isformnet = isformnet;
		this.burls = burls;

		imgload = new com.hotbitmapgg.geekcommunity.alumnus.utils.ImageLoader(context);

	}

	public int getCount()
	{

		return infos.size();
	}

	public Object getItem(int position)
	{

		return null;
	}

	public long getItemId(int position)
	{

		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		// TODO Auto-generated method stub
		ViewHolder viewholder;

		if (convertView == null)
		{
			viewholder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.archives_item_picture_grid_item, null);
			viewholder.lyt = (LinearLayout) convertView.findViewById(R.id.lyt_item);

			float offset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8 + 8 + 11 + 11 + 1 * 6, context.getResources().getDisplayMetrics());

			// viewholder.lyt.setLayoutParams(newLinearLayout.LayoutParams((int)((ImgView.width - offset) /3),(int)((ImgView.width - offset)/ 3)));
			viewholder.lyt.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) ((ImgView.width - offset) / 3)));
			viewholder.img = (ImageView) convertView.findViewById(R.id.img_item);

			convertView.setTag(viewholder);
		}
		else
		{
			viewholder = (ViewHolder) convertView.getTag();
		}
		viewholder.img.setTag(position);
		viewholder.img.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View view)
			{
				// TODO Auto-generated method stub
				if (isformnet == true)
				{
					return;
				}

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

		try
		{
			String url = infos.get(position);
			Log.i("ddd", url);
			try
			{
				url = java.net.URLDecoder.decode(url, "UTF-8");
			} catch (UnsupportedEncodingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (!TextUtils.isEmpty(url))
			{
				if (!isformnet)
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
					ImageLoader.getInstance().displayImage(url, viewholder.img, options);
				}
				else
				{
					// Bitmap bm = BitmapFactory.decodeFile(url);
					// viewholder.img.setImageBitmap(bm);
					com.hotbitmapgg.geekcommunity.alumuns.imageutils.ImageLoader.getInstance().loadImage(url, viewholder.img, false);
				}
			}
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("eee", "ImgView " + e.toString());
		}
		return convertView;
	}

}