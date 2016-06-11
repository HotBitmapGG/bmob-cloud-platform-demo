package com.hotbitmapgg.geekcommunity.pick;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoUtils
{

    public static List<ImageItem> sImageItems = new ArrayList<ImageItem>();

    public static ArrayList<ImageItem> getImageItems(Context context)
    {

        ArrayList<ImageItem> ImageItems = new ArrayList<ImageItem>();

        // which image properties are we querying
        String[] projection = new String[]{MediaStore.Video.Media._ID, MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media.BUCKET_ID, MediaStore.Video.Media.DATA, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DATE_MODIFIED};

        Uri videos = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        // Make the query.
        Cursor cur = context.getContentResolver().query(videos, projection, // Which
                // columns
                // to
                // return
                MediaStore.Video.Media.SIZE + ">=?", // Which rows to return
                // (all rows)
                new String[]{"1000"}, // Selection arguments (none)
                MediaStore.Video.Media.DATE_MODIFIED // Ordering
        );

        if (cur.moveToFirst())
        {
            int id = cur.getColumnIndex(MediaStore.Video.Media._ID);
            int dataIndex = cur.getColumnIndex(MediaStore.Video.Media.DATA);
            int imageName = cur.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME);
            int imageDate = cur.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED);

            do
            {
                /* create a new ImageItem object */
                String path = cur.getString(dataIndex);
                // 判断文件存不存在，不存在，就不要加进�?
                if (new File(path).exists())
                {
                    ImageItem img = new ImageItem();
                    img.setId(cur.getInt(id));
                    img.setTitle(cur.getString(imageName));
                    img.setSubtitle(cur.getString(imageDate));
                    img.setPath(path);
                    img.setmThumbPath(getThumbPath(context, cur.getInt(id)));
                    ImageItems.add(img);
                }
            } while (cur.moveToNext());
        }
		/* close the cursor */
        cur.close();
		/* return the ImageItems list */
        return ImageItems;
    }

    public static String getThumbPath(Context context, int id)
    {
        // which image properties are we querying
        String[] projection = new String[]{MediaStore.Video.Thumbnails.DATA};

        Uri thumbUri = MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI;
        // Make the query.
        Cursor cur = context.getContentResolver().query(thumbUri, projection, // Which
                // columns
                // to
                // return
                MediaStore.Video.Thumbnails.VIDEO_ID + "=?", // Which rows to
                // return (all
                // rows)
                new String[]{"" + id}, // Selection arguments (none)
                null // Ordering
        );
        String path = "";
        if (cur.moveToFirst())
        {
            int data = cur.getColumnIndex(MediaStore.Video.Thumbnails.DATA);
            path = cur.getString(data);
        }
		/* close the cursor */
        cur.close();
		/* return the ImageItems list */
        return path;
    }

    public static DisplayImageOptions displayOptions()
    {

        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.cacheInMemory(true);
        builder.cacheOnDisk(true);
        builder.considerExifParams(true);
        builder.resetViewBeforeLoading(false);
        builder.bitmapConfig(Bitmap.Config.RGB_565);
        return builder.build();
    }
}
