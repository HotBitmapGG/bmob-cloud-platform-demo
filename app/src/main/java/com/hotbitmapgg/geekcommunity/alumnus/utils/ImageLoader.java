package com.hotbitmapgg.geekcommunity.alumnus.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.hotbitmapgg.geekcommunity.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * 用于异步加载图片处理
 *
 */
public class ImageLoader
{

	MemoryCache memoryCache = new MemoryCache();
	FileCache fileCache;
	private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	// 线程池
	ExecutorService executorService;

	public ImageLoader()
	{

	}

	public ImageLoader(Context context)
	{
		if (issdcard())
		{
			fileCache = new FileCache(context);
		}
		executorService = Executors.newFixedThreadPool(5);

	}

	// 当进入listview时默认的图片，可换成你自己的默认图片
	final int stub_id = R.drawable.no_media;

	// 最主要的方法
	public void DisplayImage(String url, ImageView imageView)
	{
		// 先从内存缓存中查找
		// imageView.setImageResource(R.drawable.ic_launcher);
		try
		{
			imageViews.put(imageView, url);
			if (issdcard())
			{
				Bitmap bitmap = memoryCache.get(url);
				if (bitmap != null)

					imageView.setImageBitmap(bitmap);

				else
				{
					// 若没有的话则开启新线程加载图片
					queuePhoto(url, imageView);
					// imageView.setImageResource(stub_id);
				}
			}

			else
			{
				queuePhoto(url, imageView);
				// imageView.setImageResource(stub_id);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public interface ILoad
	{
		void loadSuccess(Bitmap bitmap);

		void loadFailure();
	}

	private ILoad loadListener;

	public void DisplayImage(String url, ImageView imageView, ILoad loadListener)
	{
		// 先从内存缓存中查找
		// imageView.setImageResource(R.drawable.ic_launcher);
		this.loadListener = loadListener;
		try
		{
			imageViews.put(imageView, url);
			if (issdcard())
			{
				Bitmap bitmap = memoryCache.get(url);
				if (bitmap != null)

					imageView.setImageBitmap(bitmap);

				else
				{
					// 若没有的话则开启新线程加载图片
					queuePhoto(url, imageView);
					// imageView.setImageResource(stub_id);
				}
			}

			else
			{
				queuePhoto(url, imageView);
				// imageView.setImageResource(stub_id);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * 判断SdCard是否存在
	 *
	 * @return
	 */
	public boolean issdcard()
	{

		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED))
		{//
			return true;
		}
		else
		{
			return false;
		}

	}

	private void queuePhoto(String url, ImageView imageView)
	{
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	public Bitmap getBitmap(String url)
	{

		try
		{
			if (!issdcard())
			{
				URL Url = new URL(url);
				HttpURLConnection urlconn = (HttpURLConnection) Url.openConnection();
				urlconn.setConnectTimeout(2 * 1000);
				urlconn.setReadTimeout(2 * 1000);
				InputStream in = urlconn.getInputStream();
				return BitmapFactory.decodeStream(in);
			}

			File f = fileCache.getFile(url);

			// 先从文件缓存中查找是否有
			Bitmap b = decodeFile(f);
			if (b != null)
				return b;

			// 最后从指定的url中下载图片
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			Utils.CopyStream(is, os);
			os.close();
			bitmap = decodeFile(f);
			return bitmap;
		} catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public static byte[] getPicData(String url)
	{

		try
		{
			Log.i("http", "imgloader " + url);
			// 最后从指定的url中下载图片
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();

			return getBytes(is);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public static byte[] getBytes(InputStream is) throws Exception
	{
		byte[] data = null;

		Collection chunks = new ArrayList();
		byte[] buffer = new byte[1024 * 1000];
		int read = -1;
		int size = 0;

		while ((read = is.read(buffer)) != -1)
		{
			if (read > 0)
			{
				byte[] chunk = new byte[read];
				System.arraycopy(buffer, 0, chunk, 0, read);
				chunks.add(chunk);
				size += chunk.length;
			}
		}

		if (size > 0)
		{
			ByteArrayOutputStream bos = null;
			try
			{
				bos = new ByteArrayOutputStream(size);
				for (Iterator itr = chunks.iterator(); itr.hasNext();)
				{
					byte[] chunk = (byte[]) itr.next();
					bos.write(chunk);
				}
				data = bos.toByteArray();
			} finally
			{
				if (bos != null)
				{
					bos.close();
				}
			}
		}
		return data;
	}

	// decode这个图片并且按比例缩放以减少内存消耗，虚拟机对每张图片的缓存大小也是有限制的
	public Bitmap decodeFile(File f)
	{
		try
		{
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 70;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true)
			{
				if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e)
		{
		}
		return null;
	}

	// Task for the queue
	private class PhotoToLoad
	{
		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i)
		{
			url = u;
			imageView = i;
		}
	}

	class PhotosLoader implements Runnable
	{
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad)
		{
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run()
		{
			if (imageViewReused(photoToLoad))
				return;
			Bitmap bmp = getBitmap(photoToLoad.url);

			if (issdcard())
			{
				memoryCache.put(photoToLoad.url, bmp);
			}

			if (imageViewReused(photoToLoad))
				return;
			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			// 更新的操作放在UI线程中
			Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	/**
	 * 防止图片错位
	 *
	 * @param photoToLoad
	 * @return
	 */
	boolean imageViewReused(PhotoToLoad photoToLoad)
	{
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	// 用于在UI线程中更新界面
	class BitmapDisplayer implements Runnable
	{
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p)
		{
			bitmap = b;
			photoToLoad = p;
		}

		public void run()
		{
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null)
			{
				if (loadListener == null)
					photoToLoad.imageView.setImageBitmap(bitmap);
				else
					loadListener.loadSuccess(bitmap);
			}
			else
			{
				if (loadListener != null)
					loadListener.loadFailure();
				// photoToLoad.imageView.setImageResource(stub_id);
			}
		}
	}

	public void clearCache()
	{
		memoryCache.clear();
		fileCache.clear();
	}
}