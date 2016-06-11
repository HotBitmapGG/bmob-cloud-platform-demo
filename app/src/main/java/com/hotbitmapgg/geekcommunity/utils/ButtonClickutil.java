package com.hotbitmapgg.geekcommunity.utils;

public class ButtonClickutil
{
	/**
	 * 防止按钮多次点击工具类
	 */
	private static long lastBtnClickTime = 0;

	public ButtonClickutil()
	{

	}

	public static boolean isFastDoubleClick()
	{
		// 获取当前系统时间
		long timeMillis = System.currentTimeMillis();

		long clickTime = timeMillis - lastBtnClickTime;

		if (clickTime > 0 && clickTime < 500)
		{

			return true;
		}

		lastBtnClickTime = timeMillis;

		return false;
	}

}
