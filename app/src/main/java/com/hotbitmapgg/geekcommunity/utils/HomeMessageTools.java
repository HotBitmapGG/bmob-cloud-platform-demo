package com.hotbitmapgg.geekcommunity.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.text.format.DateUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用工具类
 *
 * @author Administrator
 *
 */
public class HomeMessageTools
{
	public static int APP_ITEM_UNIT = 60;

	public static String GroupId = "";

	public static int getVersionCode(Context context)
	{
		int versionCode = 0;
		try
		{
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			versionCode = info.versionCode;
		} catch (NameNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return versionCode;
	}

	public static String getVersionName(Context context)
	{
		String versionName = null;
		try
		{
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			versionName = info.versionName;
		} catch (NameNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return versionName = TextUtils.isEmpty(versionName) ? "" : versionName;
	}

	/**
	 * Retrieve the relative time span for displaying this message.
	 *
	 * @return
	 */
	public static final CharSequence getTimeSpan(long time)
	{
		return DateUtils.getRelativeTimeSpanString(time, System.currentTimeMillis(), 0, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_ABBREV_RELATIVE);
	}

	public static List<Activity> bindActivitys = new ArrayList<Activity>();

	public static List<Activity> unBindActivitys = new ArrayList<Activity>();

	/**
	 * @description 获取一段字符串的字符个数（包含中英文，一个中文算2个字符）
	 * @param content
	 * @return
	 */

	public static int getCharacterNum(final String content)
	{
		if (null == content || "".equals(content))
		{
			return 0;
		}
		else
		{
			return (content.length() + getChineseNum(content));
		}

	}

	/**
	 * @description 返回字符串里中文字或者全角字符的个数
	 * @param s
	 * @return
	 */
	public static int getChineseNum(String s)
	{
		int num = 0;
		char[] myChar = s.toCharArray();
		for (int i = 0; i < myChar.length; i++)
		{
			if ((char) (byte) myChar[i] != myChar[i])
			{
				num++;
			}
		}
		return num;
	}

	public static byte[] bitmap2Bytes(Bitmap bm)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	public static Bitmap bytes2Bimap(byte[] b)
	{
		if (b.length != 0)
		{
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		}
		else
		{
			return null;
		}
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

	public static String getVoicetimeDesc(long timespan)
	{
		String desc;
		if (timespan < 1000)
		{
			desc = "1\"";
		}
		else if (timespan < 60 * 1000)
		{
			long sec = timespan / 1000;
			if (timespan % 1000 > 500)
				sec++;
			desc = sec + "\"";
		}
		else
		{
			desc = "60\"";
		}

		return desc;
	}

	public static String getVoicetimeLen(long timespan)
	{
		String desc;
		if (timespan < 1000)
		{
			desc = "1";
		}
		else if (timespan < 60 * 1000)
		{
			long sec = timespan / 1000;
			if (timespan % 1000 > 500)
				sec++;
			desc = sec + "";
		}
		else
		{
			desc = "60";
		}

		return desc;
	}
}
