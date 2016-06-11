package com.hotbitmapgg.geekcommunity.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesUtil
{
	public static void setStringData(Context mContext, String key, String value)
	{
		if (mContext == null)
		{
			return;
		}

		SharedPreferences settings = mContext.getSharedPreferences(key, 0);

		SharedPreferences.Editor editor = settings.edit();

		editor.putString(key, value);

		editor.commit();
	}

	public static void setBooleanData(Context mContext, String key, boolean value)
	{
		if (mContext == null)
		{
			return;
		}

		SharedPreferences settings = mContext.getSharedPreferences(key, 0);

		SharedPreferences.Editor editor = settings.edit();

		editor.putBoolean(key, value);

		editor.commit();
	}

	public static void setIntData(Context mContext, String key, int value)
	{
		if (mContext == null)
		{
			return;
		}

		SharedPreferences settings = mContext.getSharedPreferences(key, 0);

		SharedPreferences.Editor editor = settings.edit();

		editor.putInt(key, value);

		editor.commit();
	}

	public static int getIntData(Context mContext, String key)
	{
		if (mContext == null)
		{
			return 0;
		}
		SharedPreferences settings = mContext.getSharedPreferences(key, 0);
		return settings.getInt(key, 0);
	}

	public static boolean getBooleanData(Context mContext, String key)
	{
		if (mContext == null)
		{
			return false;
		}
		SharedPreferences settings = mContext.getSharedPreferences(key, 0);
		return settings.getBoolean(key, false);
	}

	public static String getStringData(Context mContext, String key)
	{
		if (mContext == null)
		{
			return null;
		}
		SharedPreferences settings = mContext.getSharedPreferences(key, 0);
		return settings.getString(key, null);
	}

	public static String getTeacherIp(Context mContext)
	{
		return getStringData(mContext, "teacherIp");
	}

	public static int getTeacherPort(Context mContext)
	{
		return getIntData(mContext, "teacherPort");
	}
}
