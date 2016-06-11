package com.hotbitmapgg.geekcommunity.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class DateUtil
{

	public static Date convert(Date d)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dd = format.format(d);
		Date ddd = null;
		try
		{
			ddd = format.parse(dd);
			System.out.println("dd>>>>  " + dd);
			System.out.println("ddd>>>  " + format.format(ddd));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return ddd;
	}

	public static String getDateStr(Date d)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dd = format.format(d);
		return dd;
	}

	public static <T> void SetConvertList(Set<T> set, List<T> list)
	{
		for (T o : set)
		{
			list.add(o);
		}
	}

	public static Date convertSignIn(Date d)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String dd = format.format(d);
		Date ddd = null;
		try
		{
			ddd = format.parse(dd);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return ddd;
	}
}
