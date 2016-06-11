package com.hotbitmapgg.geekcommunity.utils;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class ImageLoadUtil
{

    /**
     * ImageLoad配置初始化
     *
     * @return
     */
    public static DisplayImageOptions defaultOptions()
    {

        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.cacheInMemory(true);
        builder.cacheOnDisk(true);
        builder.considerExifParams(true);

        builder.bitmapConfig(Bitmap.Config.RGB_565);
        return builder.build();
    }
}
