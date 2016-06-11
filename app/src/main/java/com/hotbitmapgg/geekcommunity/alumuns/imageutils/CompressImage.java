package com.hotbitmapgg.geekcommunity.alumuns.imageutils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Environment;

public class CompressImage
{
	@SuppressLint("NewApi")
	private static Bitmap scalePic(Bitmap cameraBitmap)
	{
		final int IMAGE_MAX_SIZE = 1280;
		Bitmap bitmap = null;
		if (cameraBitmap.getWidth() > IMAGE_MAX_SIZE || cameraBitmap.getHeight() > IMAGE_MAX_SIZE)
		{
			float scale = 0;
			if (cameraBitmap.getWidth() > cameraBitmap.getHeight())
			{
				scale = (float) IMAGE_MAX_SIZE / (float) cameraBitmap.getWidth();
			}
			else
			{
				scale = (float) IMAGE_MAX_SIZE / (float) cameraBitmap.getHeight();
			}
			Matrix matrix = new Matrix();
			matrix.postScale(scale, scale);
			bitmap = Bitmap.createBitmap(cameraBitmap, 0, 0, cameraBitmap.getWidth(), cameraBitmap.getHeight(), matrix, true);

			if (bitmap.getWidth() > IMAGE_MAX_SIZE || bitmap.getHeight() > IMAGE_MAX_SIZE)
			{
				scalePic(bitmap);
			}
			return bitmap;
		}
		return cameraBitmap;
	}

	@SuppressLint("NewApi")
	public static ArrayList<String> compressedImagesPaths(List<String> photos)
	{
		ArrayList<String> compressedImagesPaths = new ArrayList<String>();
		// 对给定路径的图片进行压缩
		File file;
		Bitmap bitmap = null, compressedBitmap = null;
		FileOutputStream fos = null;
		for (int i = 0; i < photos.size(); i++)
		{
			Options options = new Options();
			options.inPreferredConfig = Bitmap.Config.RGB_565; // 默认是Bitmap.Config.ARGB_8888
			/* 下面两个字段需要组合使用 */
			options.inPurgeable = true;
			options.inInputShareable = true;
			int times = 1;
			int inSampleSize = 1;
			options.inSampleSize = inSampleSize;

			while (bitmap == null)
			{
				// 如果bitmap解析时遇到OOM，则降低inSampleSize循环解析，最多解析3次，inSampleSize最大为8,3次解析不成功则放弃这张图片的解析
				if (times > 3)
				{
					break;
				}
				try
				{
					bitmap = BitmapFactory.decodeFile(photos.get(i), options);
				} catch (OutOfMemoryError e)
				{

				}
				times++;
				inSampleSize = inSampleSize * 2;
				options.inSampleSize = inSampleSize;
			}

			if (bitmap == null)
			{
				continue;
			}

			int detree = readPictureDegree(photos.get(i));
			bitmap = rotaingImageView(detree, bitmap);

			compressedBitmap = scalePic(bitmap);
			if (compressedBitmap != bitmap)
			{
				// 如果bitmap已经做了缩放处理，则释放旧的bitmap
				if (bitmap != null && !bitmap.isRecycled())
				{
					bitmap.recycle();
					bitmap = null;
				}
			}

			String path = Environment.getExternalStorageDirectory() + "/tempImages/";
			file = new File(path);
			if (!file.exists())
			{
				file.mkdirs();
			}

			// int index = photos.get(i).lastIndexOf("/");
			// String filename = System.currentTimeMillis() +
			// photos.get(i).substring(index + 1);
			String filename = System.currentTimeMillis() + ".jpg";

			String filepath = path + filename;

			try
			{
				fos = new FileOutputStream(filepath);
				boolean isSuccess;
				if (Build.VERSION.SDK_INT < 12)
				{
					isSuccess = compressedBitmap.compress(CompressFormat.JPEG, 100, fos);
				}
				else
				{
					if (compressedBitmap.getByteCount() >= 1024 * 1024 * 2)
					{
						isSuccess = compressedBitmap.compress(CompressFormat.JPEG, 25, fos);
					}
					else if (compressedBitmap.getByteCount() >= 1024 * 1024 * 1)
					{
						isSuccess = compressedBitmap.compress(CompressFormat.JPEG, 50, fos);
					}
					else if (compressedBitmap.getByteCount() >= 1024 * 1024 * 0.75)
					{
						isSuccess = compressedBitmap.compress(CompressFormat.JPEG, 60, fos);
					}
					else if (compressedBitmap.getByteCount() >= 1024 * 1024 * 0.2)
					{
						isSuccess = compressedBitmap.compress(CompressFormat.JPEG, 75, fos);
					}
					else
					{
						isSuccess = compressedBitmap.compress(CompressFormat.JPEG, 100, fos);
					}
				}

				if (isSuccess)
				{
					compressedImagesPaths.add(filepath);
					fos.flush();
					fos.close();
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			} finally
			{
				try
				{
					if (bitmap != null && !bitmap.isRecycled())
					{
						bitmap.recycle(); // 此句造成的以上异常
						bitmap = null;
					}
					if (compressedBitmap != null && !compressedBitmap.isRecycled())
					{
						compressedBitmap.recycle();
						compressedBitmap = null;
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		System.gc();
		return compressedImagesPaths;
	}

	// 删除
	public static void delfile(final ArrayList<String> files)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				for (int i = 0; i < files.size(); i++)
				{
					try
					{
						deleteFile(new File(files.get(i)));
					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	// 将SD卡文件删除
	public static void deleteFile(File file)
	{
		// if(sdState.equals(Environment.MEDIA_MOUNTED)){
		{
			if (file.exists())
			{
				if (file.isFile())
				{
					file.delete();
				}
				// 如果它是一个目录
				else if (file.isDirectory())
				{
					// 声明目录下所有的文件 files[];
					File files[] = file.listFiles();
					for (int i = 0; i < files.length; i++)
					{ // 遍历目录下所有的文件
						deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
					}
				}
				file.delete();
			}
		}
	}

	// 新图
	@SuppressLint("NewApi")
	public static String newImagesPath(String oldPath)
	{

		// 对给定路径的图片进行压缩
		File file;
		Bitmap bitmap = null, compressedBitmap = null;
		FileOutputStream fos = null;
		Options options = new Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565; // 默认是Bitmap.Config.ARGB_8888
		/* 下面两个字段需要组合使用 */
		options.inPurgeable = true;
		options.inInputShareable = true;
		int times = 1;
		int inSampleSize = 1;
		options.inSampleSize = inSampleSize;

		while (bitmap == null)
		{
			// 如果bitmap解析时遇到OOM，则降低inSampleSize循环解析，最多解析3次，inSampleSize最大为8,3次解析不成功则放弃这张图片的解析
			if (times > 3)
			{
				break;
			}
			try
			{
				bitmap = BitmapFactory.decodeFile(oldPath, options);
			} catch (OutOfMemoryError e)
			{

			}
			times++;
			inSampleSize = inSampleSize * 2;
			options.inSampleSize = inSampleSize;
		}

		// bitmap = BitmapFactory.decodeFile(oldPath, options);

		int detree = readPictureDegree(oldPath);
		bitmap = rotaingImageView(detree, bitmap);

		compressedBitmap = scalePic(bitmap);
		if (compressedBitmap != bitmap)
		{
			// 如果bitmap已经做了缩放处理，则释放旧的bitmap
			if (bitmap != null && !bitmap.isRecycled())
			{
				bitmap.recycle();
				bitmap = null;
			}
		}

		String path = Environment.getExternalStorageDirectory() + "/tempImages/";
		file = new File(path);
		if (!file.exists())
		{
			file.mkdirs();
		}

		int index = oldPath.lastIndexOf("/");
		String filename = System.currentTimeMillis() + oldPath.substring(index + 1);

		String filepath = path + filename;

		try
		{
			fos = new FileOutputStream(filepath);
			boolean isSuccess;
			if (Build.VERSION.SDK_INT < 12)
			{
				isSuccess = compressedBitmap.compress(CompressFormat.JPEG, 100, fos);
			}
			else
			{
				if (compressedBitmap.getByteCount() >= 1024 * 1024 * 2)
				{
					isSuccess = compressedBitmap.compress(CompressFormat.JPEG, 25, fos);
				}
				else if (compressedBitmap.getByteCount() >= 1024 * 1024 * 1)
				{
					isSuccess = compressedBitmap.compress(CompressFormat.JPEG, 50, fos);
				}
				else if (compressedBitmap.getByteCount() >= 1024 * 1024 * 0.75)
				{
					isSuccess = compressedBitmap.compress(CompressFormat.JPEG, 60, fos);
				}
				else if (compressedBitmap.getByteCount() >= 1024 * 1024 * 0.2)
				{
					isSuccess = compressedBitmap.compress(CompressFormat.JPEG, 75, fos);
				}
				else
				{
					isSuccess = compressedBitmap.compress(CompressFormat.JPEG, 100, fos);
				}
			}

			if (isSuccess)
			{
				fos.flush();
				fos.close();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (bitmap != null && !bitmap.isRecycled())
				{
					bitmap.recycle(); // 此句造成的以上异常
					bitmap = null;
				}
				if (compressedBitmap != null && !compressedBitmap.isRecycled())
				{
					compressedBitmap.recycle();
					compressedBitmap = null;
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		System.gc();
		return filepath;
	}

	public static int readPictureDegree(String path)
	{
		int degree = 0;
		try
		{
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation)
			{
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return degree;
	}

	public static Bitmap rotaingImageView(int angle, Bitmap bitmap)
	{
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		System.out.println("angle2=" + angle);
		// 创建新的图片
		Bitmap resizedBitmap = null;
		try
		{
			resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		} catch (OutOfMemoryError e)
		{
			e.printStackTrace();
			return null;
		}
		return resizedBitmap;
	}
}
