package com.hotbitmapgg.geekcommunity.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.activity.SplashActivity;
import com.hotbitmapgg.geekcommunity.base.HomeMsgApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * @author adison Activity帮助器类
 */
public final class NetopenUtils
{

	/**
	 * 获取屏幕宽高
	 * 
	 * @param activity
	 * @return
	 */
	public static int[] getScreenSize()
	{
		int[] screens;

		DisplayMetrics dm = new DisplayMetrics();
		dm = HomeMsgApplication.getInstance().getResources().getDisplayMetrics();
		screens = new int[] { dm.widthPixels, dm.heightPixels };
		return screens;
	}

	public static float[] getBitmapConfiguration(Bitmap bitmap, ImageView imageView, float screenRadio)
	{
		int screenWidth = NetopenUtils.getScreenSize()[0];
		float rawWidth = 0;
		float rawHeight = 0;
		float width = 0;
		float height = 0;
		if (bitmap == null)
		{

			width = (float) (screenWidth / screenRadio);
			height = (float) width;
			imageView.setScaleType(ScaleType.FIT_XY);
		}
		else
		{
			rawWidth = bitmap.getWidth();
			rawHeight = bitmap.getHeight();
			if (rawHeight > 10 * rawWidth)
			{
				imageView.setScaleType(ScaleType.CENTER);
			}
			else
			{
				imageView.setScaleType(ScaleType.FIT_XY);
			}
			float radio = rawHeight / rawWidth;

			width = (float) (screenWidth / screenRadio);
			height = (float) (radio * width);
		}
		return new float[] { width, height };
	}

	/**
	 * 获取应用版本号
	 * 
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context)
	{
		PackageManager pm = context.getPackageManager();
		try
		{
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 通过外部浏览器打开连接
	 * 
	 * @param context
	 * @param url
	 */
	public static void openBrowser(Context context, String urlText)
	{
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri url = Uri.parse(urlText);
		intent.setData(url);
		context.startActivity(intent);
	}

	/**
	 * 切换全屏状态
	 * 
	 * @param activity
	 *            Activity
	 * @param isFull
	 *            设置为true则全屏，否则非全屏
	 */
	public static void toggleFullScreen(Activity activity, boolean isFull)
	{
		hideTitleBar(activity);
		Window window = activity.getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		if (isFull)
		{
			params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			window.setAttributes(params);
			window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		}
		else
		{
			params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
			window.setAttributes(params);
			window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		}
	}

	/**
	 * 设置为全屏
	 * 
	 * @param activity
	 *            Activity
	 */
	public static void setFullScreen(Activity activity)
	{
		toggleFullScreen(activity, true);
	}

	/**
	 * 获取系统状态栏高度
	 * 
	 * @param activity
	 *            Activity
	 * @return 状态栏高度
	 */
	public static int getStatusBarHeight(Activity activity)
	{
		try
		{
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			Field field = clazz.getField("status_bar_height");
			int dpHeight = Integer.parseInt(field.get(object).toString());
			return activity.getResources().getDimensionPixelSize(dpHeight);
		} catch (Exception e1)
		{
			e1.printStackTrace();
			return 0;
		}
	}

	/**
	 * 隐藏Activity的系统默认标题栏
	 * 
	 * @param activity
	 *            Activity
	 */
	public static void hideTitleBar(Activity activity)
	{
		activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	/**
	 * 强制设置Actiity的显示方向为垂直方向
	 * 
	 * @param activity
	 *            Activity
	 */
	public static void setScreenVertical(Activity activity)
	{
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	/**
	 * 强制设置Activity的显示方向为横向方向
	 * 
	 * @param activity
	 *            Activity
	 */
	public static void setScreenHorizontal(Activity activity)
	{
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	/**
	 * 隐藏软件输入键盘
	 * 
	 * @param activity
	 *            Activity
	 */
	public static void hideSoftInput(Activity activity)
	{
		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	/**
	 * 关闭已经显示的输入法窗口
	 * 
	 * @param context
	 * 
	 * @param focusingView
	 *            输入法所在焦点的View
	 */
	public static void closeSoftInput(Context context, View focusingView)
	{
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(focusingView.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
	}

	/**
	 * 使UI适配输入框
	 * 
	 * @param activity
	 *            Activity
	 */
	public static void adjustSoftInput(Activity activity)
	{
		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}

	public interface MessageFilter
	{

		String filter(String msg);
	}

	public static MessageFilter msgFilter;

	public static int dip2px(Context context, float dpValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 快捷方式是否存在
	 * 
	 * @return
	 */
	public static boolean ifAddShortCut(Context context)
	{
		boolean isInstallShortCut = false;
		ContentResolver cr = context.getContentResolver();
		String authority = "com.android.launcher2.settings";
		Uri uri = Uri.parse("content://" + authority + "/favorites?notify=true");
		Cursor c = cr.query(uri, new String[] { "title", "iconResource" }, "title=?", new String[] { context.getString(R.string.app_name) }, null);
		if (null != c && c.getCount() > 0)
		{
			isInstallShortCut = true;
		}
		return isInstallShortCut;
	}

	/**
	 * 创建快捷方式
	 */
	public static void addShortCut(Context context)
	{
		Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");

		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.app_name));
		ShortcutIconResource resource = ShortcutIconResource.fromContext(context, R.drawable.app_icon);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, resource);
		// 是否允许重复创建
		shortcut.putExtra("duplicate", false);
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setClass(context, SplashActivity.class);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		context.sendBroadcast(shortcut);
	}

	/**
	 * 得到view高度
	 * 
	 * @param w
	 * @param bmw
	 * @param bmh
	 * @return
	 */
	public static int getViewHeight(int w, int bmw, int bmh)
	{
		return w * bmh / bmw;
	}

	/**
	 * 获取屏幕宽高
	 * 
	 * @param activity
	 * @return
	 */
	public static int[] getScreenSize(Activity activity)
	{
		int[] screens;

		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		screens = new int[] { dm.widthPixels, dm.heightPixels };
		return screens;
	}

	// MD5変換
	public static String Md5(String str)
	{
		if (str != null && !str.equals(""))
		{
			try
			{
				MessageDigest md5 = MessageDigest.getInstance("MD5");
				char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
				byte[] md5Byte = md5.digest(str.getBytes("UTF8"));
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < md5Byte.length; i++)
				{
					sb.append(HEX[(int) (md5Byte[i] & 0xff) / 16]);
					sb.append(HEX[(int) (md5Byte[i] & 0xff) % 16]);
				}
				str = sb.toString();
			} catch (NoSuchAlgorithmException e)
			{

			} catch (Exception e)
			{
			}
		}
		return str;
	}

	/**
	 * 得到手机IMEI
	 * 
	 * @param context
	 * @return
	 */
	public static String getImei(Context context)
	{
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		return tm.getDeviceId();
	}

	/**
	 * 判断网络连接是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean hasNetwork(Context context)
	{

		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null)
		{

			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null && info.isConnected())
			{
				// 判断当前网络是否已经连接
				if (info.getState() == NetworkInfo.State.CONNECTED)
				{
					return true;
				}
			}
		}
		return false;

	}

	/**
	 * 安装APK文件
	 * 
	 * @param file
	 */
	public static void installApk(Context context, File file)
	{
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/**
	 * 将指定byte数组转换16进制大写字符
	 * 
	 * @param b
	 * @return
	 */
	public static String byteToHexString(byte[] b)
	{
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < b.length; i++)
		{
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1)
			{
				hex = '0' + hex;
			}
			hexString.append(hex.toUpperCase());
		}
		return hexString.toString();
	}

	/**
	 * 将输入流中的数据全部读取出来
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static byte[] load(InputStream is) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		while ((len = is.read(buffer)) != -1)
			baos.write(buffer, 0, len);
		baos.close();
		is.close();
		return baos.toByteArray();
	}

	/**
	 * 根据文件得到字节
	 * 
	 * @param file
	 * @return
	 */
	public static byte[] getFileByte(File file)
	{
		if (!file.exists())
		{
			return null;
		}
		try
		{
			FileInputStream fis = new FileInputStream(file);
			int len = fis.available();
			byte[] bytes = new byte[len];
			fis.read(bytes);
			fis.close();
			return bytes;
		} catch (Exception e)
		{

		}

		return null;
	}

	/**
	 * 根据图片大小得到合适的缩放率
	 * 
	 * @param value
	 * @return
	 */
	public static int getSimpleNumber(int value)
	{
		if (value > 30)
		{
			return 1 + getSimpleNumber(value / 4);
		}
		else
		{
			return 1;
		}
	}

	public static List<String> getAllSameElement2(String[] strArr1, String[] strArr2)
	{
		if (strArr1 == null || strArr2 == null)
		{
			return null;
		}
		Arrays.sort(strArr1);
		Arrays.sort(strArr2);
		List<String> list = new ArrayList<String>();
		int k = 0;
		int j = 0;
		while (k < strArr1.length && j < strArr2.length)
		{
			if (strArr1[k].compareTo(strArr2[j]) == 0)
			{
				if (strArr1[k].equals(strArr2[j]))
				{
					list.add(strArr1[k]);
					k++;
					j++;
				}
				continue;
			}
			else if (strArr1[k].compareTo(strArr2[j]) < 0)
			{
				k++;
			}
			else
			{
				j++;
			}
		}
		return list;
	}

	/**
	 * 获取当前的年、月、日 对应的时间
	 * 
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static long getTime()
	{
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateNowStr = sdf.format(d);

		Date d2 = null;
		try
		{
			d2 = sdf.parse(dateNowStr);

			return d2.getTime();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public static <T extends View> T get(View view, int id)
	{
		SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
		if (viewHolder == null)
		{
			viewHolder = new SparseArray<View>();
			view.setTag(viewHolder);
		}
		View childView = viewHolder.get(id);
		if (childView == null)
		{
			childView = view.findViewById(id);
			viewHolder.put(id, childView);
		}
		return (T) childView;
	}

}
