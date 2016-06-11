package com.hotbitmapgg.geekcommunity.pick;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hotbitmapgg.geekcommunity.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class GalleryAdapter extends BaseAdapter
{

	public interface OnItemCheckListener
	{
		public void onCheck(int position, ImageItem imageItem);
	}

	private OnItemCheckListener mOnItemCheckListener;

	public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener)
	{
		mOnItemCheckListener = onItemCheckListener;
	}

	private Context mContext;
	private LayoutInflater infalter;
	private ArrayList<ImageItem> data = new ArrayList<ImageItem>();
	private ImageLoader mImageLoader;
	private boolean isActionMultiplePick;

	public GalleryAdapter(Context c, ImageLoader imageLoader)
	{
		infalter = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = c;
		mImageLoader = imageLoader;
		clearCache();
	}

	@Override
	public int getCount()
	{
		return data.size();
	}

	@Override
	public ImageItem getItem(int position)
	{
		return data.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	public void setMultiplePick(boolean isMultiplePick)
	{
		this.isActionMultiplePick = isMultiplePick;
	}

	public void selectAll(boolean selection)
	{
		for (int i = 0; i < data.size(); i++)
		{
			data.get(i).setSelected(selection);

		}
		notifyDataSetChanged();
	}

	public boolean isAllSelected()
	{
		boolean isAllSelected = true;

		for (int i = 0; i < data.size(); i++)
		{
			if (!data.get(i).isSelected())
			{
				isAllSelected = false;
				break;
			}
		}

		return isAllSelected;
	}

	public boolean isAnySelected()
	{
		boolean isAnySelected = false;

		for (int i = 0; i < data.size(); i++)
		{
			if (data.get(i).isSelected())
			{
				isAnySelected = true;
				break;
			}
		}

		return isAnySelected;
	}

	public ArrayList<ImageItem> getSelected()
	{
		ArrayList<ImageItem> dataT = new ArrayList<ImageItem>();

		for (int i = 0; i < data.size(); i++)
		{
			if (data.get(i).isSelected())
			{
				dataT.add(data.get(i));
			}
		}

		return dataT;
	}

	public void addAll(ArrayList<ImageItem> files)
	{

		try
		{
			clearCache();
			this.data.clear();
			this.data.addAll(files);

		} catch (Exception e)
		{
			e.printStackTrace();
		}

		notifyDataSetChanged();
	}

	public void updateSelection(ImageItem imageItem)
	{
		for (int i = 0; i < getCount(); i++)
		{
			ImageItem ii = getItem(i);
			if (ii.equals(imageItem))
			{
				ii.setSelected(true);
			}
		}
	}

	public void changeSelection(int position)
	{
		if (data.get(position).isSelected())
		{
			data.get(position).setSelected(false);
		}
		else
		{
			data.get(position).setSelected(true);
		}
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{

		final ViewHolder holder;
		if (convertView == null)
		{

			convertView = infalter.inflate(R.layout.gallery_item, null);
			holder = new ViewHolder();
			holder.imgQueue = (ImageView) convertView.findViewById(R.id.imgQueue);

			holder.imgQueueMultiSelected = (ImageView) convertView.findViewById(R.id.imgQueueMultiSelected);
			holder.selectLL = (LinearLayout) convertView.findViewById(R.id.select_ll);

			if (isActionMultiplePick)
			{
				holder.imgQueueMultiSelected.setVisibility(View.VISIBLE);
				holder.selectLL.setVisibility(View.VISIBLE);
			}
			else
			{
				holder.imgQueueMultiSelected.setVisibility(View.GONE);
				holder.selectLL.setVisibility(View.GONE);
			}

			convertView.setTag(holder);

		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		holder.selectLL.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (null != mOnItemCheckListener)
				{
					mOnItemCheckListener.onCheck(position, data.get(position));
					if (!ImageUtils.sImageItems.contains(data.get(position)) && ImageUtils.sImageItems.size() >= CustomGalleryActivity.limit)
					{
						return;
					}
					data.get(position).setSelected(!data.get(position).isSelected());
					holder.imgQueueMultiSelected.setSelected(data.get(position).isSelected());
					if (data.get(position).isSelected())
					{
						holder.imgQueue.setColorFilter(Color.parseColor("#77000000"));
					}
					else
					{
						holder.imgQueue.setColorFilter(null);
					}
				}
			}
		});
		if (data.get(position).isSelected())
		{
			holder.imgQueue.setColorFilter(Color.parseColor("#77000000"));
		}
		else
		{
			holder.imgQueue.setColorFilter(null);
		}
		try
		{
			if (isActionMultiplePick)
			{
				holder.imgQueueMultiSelected.setSelected(data.get(position).isSelected());
			}
			holder.imgQueue.setTag(position);
			holder.imgQueue.setImageResource(R.drawable.no_media);
			GalleryImageViewAware aware = new GalleryImageViewAware(holder.imgQueue);
			String thumbPath = ImageUtils.getThumbPath(mContext, data.get(position).getId());
			if (!TextUtils.isEmpty(thumbPath))
			{
				ImageLoader.getInstance().displayImage("file:///" + thumbPath, holder.imgQueue);
			}
			else
			{
				ImageLoader.getInstance().displayImage("file:///" + data.get(position).getPath(), aware, ImageUtils.displayOptions());
			}
			// ImageUtils.displayThumb(mContext, data.get(position).getPath(),
			// new ImageSize(60, 60), holder.imgQueue);

		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return convertView;
	}

	public class ViewHolder
	{
		ImageView imgQueue;
		ImageView imgQueueMultiSelected;
		LinearLayout selectLL;
	}

	public void clearCache()
	{
		mImageLoader.clearMemoryCache();
	}

	public void clear()
	{
		data.clear();
		notifyDataSetChanged();
	}
}
