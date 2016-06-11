package com.hotbitmapgg.geekcommunity.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageFile
{

	// public static String CACHE_PATH = "/sdcard/.fezomessenger-Cache/photo/";
	public static int DEFMUM_IMAGE_COMPRESSION_QUALITY = 100;
	public static int MINIMUM_IMAGE_COMPRESSION_QUALITY = 75;
	private String mPath;
	private String mSrc;
	private BitmapFactory.Options sampleOpt;

	public ImageFile()
	{

	}

	public ImageFile(String filepath)
	{
		if (null == filepath)
		{
			throw new IllegalArgumentException();
		}

		mPath = filepath;
		mSrc = mPath.substring(mPath.lastIndexOf('/') + 1);
		mSrc = mSrc.replace(' ', '_');
		decodeBoundsInfo();

	}

	private void decodeBoundsInfo()
	{
		sampleOpt = new BitmapFactory.Options();
		sampleOpt.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mPath, sampleOpt);
	}

	public String getContentType()
	{
		return sampleOpt.outMimeType;
	}

	public String getSrc()
	{
		return mSrc;
	}

	public int getWidth()
	{
		return sampleOpt.outWidth;
	}

	public int getHeight()
	{
		return sampleOpt.outHeight;
	}

	//
	// public Bitmap getBitmapSample(int widthLimit, int heightLimit) {
	// // Find the correct scale value. It should be the power of 2.
	// int width_tmp = mWidth, height_tmp = mHeight;
	//
	// int scale = 1;
	// while (true) {
	// if (width_tmp / 2 < widthLimit || height_tmp / 2 < heightLimit)
	// break;
	// width_tmp /= 2;
	// height_tmp /= 2;
	// scale *= 2;
	// }
	//
	// // Decode with inSampleSize
	// BitmapFactory.Options o2 = new BitmapFactory.Options();
	// o2.inSampleSize = scale;
	// o2.inTargetDensity = 1;
	// o2.inJustDecodeBounds = false;
	// o2.inDither = true;
	// o2.inPreferredConfig = Bitmap.Config.ARGB_8888;
	// Bitmap bmp = BitmapFactory.decodeFile(mPath, o2);
	// return bmp;
	// }

	public Bitmap getBitmapSample(int minSideLength, int maxNumOfPixels)
	{
		// Decode with inSampleSize
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inSampleSize = computeSampleSize(sampleOpt, minSideLength, maxNumOfPixels);
		// opt.inTargetDensity = 1;
		opt.inJustDecodeBounds = false;
		opt.inDither = false;
		opt.inPurgeable = false;
		// opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
		// modify by xiejin 20150407 ȡ��͸������Ϣ������һ���ڴ� ��ֹOOM
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		// end
		opt.inTempStorage = new byte[4 * 1024];
		Bitmap bmp = BitmapFactory.decodeFile(mPath, opt);
		return bmp;
	}

	public boolean writeBitmapToFile(Bitmap bmp, String filepath, CompressFormat format, int maxSizeLimit)
	{
		int quality = DEFMUM_IMAGE_COMPRESSION_QUALITY;

		int index = filepath.lastIndexOf("/");
		if (index == -1)
		{
			Log.e(ImageFile.class.getName(), "Audio file path is error: " + filepath);
			return false;
		}

		String path = filepath.substring(0, index);
		File dir = new File(path);
		if (!dir.exists())
			dir.mkdirs();

		FileOutputStream oStream;
		File fImage = new File(filepath);
		try
		{
			if (fImage.exists())
			{
				fImage.delete();
			}
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			bmp.compress(format, quality, os);
			if (os.size() > maxSizeLimit)
			{
				int reducedQuality = quality * maxSizeLimit / os.size();
				if (reducedQuality > MINIMUM_IMAGE_COMPRESSION_QUALITY)
				{
					quality = reducedQuality;
				}
				else
					quality = MINIMUM_IMAGE_COMPRESSION_QUALITY;
			}
			os.close();

			oStream = new FileOutputStream(fImage);
			bmp.compress(format, quality, oStream);
			oStream.flush();
			oStream.close();
			return true;
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels)
	{
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8)
		{
			roundedSize = 1;
			while (roundedSize < initialSize)
			{
				roundedSize <<= 1;
			}
			if (initialSize == 5)
				roundedSize = 4;
			// roundedSize = initialSize == 0 ? 1 : initialSize;
		}
		else
		{
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;

	}

	private int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels)
	{
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.round(Math.sqrt(w * h / maxNumOfPixels));
		// int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
		// .sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound)
		{
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1))
		{
			return 1;
		}
		else if (minSideLength == -1)
		{
			return lowerBound;
		}
		else
		{
			return upperBound;
		}
	}

}
