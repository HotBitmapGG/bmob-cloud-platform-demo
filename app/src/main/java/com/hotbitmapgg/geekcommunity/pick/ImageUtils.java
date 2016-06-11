package com.hotbitmapgg.geekcommunity.pick;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.hotbitmapgg.geekcommunity.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageUtils
{

	public static List<ImageItem> sImageItems = new ArrayList<ImageItem>();

	public static ArrayList<Album> getAlbums(Context context)
	{
		ArrayList<Album> albums = new ArrayList<Album>();

		// which image properties are we querying
		String[] projection = new String[] { MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATE_MODIFIED };

		Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		// Make the query.
		Cursor cur = context.getContentResolver().query(images, projection, // Which
																			// columns
																			// to
																			// return
				MediaStore.Images.Media.SIZE + ">=? and " + MediaStore.Images.Media.MIME_TYPE + " like 'image%'", // Which
																													// rows
																													// to
																													// return
																													// (all
																													// rows)
				new String[] { "9500" }, // Selection arguments (none)
				MediaStore.Images.Media.DATE_MODIFIED // Ordering
				);

		if (cur.moveToFirst())
		{
			int id = cur.getColumnIndex(MediaStore.Images.Media._ID);
			int bucketName = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
			int bucketId = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
			int dataIndex = cur.getColumnIndex(MediaStore.Images.Media.DATA);
			int imageName = cur.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
			int imageDate = cur.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED);

			do
			{
				/* create a new ImageItem object */
				String path = cur.getString(dataIndex);
				// 判断文件存不存在，不存在，就不要加进�?
				if (new File(path).exists())
				{
					ImageItem img = new ImageItem();
					img.setId(cur.getInt(id));
					img.setTitle(cur.getString(imageName));
					img.setSubtitle(cur.getString(imageDate));
					img.setPath(path);
					// img.setmThumbPath(getThumbPath(context, cur.getInt(id)));

					/* get the bucket id */
					String bucket_id = cur.getString(bucketId);

					/* check if there is a bucket for this image */
					boolean ok = false;
					for (int i = 0; i < albums.size(); i++)
					{
						if (albums.get(i).getId().equals(bucket_id))
						{
							/* found a bucket, add image */
							albums.get(i).addImage(img);
							ok = true;
							break;
						}
					}

					if (!ok)
					{ /* this image doesn't have a bucket yet */
						Album alb = new Album();
						alb.setId(bucket_id);
						alb.setName(cur.getString(bucketName));
						alb.addImage(img);
						albums.add(alb);
					}

				}
			} while (cur.moveToNext());
		}
		/* close the cursor */
		cur.close();
		/* return the albums list */
		return albums;
	}

	public static String getThumbPath(Context context, int id)
	{
		// which image properties are we querying
		String[] projection = new String[] { MediaStore.Images.Thumbnails.DATA };

		Uri thumbUri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
		// Make the query.
		Cursor cur = context.getContentResolver().query(thumbUri, projection, // Which
																				// columns
																				// to
																				// return
				MediaStore.Images.Thumbnails.IMAGE_ID + "=?", // Which rows to
																// return (all
																// rows)
				new String[] { "" + id }, // Selection arguments (none)
				null // Ordering
				);
		String path = "";
		if (cur.moveToFirst())
		{
			int data = cur.getColumnIndex(MediaStore.Images.Thumbnails.DATA);
			path = cur.getString(data);
		}
		/* close the cursor */
		cur.close();
		/* return the albums list */
		return path;
	}

	public static DisplayImageOptions displayOptions()
	{
		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
		builder.cacheInMemory(true);
		builder.cacheOnDisk(true);
		builder.considerExifParams(true);
		builder.resetViewBeforeLoading(false);
		builder.bitmapConfig(Bitmap.Config.RGB_565);
		return builder.build();
	}

	public static void displayThumb(Context context, String filePath, ImageSize size, final ImageView into)
	{
		ImageLoader.getInstance().loadImage("file:///" + filePath, size, displayOptions(), new SimpleImageLoadingListener()
		{

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason)
			{
				into.setImageResource(R.drawable.no_media);
				super.onLoadingFailed(imageUri, view, failReason);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
			{
				into.setImageBitmap(loadedImage);
				super.onLoadingComplete(imageUri, view, loadedImage);
			}

			@Override
			public void onLoadingStarted(String imageUri, View view)
			{
				into.setImageResource(R.drawable.no_media);
				super.onLoadingStarted(imageUri, view);
			}
		});
	}
}
