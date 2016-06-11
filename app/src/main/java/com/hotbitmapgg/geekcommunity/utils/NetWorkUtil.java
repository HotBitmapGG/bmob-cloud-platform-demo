package com.hotbitmapgg.geekcommunity.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.hotbitmapgg.geekcommunity.base.HomeMsgApplication;

public class NetWorkUtil
{

    public static boolean isAvailable(Context context)
    {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable();
    }

    /**
     * @param context
     * @return 0、手机网络 1、wifi -1、无网络
     */
    public static int networkType(Context context)
    {

        int mNetworkType = -1;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable())
        {
            mNetworkType = networkInfo.getType();
        }

        return mNetworkType;
    }

    /**
     * info.getSubtype()取值列表如下： NETWORK_TYPE_CDMA 网络类型为CDMA NETWORK_TYPE_EDGE
     * 网络类型为EDGE NETWORK_TYPE_EVDO_0 网络类型为EVDO0 NETWORK_TYPE_EVDO_A 网络类型为EVDOA
     * NETWORK_TYPE_GPRS 网络类型为GPRS NETWORK_TYPE_HSDPA 网络类型为HSDPA
     * NETWORK_TYPE_HSPA 网络类型为HSPA NETWORK_TYPE_HSUPA 网络类型为HSUPA
     * NETWORK_TYPE_UMTS 网络类型为UMTS
     * <p/>
     * 联通的3G为UMTS或HSDPA，移动和联通的2G为GPRS或EDGE，电信的2G为CDMA，电信 的3G为EVDO
     */


    public static boolean isNetworkConnected()
    {

        if (HomeMsgApplication.getInstance() != null)
        {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) HomeMsgApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null)
            {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static boolean isWifiConnected()
    {

        if (HomeMsgApplication.getInstance() != null)
        {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) HomeMsgApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null)
            {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}
