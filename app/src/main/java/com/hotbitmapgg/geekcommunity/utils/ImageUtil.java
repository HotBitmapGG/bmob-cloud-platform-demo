package com.hotbitmapgg.geekcommunity.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.view.View;

public class ImageUtil
{

	public static final int IMG_FIX_HEIGHT = 1024;
	public static final int IMG_FIX_WIDTH = 1024;

	public static Bitmap viewToBitmap(View view)
	{
		int width = view.getWidth();
		int height = view.getHeight();

		Bitmap bit = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas c = new Canvas(bit);
		view.draw(c);
		return bit;
	}

	public static Bitmap drawableToBitmap(Drawable drawable)
	{
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888 : Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}

	public static Rect calculateZoomScale(Bitmap bmp, float maxW, float maxH, boolean isCent)
	{
		float bmpW = bmp.getWidth();
		float bmpH = bmp.getHeight();
		Rect newRect = new Rect();

		float scale = maxW / bmpW;

		if (bmpH * scale > maxH)
		{
			scale = maxH / bmpH;
		}
		if (isCent == true)
		{
			newRect.left = (int) ((maxW - bmpW * scale) / 2);
			newRect.top = (int) ((maxH - bmpH * scale) / 2);
		}
		else
		{
			newRect.left = 0;
			newRect.top = 0;
		}
		newRect.right = (int) (bmpW * scale + newRect.left);
		newRect.bottom = (int) (bmpH * scale + newRect.top);
		return newRect;
	}

	public static Bitmap scaleBitmap(Bitmap bitmap, int w, int h)
	{
		// 获取这个图片的宽和高
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		// 定义预转换成的图片的宽度和高�? // 计算缩放率，新尺寸除原始尺寸
		float scaleWidth = ((float) w) / width;
		float scaleHeight = ((float) h) / height;
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		// 旋转图片 动作
		// matrix.postRotate(45);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		return resizedBitmap;
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx)
	{

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle)
	{
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 50, output);
		if (needRecycle)
		{
			bmp.recycle();
		}

		byte[] result = output.toByteArray();
		try
		{
			output.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 旋转图片
	 * 
	 * @param angle
	 * @param bitmap
	 * @return Bitmap
	 */
	public static Bitmap rotaingImageView(int angle, Bitmap bitmap)
	{
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		;
		matrix.postRotate(angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	/**
	 * 读取图片属性：旋转的角度
	 * 
	 * @param path
	 *            图片绝对路径
	 * @return degree旋转的角度
	 */
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
}
