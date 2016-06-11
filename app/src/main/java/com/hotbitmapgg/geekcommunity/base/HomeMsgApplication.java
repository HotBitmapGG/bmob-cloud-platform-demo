package com.hotbitmapgg.geekcommunity.base;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.bean.AlummusBean;
import com.hotbitmapgg.geekcommunity.bean.User;
import com.hotbitmapgg.geekcommunity.utils.CollectionUtils;
import com.hotbitmapgg.geekcommunity.utils.SharePreferenceUtil;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.L;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;

public class HomeMsgApplication extends Application
{

    /**
     * 家信全局自定义Application类
     */

    public static HomeMsgApplication mInstance;

    private String latitude = "";

    // 单例模式，才能及时返回数据
    SharePreferenceUtil mSpUtil;

    public static final String PREFERENCE_NAME = "_sharedinfo";

    NotificationManager mNotificationManager;

    private String longtitude = "";

    MediaPlayer mMediaPlayer;

    private AlummusBean mAlummusBean;

    public AlummusBean getmAlummusBean()
    {

        return mAlummusBean;
    }

    public void setmAlummusBean(AlummusBean mAlummusBean)
    {

        this.mAlummusBean = mAlummusBean;
    }

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
        // 是否开启debug模式--默认开启状态
        BmobChat.DEBUG_MODE = true;
        mInstance = this;
        init();
    }

    private void init()
    {

        mMediaPlayer = MediaPlayer.create(this, R.raw.notify);
        mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        initImageLoader(this);
        // 若用户登陆过，则先从好友数据库中取出好友list存入内存中
        if (BmobUserManager.getInstance(getApplicationContext()).getCurrentUser() != null)
        {
            // 获取本地好友user list到内存,方便以后获取好友list
            contactList = CollectionUtils.list2map(BmobDB.create(getApplicationContext()).getContactList());
        }
    }


    /**
     * 初始化ImageLoad
     *
     * @param context
     */
    private void initImageLoader(Context context)
    {

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory().memoryCache(new LruMemoryCache(2 * 1024 * 1024)).memoryCacheSize(2 * 1024 * 1024).memoryCacheSizePercentage(13)
                // .diskCache(new
                // LimitedAgeDiscCache(StorageUtils.getCacheDirectory(context),6 * 60 *
                // 60 * 1000))
                // .diskCache(newUnlimitedDiskCache(StorageUtils.getCacheDirectory(context)))
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()).diskCacheSize(50 * 1024 * 1024).diskCacheFileCount(1000).tasksProcessingOrder(QueueProcessingType.LIFO)
                // .writeDebugLogs()
                .build();

        ImageLoader.getInstance().init(config);
        L.writeLogs(false);
    }

    public static HomeMsgApplication getInstance()
    {

        return mInstance;
    }

    public synchronized SharePreferenceUtil getSpUtil()
    {

        if (mSpUtil == null)
        {
            String currentId = BmobUserManager.getInstance(getApplicationContext()).getCurrentUserObjectId();
            String sharedName = currentId + PREFERENCE_NAME;
            mSpUtil = new SharePreferenceUtil(this, sharedName);
        }
        return mSpUtil;
    }

    public NotificationManager getNotificationManager()
    {

        if (mNotificationManager == null)
            mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        return mNotificationManager;
    }

    public synchronized MediaPlayer getMediaPlayer()
    {

        if (mMediaPlayer == null)
            mMediaPlayer = MediaPlayer.create(this, R.raw.notify);
        return mMediaPlayer;
    }


    private Map<String,BmobChatUser> contactList = new HashMap<String,BmobChatUser>();

    /**
     * 获取内存中好友user list
     *
     * @return
     */
    public Map<String,BmobChatUser> getContactList()
    {

        return contactList;
    }

    /**
     * 设置好友user list到内存中
     *
     * @param contactList
     */
    public void setContactList(Map<String,BmobChatUser> contactList)
    {

        if (this.contactList != null)
        {
            this.contactList.clear();
        }
        this.contactList = contactList;
    }

    /**
     * 退出登录,清空缓存数据
     */
    public void logout()
    {

        BmobUserManager.getInstance(getApplicationContext()).logout();
        setContactList(null);
    }

    /**
     * 获取当前用户信息
     *
     * @return
     */
    public User getCurrentUser()
    {

        User user = BmobUser.getCurrentUser(this, User.class);
        if (user != null)
        {
            return user;
        }
        return null;
    }

    /**
     * 读取对象
     *
     * @param file
     * @return
     * @throws IOException
     */
    public Serializable readObject(String file, long... time)
    {

        if (!isExistDataCache(file, time))
            return null;

        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try
        {
            fis = openFileInput(file);
            ois = new ObjectInputStream(fis);
            return (Serializable) ois.readObject();
        } catch (FileNotFoundException e)
        {
        } catch (Exception e)
        {
            e.printStackTrace();
            // 反序列化失败 - 删除缓存文件
            if (e instanceof InvalidClassException)
            {
                File data = getFileStreamPath(file);
                data.delete();
            }
        } finally
        {
            try
            {
                ois.close();
            } catch (Exception e)
            {
            }
            try
            {
                fis.close();
            } catch (Exception e)
            {
            }
        }
        return null;
    }

    /**
     * 判断缓存是否存在
     *
     * @param cachefile
     * @param persistent 是否为永久
     * @return
     */
    public boolean isExistDataCache(String cachefile, long... time)
    {

        boolean exist = false;
        File data = getFileStreamPath(cachefile);
        if (data.exists())
            exist = true;

        boolean persistent = time == null || time.length == 0;
        return exist && (persistent || isEffictiveData(data.lastModified(), time[0]));
    }

    /**
     * 删除本地缓存数据
     *
     * @param cachefile
     * @return
     */
    public synchronized boolean delCache(String cachefile)
    {

        boolean isDel = false;
        File data = getFileStreamPath(cachefile);
        if (data.exists())
        {
            isDel = data.delete();
        }

        return isDel;
    }

    /**
     * @param modifyTime 文件创建时间
     * @param effictTime 文件有效期
     * @return
     */
    private boolean isEffictiveData(long modifyTime, long effictTime)
    {

        long diff = System.currentTimeMillis() - modifyTime;
        return diff > effictTime ? false : true;
    }
}

