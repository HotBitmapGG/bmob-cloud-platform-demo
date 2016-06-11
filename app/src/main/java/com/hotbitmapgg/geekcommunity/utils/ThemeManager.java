package com.hotbitmapgg.geekcommunity.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;

public class ThemeManager extends ContextWrapper
{

	private static ThemeManager sInstance;
	private PackageManager packageManager;
	private Context context;

	public static ThemeManager init(final Context context)
	{
		if (null == sInstance)
		{
			sInstance = new ThemeManager(context);
		}
		return sInstance;
	}

	public static ThemeManager getInstance()
	{
		if (null == sInstance)
		{
			throw new IllegalArgumentException("You must call init() method before call getInstance()");
		}
		return sInstance;
	}

	/**
	 * @param base
	 */
	private ThemeManager(Context base)
	{
		super(base);
		context = base;
		packageManager = getPackageManager();
	}

	public Drawable getDrawable(final String resName)
	{
		String pkgName = getPkgName();
		try
		{
			Resources resources = packageManager.getResourcesForApplication(pkgName);
			int id = resources.getIdentifier(resName, "drawable", pkgName);
			Drawable icon = resources.getDrawable(id);
			return icon;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private String getPkgName()
	{
		return context.getPackageName();
	}

	public String getString(final String resName)
	{
		String pkgName = getPkgName();
		try
		{
			Resources resources = packageManager.getResourcesForApplication(pkgName);
			int id = resources.getIdentifier(resName, "string", pkgName);
			String string = resources.getString(id);
			return string;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public Animation getAnimation(final String resName)
	{
		String pkgName = getPkgName();
		try
		{
			Resources resources = packageManager.getResourcesForApplication(pkgName);
			int id = resources.getIdentifier(resName, "anim", pkgName);
			Animation anim = (Animation) resources.getAnimation(id);
			return anim;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public int getColor(final String resName)
	{
		String pkgName = getPkgName();
		try
		{
			Resources resources = packageManager.getResourcesForApplication(pkgName);
			int id = resources.getIdentifier(resName, "color", pkgName);
			int color = resources.getColor(id);
			return color;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return -1;
	}

	public View getView(final String resName)
	{
		String pkgName = getPkgName();
		try
		{
			Resources resources = packageManager.getResourcesForApplication(pkgName);
			int id = resources.getIdentifier(resName, "layout", pkgName);
			LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			return inflater.inflate(id, null);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public ColorStateList getSelectColor(final String resName)
	{
		String pkgName = getPkgName();
		try
		{
			Resources resources = packageManager.getResourcesForApplication(pkgName);
			int id = resources.getIdentifier(resName, "drawable", pkgName);
			ColorStateList color = resources.getColorStateList(id);
			return color;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
