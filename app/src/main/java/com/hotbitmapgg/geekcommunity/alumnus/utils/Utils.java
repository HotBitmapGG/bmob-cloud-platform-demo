package com.hotbitmapgg.geekcommunity.alumnus.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;

import com.hotbitmapgg.geekcommunity.activity.SlidePreviewImageActivity;
import com.hotbitmapgg.geekcommunity.bean.ImagePackage;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;


public class Utils
{
	public static void CopyStream(InputStream is, OutputStream os)
	{
		final int buffer_size = 1024;
		try
		{
			byte[] bytes = new byte[buffer_size];
			for (;;)
			{
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
			is.close();
			os.close();
		} catch (Exception ex)
		{
		}
	}

	/**
	 * 根据视频url获取制定宽高的视频缩略图
	 *
	 * @param url
	 * @param width
	 * @param height
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public static Bitmap createVideoThumbnail(String url, int width, int height)
	{
		Bitmap bitmap = null;
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		int kind = MediaStore.Video.Thumbnails.MICRO_KIND;
		try
		{
			if (Build.VERSION.SDK_INT >= 14)
			{
				retriever.setDataSource(url, new HashMap<String, String>());
			}
			else
			{
				retriever.setDataSource(url);
			}
			bitmap = retriever.getFrameAtTime(1);
		} catch (IllegalArgumentException ex)
		{
			// Assume this is a corrupt video file
		} catch (RuntimeException ex)
		{
			// Assume this is a corrupt video file.
		} finally
		{
			try
			{
				retriever.release();
			} catch (RuntimeException ex)
			{
				// Ignore failures while cleaning up.
			}
		}
		if (kind == Images.Thumbnails.MICRO_KIND && bitmap != null)
		{
			bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		}
		return bitmap;
	}

	/**
	 * 根据视频url获取视频缩略图
	 *
	 * @param url
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public static Bitmap createVideoThumbnail(String url)
	{
		Bitmap bitmap = null;
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		try
		{
			if (Build.VERSION.SDK_INT >= 14)
			{
				retriever.setDataSource(url, new HashMap<String, String>());
			}
			else
			{
				retriever.setDataSource(url);
			}
			bitmap = retriever.getFrameAtTime(1);
		} catch (IllegalArgumentException ex)
		{
			// Assume this is a corrupt video file
		} catch (RuntimeException ex)
		{
			// Assume this is a corrupt video file.
		} finally
		{
			try
			{
				retriever.release();
			} catch (RuntimeException ex)
			{
				// Ignore failures while cleaning up.
			}
		}
		return bitmap;
	}

	/**
	 * 预览图片
	 *
	 * @param imagePackage
	 * @param index
	 */
	public static void openPic(Context context, ImagePackage imagePackage, int index) {
		Intent intent = new Intent(context, SlidePreviewImageActivity.class);
		intent.putExtra("imagePackage", imagePackage);
		intent.putExtra("index", index);
		context.startActivity(intent);
	}
}