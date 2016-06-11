package com.hotbitmapgg.geekcommunity.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;


public class LocalFileControl
{

	private String TAG_CLASSNAME = LocalFileControl.class.getName();

	private final static String CACHE_DIR = "alumnus";
	private static String CACHE_PATH;
	private String AUDIO_CACHE_PATH = "audio";
	private String IMG_CACHE_PATH = "image";
	private String FILE_CACHE_PATH = "file";
	private String USERPHOTO_CACHE_PATH = "userPhoto";
	private String GROUPPHOTO_CACHE_PATH = "groupPhoto";
	private String APPLOGO_CACHE_PATH = "appLogo";
	private String EXCEPTION_CACHE_PATH = "exception";
	private String VIDEO_CACHE_PATH = "video";

	private RandomAccessFile localFile;
	private String filename;

	private static volatile LocalFileControl instance = null;

	public static LocalFileControl getInstance(Context context)
	{
		if (instance == null)
		{
			synchronized (LocalFileControl.class)
			{
				if (instance == null || TextUtils.isEmpty(CACHE_PATH))
				{
					instance = new LocalFileControl(context);
				}
			}
		}
		return instance;
	}

	public LocalFileControl(Context context)
	{
		File cacheDir = StorageUtils.getOwnCacheDirectory(context, CACHE_DIR);
		CACHE_PATH = cacheDir.getPath();
	}

	public String getAudioPath()
	{
		File audio = new File(CACHE_PATH, AUDIO_CACHE_PATH);
		if (!audio.exists())
			if (!audio.mkdirs())
				return CACHE_PATH;
		return audio.getPath();
	}

	public String getVideoPath()
	{
		File video = new File(CACHE_PATH, VIDEO_CACHE_PATH);
		if (!video.exists())
			if (!video.mkdirs())
				return CACHE_PATH;
		return video.getPath();
	}

	public String getIMGPath()
	{
		File img = new File(CACHE_PATH, IMG_CACHE_PATH);
		if (!img.exists())
			if (!img.mkdirs())
				return CACHE_PATH;
		return img.getPath();
	}

	public String getFilePath()
	{
		File file = new File(CACHE_PATH, FILE_CACHE_PATH);
		if (!file.exists())
			if (!file.mkdirs())
				return CACHE_PATH;
		return file.getPath();
	}

	public String getUserPhotoPath()
	{
		File file = new File(CACHE_PATH, USERPHOTO_CACHE_PATH);
		if (!file.exists())
			if (!file.mkdirs())
				return CACHE_PATH;
		return file.getPath();
	}

	public String getGroupPhotoPath()
	{
		File file = new File(CACHE_PATH, GROUPPHOTO_CACHE_PATH);
		if (!file.exists())
			if (!file.mkdirs())
				return CACHE_PATH;
		return file.getPath();
	}

	public String getAppLogoPath()
	{
		File file = new File(CACHE_PATH, APPLOGO_CACHE_PATH);
		if (!file.exists())
			if (!file.mkdirs())
				return CACHE_PATH;
		return file.getPath();
	}

	public String getExceptionPath()
	{
		File file = new File(CACHE_PATH, EXCEPTION_CACHE_PATH);
		if (!file.exists())
			if (!file.mkdirs())
				return CACHE_PATH;
		return file.getPath();
	}

	public LocalFileControl(String fileName)
	{
		startControl(fileName);
	}

	public long getFileSize()
	{
		if (localFile == null)
		{
			Log.i(TAG_CLASSNAME, "this file is closed");
			return 0;
		}
		try
		{
			return localFile.length();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public String getFileName()
	{
		return filename;
	}

	public void startControl(String audioFileName)
	{
		try
		{
			// ��һ���������ļ���������д��ʽ
			int index = audioFileName.lastIndexOf("/");
			if (index == -1)
			{
				Log.e(TAG_CLASSNAME, "Audio file path is error: " + audioFileName);
				return;
			}

			String path = audioFileName.substring(0, index);
			File dir = new File(path);
			if (!dir.exists())
				dir.mkdirs();
			filename = dir.getName();
			localFile = new RandomAccessFile(audioFileName, "rw");
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			Log.e(TAG_CLASSNAME, "The file : " + audioFileName + "cannot be opened or created");
			e.printStackTrace();

		}
	}

	public void closeControl()
	{
		if (localFile == null)
		{
			Log.i(TAG_CLASSNAME, "this file is closed");
			return;
		}
		try
		{
			localFile.close();
			localFile = null;
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void append(byte[] content, int offset, int length)
	{

		if (localFile == null)
		{
			return;
		}

		// �ļ����ȣ��ֽ���
		long fileLength;
		try
		{
			fileLength = localFile.length();
			// Log.d(TAG_CLASSNAME, "current file size:" + fileLength);
			// ��д�ļ�ָ���Ƶ��ļ�β��
			localFile.seek(fileLength);
			localFile.write(content, offset, length);
		} catch (IOException e)
		{
			Log.e(TAG_CLASSNAME, "this file is closed, or another I/O error occurs");
			e.printStackTrace();
		}
	}

	public int read(long pos, byte[] content, int maxLen)
	{

		if (localFile == null)
		{
			return 0;
		}

		long fileLength;
		try
		{
			fileLength = localFile.length();
			if (pos > fileLength)
			{
				return 0;
			}
			// ���ļ�ָ���Ƶ�ָ����ַ��
			localFile.seek(pos);
			int readlen = localFile.read(content, 0, maxLen);
			return readlen;
		} catch (IOException e)
		{
			Log.e(TAG_CLASSNAME, "this file is closed, pos < 0 or another I/O error occurs");
			e.printStackTrace();
		}
		return 0;
	}

	public void cleanPhotoCache()
	{
		File rootCachePath = new File(CACHE_PATH);
		if (rootCachePath.exists())
		{
			File[] subPaths = rootCachePath.listFiles();
			if (subPaths != null)
			{
				for (File path : subPaths)
				{
					cleanPath(path);
				}
			}

		}
	}

	private void cleanPath(File path)
	{
		File[] photos = path.listFiles();
		if (photos != null)
		{

			SortedMap<Long, File> map = new TreeMap<Long, File>();
			for (File photo : photos)
			{
				map.put(photo.lastModified(), photo);
			}
			int removeSize = photos.length;
			int counter = 0;
			for (Entry<Long, File> entry : map.entrySet())
			{
				if (counter < removeSize)
				{
					entry.getValue().delete();
					counter++;
				}
			}
			Log.i(LocalFileControl.class.getName(), "clean " + path.getName() + " count :" + removeSize);
		}

	}
}
