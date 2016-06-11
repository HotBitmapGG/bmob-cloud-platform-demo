package com.hotbitmapgg.geekcommunity.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.hotbitmapgg.geekcommunity.base.HomeMsgApplication;
import com.hotbitmapgg.geekcommunity.bean.AlummusBean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class DatabaseUtil
{
	private static final String TAG = "DatabaseUtil";

	private static DatabaseUtil instance;

	/** 数据库帮助类 **/
	private DBHelper dbHelper;

	public synchronized static DatabaseUtil getInstance(Context context)
	{
		if (instance == null)
		{
			instance = new DatabaseUtil(context);
		}
		return instance;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	private DatabaseUtil(Context context)
	{
		dbHelper = new DBHelper(context);
	}

	/**
	 * 销毁
	 */
	public static void destory()
	{
		if (instance != null)
		{
			instance.onDestory();
		}
	}

	/**
	 * 销毁
	 */
	public void onDestory()
	{
		instance = null;
		if (dbHelper != null)
		{
			dbHelper.close();
			dbHelper = null;
		}
	}

	public void deleteFav(AlummusBean bean)
	{
		Cursor cursor = null;
		String where = DBHelper.FavTable.USER_ID + " = '" + HomeMsgApplication.getInstance().getCurrentUser().getObjectId() + "' AND " + DBHelper.FavTable.OBJECT_ID + " = '" + bean.getObjectId() + "'";
		cursor = dbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
		if (cursor != null && cursor.getCount() > 0)
		{
			cursor.moveToFirst();
			int isLove = cursor.getInt(cursor.getColumnIndex(DBHelper.FavTable.IS_LOVE));
			if (isLove == 0)
			{
				dbHelper.delete(DBHelper.TABLE_NAME, where, null);
			}
			else
			{
				ContentValues cv = new ContentValues();
				cv.put(DBHelper.FavTable.IS_FAV, 0);
				dbHelper.update(DBHelper.TABLE_NAME, cv, where, null);
			}
		}
		if (cursor != null)
		{
			cursor.close();
			dbHelper.close();
		}
	}

	public boolean isLoved(AlummusBean bean)
	{
		Cursor cursor = null;
		String where = DBHelper.FavTable.USER_ID + " = '" + HomeMsgApplication.getInstance().getCurrentUser().getObjectId() + "' AND " + DBHelper.FavTable.OBJECT_ID + " = '" + bean.getObjectId() + "'";
		cursor = dbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
		if (cursor != null && cursor.getCount() > 0)
		{
			cursor.moveToFirst();
			if (cursor.getInt(cursor.getColumnIndex(DBHelper.FavTable.IS_LOVE)) == 1)
			{
				return true;
			}
		}
		return false;
	}

	public long insertFav(AlummusBean bean)
	{
		long uri = 0;
		Cursor cursor = null;
		String where = DBHelper.FavTable.USER_ID + " = '" + HomeMsgApplication.getInstance().getCurrentUser().getObjectId() + "' AND " + DBHelper.FavTable.OBJECT_ID + " = '" + bean.getObjectId() + "'";
		cursor = dbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
		if (cursor != null && cursor.getCount() > 0)
		{
			cursor.moveToFirst();
			ContentValues conv = new ContentValues();
			conv.put(DBHelper.FavTable.IS_FAV, 1);
			conv.put(DBHelper.FavTable.IS_LOVE, 1);
			dbHelper.update(DBHelper.TABLE_NAME, conv, where, null);
		}
		else
		{
			ContentValues cv = new ContentValues();
			cv.put(DBHelper.FavTable.USER_ID, HomeMsgApplication.getInstance().getCurrentUser().getObjectId());
			cv.put(DBHelper.FavTable.OBJECT_ID, bean.getObjectId());
			cv.put(DBHelper.FavTable.IS_LOVE, bean.getMyLove() == true ? 1 : 0);
			cv.put(DBHelper.FavTable.IS_FAV, bean.getMyFav() == true ? 1 : 0);
			uri = dbHelper.insert(DBHelper.TABLE_NAME, null, cv);
		}
		if (cursor != null)
		{
			cursor.close();
			dbHelper.close();
		}
		return uri;
	}

	/**
	 * 设置内容的收藏状态
	 *
	 * @param lists
	 */
	public List<AlummusBean> setFav(List<AlummusBean> lists)
	{
		Cursor cursor = null;
		if (lists != null && lists.size() > 0)
		{
			for (Iterator iterator = lists.iterator(); iterator.hasNext();)
			{
				AlummusBean content = (AlummusBean) iterator.next();
				String where = DBHelper.FavTable.USER_ID + " = '" + HomeMsgApplication.getInstance().getCurrentUser().getObjectId()// content.getAuthor().getObjectId()
						+ "' AND " + DBHelper.FavTable.OBJECT_ID + " = '" + content.getObjectId() + "'";
				cursor = dbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
				if (cursor != null && cursor.getCount() > 0)
				{
					cursor.moveToFirst();
					if (cursor.getInt(cursor.getColumnIndex(DBHelper.FavTable.IS_FAV)) == 1)
					{
						content.setMyFav(true);
					}
					else
					{
						content.setMyFav(false);
					}
					if (cursor.getInt(cursor.getColumnIndex(DBHelper.FavTable.IS_LOVE)) == 1)
					{
						content.setMyLove(true);
					}
					else
					{
						content.setMyLove(false);
					}
				}

			}
		}
		if (cursor != null)
		{
			cursor.close();
			dbHelper.close();
		}
		return lists;
	}

	/**
	 * 设置内容的收藏状态
	 *
	 * @param lists
	 */
	public List<AlummusBean> setFavInFav(List<AlummusBean> lists)
	{
		Cursor cursor = null;
		if (lists != null && lists.size() > 0)
		{
			for (Iterator iterator = lists.iterator(); iterator.hasNext();)
			{
				AlummusBean content = (AlummusBean) iterator.next();
				content.setMyFav(true);
				String where = DBHelper.FavTable.USER_ID + " = '" + HomeMsgApplication.getInstance().getCurrentUser().getObjectId() + "' AND " + DBHelper.FavTable.OBJECT_ID + " = '" + content.getObjectId() + "'";
				cursor = dbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
				if (cursor != null && cursor.getCount() > 0)
				{
					cursor.moveToFirst();
					if (cursor.getInt(cursor.getColumnIndex(DBHelper.FavTable.IS_LOVE)) == 1)
					{
						content.setMyLove(true);
					}
					else
					{
						content.setMyLove(false);
					}
				}

			}
		}
		if (cursor != null)
		{
			cursor.close();
			dbHelper.close();
		}
		return lists;
	}

	public ArrayList<AlummusBean> queryFav()
	{
		ArrayList<AlummusBean> contents = null;

		Cursor cursor = dbHelper.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);

		if (cursor == null)
		{
			return null;
		}
		contents = new ArrayList<AlummusBean>();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
		{
			AlummusBean content = new AlummusBean();
			content.setMyFav(cursor.getInt(3) == 1 ? true : false);
			content.setMyLove(cursor.getInt(4) == 1 ? true : false);

			contents.add(content);
		}
		if (cursor != null)
		{
			cursor.close();
		}

		return contents;
	}

}
